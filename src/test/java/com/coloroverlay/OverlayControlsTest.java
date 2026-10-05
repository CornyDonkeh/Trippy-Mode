package com.coloroverlay;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.runelite.client.config.ConfigManager;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class OverlayControlsTest
{
    @Test public void editingSavingLoadingAndDeletingMatchRendererColors()
    {
        Map<String, Object> data = new HashMap<>();
        data.put("palette", PalettePreset.CUSTOM); data.put("colorCount", 3);
        data.put("customColors", "#FF0000,#0000FF,#00FF00,#123456");
        data.put("savedPalettes", "[]"); data.put("savedPaletteName", "");
        ColorOverlayConfig config = mock(ColorOverlayConfig.class);
        when(config.palette()).thenAnswer(i -> data.get("palette"));
        when(config.colorCount()).thenAnswer(i -> data.get("colorCount"));
        when(config.customColors()).thenAnswer(i -> data.get("customColors"));
        when(config.savedPalettes()).thenAnswer(i -> data.get("savedPalettes"));
        when(config.savedPaletteName()).thenAnswer(i -> data.get("savedPaletteName"));
        ConfigManager manager = mock(ConfigManager.class);
        doAnswer(i -> { data.put(i.getArgument(1), i.getArgument(2)); return null; })
            .when(manager).setConfiguration(eq("coloroverlay"), anyString(), any(Object.class));
        OverlayControls controls = new OverlayControls(manager, config, new com.google.gson.Gson());
        assertArrayEquals(new Color[]{Color.RED, Color.BLUE, Color.GREEN}, controls.colors());
        controls.editColor(1, Color.WHITE);
        assertArrayEquals(new Color[]{Color.RED, Color.WHITE, Color.GREEN}, controls.colors());
        controls.count(1); assertEquals(1, controls.colors().length);
        controls.count(4); assertEquals(new Color(0x123456), controls.colors()[3]);
        controls.count(3); controls.save("My ocean");
        assertEquals("My ocean", config.savedPaletteName());
        assertEquals(1, controls.saved().size());
        controls.selectPreset(PalettePreset.SUNSET); controls.editColor(0, Color.BLACK);
        assertEquals(PalettePreset.CUSTOM, config.palette());
        assertEquals(Color.BLACK, controls.colors()[0]);
        controls.selectSaved(controls.saved().get(0));
        assertArrayEquals(new Color[]{Color.RED, Color.WHITE, Color.GREEN}, controls.colors());
        EffectPalette rendered = EffectPalette.create(config.palette(), config.customColors(), config.colorCount());
        assertEquals(Color.WHITE, rendered.sample(1.0 / 3));
        controls.editColor(2, Color.YELLOW); controls.save("my OCEAN");
        assertEquals(1, controls.saved().size());
        assertEquals(Color.YELLOW, controls.saved().isEmpty() ? null : controls.colors()[2]);
        controls.delete("my OCEAN");
        assertTrue(controls.saved().isEmpty());
        assertEquals("", config.savedPaletteName());
        assertEquals(Color.YELLOW, controls.colors()[2]);
        assertTrue(PaletteLibrary.read("bad json", new com.google.gson.Gson()).isEmpty());
    }
}
