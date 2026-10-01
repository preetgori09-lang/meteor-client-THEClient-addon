/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.addons;

/**
 * Base class for addons written natively for THE Client.
 * <p>
 * Declare it in your {@code fabric.mod.json}:
 * <pre>{@code
 * {
 *   "entrypoints": {
 *     "the-client": ["com.example.MyAddon"]
 *   },
 *   "custom": {
 *     "the-client:color": "124,92,255"
 *   }
 * }
 * }</pre>
 * <p>
 * Regular <b>Meteor Client</b> addons keep working untouched: they declare the {@code meteor}
 * entrypoint instead, and are loaded through exactly the same pipeline. Both kinds of addon share
 * the same API ({@link MeteorAddon}), so modules, categories, commands, event handlers,
 * {@code @PreInit}/{@code @PostInit} tasks and title screen credits all behave identically.
 */
public abstract class TheAddon extends MeteorAddon {
}
