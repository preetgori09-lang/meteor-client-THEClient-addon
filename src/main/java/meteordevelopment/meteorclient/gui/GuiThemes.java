/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.the.TheGuiTheme;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.PreInit;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class GuiThemes {
    private static final File FOLDER = new File(MeteorClient.FOLDER, "gui");
    private static final File THEMES_FOLDER = new File(FOLDER, "themes");
    private static final File FILE = new File(FOLDER, "gui.nbt");

    /** Marks that the one-time "Meteor" → "THE" theme migration has been handled. */
    private static final String MIGRATED_KEY = "theMigrated";

    private static final List<GuiTheme> themes = new ArrayList<>();
    private static GuiTheme theme;

    private GuiThemes() {
    }

    @PreInit
    public static void init() {
        add(new TheGuiTheme());
        add(new MeteorGuiTheme());
    }

    @PostInit
    public static void postInit() {
        if (FILE.exists()) {
            try {
                NbtCompound tag = NbtIo.read(FILE.toPath());

                if (tag != null) {
                    String saved = tag.getString("currentTheme", "");

                    // One-time migration: installs that previously ran stock Meteor have their
                    // theme saved as "Meteor". Switch them to THE once; afterwards the flag is
                    // persisted with every save, so any later deliberate choice is respected.
                    if (!tag.getBoolean(MIGRATED_KEY, false) && saved.equals("Meteor")) {
                        select("THE");
                        return;
                    }

                    select(saved);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (theme == null) select("THE");
    }

    public static void add(GuiTheme theme) {
        for (ListIterator<GuiTheme> it = themes.listIterator(); it.hasNext();) {
            if (it.next().name.equals(theme.name)) {
                // Replace the old one with same name
                it.set(theme);

                MeteorClient.LOG.error("Theme with the name '{}' has already been added.", theme.name);
                return;
            }
        }

        themes.add(theme);
    }

    public static void select(String name) {
        // Find theme with the provided name
        GuiTheme theme = null;

        for (GuiTheme t : themes) {
            if (t.name.equals(name)) {
                theme = t;
                break;
            }
        }

        if (theme != null) {
            // Save current theme
            saveTheme();

            // Select new theme
            GuiThemes.theme = theme;

            // Load new theme
            try {
                File file = new File(THEMES_FOLDER, get().name + ".nbt");

                if (file.exists()) {
                    NbtCompound tag = NbtIo.read(file.toPath());
                    if (tag != null) get().fromTag(tag);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Save global gui settings with the new theme
            saveGlobal();
        }
    }

    public static GuiTheme get() {
        return theme;
    }

    public static String[] getNames() {
        String[] names = new String[themes.size()];

        for (int i = 0; i < themes.size(); i++) {
            names[i] = themes.get(i).name;
        }

        return names;
    }

    // Saving

    private static void saveTheme() {
        if (get() != null) {
            try {
                NbtCompound tag = get().toTag();

                THEMES_FOLDER.mkdirs();
                NbtIo.write(tag, new File(THEMES_FOLDER, get().name + ".nbt").toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void saveGlobal() {
        try {
            NbtCompound tag = new NbtCompound();
            tag.putString("currentTheme", get().name);
            tag.putBoolean(MIGRATED_KEY, true);

            FOLDER.mkdirs();
            NbtIo.write(tag, FILE.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        saveTheme();
        saveGlobal();
    }
}
