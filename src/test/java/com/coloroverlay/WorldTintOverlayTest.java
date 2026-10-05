package com.coloroverlay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.ui.overlay.OverlayLayer;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WorldTintOverlayTest
{
    @Test public void everyStyleIsPeriodicAndPreservesStaticTintWhenDisabled()
    {
        Color base = new Color(25, 50, 100);
        for (TrippyStyle style : TrippyStyle.values())
        {
            assertEquals(TintColor.at(base, 40, true, style, 10, 0, 0.2, 0.3),
                TintColor.at(base, 40, true, style, 10, 10, 0.2, 0.3));
            assertEquals(new Color(25, 50, 100, 102),
                TintColor.at(base, 40, false, style, 10, 5, 0.2, 0.3));
            for (int i = 0; i < 100; i++)
            {
                int alpha = TintColor.at(base, 40, true, style, 10, i / 10.0, 0.2, 0.3).getAlpha();
                assertEquals(102, alpha);
            }
        }
        for (TrippyStyle style : TrippyStyle.values())
        {
            if (!style.isSpatial()) { continue; }
            assertNotEquals(TintColor.at(base, 40, true, style, 10, 0, 0, 0),
                TintColor.at(base, 40, true, style, 10, 0, 0.3, 0.2));
        }
    }

    @Test public void spatialStylesRemainInsideViewportAfterResize()
    {
        Client client = mock(Client.class);
        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(client.getViewportXOffset()).thenReturn(10);
        when(client.getViewportYOffset()).thenReturn(20);
        when(client.getViewportWidth()).thenReturn(30, 50);
        when(client.getViewportHeight()).thenReturn(40, 60);
        for (TrippyStyle style : TrippyStyle.values())
        {
            if (!style.isSpatial()) { continue; }
            when(client.getViewportWidth()).thenReturn(30, 50);
            when(client.getViewportHeight()).thenReturn(40, 60);
            WorldTintOverlay overlay = new WorldTintOverlay(client, new ColorOverlayConfig() {
                @Override public boolean trippyMode() { return true; }
                @Override public TrippyStyle trippyStyle() { return style; }
            });
            for (int i = 0; i < 2; i++)
            {
                BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = image.createGraphics();
                try { overlay.render(graphics); }
                finally { graphics.dispose(); }
                assertEquals(0, image.getRGB(9, 20));
                assertEquals(0, image.getRGB(60, 20));
                assertEquals(0, image.getRGB(10, 80));
                assertEquals(64, image.getRGB(10, 20) >>> 24);
            }
        }
    }

    @Test public void tintStaysInsideViewportAndPreservesGraphics()
    {
        Client client = mock(Client.class);
        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(client.getViewportXOffset()).thenReturn(10);
        when(client.getViewportYOffset()).thenReturn(20);
        when(client.getViewportWidth()).thenReturn(30);
        when(client.getViewportHeight()).thenReturn(40);
        WorldTintOverlay overlay = new WorldTintOverlay(client, new ColorOverlayConfig() {});
        assertEquals(OverlayLayer.ABOVE_SCENE, overlay.getLayer());
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try
        {
            graphics.setColor(Color.ORANGE);
            assertNull(overlay.render(graphics));
            assertEquals(Color.ORANGE, graphics.getColor());
            assertEquals(0, image.getRGB(9, 20));
            assertEquals(0, image.getRGB(10, 19));
            assertEquals(0, image.getRGB(40, 20));
            assertEquals(0, image.getRGB(10, 60));
            assertEquals(64, image.getRGB(10, 20) >>> 24);
            assertEquals(64, image.getRGB(39, 59) >>> 24);
            when(client.getGameState()).thenReturn(GameState.LOGIN_SCREEN);
            overlay.render(graphics);
            assertEquals(64, image.getRGB(10, 20) >>> 24);
        }
        finally { graphics.dispose(); }
    }

    @Test public void rainbowIsPeriodicAndOpacityStaysConstant()
    {
        Color base = new Color(25, 50, 100);
        assertEquals(new Color(25, 50, 100, 128), TintColor.at(base, 50, false, 10, 5));
        assertEquals(TintColor.at(base, 50, true, 10, 0), TintColor.at(base, 50, true, 10, 10));
        assertNotEquals(TintColor.at(base, 50, true, 10, 0), TintColor.at(base, 50, true, 10, 5));
        assertEquals(128, TintColor.at(base, 50, true, 10, 5).getAlpha());
        assertEquals(128, TintColor.at(base, 50, true, 10, 0).getAlpha());
        assertEquals(0, TintColor.at(base, -10, false, 10, 0).getAlpha());
        assertEquals(255, TintColor.at(base, 200, false, 10, 0).getAlpha());
        assertFalse(new ColorOverlayConfig() {}.trippyMode());
    }
}
