package dev.djefrey.colorwheel.mixin.mc;

import dev.djefrey.colorwheel.Colorwheel;
import dev.djefrey.colorwheel.engine.ClrwlRenderMatrices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, priority = 2000)
public class LevelRendererMixin
{
    @Shadow
    @Nullable
    private ClientLevel level;

    // Render translucent Flywheel geometry the moment the translucent phase begins. MC 26.1 moved the
    // translucent terrain rendering out of renderLevel into lambda$addMainPass$0; Iris hooks the same
    // FeatureRenderDispatcher.renderTranslucentFeatures() call to run pipeline.beginTranslucents(), so
    // we inject there too with a higher order to run *after* Iris has set up the translucent target.
    // The matrices are no longer in scope here, so GameRendererMixin captured them up-front.
    @Inject(method = "lambda$addMainPass$0(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/util/profiling/ProfilerFiller;Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;Lcom/mojang/blaze3d/resource/ResourceHandle;ZLorg/joml/Matrix4fc;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;renderTranslucentFeatures()V"),
            order = 2000) // After Iris
    public void colorwheel$injectRenderTranslucents(CallbackInfo ci)
    {
        if (level != null && Colorwheel.getSafeFlw().isColorwheelCurrentBackend())
        {
            Colorwheel.getSafeFlw().submitTranslucentRenderContext(
                level,
                Minecraft.getInstance().gameRenderer.getMainCamera(),
                ClrwlRenderMatrices.modelView(),
                ClrwlRenderMatrices.projection(),
                Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true));
        }
    }
}
