package dev.djefrey.colorwheel.fabric;

import net.fabricmc.api.ModInitializer;

import dev.djefrey.colorwheel.Colorwheel;
import net.fabricmc.loader.api.FabricLoader;

public final class ClrwlFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        boolean hasFlywheel = hasFlywheel();

        if (hasFlywheel && !isFlywheelVersionSupported())
        {
            throw new RuntimeException("Flywheel version is not compatible with Colorwheel");
        }

        Colorwheel.init(hasFlywheel);

        if (hasFlywheel)
        {
            ClrwlConfigFabric.INSTANCE.load();
        }
    }

    public static boolean hasFlywheel()
    {
        // For 26.1 Flywheel is bundled inside Create-Fly (mod id "create"); there is no standalone
        // "flywheel" mod. Accept either.
        var loader = FabricLoader.getInstance();
        return loader.isModLoaded("flywheel") || loader.isModLoaded("create");
    }

    public static boolean hasPonder()
    {
        // Ponder is likewise bundled inside Create-Fly.
        var loader = FabricLoader.getInstance();
        return loader.isModLoaded("ponder") || loader.isModLoaded("create");
    }

    public static boolean isFlywheelVersionSupported()
    {
        var flw = FabricLoader.getInstance().getModContainer("flywheel");

        // When Flywheel is provided by Create-Fly there is no standalone "flywheel" mod to version
        // check; this port targets Create-Fly's bundled Flywheel directly, so accept it.
        if (flw.isEmpty())
        {
            return true;
        }

        var dependencies = FabricLoader.getInstance().getModContainer(Colorwheel.MOD_ID).get().getMetadata().getDependencies();
        var flwVersion = flw.get().getMetadata().getVersion();

        for (var dep : dependencies)
        {
            if (dep.getModId().equals("flywheel"))
            {
                return dep.matches(flwVersion);
            }
        }

        return true;
    }
}
