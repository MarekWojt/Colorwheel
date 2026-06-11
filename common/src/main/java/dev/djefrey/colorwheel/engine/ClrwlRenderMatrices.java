package dev.djefrey.colorwheel.engine;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

// Captures the per-frame model-view and projection matrices from GameRenderer.renderLevel — the
// same source Flywheel's RenderContextHolder uses. The translucent hook in LevelRendererMixin runs
// deep inside lambda$addMainPass$0, where these matrices are no longer in scope, and MC 26.1 removed
// RenderSystem.getProjectionMatrix(), so we stash them here up-front instead.
public final class ClrwlRenderMatrices
{
    private static Matrix4fc modelView = new Matrix4f();
    private static Matrix4f projection = new Matrix4f();

    private ClrwlRenderMatrices()
    {
    }

    public static void set(Matrix4fc modelView, Matrix4f projection)
    {
        ClrwlRenderMatrices.modelView = modelView;
        ClrwlRenderMatrices.projection = projection;
    }

    public static Matrix4fc modelView()
    {
        return modelView;
    }

    public static Matrix4f projection()
    {
        return projection;
    }
}
