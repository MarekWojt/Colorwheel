package dev.djefrey.colorwheel;

import com.zurrtum.create.client.flywheel.api.backend.Engine;
import com.zurrtum.create.client.flywheel.api.backend.RenderContext;

public interface ExtendedEngine extends Engine
{
    void beginFrame(RenderContext ctx);
}
