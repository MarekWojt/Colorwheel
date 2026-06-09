package dev.djefrey.colorwheel.engine.uniform;

import com.zurrtum.create.client.flywheel.backend.engine.uniform.UniformBuffer;

public final class ClrwlFogUniforms extends UniformWriter
{
	private static final int SIZE = 4 * 7;
	static final UniformBuffer BUFFER = new UniformBuffer(ClrwlUniforms.FOG_INDEX, SIZE);

	// Minecraft 26.1 no longer exposes RenderSystem.getShaderFog{Color,Start,End,Shape}().
	// Fog is now built by the FogRenderer into a GPU buffer, so we capture the values via a
	// hook (see Create-Fly's FogRendererMixin) into this cache. Defaults to "no fog".
	// TODO(26.1): add a FogRenderer mixin that calls setFog(...) so fog reaches shaders.
	private static final float[] COLOR = {0f, 0f, 0f, 0f};
	private static float fogStart = Float.MAX_VALUE;
	private static float fogEnd = Float.MAX_VALUE;

	private ClrwlFogUniforms()
	{
	}

	public static void setFog(float r, float g, float b, float a, float start, float end)
	{
		COLOR[0] = r;
		COLOR[1] = g;
		COLOR[2] = b;
		COLOR[3] = a;
		fogStart = start;
		fogEnd = end;
	}

	public static void update()
	{
		long ptr = BUFFER.ptr();

		ptr = writeFloat(ptr, COLOR[0]);
		ptr = writeFloat(ptr, COLOR[1]);
		ptr = writeFloat(ptr, COLOR[2]);
		ptr = writeFloat(ptr, COLOR[3]);
		ptr = writeFloat(ptr, fogStart);
		ptr = writeFloat(ptr, fogEnd);

		// FogShape was removed in 26.1; default to spherical fog (index 0).
		ptr = writeInt(ptr, 0);

		BUFFER.markDirty();
	}
}
