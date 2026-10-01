<h1 align="center">THE Client</h1>

<p align="center">
  <b>A redesigned GUI for Meteor Client — Minecraft 1.21.11</b><br>
  Same modules, same commands, same power. A whole new face.
</p>

<p align="center">
  <a href="https://github.com/preetgori09-lang/meteor-client-THEClient-addon/actions/workflows/build.yml"><img src="https://img.shields.io/github/actions/workflow/status/preetgori09-lang/meteor-client-THEClient-addon/build.yml?branch=main&label=build&logo=github" alt="Build status"></a>
  <img src="https://img.shields.io/badge/Minecraft-1.21.11-3858a6" alt="Minecraft 1.21.11">
  <a href="https://github.com/preetgori09-lang/meteor-client-THEClient-addon/blob/master/LICENSE"><img src="https://img.shields.io/github/license/preetgori09-lang/meteor-client-THEClient-addon" alt="License"></a>
  <a href="https://github.com/MeteorDevelopment/meteor-client"><img src="https://img.shields.io/badge/fork_of-Meteor_Client-8d5bd0" alt="Fork of Meteor Client"></a>
</p>

---

> [!WARNING]
> **This is a fork of [Meteor Client](https://github.com/MeteorDevelopment/meteor-client).**
> It is **not** made by, affiliated with, or endorsed by Meteor Development. All core systems and
> almost all of the source code come from the original project — this fork only adds the redesigned
> GUI and addon-quality-of-life changes described below. Please do not report vanilla Meteor bugs here.

## What is THE Client?

THE Client keeps everything that makes Meteor Client great — all `src/modules` systems, commands,
HUD, profiles, accounts and rendering — and replaces the interface with a modern, more aesthetic theme.

### The THE theme

- Rounded, glassy panels with hairline borders and soft drop shadows
- Violet → cyan accent gradient (`#7C5CFF` → `#22D3EE`) across headers, sliders, separators and pills
- Module rows rendered as inset cards with an animated gradient pill while enabled
- Accent focus rings on text boxes, gradient slider tracks with a glowing handle
- Animated checkbox ticks and pill-style top bar buttons

Everything is configurable under **GUI → theme settings**, in the `THE` group:

| Setting | Default |
| --- | --- |
| `corner-radius` | `7` |
| `shadows` | enabled |
| `accent` | `124, 92, 255` (violet) |
| `accent-2` | `34, 211, 238` (cyan) |
| `header` | `26, 26, 38` |
| `shadow` | `0, 0, 0, 110` |

The original **Meteor** theme is still included — switch back anytime from the GUI tab.

### Addons tab & improved addon support

- New **Addons** tab listing every loaded addon with name, version, authors and links
- Failed addons are shown with the reason instead of crashing the game
- Loads both the legacy `meteor` entrypoint (all existing Meteor addons) and a native `the-client` entrypoint
- *Open Mods Folder* and *Get Addons* shortcuts built in

See [ADDONS.md](ADDONS.md) for how to install and write addons.

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for **Minecraft 1.21.11**
2. Drop [the-client jar](../../releases) into your `.minecraft/mods` folder
3. *(Optional)* Drop any Meteor Client addons in the same folder — they work as-is
4. Launch the game and press **Right Shift**

> [!IMPORTANT]
> Remove any other `meteor-client` jar from your mods folder first. If THE Client still looks like
> stock Meteor, your old settings simply carried over: go to **GUI → Theme → THE** once, and it will
> be remembered. New installs get THE theme automatically.

## Building from source

```bash
./gradlew build
```

The jar is written to `build/libs/`. Requires Java 21 (a JDK is fetched automatically if needed).

Every push to `main` is built by GitHub Actions — grab a ready-made jar from the
[Actions artifacts](../../actions) or the [Releases](../../releases) page.

## Contributing

Issues and pull requests are welcome. If you're porting a change from upstream Meteor, mention the
upstream commit so it's easy to track merges.

## Credits

THE Client would not exist without [Meteor Development](https://github.com/MeteorDevelopment) and
everyone who contributed to Meteor Client. **All credit for the underlying client goes to them** —
this project is a fork and only adds the GUI work on top:

- [MineGame159](https://github.com/MineGame159), [squidoodly](https://github.com/squidoodly) and [seasnail](https://github.com/seasnail) — original Meteor Client authors
- [Cabaletta](https://github.com/cabaletta) and [WagYourTail](https://github.com/wagyourtail) for [Baritone](https://github.com/cabaletta/baritone)
- The [Fabric Team](https://github.com/FabricMC) for [Fabric](https://github.com/FabricMC/fabric-loader)
- GUI redesign and fork maintenance: [preetgori09-lang](https://github.com/preetgori09-lang)

## License

This project is licensed under the [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html),
the same license as Meteor Client — see [LICENSE](LICENSE).

Meteor Client is Copyright (c) Meteor Development. This fork contains their work, redistributed under
the GPL-3.0; the GUI changes in this fork are likewise released under GPL-3.0.

If you use **ANY** code from this repository (or from Meteor Client):

- You must disclose the source code of your modified work and the source code you took from this project.
- You must state clearly and obviously to all end users that you are using code from this project and from Meteor Client.
- Your application must also be licensed under the GPL-3.0.
