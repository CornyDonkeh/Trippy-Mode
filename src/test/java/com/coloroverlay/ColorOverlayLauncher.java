package com.coloroverlay;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public final class ColorOverlayLauncher
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(ColorOverlayPlugin.class);
        RuneLite.main(args);
    }
}
