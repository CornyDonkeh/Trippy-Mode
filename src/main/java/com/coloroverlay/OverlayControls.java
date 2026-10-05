package com.coloroverlay;

import com.google.gson.Gson;

import java.awt.Color;
import java.util.List;
import net.runelite.client.config.ConfigManager;

final class OverlayControls
{
    private final ConfigManager manager;
    private final ColorOverlayConfig config;
    private final Gson gson;
    OverlayControls(ConfigManager manager, ColorOverlayConfig config, Gson gson) {
        this.manager = manager; this.config = config; this.gson = gson;
    }
    List<PaletteLibrary.Entry> saved() { return PaletteLibrary.read(config.savedPalettes(), gson); }
    void set(String key, Object value) { manager.setConfiguration("coloroverlay", key, value); }

    Color[] colors()
    {
        int count = Math.max(1, Math.min(16, config.colorCount()));
        EffectPalette palette = EffectPalette.create(config.palette(), config.customColors(), count);
        Color[] colors = new Color[count];
        for (int i = 0; i < count; i++) { colors[i] = palette.sample(i / (double) count); }
        return colors;
    }

    void selectPreset(PalettePreset preset)
    {
        set("savedPaletteName", ""); set("palette", preset);
    }

    void selectSaved(PaletteLibrary.Entry entry)
    {
        set("customColors", entry.colors); set("colorCount", entry.count);
        set("palette", PalettePreset.CUSTOM); set("savedPaletteName", entry.name);
    }

    void editColor(int index, Color color)
    {
        Color[] visible = colors();
        if (index < 0 || index >= visible.length) { return; }
        EffectPalette hidden = EffectPalette.create(PalettePreset.CUSTOM, config.customColors(), 16);
        Color[] all = new Color[16];
        for (int i = 0; i < 16; i++) {
            all[i] = i < visible.length ? visible[i] : hidden.sample(i / 16.0);
        }
        all[index] = color;
        set("customColors", encode(all)); set("savedPaletteName", ""); set("palette", PalettePreset.CUSTOM);
    }

    void count(int count)
    {
        set("savedPaletteName", ""); set("colorCount", Math.max(1, Math.min(16, count)));
    }

    void save(String name)
    {
        name = name.trim();
        if (name.isEmpty() || name.length() > 40) { throw new IllegalArgumentException("Use a palette name with 1–40 characters."); }
        final String selectedName = name;
        List<PaletteLibrary.Entry> entries = saved();
        entries.removeIf(entry -> entry.name.equalsIgnoreCase(selectedName));
        Color[] colors = colors();
        PaletteLibrary.Entry entry = new PaletteLibrary.Entry(name, encode(colors), colors.length);
        entries.add(entry); set("savedPalettes", PaletteLibrary.write(entries, gson)); selectSaved(entry);
    }

    void delete(String name)
    {
        List<PaletteLibrary.Entry> entries = saved();
        entries.removeIf(entry -> entry.name.equals(name));
        set("savedPalettes", PaletteLibrary.write(entries, gson)); set("savedPaletteName", "");
    }

    private static String encode(Color[] colors)
    {
        StringBuilder result = new StringBuilder();
        for (Color color : colors) {
            if (result.length() > 0) { result.append(','); }
            result.append(String.format("#%06X", color.getRGB() & 0xffffff));
        }
        return result.toString();
    }
}
