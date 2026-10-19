# Trippy Mode

By CornyDonkeh.

Color tints and animated patterns for long grinds. The effect covers the game world and leaves the UI clear.

## Using the plugin

Enable Trippy Mode, then open the rainbow icon in the sidebar. All settings are in that panel.

Choose a flat tint or turn on Trippy Mode for animated effects:

- Rainbow cycle, Rainbow wave, and Color swirl
- Kaleidoscope, Stars, Triangles, Flower, and Tunnel
- Rainbow rings, Plasma, and Aurora

Adjust strength and speed to taste. Kaleidoscope points and layers change the pattern's shape.

## Palettes

Pick Original colors, Rainbow, Ocean, Pastel, Sunset, Forest, Neon, or Custom colors.

Set the number of colors, then click a swatch to change it. Editing a preset makes a custom copy. Reducing the count keeps unused custom slots saved.

To save a palette, enter a name and click **Save / update palette**. Saved palettes appear in the dropdown. Saving the same name updates it. **Delete saved palette** removes the selected saved palette without changing the colors currently on screen.

Original colors keeps the original rainbow and Aurora effects. Its swatches show a rainbow preview; editing or saving them creates a custom palette from that preview.

## Building

Requires JDK 11 or newer.

```powershell
.\gradlew.bat test build
.\gradlew.bat run
```

On macOS or Linux, use `./gradlew` instead. The run task opens a developer client with the plugin loaded.

The JAR is written to `build/libs/trippy-mode-1.0.0.jar`.

## License

[BSD 2-Clause](LICENSE).
