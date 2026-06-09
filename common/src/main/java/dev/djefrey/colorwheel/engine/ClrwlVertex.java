package dev.djefrey.colorwheel.engine;

import dev.djefrey.colorwheel.Colorwheel;
import com.zurrtum.create.client.flywheel.api.layout.FloatRepr;
import com.zurrtum.create.client.flywheel.api.layout.Layout;
import com.zurrtum.create.client.flywheel.api.layout.LayoutBuilder;
import com.zurrtum.create.client.flywheel.backend.LayoutAttributes;
import com.zurrtum.create.client.flywheel.backend.gl.array.VertexAttribute;
import net.minecraft.resources.Identifier;

import java.util.List;

public final class ClrwlVertex {
	public static final Layout LAYOUT = LayoutBuilder.create()
			.vector("position", FloatRepr.FLOAT, 3)
			.vector("color", FloatRepr.NORMALIZED_UNSIGNED_BYTE, 4)
			.vector("tex", FloatRepr.FLOAT, 2)
			.vector("light", FloatRepr.UNSIGNED_SHORT, 2)
			.vector("normal", FloatRepr.NORMALIZED_BYTE, 4)
			.vector("entity", FloatRepr.SHORT, 2)
			.vector("midTexCoord", FloatRepr.FLOAT, 2)
			.vector("tangent", FloatRepr.NORMALIZED_BYTE, 4)
			.vector("midBlock", FloatRepr.BYTE, 4)
			.vector("overlay", FloatRepr.SHORT, 2)
			.build();

	public static final List<VertexAttribute> ATTRIBUTES = LayoutAttributes.attributes(LAYOUT);
	public static final int STRIDE = LAYOUT.byteSize();

	public static final Identifier LAYOUT_SHADER = Colorwheel.rl("internal/vertex_input.vert");

	private ClrwlVertex() {
	}

	public static ClrwlVertexView createVertexView() {
		return new ClrwlVertexView();
	}
}
