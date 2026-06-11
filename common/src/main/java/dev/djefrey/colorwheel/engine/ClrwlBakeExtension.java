package dev.djefrey.colorwheel.engine;

import net.irisshaders.iris.vertices.ImmediateState;

// Flywheel bakes its block models on worker threads, outside the level-render window where Iris's
// transparent vertex-format extension is active (ImmediateState.isRenderingLevel). Without that, a
// TERRAIN-format buffer never gets at_tangent / mc_midTexCoord filled and the vertex finalizer
// throws. We bracket each bake (MeshEmitterManager.prepare .. end) with isRenderingLevel = true so
// Iris's own extension machinery (MixinBufferBuilder) fills those elements — no reimplementation.
//
// isRenderingLevel is a global flag the render thread also drives, so we guard with a depth counter:
// the first bake on the stack saves the prior value and forces true; the last restores it. The lock
// keeps the counter and the save/restore atomic. In practice Flywheel syncs its bake workers against
// the render thread, so the saved value is stable across a bake batch.
public final class ClrwlBakeExtension
{
    private static final Object LOCK = new Object();
    private static int depth = 0;
    private static boolean saved = false;

    private ClrwlBakeExtension()
    {
    }

    public static void enter()
    {
        synchronized (LOCK)
        {
            if (depth++ == 0)
            {
                saved = ImmediateState.isRenderingLevel;
                ImmediateState.isRenderingLevel = true;
            }
        }
    }

    public static void exit()
    {
        synchronized (LOCK)
        {
            if (depth > 0 && --depth == 0)
            {
                ImmediateState.isRenderingLevel = saved;
            }
        }
    }
}
