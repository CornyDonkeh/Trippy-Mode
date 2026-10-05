# Trippy Mode

A cosmetic RuneLite overlay that tints the whole game world beneath overhead text and widgets. No NPC targeting, boss settings, model edits, or custom characters. Uses standard overlay rendering without requiring GPU.

## One control panel

Enable Trippy Mode in the plugin list, then click its **rainbow sidebar icon**. All controls live there: enable overlay, tint color, strength, Trippy Mode, effect style, cycle speed, palettes, color count, editable swatches, palette saving/deletion, and kaleidoscope points/layers. The ordinary plugin settings contain no duplicate controls.

- Choose **Original colors** to preserve the earlier rainbow and Aurora appearance, or choose Rainbow, Ocean, Pastel, Sunset, Forest, Neon, or Custom colors.
- **Number of colors** shows exactly 1-16 swatches. Click a swatch to open RuneLite's normal color picker. Editing a suggested palette creates a custom copy.
- Custom colors use individual slots and blend smoothly between them. Reducing the count hides extra saved custom slots; raising it restores them. Older hex lists remain readable; additional slots repeat their last color.
- Original colors uses the original continuous hue mapping and ignores count in the renderer. Its swatches preview the rainbow cycle; Aurora keeps its original green/cyan/violet mapping. Editing or saving that preview makes a custom palette using the displayed colors.
- Enter a **Palette name**, then click **Save / update palette**. Names use 1-40 characters. Saved palettes appear in the same dropdown with a **Saved:** prefix. Selecting one restores its colors and color count. Saving the same name (case-insensitive) updates it. Editing a loaded palette leaves the saved version unchanged until you save again.
- **Delete saved palette** removes the selected named palette from the dropdown, retaining its current colors as an unsaved custom palette. Built-in presets cannot be deleted.
- All preferences and named palettes persist through RuneLite's normal configuration system. Palette choices are shared by all styles.

## Effects

Rainbow cycle, Rainbow wave, Color swirl, Kaleidoscope, Rainbow rings, Plasma, Aurora, Kaleidoscope stars, Kaleidoscope triangles, Kaleidoscope flower, and Kaleidoscope tunnel.

Kaleidoscope points (3-24) controls mirrored sectors/star points/petals. Layers (1-12) controls radial repetitions. Effect cycle runs from 2-120 seconds. Strength controls opacity from 0-100%; 100 covers the scene completely.

Animations follow elapsed time independently of game ticks and keep opacity steady. Breathing Tint and opacity pulsing are removed. Spatial effects use a reusable small color field with smooth scaling. They tint the world rather than distort geometry.

The tint follows the viewport in fixed/resizable layouts. Transparent interfaces show the tinted world behind them, while widgets, inventory, chat, minimap, menus, and RuneLite panels retain their own colors.

## Build and run

Requires JDK 11 or newer. Gradle 8.10; RuneLite 1.13.1; Java 11 output.

```powershell
.\gradlew.bat test build
.\gradlew.bat run
```

On macOS/Linux, use `bash gradlew test build` and `bash gradlew run`.

The run task launches developer RuneLite with Trippy Mode loaded. Build output: `build/libs/trippy-mode-1.0.0.jar`. A standalone JAR does not install automatically into normal RuneLite; Plugin Hub distribution requires separate submission/review.

Automated checks cover viewport boundaries, all spatial styles, resizing, graphics isolation, login suppression, constant opacity, palette parsing, custom colors reaching renderer data, named palette round-tripping/update/deletion, preserved hidden custom slots, and displayed swatch counts. Live in-game verification remains necessary for fixed/resizable layouts, transparent interfaces, GPU on/off, the color picker, and disabling/re-enabling the plugin.
