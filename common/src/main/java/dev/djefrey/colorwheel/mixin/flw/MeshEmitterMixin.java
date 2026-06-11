package dev.djefrey.colorwheel.mixin.flw;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.vertices.ImmediateState;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// Create-Fly's MeshEmitter builds its baked-model BufferBuilders with DefaultVertexFormat.ENTITY.
// When an Iris pack is active, force IrisVertexFormats.TERRAIN instead, so MeshHelperMixin converts
// the resulting mesh into an IrisTerrainVertexView (carrying block-sensitive data). Baking runs on
// worker threads where Iris's own format extension (gated on isRenderingLevel) won't fire, so the
// format has to be set up-front here.
@Mixin(targets = "com.zurrtum.create.client.flywheel.lib.model.baked.MeshEmitter", remap = false)
@Pseudo
public class MeshEmitterMixin
{
    @ModifyArg(method = "getBuffer",
               at = @At(value = "INVOKE",
                        target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;<init>(Lcom/mojang/blaze3d/vertex/ByteBufferBuilder;Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;Lcom/mojang/blaze3d/vertex/VertexFormat;)V"),
               index = 2,
               require = 0,
               remap = false)
    private VertexFormat colorwheel$forceTerrainFormat(VertexFormat original)
    {
        // Only force TERRAIN when Iris's vertex extension will actually run (same gate as
        // MixinBufferBuilder.iris$extendFormat) — otherwise it leaves at_tangent / mc_midTexCoord
        // unfilled and the vertex finalizer throws. Flywheel bakes many models on worker threads
        // outside the render window, where this is false, so those keep the plain ENTITY format.
        if (WorldRenderingSettings.INSTANCE.getBlockStateIds() != null
            && ImmediateState.isRenderingLevel
            && !ImmediateState.skipExtension.get())
        {
            return IrisVertexFormats.TERRAIN;
        }
        return original;
    }
}
