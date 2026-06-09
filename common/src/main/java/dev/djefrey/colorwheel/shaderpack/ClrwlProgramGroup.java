package dev.djefrey.colorwheel.shaderpack;

import dev.djefrey.colorwheel.Colorwheel;
import com.zurrtum.create.client.flywheel.api.material.Transparency;

import java.util.Optional;

public enum ClrwlProgramGroup
{
    GBUFFERS("gbuffers"),
    SHADOW("shadow");

    private final String name;

    ClrwlProgramGroup(String name)
    {
        this.name = name;
    }

    public String groupName()
    {
        return name;
    }
}
