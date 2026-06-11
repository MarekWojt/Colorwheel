package dev.djefrey.colorwheel.engine.uniform;

import com.mojang.blaze3d.platform.Lighting;
import com.zurrtum.create.client.flywheel.api.backend.RenderContext;
import com.zurrtum.create.client.flywheel.backend.engine.uniform.LevelUniforms;
import com.zurrtum.create.client.flywheel.backend.engine.uniform.UniformBuffer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.world.attribute.EnvironmentAttributeProbe;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.timeline.Timelines;

public final class ClrwlLevelUniforms extends UniformWriter
{
	private static final int SIZE = 16 * 4 + 4 * 12;
	static final UniformBuffer BUFFER = new UniformBuffer(ClrwlUniforms.LEVEL_INDEX, SIZE);

	private ClrwlLevelUniforms()
	{
	}

	public static void update(RenderContext context)
	{
		long ptr = BUFFER.ptr();

		ClientLevel level = context.level();
		float partialTick = context.partialTick();

		Camera camera = context.camera();
		EnvironmentAttributeProbe attributeProbe = camera.attributeProbe();
		int skyColor = attributeProbe.getValue(EnvironmentAttributes.SKY_COLOR, partialTick);
		int cloudColor = attributeProbe.getValue(EnvironmentAttributes.CLOUD_COLOR, partialTick);
		ptr = writeVec4(ptr, ARGB.redFloat(skyColor), ARGB.greenFloat(skyColor), ARGB.blueFloat(skyColor), 1f);
		ptr = writeVec4(ptr, ARGB.redFloat(cloudColor), ARGB.greenFloat(cloudColor), ARGB.blueFloat(cloudColor), 1f);

		// Flywheel's LIGHT_DIRECTION is a shared global pointing at whichever Lighting.setupFor() ran
		// last. By the time Colorwheel runs (deep inside Iris's level pass) an items/UI setupFor may
		// have repointed it, which zeroes the diffuse on every default (ENTITY-cardinal-lit) material —
		// i.e. most kinetic/instanced geometry. Re-point it at the LEVEL lighting before reading.
		LevelUniforms.set(Lighting.Entry.LEVEL);
		float[] lightDir = LevelUniforms.LIGHT_DIRECTION != null ? LevelUniforms.LIGHT_DIRECTION : new float[6];
		ptr = writeVec3(ptr, lightDir[0], lightDir[1], lightDir[2]);
		ptr = writeVec3(ptr, lightDir[3], lightDir[4], lightDir[5]);

		int periodTicks = level.registryAccess().get(Timelines.OVERWORLD_DAY)
			.flatMap(timeline -> timeline.value().periodTicks()).orElse(24000);
		long dayTime = level.dimensionType().defaultClock().or(() -> level.registryAccess().get(WorldClocks.OVERWORLD))
			.map(clock -> level.clockManager().getTotalTicks(clock)).orElse(0L);
		long levelDay = dayTime / periodTicks;
		float timeOfDay = (float) (dayTime % periodTicks) / periodTicks;
		ptr = writeInt(ptr, (int) (levelDay % 0x7FFFFFFFL));
		ptr = writeFloat(ptr, timeOfDay);

		ptr = writeInt(ptr, level.dimensionType().hasSkyLight() ? 1 : 0);

		ptr = writeFloat(ptr, attributeProbe.getValue(EnvironmentAttributes.SUN_ANGLE, partialTick) * (float) (Math.PI / 180.0));

		MoonPhase moonPhase = attributeProbe.getValue(EnvironmentAttributes.MOON_PHASE, partialTick);
		ptr = writeFloat(ptr, DimensionType.MOON_BRIGHTNESS_PER_PHASE[moonPhase.index()]);
		ptr = writeInt(ptr, moonPhase.index());

		ptr = writeInt(ptr, level.isRaining() ? 1 : 0);
		ptr = writeFloat(ptr, level.getRainLevel(partialTick));
		ptr = writeInt(ptr, level.isThundering() ? 1 : 0);
		ptr = writeFloat(ptr, level.getThunderLevel(partialTick));

		ptr = writeFloat(ptr, level.getSkyDarken());

		ptr = writeInt(ptr, level.dimensionType().cardinalLightType() == CardinalLighting.Type.NETHER ? 1 : 0);

		// TODO: use defines for custom dimension ids
        int dimensionId;
        ResourceKey<Level> dimension = level.dimension();
        if (Level.OVERWORLD.equals(dimension))
		{
            dimensionId = 0;
        }
		else if (Level.NETHER.equals(dimension))
		{
            dimensionId = 1;
        }
		else if (Level.END.equals(dimension))
		{
            dimensionId = 2;
        }
		else
		{
            dimensionId = -1;
        }
        ptr = writeInt(ptr, dimensionId);

		BUFFER.markDirty();
    }
}
