package com.coloroverlay;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class WorldTintOverlay extends Overlay
{
    private final Client client;
    private final ColorOverlayConfig config;
    private long animationStart;
    private BufferedImage effectImage;
    private EffectPalette palette;
    private PalettePreset palettePreset;
    private String customColors;
    private int colorCount;

    @Inject
    WorldTintOverlay(Client client, ColorOverlayConfig config)
    {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        // Scene first, tint second, overhead text and all game widgets afterward.
        setLayer(OverlayLayer.ABOVE_SCENE);
        resetAnimation();
    }

    void resetAnimation() { animationStart = System.nanoTime(); }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (client.getGameState() != GameState.LOGGED_IN || !config.enabled() || config.strength() <= 0)
        {
            return null;
        }
        int width = client.getViewportWidth();
        int height = client.getViewportHeight();
        if (width <= 0 || height <= 0) { return null; }
        Graphics2D tint = (Graphics2D) graphics.create();
        try
        {
            tint.setComposite(AlphaComposite.SrcOver);
            double elapsed = (System.nanoTime() - animationStart) / 1_000_000_000.0;
            TrippyStyle style = config.trippyStyle();
            PalettePreset selected = config.palette();
            String custom = config.customColors();
            int count = config.colorCount();
            if (palette == null || selected != palettePreset || count != colorCount
                || !java.util.Objects.equals(custom, customColors))
            {
                palette = EffectPalette.create(selected, custom, count);
                palettePreset = selected;
                customColors = custom;
                colorCount = count;
            }
            int points = config.points(), layers = config.layers();
            if (config.trippyMode() && style.isSpatial())
            {
                // A small reusable color field keeps animation cheap even on large displays.
                int imageWidth = Math.min(160, width);
                int imageHeight = Math.max(1, Math.min(160, (int) Math.round(imageWidth * (double) height / width)));
                if (effectImage == null || effectImage.getWidth() != imageWidth || effectImage.getHeight() != imageHeight)
                {
                    effectImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
                }
                java.awt.Color base = config.color();
                int strength = config.strength(), cycle = config.cycleSeconds();
                for (int y = 0; y < imageHeight; y++)
                {
                    for (int x = 0; x < imageWidth; x++)
                    {
                        double nx = (x + 0.5) / imageWidth - 0.5;
                        double ny = ((y + 0.5) / imageHeight - 0.5) * height / width;
                        effectImage.setRGB(x, y, TintColor.at(base, strength, true, style, cycle, elapsed, nx, ny,
                            palette, points, layers).getRGB());
                    }
                }
                tint.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                tint.drawImage(effectImage, client.getViewportXOffset(), client.getViewportYOffset(), width, height, null);
            }
            else
            {
                tint.setColor(TintColor.at(config.color(), config.strength(), config.trippyMode(), style,
                    config.cycleSeconds(), elapsed, 0, 0, palette, points, layers));
                tint.fillRect(client.getViewportXOffset(), client.getViewportYOffset(), width, height);
            }
        }
        finally { tint.dispose(); }
        // No bounds to drag, click, or add a menu entry to.
        return null;
    }
}
