package com.coloroverlay;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("coloroverlay")
public interface ColorOverlayConfig extends Config
{
    @ConfigItem(keyName = "color", name = "Tint color", description = "Managed in the rainbow Trippy Mode sidebar.", hidden = true)
    default Color color() { return new Color(160, 65, 255); }

    @ConfigItem(keyName = "strength", name = "Tint strength (%)", description = "Managed in the rainbow Trippy Mode sidebar.", hidden = true)
    default int strength() { return 25; }

    @ConfigItem(keyName = "trippyMode", name = "Trippy Mode", description = "Managed in the rainbow Trippy Mode sidebar.", hidden = true)
    default boolean trippyMode() { return false; }

    @ConfigItem(keyName = "trippyStyle", name = "Trippy style", description = "Managed in the rainbow Trippy Mode sidebar.", hidden = true)
    default TrippyStyle trippyStyle() { return TrippyStyle.RAINBOW_CYCLE; }

    @ConfigItem(keyName = "cycleSeconds", name = "Effect cycle (seconds)", description = "Managed in the rainbow Trippy Mode sidebar.", hidden = true)
    default int cycleSeconds() { return 15; }

    @ConfigItem(keyName = "palette", name = "Color palette", description = "Managed in the rainbow Trippy Mode sidebar.", hidden = true)
    default PalettePreset palette() { return PalettePreset.ORIGINAL; }

    @ConfigItem(keyName = "colorCount", name = "Number of colors", description = "Managed in the rainbow Trippy Mode sidebar.", hidden = true)
    default int colorCount() { return 6; }

    @ConfigItem(keyName = "customColors", name = "Custom colors", description = "Managed in the rainbow Trippy Mode sidebar.", hidden = true)
    default String customColors() { return "#002244,#0088AA,#88FFDD"; }

    @ConfigItem(keyName = "points", name = "Kaleidoscope points", description = "Managed in the rainbow Trippy Mode sidebar.", hidden = true)
    default int points() { return 6; }

    @ConfigItem(keyName = "layers", name = "Kaleidoscope layers", description = "Managed in the rainbow Trippy Mode sidebar.", hidden = true)
    default int layers() { return 3; }

    @ConfigItem(keyName = "enabled", name = "Enable overlay", description = "Managed in the rainbow Trippy Mode sidebar.", hidden = true)
    default boolean enabled() { return true; }

    @ConfigItem(keyName = "savedPalettes", name = "Saved palettes", description = "Managed in the rainbow Trippy Mode sidebar.", hidden = true)
    default String savedPalettes() { return "[]"; }

    @ConfigItem(keyName = "savedPaletteName", name = "Selected saved palette", description = "Managed in the rainbow Trippy Mode sidebar.", hidden = true)
    default String savedPaletteName() { return ""; }

}
