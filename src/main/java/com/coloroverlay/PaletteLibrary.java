package com.coloroverlay;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

final class PaletteLibrary
{
    static final class Entry
    {
        String name;
        String colors;
        int count;
        Entry(String name, String colors, int count) { this.name = name; this.colors = colors; this.count = count; }
        @Override public String toString() { return name; }
    }

    static List<Entry> read(String json)
    {
        List<Entry> entries = new ArrayList<>();
        try
        {
            Entry[] parsed = new Gson().fromJson(json, Entry[].class);
            if (parsed != null) {
                for (Entry entry : parsed) {
                    if (entry != null && entry.name != null && !entry.name.trim().isEmpty()
                        && entry.colors != null && entry.count >= 1 && entry.count <= 16) {
                        entries.removeIf(old -> old.name.equalsIgnoreCase(entry.name));
                        entries.add(entry);
                    }
                }
            }
        }
        catch (RuntimeException ignored) { /* Keep malformed storage from breaking the plugin. */ }
        return entries;
    }

    static String write(List<Entry> entries) { return new Gson().toJson(entries); }
}
