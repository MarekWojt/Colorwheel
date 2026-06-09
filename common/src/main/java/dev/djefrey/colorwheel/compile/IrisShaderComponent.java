package dev.djefrey.colorwheel.compile;

import com.zurrtum.create.client.flywheel.backend.glsl.SourceComponent;

import java.util.Collection;
import java.util.List;

public record IrisShaderComponent(String name, String source) implements SourceComponent
{
	@Override
	public Collection<? extends SourceComponent> included() {
		return List.of();
	}
}
