# Changelog

All notable changes to SimpleScrollToolTips will be documented in this file.

## [1.1.0] - 2026-07-08

### Added
- **Mod GUI compatibility**: tooltip scrolling now works in custom mod screens (profile viewers, wardrobe/museum GUIs, etc.), not just vanilla-style container screens. Tooltips are tracked and offset at the universal vanilla tooltip pipeline (`GuiGraphicsExtractor`), and scroll/key input is captured at the `MouseHandler`/`Screen` level so any open screen is covered.
- New `overflow_only` config option (default off): when enabled, tooltips outside containers only scroll if they extend past a screen edge, so the wheel is never shared with GUIs that scroll their own content.
- Debug tracing available via `-Dsimplescrolltooltips.debug=true`.

### Fixed
- `invert_horizontal` / `invert_vertical` config options had no effect (the inversion was applied twice and cancelled itself out).

### Changed
- In non-container GUIs, auto-reset now triggers when the tooltip disappears for a moment (hovering something new), mirroring the slot-change reset in containers.

## [1.0.0] - 2026-06-30

### Added
- Initial release.
- **Scroll oversized tooltips**: tooltips taller (or wider) than the screen can be scrolled into view. The tooltip position is offset *after* vanilla's screen-clamp (via `DefaultTooltipPositioner`), so it can move off-screen to reveal the clipped lines.
- Mouse-wheel vertical scrolling; **Shift + wheel** for horizontal.
- Arrow-key scrolling (↑ ↓ ← →), rebindable in Controls.
- Auto-reset of the scroll position when hovering a different slot.
- Configurable sensitivity and per-axis inversion.
- [Mod Menu](https://modrinth.com/mod/modmenu) integration.
- Multi-version support — Minecraft 26.1, 26.1.2, and 26.2 (unobfuscated, Java 25) — built with Stonecutter.
