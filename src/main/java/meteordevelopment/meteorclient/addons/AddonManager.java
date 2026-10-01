/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.addons;

import meteordevelopment.meteorclient.MeteorClient;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddonManager {
    /** Entrypoint used by regular Meteor Client addons. Kept so every existing Meteor addon keeps working. */
    public static final String METEOR_ENTRYPOINT = "meteor";
    /** Entrypoint used by addons written natively for THE Client. */
    public static final String THE_ENTRYPOINT = "the-client";

    public static final List<MeteorAddon> ADDONS = new ArrayList<>();
    public static final List<FailedAddon> FAILED = new ArrayList<>();

    private AddonManager() {
    }

    public static void init() {
        // THE Client pseudo addon
        {
            MeteorClient.ADDON = new MeteorAddon() {
                @Override
                public void onInitialize() {}

                @Override
                public String getPackage() {
                    return "meteordevelopment.meteorclient";
                }

                @Override
                public String getWebsite() {
                    return "https://meteorclient.com";
                }

                @Override
                public GithubRepo getRepo() {
                    return new GithubRepo("MeteorDevelopment", "meteor-client");
                }

                @Override
                public String getCommit() {
                    String commit = MeteorClient.MOD_META.getCustomValue(MeteorClient.MOD_ID + ":commit").getAsString();
                    return commit.isEmpty() ? null : commit;
                }
            };

            populate(MeteorClient.ADDON, FabricLoader.getInstance().getModContainer(MeteorClient.MOD_ID).get().getMetadata());

            ADDONS.add(MeteorClient.ADDON);
        }

        Set<String> registered = new HashSet<>();
        registered.add(MeteorClient.MOD_ID);

        // Legacy Meteor Client addons first, so they take priority when a mod declares both entrypoints
        load(METEOR_ENTRYPOINT, false, registered);
        load(THE_ENTRYPOINT, true, registered);
    }

    private static void load(String entrypoint, boolean theEntrypoint, Set<String> registered) {
        List<EntrypointContainer<MeteorAddon>> containers;

        try {
            containers = FabricLoader.getInstance().getEntrypointContainers(entrypoint, MeteorAddon.class);
        } catch (Throwable t) {
            MeteorClient.LOG.error("Failed to read the '{}' entrypoints of installed mods.", entrypoint, t);
            return;
        }

        for (EntrypointContainer<MeteorAddon> container : containers) {
            ModMetadata metadata = container.getProvider().getMetadata();

            // A mod declaring both entrypoints must only be registered once
            if (!registered.add(metadata.getId())) continue;

            MeteorAddon addon;

            try {
                addon = container.getEntrypoint();
            } catch (Throwable t) {
                fail(metadata, entrypoint, t);
                continue;
            }

            if (addon == null) continue;

            try {
                populate(addon, metadata);
                addon.meteorEntrypoint = !theEntrypoint;

                ADDONS.add(addon);
            } catch (Throwable t) {
                fail(metadata, entrypoint, t);
            }
        }
    }

    private static void populate(MeteorAddon addon, ModMetadata metadata) {
        addon.modId = metadata.getId();
        addon.name = metadata.getName();
        addon.version = metadata.getVersion().getFriendlyString();

        String description = metadata.getDescription();
        addon.description = description == null ? "" : description;

        if (metadata.getAuthors().isEmpty()) {
            throw new IllegalStateException("Addon \"%s\" requires at least 1 author to be defined in its fabric.mod.json file. See https://fabricmc.net/wiki/documentation:fabric_mod_json_spec".formatted(addon.name));
        }

        addon.authors = new String[metadata.getAuthors().size()];
        int i = 0;
        for (Person author : metadata.getAuthors()) addon.authors[i++] = author.getName();

        // Both addons of THE Client and Meteor Client addons can declare a brand color
        for (String key : new String[]{THE_ENTRYPOINT + ":color", MeteorClient.MOD_ID + ":color"}) {
            if (metadata.containsCustomValue(key)) {
                addon.color.parse(metadata.getCustomValue(key).getAsString());
                break;
            }
        }
    }

    private static void fail(ModMetadata metadata, String entrypoint, Throwable t) {
        String reason = t.getMessage() != null && !t.getMessage().isBlank()
            ? t.getMessage()
            : t.getClass().getSimpleName();

        MeteorClient.LOG.error("Failed to load addon '{}' through the '{}' entrypoint.", metadata.getName(), entrypoint, t);

        FAILED.add(new FailedAddon(
            metadata.getName(),
            metadata.getVersion().getFriendlyString(),
            entrypoint,
            reason
        ));
    }

    /** An addon that was declared by an installed mod but could not be loaded. */
    public record FailedAddon(String name, String version, String entrypoint, String reason) {
    }
}
