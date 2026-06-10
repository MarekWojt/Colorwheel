package dev.djefrey.colorwheel.fabric.client;

import dev.djefrey.colorwheel.fabric.ClrwlCommandsFabric;
import dev.djefrey.colorwheel.fabric.ClrwlFabric;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public final class ClrwlFabricClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        if (ClrwlFabric.hasFlywheel())
        {
            ClientCommandRegistrationCallback.EVENT.register(ClrwlCommandsFabric::registerClientCommands);
        }

        // TODO(26.1): re-wire the Ponder scene buffer rendering. It previously used
        // WorldRenderEvents.AFTER_ENTITIES/AFTER_TRANSLUCENT (removed from Fabric API in 26.1)
        // together with PonderClientMixin (Create-Fly's ponder has no onRenderWorld hook).
    }
}
