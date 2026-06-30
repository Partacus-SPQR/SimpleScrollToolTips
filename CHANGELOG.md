# Changelog

All notable changes to SimpleScrollToolTips will be documented in this file.

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
