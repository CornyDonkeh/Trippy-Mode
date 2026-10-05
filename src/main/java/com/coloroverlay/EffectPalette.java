package com.coloroverlay;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

final class EffectPalette
{
    private final Color[] colors;
    private EffectPalette(Color[] colors) { this.colors = colors; }

    static EffectPalette create(PalettePreset preset, String custom, int count)
    {
        if (preset == PalettePreset.ORIGINAL) { return new EffectPalette(null); }
        List<Color> parsed = new ArrayList<>();
        String source = preset == PalettePreset.CUSTOM ? custom : preset.colors;
        if (source != null)
        {
            for (String token : source.split("[,;\\s]+"))
            {
                String hex = token.startsWith("#") ? token.substring(1) : token;
                if (hex.matches("[0-9a-fA-F]{6}")) { parsed.add(new Color(Integer.parseInt(hex, 16))); }
                if (parsed.size() == 16) { break; }
            }
        }
        // Empty or malformed custom lists fall back to a known palette, without interrupting rendering.
        if (parsed.isEmpty()) { return create(PalettePreset.RAINBOW, "", count); }
        Color[] anchors = parsed.toArray(new Color[0]);
        int size = Math.max(1, Math.min(16, count));
        Color[] selected = new Color[size];
        for (int i = 0; i < size; i++)
        {
            // Preserve the first/last anchors when changing the requested palette size.
            selected[i] = preset == PalettePreset.CUSTOM ? anchors[Math.min(i, anchors.length - 1)]
                : sampleLine(anchors, size == 1 ? 0 : i * (anchors.length - 1.0) / (size - 1));
        }
        return new EffectPalette(selected);
    }

    Color sample(double position)
    {
        double fraction = position - Math.floor(position);
        if (colors == null) { return Color.getHSBColor((float) fraction, 1f, 1f); }
        double index = fraction * colors.length;
        int start = (int) index;
        return mix(colors[start], colors[(start + 1) % colors.length], index - start);
    }

    private static Color sampleLine(Color[] colors, double index)
    {
        int start = (int) index;
        return mix(colors[start], colors[Math.min(start + 1, colors.length - 1)], index - start);
    }

    private static Color mix(Color a, Color b, double amount)
    {
        return new Color((int) Math.round(a.getRed() + (b.getRed() - a.getRed()) * amount),
            (int) Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * amount),
            (int) Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * amount));
    }
}
