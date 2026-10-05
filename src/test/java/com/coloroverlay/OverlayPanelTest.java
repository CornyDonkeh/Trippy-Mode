package com.coloroverlay;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OverlayPanelTest
{
    @Test public void onlySelectedNumberOfSwatchesIsShown() throws Exception
    {
        int[] count = {3};
        ColorOverlayConfig config = new ColorOverlayConfig() {
            @Override public int colorCount() { return count[0]; }
        };
        SwingUtilities.invokeAndWait(() -> {
            OverlayPanel panel = new OverlayPanel(mock(Client.class), mock(ColorPickerManager.class), config,
                new OverlayControls(mock(ConfigManager.class), config, new com.google.gson.Gson()));
            assertEquals(3, swatches(panel));
            count[0] = 16; panel.refresh(); assertEquals(16, swatches(panel));
            count[0] = 1; panel.refresh(); assertEquals(1, swatches(panel));
            panel.disposePanel();
        });
    }
    private static int swatches(Container parent)
    {
        int count = 0;
        for (Component child : parent.getComponents()) {
            if (child instanceof JButton && ((JButton) child).getText() != null && ((JButton) child).getText().startsWith("Color ")) { count++; }
            else if (child instanceof Container) { count += swatches((Container) child); }
        }
        return count;
    }
}
