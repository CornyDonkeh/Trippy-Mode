package com.coloroverlay;

public enum TrippyStyle
{
    RAINBOW_CYCLE("Rainbow cycle"),
    RAINBOW_WAVE("Rainbow wave"),
    COLOR_SWIRL("Color swirl"),
    KALEIDOSCOPE("Kaleidoscope"),
    RAINBOW_RINGS("Rainbow rings"),
    PLASMA("Plasma"),
    AURORA("Aurora"),
    KALEIDOSCOPE_STARS("Kaleidoscope stars"),
    KALEIDOSCOPE_TRIANGLES("Kaleidoscope triangles"),
    KALEIDOSCOPE_FLOWER("Kaleidoscope flower"),
    KALEIDOSCOPE_TUNNEL("Kaleidoscope tunnel");

    private final String label;
    TrippyStyle(String label) { this.label = label; }
    boolean isSpatial() { return this != RAINBOW_CYCLE; }
    @Override public String toString() { return label; }
}
