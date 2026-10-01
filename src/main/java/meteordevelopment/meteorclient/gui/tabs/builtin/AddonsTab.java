/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.tabs.builtin;

import meteordevelopment.meteorclient.addons.AddonManager;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.CommitsScreen;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.Utils.getWindowWidth;

/**
 * Browser for installed addons. Works with both native {@code the-client} addons and regular
 * Meteor Client addons, so anything that runs on Meteor shows up here too.
 */
public class AddonsTab extends Tab {
    private static final Color ERROR_COLOR = new Color(255, 110, 125);

    public AddonsTab() {
        super("Addons");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return new AddonsScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof AddonsScreen;
    }

    public static Path modsFolder() {
        return FabricLoader.getInstance().getGameDir().resolve("mods");
    }

    private static class AddonsScreen extends WindowTabScreen {
        public AddonsScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);
        }

        @Override
        public void initWidgets() {
            // Hint
            WLabel hint = add(theme.label(
                "Addons are regular Fabric mods. Drop an addon .jar into the mods folder and restart the game - both Meteor Client and THE Client addons are supported.",
                getWindowWidth() / 2.0
            )).widget();
            hint.color = theme.textSecondaryColor();

            add(theme.horizontalSeparator()).expandX();

            // Installed addons
            WTable table = add(theme.table()).expandX().minWidth(360).widget();
            initTable(table);

            // Failed addons
            if (!AddonManager.FAILED.isEmpty()) {
                add(theme.horizontalSeparator()).expandX();

                WLabel failedHeader = add(theme.label("Could not load " + AddonManager.FAILED.size() + " addon(s):")).widget();
                failedHeader.color = ERROR_COLOR;

                for (AddonManager.FailedAddon failed : AddonManager.FAILED) {
                    WLabel label = add(theme.label(" • " + failed.name() + " (v" + failed.version() + ")", getWindowWidth() / 2.0)).widget();
                    label.color = ERROR_COLOR;
                    label.tooltip = failed.reason();
                }
            }

            add(theme.horizontalSeparator()).expandX();

            // Actions
            WHorizontalList actions = add(theme.horizontalList()).expandX().widget();

            WButton openFolder = actions.add(theme.button("Open Mods Folder")).expandX().widget();
            openFolder.action = () -> {
                Path folder = modsFolder();
                try {
                    Files.createDirectories(folder);
                } catch (IOException ignored) {
                }

                Util.getOperatingSystem().open(folder.toUri().toString());
            };
            openFolder.tooltip = modsFolder().toString();

            WButton browse = actions.add(theme.button("Get Addons")).widget();
            browse.action = () -> Util.getOperatingSystem().open("https://github.com/topics/meteor-client");
            browse.tooltip = "Browse open source addons on GitHub";
        }

        private void initTable(WTable table) {
            table.clear();

            if (AddonManager.ADDONS.isEmpty()) {
                WLabel empty = table.add(theme.label("No addons installed.")).widget();
                empty.color = theme.textSecondaryColor();
                table.row();
                return;
            }

            for (MeteorAddon addon : AddonManager.ADDONS) {
                // Name
                WLabel name = table.add(theme.label(addon.name)).widget();
                name.color = addon.color;
                if (addon.description != null && !addon.description.isBlank()) name.tooltip = addon.description;

                // Version
                WLabel version = table.add(theme.label(addon.version)).widget();
                version.color = theme.textSecondaryColor();

                // Authors
                String authors = addon.authors == null || addon.authors.length == 0
                    ? "Unknown"
                    : String.join(", ", addon.authors);
                WLabel authorsLabel = table.add(theme.label(authors)).widget();
                authorsLabel.color = theme.textSecondaryColor();

                // Links
                WHorizontalList links = table.add(theme.horizontalList()).expandCellX().right().widget();
                links.spacing = 4;

                String website = addon.getWebsite();
                if (website != null) {
                    WButton websiteButton = links.add(theme.button("Website")).widget();
                    websiteButton.action = () -> Util.getOperatingSystem().open(website);
                }

                GithubRepo repo = addon.getRepo();
                if (repo != null) {
                    if (addon.getCommit() != null) {
                        WButton commits = links.add(theme.button("Commits")).widget();
                        commits.action = () -> mc.setScreen(new CommitsScreen(theme, addon));
                    } else {
                        WButton source = links.add(theme.button("Repo")).widget();
                        source.action = () -> Util.getOperatingSystem().open(
                            String.format("https://github.com/%s/tree/%s", repo.getOwnerName(), repo.branch())
                        );
                    }
                }

                table.row();
            }
        }
    }
}
