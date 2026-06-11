package dev.djefrey.colorwheel.mixin.mc;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import dev.djefrey.colorwheel.engine.ClrwlRenderMatrices;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// Stash the model-view + projection matrices Minecraft hands to LevelRenderer.renderLevel so the
// translucent hook (LevelRendererMixin) can build a TranslucentRenderContext. This mirrors how
// Create-Fly's GameRendererMixin feeds Flywheel's RenderContextHolder.
@Mixin(GameRenderer.class)
public class GameRendererMixin
{
    @WrapOperation(method = "renderLevel(Lnet/minecraft/client/DeltaTracker;)V",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/renderer/state/level/CameraRenderState;Lorg/joml/Matrix4fc;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;ZLnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;)V"))
    private void colorwheel$captureRenderMatrices(
        LevelRenderer instance,
        GraphicsResourceAllocator resourceAllocator,
        DeltaTracker deltaTracker,
        boolean renderOutline,
        CameraRenderState cameraState,
        Matrix4fc modelViewMatrix,
        GpuBufferSlice terrainFog,
        Vector4f fogColor,
        boolean shouldRenderSky,
        ChunkSectionsToRender chunkSectionsToRender,
        Operation<Void> original,
        @Local Matrix4f projectionMatrix)
    {
        ClrwlRenderMatrices.set(modelViewMatrix, projectionMatrix);
        original.call(instance, resourceAllocator, deltaTracker, renderOutline, cameraState,
            modelViewMatrix, terrainFog, fogColor, shouldRenderSky, chunkSectionsToRender);
    }
}
