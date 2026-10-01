# Adding addons to THE Client

THE Client ships Meteor Client's addon loader unchanged, plus a native entrypoint of its own.
That means **every Meteor Client addon works in THE Client** as long as it targets the same
Minecraft version.

## Installing an addon

1. Get the addon's `.jar` (Modrinth, GitHub releases, etc.).
2. Drop it into `.minecraft/mods` — the **Addons** tab in the GUI has an *Open Mods Folder* button
   that takes you straight there.
3. Restart Minecraft.

The **Addons** tab (`Modules → Addons` in the top bar) lists everything that loaded, with its
version, authors and links, plus anything that failed to load and why.

## Why Meteor addons just work

| | |
| --- | --- |
| Mod id | THE Client still declares `"id": "meteor-client"`, so an addon's `"depends": { "meteor-client": "*" }` resolves normally. |
| Entrypoint | The legacy `"meteor"` entrypoint is still read by `AddonManager`. |
| API | The addon base class, event bus, `@PreInit` / `@PostInit` scanning, category registration, module attribution and title screen credits are all untouched. |
| Branding | Addons keep their own name, authors and `meteor-client:color`. |

If an addon also pins an exact `meteor-client` version (e.g. `"meteor-client": "=0.5.6"`), it will
not resolve — use an addon that depends on `"*"` or rebuild it.

## Writing an addon

An addon is an ordinary Fabric mod. The only difference is the entrypoint you declare.

### Native THE Client addon

```json
{
  "schemaVersion": 1,
  "id": "my-addon",
  "version": "1.0.0",
  "name": "My Addon",
  "authors": ["You"],
  "entrypoints": {
    "the-client": ["com.example.MyAddon"]
  },
  "depends": {
    "fabricloader": ">=0.16.0",
    "minecraft": "~1.21.11",
    "meteor-client": "*"
  },
  "custom": {
    "the-client:color": "124,92,255"
  }
}
```

```java
package com.example;

import meteordevelopment.meteorclient.addons.TheAddon;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class MyAddon extends TheAddon {
    @Override
    public void onInitialize() {
        // Register modules, commands, keybinds...
        Modules.get().add(new MyModule(Categories.Render));
    }

    @Override
    public void onRegisterCategories() {
        // Only needed for brand new categories
    }

    @Override
    public String getPackage() {
        // Scanned for @PreInit / @PostInit tasks and Orbit @EventHandler lambda factories
        return "com.example";
    }

    @Override
    public String getWebsite() {
        return "https://example.com";
    }
}
```

### Meteor Client addon

Use the exact same code, only change the entrypoint key:

```json
"entrypoints": {
  "meteor": ["com.example.MyAddon"]
}
```

```java
public class MyAddon extends MeteorAddon { ... }
```

`TheAddon` and `MeteorAddon` are the same API — `TheAddon` only marks the addon as native to
THE Client (shown as the entrypoint it was loaded from).

## Customization hooks

| `fabric.mod.json` key | Effect |
| --- | --- |
| `<entrypoint>-color` where entrypoint is `the-client` or `meteor-client` | Brand color used for the addon name on the title screen and in the Addons tab |
| `authors` | Required — at least one, otherwise the addon is reported as failed |
| `entrypoints.the-client` | Native entrypoint, class must extend `TheAddon` |
| `entrypoints.meteor` | Meteor-compatible entrypoint, class must extend `MeteorAddon` |

A mod may declare both entrypoints; it is registered only once.

## Troubleshooting

- **Addon does not appear in the Addons tab** → it either failed to load (the tab shows the error)
  or its Minecraft/`meteor-client` dependency did not resolve.
- **Game crashes with `Addon "X" is too old and cannot be ran`** → the addon was compiled against
  a different Meteor API than the one bundled in this build.
- **Check the log** for `Failed to load addon ... through the '...' entrypoint` — everything
  printed there is also surfaced in the Addons tab.
