# SimpleScrollToolTips

[![Minecraft](https://img.shields.io/badge/Minecraft-26.1--26.2-green)](https://minecraft.net)
[![Fabric](https://img.shields.io/badge/Mod%20Loader-Fabric-blue)](https://fabricmc.net)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)
[![Version](https://img.shields.io/badge/Version-1.1.0-orange)](https://modrinth.com/project/simplescrolltooltips)

**Scroll oversized item tooltips so you can actually read the parts that run off your screen.**

Ever hovered an item with so many enchantments and stats that its tooltip spills past the top *and* bottom of the screen? Vanilla just clamps it to the edge and the rest is lost. SimpleScrollToolTips lets you scroll — and pan — any tooltip with your mouse wheel or the arrow keys, so the clipped lines come into view.

**Author:** Partacus-SPQR  
**Source:** [GitHub](https://github.com/Partacus-SPQR/SimpleScrollToolTips)  
**Download:** [Modrinth](https://modrinth.com/project/simplescrolltooltips)

## Features

- **Scroll off-screen tooltips** — read tooltips taller (or wider) than your screen instead of having them clamped to the edge
- **Works in modded GUIs** — profile viewers, wardrobe/museum screens and other custom mod interfaces, not just vanilla containers
- **Mouse wheel** to scroll vertically, **Shift + wheel** to scroll horizontally
- **Arrow-key scrolling** (↑ ↓ ← →), fully rebindable
- **Auto-reset** — scroll position resets when you hover a different item
- **Adjustable sensitivity** and optional **per-axis inversion**
- **Client-side only** — works on any server, including stat-heavy ones like SkyBlock
- **[Mod Menu](https://modrinth.com/mod/modmenu)** configuration screen

## Controls

Scrolling only acts while you're hovering an item that has a tooltip.

| Input | Action |
|-------|--------|
| Mouse wheel | Scroll the tooltip vertically |
| Shift + mouse wheel | Scroll the tooltip horizontally |
| ↑ / ↓ | Scroll vertically |
| ← / → | Scroll horizontally |

All keys are rebindable in **Options → Controls → SimpleScrollToolTips**.

## Configuration

Edit in **Mod Menu**, or directly in `config/simplescrolltooltips.json`:

| Option | Default | Description |
|--------|---------|-------------|
| `enable` | `true` | Master toggle |
| `sensitivity` | `10` | Pixels moved per scroll step |
| `auto_reset` | `true` | Reset scroll when hovering a new item |
| `invert_horizontal` | `false` | Invert horizontal scroll direction |
| `invert_vertical` | `false` | Invert vertical scroll direction |
| `overflow_only` | `false` | Outside containers, only scroll tooltips that extend past the screen edge |

## Requirements

- **Minecraft** 26.1, 26.1.2, or 26.2
- **Fabric Loader** 0.16.0+
- **Fabric API**
- **Java** 25
- (optional) **Mod Menu** for in-game config

## Version Compatibility

| Minecraft | Mod Version | Fabric Loader | Java |
|-----------|-------------|---------------|------|
| 26.2 | 1.1.0 | 0.16.0+ | 25 |
| 26.1.2 | 1.1.0 | 0.16.0+ | 25 |
| 26.1 | 1.1.0 | 0.16.0+ | 25 |

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for your Minecraft version
2. Install [Fabric API](https://modrinth.com/mod/fabric-api) (and optionally [Mod Menu](https://modrinth.com/mod/modmenu))
3. Drop the matching `simplescrolltooltips-<version>.jar` into your `mods` folder

## Building

```bash
./gradlew buildAllVersions
```

Per-version jars are written to `versions/<mc>/build/libs/`.

## How it works
<img width="400" height="211" alt="bandicam 2026-06-30 02-49-07-135" src="https://github.com/user-attachments/assets/2868882c-2516-449a-a40e-715488e43157" />

Every tooltip rendered through the vanilla pipeline — containers, custom mod GUIs, any positioner — funnels through a single positioning call in `GuiGraphicsExtractor`. SimpleScrollToolTips offsets the tooltip position **after** the positioner's screen-clamp there, letting it move off-screen so the hidden lines scroll into view. Scroll input is captured at the `MouseHandler`/`Screen` level, so it works on any open screen. If you'd rather the wheel only act on tooltips that are actually cut off (so GUIs that scroll their own content are never shared), enable `overflow_only` in the config.

## License

[MIT](LICENSE)
