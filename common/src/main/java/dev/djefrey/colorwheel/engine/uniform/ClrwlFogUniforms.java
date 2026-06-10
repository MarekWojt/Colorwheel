package dev.djefrey.colorwheel.engine.uniform;

import com.zurrtum.create.client.flywheel.backend.engine.uniform.UniformBuffer;

public final class ClrwlFogUniforms extends UniformWriter
{
	// Matches the _ClrwlFogUniforms std140 block: vec4 color + 4 floats = 8 floats.
	private static final int SIZE = 4 * 8;
	static final UniformBuffer BUFFER = new UniformBuffer(ClrwlUniforms.FOG_INDEX, SIZE);

	// Minecraft 26.1 no longer exposes RenderSystem.getShaderFog{Color,Start,End,Shape}() and
	// Flywheel's fog interface changed to environmental + render-distance ranges. Values are
	// captured via setFog(...) (see Create-Fly's FogRendererMixin); defaults to "no fog"
	// (Float.MAX_VALUE ranges -> linearFog returns 0 -> no fog applied).
	// TODO(26.1): add a FogRenderer mixin that calls setFog(...) so real fog reaches shaders.
	private static final float[] COLOR = {0f, 0f, 0f, 0f};
	private static float environmentalStart = Float.MAX_VALUE;
	private static float environmentalEnd = Float.MAX_VALUE;
	private static float renderDistanceStart = Float.MAX_VALUE;
	private static float renderDistanceEnd = Float.MAX_VALUE;

	private ClrwlFogUniforms()
	{
	}

	public static void setFog(float r, float g, float b, float a, float environmentalStart, float environmentalEnd, float renderDistanceStart, float renderDistanceEnd)
	{
		COLOR[0] = r;
		COLOR[1] = g;
		COLOR[2] = b;
		COLOR[3] = a;
		ClrwlFogUniforms.environmentalStart = environmentalStart;
		ClrwlFogUniforms.environmentalEnd = environmentalEnd;
		ClrwlFogUniforms.renderDistanceStart = renderDistanceStart;
		ClrwlFogUniforms.renderDistanceEnd = renderDistanceEnd;
	}

	public static void update()
	{
		long ptr = BUFFER.ptr();

		ptr = writeFloat(ptr, COLOR[0]);
		ptr = writeFloat(ptr, COLOR[1]);
		ptr = writeFloat(ptr, COLOR[2]);
		ptr = writeFloat(ptr, COLOR[3]);
		ptr = writeFloat(ptr, environmentalStart);
		ptr = writeFloat(ptr, environmentalEnd);
		ptr = writeFloat(ptr, renderDistanceStart);
		ptr = writeFloat(ptr, renderDistanceEnd);

		BUFFER.markDirty();
	}
}
