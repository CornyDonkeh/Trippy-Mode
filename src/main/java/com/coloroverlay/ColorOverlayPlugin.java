package com.coloroverlay;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import net.runelite.api.Client;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name = "Trippy Mode", description = "A world tint with optional Trippy styles", tags = {"color", "tint", "trippy", "rainbow"})
public class ColorOverlayPlugin extends Plugin
{
    @Inject private OverlayManager overlayManager;
    @Inject private WorldTintOverlay overlay;
    @Inject private ConfigManager manager;
    @Inject private ColorOverlayConfig config;
    @Inject private Client client;
    @Inject private ClientToolbar toolbar;
    @Inject private ColorPickerManager picker;
    private OverlayPanel panel;
    private NavigationButton navigation;
    private volatile boolean running;

    @Provides ColorOverlayConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ColorOverlayConfig.class);
    }

    @Override protected void startUp()
    {
        running = true;
        overlay.resetAnimation(); overlayManager.add(overlay);
        SwingUtilities.invokeLater(() -> {
            if (!running) { return; }
            panel = new OverlayPanel(client, picker, config, new OverlayControls(manager, config));
            BufferedImage icon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = icon.createGraphics();
            try {
                for (int i = 0; i < 6; i++) {
                    graphics.setColor(Color.getHSBColor(i / 6f, .9f, 1f));
                    graphics.fillArc(i, i + 2, 16 - i * 2, 24 - i * 2, 0, 180);
                }
                graphics.setComposite(java.awt.AlphaComposite.Clear);
                graphics.fillOval(6, 8, 4, 12);
            } finally { graphics.dispose(); }
            navigation = NavigationButton.builder().tooltip("Trippy Mode").icon(icon).priority(6).panel(panel).build();
            toolbar.addNavigation(navigation);
        });
    }

    @Subscribe public void onConfigChanged(ConfigChanged event)
    {
        if ("coloroverlay".equals(event.getGroup())) {
            SwingUtilities.invokeLater(() -> { if (running && panel != null) { panel.refresh(); } });
        }
    }

    @Override protected void shutDown()
    {
        running = false;
        overlayManager.remove(overlay);
        SwingUtilities.invokeLater(() -> {
            if (navigation != null) { toolbar.removeNavigation(navigation); navigation = null; }
            if (panel != null) { panel.disposePanel(); panel = null; }
        });
    }
}
