package dev.djefrey.colorwheel.mixin.mc;

import dev.djefrey.colorwheel.engine.uniform.ClrwlFogUniforms;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

// Minecraft 26.1 dropped RenderSystem.getShaderFog{Color,Start,End,Shape}(), so the fog values
// have to be captured where vanilla computes them. This caches the environmental + render-distance
// ranges each pass; ClrwlFogUniforms.update() writes them into the _ClrwlFogUniforms block during
// Colorwheel's per-frame uniform update. Mirrors Create-Fly's FogRendererMixin (which feeds the
// bundled Flywheel's FogUniforms the same way).
@Mixin(FogRenderer.class)
public class FogRendererMixin
{
    @Inject(method = "updateBuffer(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", at = @At("TAIL"))
    private void colorwheel$applyFog(
        ByteBuffer buffer,
        int bufPos,
        Vector4f fogColor,
        float environmentalStart,
        float environmentalEnd,
        float renderDistanceStart,
        float renderDistanceEnd,
        float skyEnd,
        float cloudEnd,
        CallbackInfo ci)
    {
        ClrwlFogUniforms.setFog(
            fogColor.x, fogColor.y, fogColor.z, fogColor.w,
            environmentalStart, environmentalEnd,
            renderDistanceStart, renderDistanceEnd);
    }
}
