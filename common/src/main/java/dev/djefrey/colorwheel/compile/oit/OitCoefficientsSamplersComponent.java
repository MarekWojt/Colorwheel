package dev.djefrey.colorwheel.compile.oit;

import dev.djefrey.colorwheel.Colorwheel;
import com.zurrtum.create.client.flywheel.backend.glsl.SourceComponent;
import com.zurrtum.create.client.flywheel.backend.glsl.generate.GlslBuilder;
import com.zurrtum.create.client.flywheel.backend.glsl.generate.GlslUniform;

import java.util.Collection;
import java.util.List;

public class OitCoefficientsSamplersComponent implements SourceComponent
{
    private final int coeffCount;

    public OitCoefficientsSamplersComponent(int coeffCount)
    {
        this.coeffCount = coeffCount;
    }

    @Override
    public Collection<? extends SourceComponent> included()
    {
        return List.of();
    }

    @Override
    public String source()
    {
        var builder = new GlslBuilder();

        addSamplers(builder, coeffCount);

        return builder.build();
    }

    @Override
    public String name()
    {
        return Colorwheel.rl("oit_coefficients_samplers").toString();
    }

    public static void addSamplers(GlslBuilder builder, int coeffCount)
    {
        for (int i = 0; i < coeffCount; i++)
        {
            var uniform = new GlslUniform()
                    .type("sampler2DArray")
                    .name("clrwl_coefficients" + i);

            builder.add(uniform);
        }
    }
}
