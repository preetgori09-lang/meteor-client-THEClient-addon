/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.addons;

import meteordevelopment.meteorclient.utils.render.color.Color;

public abstract class MeteorAddon {
    /** This field is automatically assigned from fabric.mod.json file. */
    public String name;

    /** This field is automatically assigned from fabric.mod.json file. */
    public String[] authors;

    /** This field is automatically assigned from the fabric.mod.json file. */
    public String modId = "";

    /** This field is automatically assigned from the fabric.mod.json file. */
    public String version = "";

    /** This field is automatically assigned from the fabric.mod.json file. */
    public String description = "";

    /**
     * {@code true} when the addon registered itself through the legacy {@code meteor} entrypoint
     * instead of the native {@code the-client} one. Both kinds of addons are fully supported.
     */
    public boolean meteorEntrypoint = false;

    /** This field is automatically assigned from the meteor-client:color property in fabric.mod.json file. */
    public final Color color = new Color(255, 255, 255);

    public abstract void onInitialize();

    public void onRegisterCategories() {}

    public abstract String getPackage();

    public String getWebsite() {
        return null;
    }

    public GithubRepo getRepo() {
        return null;
    }

    public String getCommit() {
        return null;
    }
}
