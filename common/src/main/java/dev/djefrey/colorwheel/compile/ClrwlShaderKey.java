package dev.djefrey.colorwheel.compile;

import dev.djefrey.colorwheel.engine.uniform.ClrwlFrameUniforms;
import com.zurrtum.create.client.flywheel.api.instance.InstanceType;
import com.zurrtum.create.client.flywheel.api.material.*;
import com.zurrtum.create.client.flywheel.backend.compile.ContextShader;
import com.zurrtum.create.client.flywheel.lib.util.ResourceUtil;

public record ClrwlShaderKey(InstanceType<?> instanceType,
                             MaterialShaders material,
                             FogShader fog,
                             CutoutShader cutout,
                             LightShader light,
                             Transparency transparency,
                             ContextShader context,
                             boolean isShadow,
                             boolean isDebugEnabled,
                             ClrwlPipelineCompiler.OitMode oit)
{
    public static ClrwlShaderKey fromMaterial(InstanceType<?> instanceType, Material material, ContextShader context, boolean isShadow, ClrwlPipelineCompiler.OitMode oit)
    {
        return new ClrwlShaderKey(instanceType, material.shaders(), material.fog(), material.cutout(), material.light(), material.transparency(), context, isShadow, ClrwlFrameUniforms.debugOn(), oit);
    }

    public String getPath()
    {
        var instanceName = ResourceUtil.toDebugFileNameNoExtension(instanceType.vertexShader());
        var materialName = ResourceUtil.toDebugFileNameNoExtension(material.vertexSource());
        var contextName = context.nameLowerCase();
        var debug = isDebugEnabled ? "_debug" : "";

        return instanceName + '/' + materialName + '_' + contextName + oit.name + debug;
    }
}
