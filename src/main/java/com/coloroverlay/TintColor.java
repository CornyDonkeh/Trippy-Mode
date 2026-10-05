package com.coloroverlay;

import java.awt.Color;

final class TintColor
{
    private TintColor() {}

    static Color at(Color base, int strength, boolean trippy, int cycleSeconds, double elapsedSeconds)
    {
        return at(base, strength, trippy, TrippyStyle.RAINBOW_CYCLE, cycleSeconds, elapsedSeconds, 0, 0);
    }

    static Color at(Color base, int strength, boolean trippy, TrippyStyle style,
        int cycleSeconds, double elapsedSeconds, double x, double y)
    {
        return at(base, strength, trippy, style, cycleSeconds, elapsedSeconds, x, y,
            EffectPalette.create(PalettePreset.ORIGINAL, "", 6), 6, 3);
    }

    static Color at(Color base, int strength, boolean trippy, TrippyStyle style,
        int cycleSeconds, double elapsedSeconds, double x, double y, EffectPalette palette, int points, int layers)
    {
        double opacity = clamp(strength) / 100.0;
        Color rgb = base;
        if (trippy)
        {
            double phase = elapsedSeconds / Math.max(2, Math.min(120, cycleSeconds));
            double hue = phase;
            int sectors = Math.max(3, Math.min(24, points));
            double repetitions = Math.max(1, Math.min(12, layers));
            switch (style)
            {
                case RAINBOW_WAVE:
                    hue += x + 0.18 * Math.sin(2 * Math.PI * (y + phase));
                    break;
                case COLOR_SWIRL:
                    double radius = Math.hypot(x, y);
                    // Fade the angular term at the center to avoid a sharp seam or singularity.
                    hue += radius * 1.5 + 0.35 * Math.min(1, radius * 3)
                        * Math.sin(3 * Math.atan2(y, x) + radius * 8 - phase * Math.PI * 2);
                    break;
                case KALEIDOSCOPE:
                    double kaleidoRadius = Math.hypot(x, y);
                    double petals = Math.cos(sectors * Math.atan2(y, x));
                    hue += kaleidoRadius * repetitions * 2 / 3 + 0.3 * Math.min(1, kaleidoRadius * 4)
                        * petals * Math.sin(kaleidoRadius * repetitions * 10 / 3 - phase * Math.PI * 2);
                    break;
                case KALEIDOSCOPE_STARS:
                    double starRadius = Math.hypot(x, y);
                    double star = 0.65 + 0.35 * Math.cos(sectors * Math.atan2(y, x));
                    hue = starRadius * repetitions * 2 / star - phase;
                    break;
                case KALEIDOSCOPE_TRIANGLES:
                    double angle = Math.atan2(y, x) * sectors / (Math.PI * 2);
                    double folded = Math.abs(2 * (angle - Math.floor(angle)) - 1);
                    hue = Math.hypot(x, y) * repetitions * 2 + folded * Math.min(1, Math.hypot(x, y) * 5) - phase;
                    break;
                case KALEIDOSCOPE_FLOWER:
                    double flowerRadius = Math.hypot(x, y);
                    hue += flowerRadius * repetitions + 0.4 * Math.min(1, flowerRadius * 4)
                        * Math.cos(sectors * Math.atan2(y, x) + 2 * Math.PI * phase)
                        * Math.sin(flowerRadius * repetitions * Math.PI * 2);
                    break;
                case KALEIDOSCOPE_TUNNEL:
                    double tunnelRadius = Math.hypot(x, y);
                    hue = Math.log1p(tunnelRadius * 8) * repetitions - phase
                        + 0.25 * Math.min(1, tunnelRadius * 5) * Math.cos(sectors * Math.atan2(y, x) - phase * Math.PI * 2);
                    break;
                case RAINBOW_RINGS:
                    // Increasing radial hue moves each rainbow band outward over time.
                    hue = Math.hypot(x, y) * 3 - phase;
                    break;
                case PLASMA:
                    double time = phase * Math.PI * 2;
                    hue += 0.22 * (Math.sin(x * 9 + time) + Math.sin(y * 11 - time)
                        + Math.sin((x + y) * 7 + time) + Math.cos(Math.hypot(x, y) * 12 - time));
                    break;
                case AURORA:
                    double curtain = y * 3 + 0.35 * Math.sin(x * 8 + phase * Math.PI * 2)
                        + 0.15 * Math.sin(x * 17 - phase * Math.PI * 2);
                    // Green, cyan and violet ribbons at constant opacity.
                    hue = 0.5 + 0.22 * Math.sin(curtain * Math.PI * 2 + phase * Math.PI * 2);
                    break;
                default:
                    break;
            }
            rgb = palette.sample(hue);
        }
        return new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), (int) Math.round(255 * opacity));
    }

    private static int clamp(int value) { return Math.max(0, Math.min(100, value)); }
}
