package dev.djefrey.colorwheel.compile;

import com.zurrtum.create.client.flywheel.backend.glsl.LoadResult;
import com.zurrtum.create.client.flywheel.backend.glsl.ShaderSources;
import dev.djefrey.colorwheel.Colorwheel;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A {@link ShaderSources} that shields Colorwheel from transient losses of its own shader resources.
 *
 * <p>Colorwheel's shader sources live under the {@code colorwheel:} namespace and are normally loaded
 * alongside Flywheel's on every resource reload. In large modpacks a mid-game resource reload (e.g.
 * toggling Sodium's texture filtering) can leave the rebuilt resource manager without Colorwheel's
 * resource pack: all of the {@code colorwheel:internal/*} sources vanish, every shader compile throws
 * {@code "colorwheel:... was not found"}, and the engine latches into a broken (invisible) state that
 * persists across further reloads until a full game restart.</p>
 *
 * <p>The cause is external -- the resource manager dropping our pack, not Colorwheel's own code -- so
 * this layer makes Colorwheel resilient instead of trying to fix the reload itself: every
 * successfully loaded {@code colorwheel:} source is remembered, and when a later (incomplete) load can
 * no longer find one, the last known-good copy is served instead. A parsed
 * {@link com.zurrtum.create.client.flywheel.backend.glsl.SourceFile} carries its resolved includes by
 * reference, so a cached entry is fully self-contained. Sources from other namespaces (e.g.
 * {@code flywheel:}) are passed straight through -- those never go missing.</p>
 */
public class ClrwlShaderSources extends ShaderSources
{
    // Last known-good Colorwheel sources, surviving resource managers that transiently drop our pack.
    // Static so the memory persists across the per-reload ShaderSources instances; concurrent because
    // sources may be resolved off the render thread.
    private static final Map<Identifier, LoadResult> LAST_GOOD = new ConcurrentHashMap<>();

    public ClrwlShaderSources(ResourceManager manager)
    {
        super(manager);
    }

    @Override
    public LoadResult find(Identifier location)
    {
        LoadResult live = super.find(location);

        // Only our own namespace is at risk of vanishing; everything else passes straight through.
        if (!Colorwheel.MOD_ID.equals(location.getNamespace()))
        {
            return live;
        }

        if (live instanceof LoadResult.Success)
        {
            LAST_GOOD.put(location, live);
            return live;
        }

        // Live load failed (our pack was dropped by a reload) -- serve the last good copy if we have
        // one. Falls back to the original failure on a genuine first-load miss.
        LoadResult cached = LAST_GOOD.get(location);
        return cached != null ? cached : live;
    }
}
