package dev.djefrey.colorwheel.util;

import org.lwjgl.opengl.GL11;
import dev.djefrey.colorwheel.Colorwheel;

import java.util.Locale;

public class GlCompat
{
    public static final boolean SUPPORTS_OIT = isOitSupported();

    public static void init()
    {

    }

    private static boolean isOitSupported()
    {
        var isSupported = !GL11.glGetString(GL11.GL_RENDERER).toLowerCase(Locale.ROOT).startsWith("apple");

        Colorwheel.LOGGER.info("Is OIT supported: {}", isSupported);
        return isSupported;
    }
}
