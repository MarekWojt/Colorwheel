package dev.djefrey.colorwheel.mixin.flw;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.create.client.flywheel.lib.model.baked.VanillinMeshEmitterManager;
import dev.djefrey.colorwheel.accessors.MeshEmitterManagerAccessor;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.vertices.ExtendedDataHelper;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

// Feeds the per-block Iris metadata into the mesh manager as bufferBlocks() iterates positions.
// The new bake path no longer goes through MC's ModelBlockRenderer/BlockRenderDispatcher, so we hook
// the per-block `level.getBlockState(pos)` call (one per iteration) to stamp block id / render type /
// emission before the block's geometry is buffered. All baked-block geometry here is treated as
// terrain (isTerrain = true), mirroring the old prepareTerrain path.
@Mixin(targets = "com.zurrtum.create.client.flywheel.lib.model.baked.BakedModelBufferer", remap = false)
@Pseudo
public class BakedModelBuffererMixin
{
    @WrapOperation(method = "bufferBlocks",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/client/renderer/block/BlockAndTintGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"),
                   require = 0,
                   remap = false)
    private static BlockState colorwheel$captureBlock(BlockAndTintGetter level, BlockPos pos, Operation<BlockState> original, @Local VanillinMeshEmitterManager emitters)
    {
        BlockState state = original.call(level, pos);

        // Only stamp metadata while an Iris pack is actively rendering (matches the TERRAIN format
        // forcing); getBlockStateIds() stays non-null after a shader toggle, so it can't be the gate.
        var ids = WorldRenderingSettings.INSTANCE.getBlockStateIds();
        if (Iris.isPackInUseQuick() && ids != null)
        {
            FluidState fluidState = state.getFluidState();
            byte renderType = fluidState.isEmpty()
                ? (byte) ExtendedDataHelper.BLOCK_RENDER_TYPE
                : (byte) ExtendedDataHelper.FLUID_RENDER_TYPE;

            ((MeshEmitterManagerAccessor) emitters).colorwheel$prepareBlock(
                ids.getOrDefault(state, -1),
                renderType,
                (byte) state.getLightEmission(),
                true,
                pos.getX(),
                pos.getY(),
                pos.getZ());
        }

        return state;
    }
}
