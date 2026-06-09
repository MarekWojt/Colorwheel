package dev.djefrey.colorwheel.neoforge.mixin.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.foundation.events.ClientEvents;
import dev.djefrey.colorwheel.Colorwheel;
import dev.djefrey.colorwheel.mod_compat.PonderCompat;
import com.zurrtum.create.client.catnip.render.SuperRenderTypeBuffer;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientEvents.class)
public class ClientEventsMixin
{
    // This mixin is used to fix Create's schematic rendering with shaders

    @Redirect(method = "onRenderWorld",
            at = @At(value = "FIELD",
                    target = "Lnet/neoforged/neoforge/client/event/RenderLevelStageEvent$Stage;AFTER_PARTICLES:Lnet/neoforged/neoforge/client/event/RenderLevelStageEvent$Stage;",
                    opcode = Opcodes.GETSTATIC),
            require = 0,
            remap = false)
    private static RenderLevelStageEvent.Stage changeRenderStage()
    {
        if (Colorwheel.getSafeFlw().isColorwheelCurrentBackend())
        {
            return RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES;
        }

        return RenderLevelStageEvent.Stage.AFTER_PARTICLES;
    }

    @ModifyVariable(method = "onRenderWorld",
            at = @At("STORE"), name = "buffer",
            require = 0,
            remap = false)
    private static SuperRenderTypeBuffer useClrwBuffer(SuperRenderTypeBuffer buffer)
    {
        if (Colorwheel.getSafeFlw().isColorwheelCurrentBackend())
        {
            return PonderCompat.getBufferInstance();
        }

        return buffer;
    }

    @WrapOperation(method = "onRenderWorld",
            at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/render/SuperRenderTypeBuffer;draw()V"),
            require = 0,
            remap = false)
    private static void cancelDraw(SuperRenderTypeBuffer instance, Operation<Void> original)
    {
        if (Colorwheel.getSafeFlw().isColorwheelCurrentBackend())
        {
            return;
        }

        original.call(instance);
    }
}
