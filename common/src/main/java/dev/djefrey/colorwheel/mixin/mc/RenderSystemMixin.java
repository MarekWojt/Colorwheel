package dev.djefrey.colorwheel.mixin.mc;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.djefrey.colorwheel.util.GlCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class RenderSystemMixin
{
    // initRenderer's parameters changed in 26.1 ((int, boolean) -> (GpuDevice)); the captured
    // args were unused, so drop them and the inject matches regardless of the target signature.
    @Inject(method = "initRenderer", at = @At("RETURN"), remap = false)
    private static void colorwheel$init(CallbackInfo ci)
    {
        GlCompat.init();
    }
}
