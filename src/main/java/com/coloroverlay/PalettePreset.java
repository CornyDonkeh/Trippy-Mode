package com.coloroverlay;

public enum PalettePreset
{
    ORIGINAL("Original colors", ""),
    RAINBOW("Rainbow", "#FF0000,#FFFF00,#00FF00,#00FFFF,#0000FF,#FF00FF"),
    OCEAN("Ocean", "#062A78,#0077B6,#00B4D8,#90E0EF"),
    PASTEL("Pastel", "#FFADAD,#FFD6A5,#FDFFB6,#CAFFBF,#BDB2FF,#FFC6FF"),
    SUNSET("Sunset", "#FFB347,#FF7043,#E94057,#8A2387,#38205C"),
    FOREST("Forest", "#153B24,#2D6A4F,#74C69D,#D8F3DC"),
    NEON("Neon", "#FF00C8,#7400FF,#00E5FF,#A8FF00"),
    CUSTOM("Custom colors", "");

    final String colors;
    private final String label;
    PalettePreset(String label, String colors) { this.label = label; this.colors = colors; }
    @Override public String toString() { return label; }
}
