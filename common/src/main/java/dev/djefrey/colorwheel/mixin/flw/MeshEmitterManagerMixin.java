package dev.djefrey.colorwheel.mixin.flw;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.zurrtum.create.client.flywheel.lib.model.SimpleModel;
import dev.djefrey.colorwheel.ColorwheelBufferBuilder;
import dev.djefrey.colorwheel.accessors.MeshEmitterManagerAccessor;
import dev.djefrey.colorwheel.engine.ClrwlBakeExtension;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Holds the current block's Iris metadata (set per-block by BakedModelBuffererMixin) and stamps it
// onto every BufferBuilder the manager hands out, so the baked-model vertices carry the right block
// id / render type / emission / mid-block. MeshEmitter buffers are ColorwheelBufferBuilders via
// mc.BufferBuilderMixin. Manager instances are thread-local, so the @Unique state is per-thread.
@Mixin(targets = "com.zurrtum.create.client.flywheel.lib.model.baked.MeshEmitterManager", remap = false)
@Pseudo
public class MeshEmitterManagerMixin implements MeshEmitterManagerAccessor
{
    @Unique private int colorwheel$block = -1;
    @Unique private byte colorwheel$renderType = -1;
    @Unique private byte colorwheel$emission = -1;
    @Unique private boolean colorwheel$isTerrain = false;
    @Unique private int colorwheel$posX = 0;
    @Unique private int colorwheel$posY = 0;
    @Unique private int colorwheel$posZ = 0;

    @Unique private boolean colorwheel$enteredBake = false;

    // Bracket the whole bake (prepare .. end) with Iris's vertex extension enabled so it fills
    // at_tangent / mc_midTexCoord on the forced TERRAIN buffers. The per-bake flag keeps enter/exit
    // paired even when the pack state changes; managers are thread-local so it's per-bake-thread.
    @Inject(method = "prepare", at = @At("HEAD"), require = 0, remap = false)
    private void colorwheel$enterBake(CallbackInfo ci)
    {
        colorwheel$enteredBake = WorldRenderingSettings.INSTANCE.getBlockStateIds() != null;
        if (colorwheel$enteredBake)
        {
            ClrwlBakeExtension.enter();
        }
    }

    @Inject(method = "end", at = @At("RETURN"), require = 0, remap = false)
    private void colorwheel$exitBake(CallbackInfoReturnable<SimpleModel> cir)
    {
        if (colorwheel$enteredBake)
        {
            ClrwlBakeExtension.exit();
            colorwheel$enteredBake = false;
        }
    }

    @Override
    public void colorwheel$prepareBlock(int block, byte renderType, byte emission, boolean isTerrain, int posX, int posY, int posZ)
    {
        this.colorwheel$block = block;
        this.colorwheel$renderType = renderType;
        this.colorwheel$emission = emission;
        this.colorwheel$isTerrain = isTerrain;
        this.colorwheel$posX = posX;
        this.colorwheel$posY = posY;
        this.colorwheel$posZ = posZ;
    }

    @Inject(method = "getBuffer", at = @At("RETURN"), require = 0, remap = false)
    private void colorwheel$tagBuffer(CallbackInfoReturnable<BufferBuilder> cir)
    {
        if (WorldRenderingSettings.INSTANCE.getBlockStateIds() == null)
        {
            return;
        }

        if (cir.getReturnValue() instanceof ColorwheelBufferBuilder clrwl)
        {
            clrwl.clrwlBeginBlock(colorwheel$block, colorwheel$renderType, colorwheel$emission, colorwheel$isTerrain, colorwheel$posX, colorwheel$posY, colorwheel$posZ);
        }
    }
}
