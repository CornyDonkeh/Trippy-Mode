package com.coloroverlay;

import java.awt.Color;
import org.junit.Test;
import static org.junit.Assert.*;

public class EffectPaletteTest
{
    @Test public void customColorsRespectCountAndWrapSmoothly()
    {
        EffectPalette palette = EffectPalette.create(PalettePreset.CUSTOM, "#FF0000,#0000FF", 2);
        assertEquals(Color.RED, palette.sample(0));
        assertEquals(Color.BLUE, palette.sample(0.5));
        assertEquals(new Color(128, 0, 128), palette.sample(0.25));
        assertEquals(palette.sample(0.25), palette.sample(1.25));
        EffectPalette single = EffectPalette.create(PalettePreset.CUSTOM, "#123456,#FFFFFF", 1);
        assertEquals(new Color(0x123456), single.sample(0.77));
        EffectPalette three = EffectPalette.create(PalettePreset.CUSTOM, "#FF0000,#0000FF", 3);
        assertEquals(Color.BLUE, three.sample(1.0 / 3));
        assertEquals(Color.BLUE, three.sample(2.0 / 3));
    }

    @Test public void invalidEntriesAreSkippedAndEmptyListsFallBack()
    {
        assertEquals(Color.GREEN, EffectPalette.create(PalettePreset.CUSTOM, "bad,#00FF00,#wrong", 1).sample(0));
        assertEquals(EffectPalette.create(PalettePreset.RAINBOW, "", 6).sample(0.4),
            EffectPalette.create(PalettePreset.CUSTOM, "bad", 6).sample(0.4));
        for (PalettePreset preset : PalettePreset.values())
        {
            assertNotNull(EffectPalette.create(preset, null, 100).sample(-0.5));
        }
    }

    @Test public void allStylesUseCustomColorsWithoutChangingOpacity()
    {
        EffectPalette palette = EffectPalette.create(PalettePreset.CUSTOM, "#123456", 1);
        for (TrippyStyle style : TrippyStyle.values())
        {
            assertEquals(new Color(0x12, 0x34, 0x56, 102),
                TintColor.at(Color.RED, 40, true, style, 10, 2, 0.2, 0.3, palette, 7, 4));
            assertEquals(new Color(255, 0, 0, 102),
                TintColor.at(Color.RED, 40, false, style, 10, 2, 0.2, 0.3, palette, 7, 4));
        }
    }

    @Test public void kaleidoscopeShapeControlsChangePatternAndPreserveSymmetry()
    {
        EffectPalette palette = EffectPalette.create(PalettePreset.ORIGINAL, "", 6);
        for (TrippyStyle style : new TrippyStyle[]{TrippyStyle.KALEIDOSCOPE, TrippyStyle.KALEIDOSCOPE_STARS,
            TrippyStyle.KALEIDOSCOPE_TRIANGLES, TrippyStyle.KALEIDOSCOPE_FLOWER, TrippyStyle.KALEIDOSCOPE_TUNNEL})
        {
            Color original = TintColor.at(Color.RED, 40, true, style, 10, 0, 0.3, 0.2, palette, 6, 3);
            assertNotEquals(original, TintColor.at(Color.RED, 40, true, style, 10, 0, 0.3, 0.2, palette, 9, 3));
            assertNotEquals(original, TintColor.at(Color.RED, 40, true, style, 10, 0, 0.3, 0.2, palette, 6, 5));
            double angle = 2 * Math.PI / 6;
            double x = 0.3 * Math.cos(angle) - 0.2 * Math.sin(angle);
            double y = 0.3 * Math.sin(angle) + 0.2 * Math.cos(angle);
            assertEquals(original, TintColor.at(Color.RED, 40, true, style, 10, 0, x, y, palette, 6, 3));
        }
    }
}
