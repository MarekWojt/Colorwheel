package dev.djefrey.colorwheel.engine;

import com.zurrtum.create.client.flywheel.api.instance.Instance;
import com.zurrtum.create.client.flywheel.api.instance.InstanceType;
import com.zurrtum.create.client.flywheel.api.instance.Instancer;
import com.zurrtum.create.client.flywheel.api.instance.InstancerProvider;
import com.zurrtum.create.client.flywheel.api.model.Model;
import com.zurrtum.create.client.flywheel.backend.engine.embed.GlobalEnvironment;

public record ClrwlInstancerProvider(ClrwlEngine engine, ClrwlInstanceVisual visual) implements InstancerProvider
{
    @Override
    public <I extends Instance> Instancer<I> instancer(InstanceType<I> type, Model model, int bias)
    {
        return engine.instancer(visual, GlobalEnvironment.INSTANCE, type, model, bias);
    }
}
