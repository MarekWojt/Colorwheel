package dev.djefrey.colorwheel.engine;

import com.mojang.blaze3d.opengl.GlSampler;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.zurrtum.create.client.flywheel.api.material.DepthTest;
import com.zurrtum.create.client.flywheel.api.material.Material;
import com.zurrtum.create.client.flywheel.api.material.Transparency;
import com.zurrtum.create.client.flywheel.api.material.WriteMask;
import com.zurrtum.create.client.flywheel.backend.Samplers;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.gl.blending.BufferBlendInformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL33C;

import java.util.List;

import static com.mojang.blaze3d.opengl.GlConst.*;

public final class ClrwlMaterialRenderState
{
    public static void setup(Material material, @Nullable ClrwlBlendModeOverride blendOverride, List<BufferBlendInformation> bufferBlendOverrides)
    {
        setupTexture(material);
        setupBackfaceCulling(material.backfaceCulling());
        setupPolygonOffset(material.polygonOffset());
        setupDepthTest(material.depthTest());
        setupTransparency(material.transparency(), blendOverride, bufferBlendOverrides);
        setupWriteMask(material.writeMask());
    }

    public static void setupOit(Material material)
    {
        setupTexture(material);
        setupBackfaceCulling(material.backfaceCulling());
        setupPolygonOffset(material.polygonOffset());
        setupDepthTest(material.depthTest());
        GlStateManager._colorMask(material.writeMask().color());
    }

    private static void setupTexture(Material material)
    {
        Samplers.DIFFUSE.makeActive();
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(material.texture());
        GlTexture glTexture = (GlTexture) texture.getTexture();
        int target;
        if ((glTexture.usage() & 16) != 0) {
            target = GL13.GL_TEXTURE_CUBE_MAP;
            GL11.glBindTexture(target, glTexture.glId());
        } else {
            target = GL_TEXTURE_2D;
            GlStateManager._bindTexture(glTexture.glId());
        }
        GpuSampler textureSampler = texture.getSampler();
        FilterMode filterMode = material.blur() ? FilterMode.LINEAR : FilterMode.NEAREST;
        GlSampler sampler = (GlSampler) RenderSystem.getSamplerCache().getSampler(
            textureSampler.getAddressModeU(),
            textureSampler.getAddressModeV(),
            filterMode,
            filterMode,
            material.mipmap()
        );
        GL33C.glBindSampler(Samplers.DIFFUSE.number, sampler.getId());
        GpuTextureView textureView = texture.getTextureView();
        int mipLevel = textureView.baseMipLevel();
        GlStateManager._texParameter(target, GL12.GL_TEXTURE_BASE_LEVEL, mipLevel);
        GlStateManager._texParameter(target, GL12.GL_TEXTURE_MAX_LEVEL, mipLevel + textureView.mipLevels() - 1);
    }

    private static void setupBackfaceCulling(boolean backfaceCulling)
    {
        if (backfaceCulling) {
            GlStateManager._enableCull();
        } else {
            GlStateManager._disableCull();
        }
    }

    private static void setupPolygonOffset(boolean polygonOffset)
    {
        if (polygonOffset) {
            GlStateManager._polygonOffset(-1.0F, -10.0F);
            GlStateManager._enablePolygonOffset();
        } else {
            GlStateManager._polygonOffset(0.0F, 0.0F);
            GlStateManager._disablePolygonOffset();
        }
    }

    private static void setupDepthTest(DepthTest depthTest)
    {
        switch (depthTest) {
            case OFF:
                GlStateManager._disableDepthTest();
                break;
            case NEVER:
                GlStateManager._enableDepthTest();
                GlStateManager._depthFunc(512);
                break;
            case LESS:
                GlStateManager._enableDepthTest();
                GlStateManager._depthFunc(513);
                break;
            case EQUAL:
                GlStateManager._enableDepthTest();
                GlStateManager._depthFunc(514);
                break;
            case LEQUAL:
                GlStateManager._enableDepthTest();
                GlStateManager._depthFunc(515);
                break;
            case GREATER:
                GlStateManager._enableDepthTest();
                GlStateManager._depthFunc(516);
                break;
            case NOTEQUAL:
                GlStateManager._enableDepthTest();
                GlStateManager._depthFunc(517);
                break;
            case GEQUAL:
                GlStateManager._enableDepthTest();
                GlStateManager._depthFunc(518);
                break;
            case ALWAYS:
                GlStateManager._enableDepthTest();
                GlStateManager._depthFunc(519);
        }
    }

    private static void setupTransparency(Transparency transparency, @Nullable ClrwlBlendModeOverride blendOverride, List<BufferBlendInformation> bufferBlendOverrides)
    {
        if (blendOverride == null)
        {
            switch (transparency) {
                case OPAQUE:
                    GlStateManager._disableBlend();
                    break;
                case ADDITIVE:
                    GlStateManager._enableBlend();
                    GlStateManager._blendFuncSeparate(GL_ONE, GL_ONE, GL_ONE, GL_ONE);
                    break;
                case LIGHTNING:
                    GlStateManager._enableBlend();
                    GlStateManager._blendFuncSeparate(GL_SRC_ALPHA, GL_ONE, GL_SRC_ALPHA, GL_ONE);
                    break;
                case GLINT:
                    GlStateManager._enableBlend();
                    GlStateManager._blendFuncSeparate(GL_SRC_COLOR, GL_ONE, GL_ZERO, GL_ONE);
                    break;
                case CRUMBLING:
                    GlStateManager._enableBlend();
                    GlStateManager._blendFuncSeparate(GL_DST_COLOR, GL_SRC_COLOR, GL_ONE, GL_ZERO);
                    break;
                case TRANSLUCENT:
                    GlStateManager._enableBlend();
                    GlStateManager._blendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
            }
        }
        else
        {
            if (blendOverride.blendMode() == null)
            {
                GlStateManager._disableBlend();
            }
            else
            {
                GlStateManager._enableBlend();
                GlStateManager._blendFuncSeparate(blendOverride.blendMode().srcRgb(),
                                               blendOverride.blendMode().dstRgb(),
                                               blendOverride.blendMode().srcAlpha(),
                                               blendOverride.blendMode().dstAlpha());
            }
        }

        for (var entry : bufferBlendOverrides)
        {
            if (entry.blendMode() == null)
            {
                IrisRenderSystem.disableBufferBlend(entry.index());
            }
            else
            {
                IrisRenderSystem.enableBufferBlend(entry.index());
                IrisRenderSystem.blendFuncSeparatei(entry.index(),
                        entry.blendMode().srcRgb(),
                        entry.blendMode().dstRgb(),
                        entry.blendMode().srcAlpha(),
                        entry.blendMode().dstAlpha());
            }
        }
    }

    private static void setupWriteMask(WriteMask mask)
    {
        GlStateManager._depthMask(mask.depth());
        GlStateManager._colorMask(mask.color());
    }

    public static void reset()
    {
        resetTexture();
        resetBackfaceCulling();
        resetPolygonOffset();
        resetDepthTest();
        resetTransparency();
        resetWriteMask();
    }

    private static void resetTexture()
    {
        Samplers.DIFFUSE.makeActive();
    }

    private static void resetBackfaceCulling()
    {
        GlStateManager._enableCull();
    }

    private static void resetPolygonOffset()
    {
        GlStateManager._polygonOffset(0.0F, 0.0F);
        GlStateManager._disablePolygonOffset();
    }

    private static void resetDepthTest()
    {
        GlStateManager._disableDepthTest();
        GlStateManager._depthFunc(515);
    }

    private static void resetTransparency()
    {
        GlStateManager._disableBlend();
        GlStateManager._blendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
    }

    private static void resetWriteMask()
    {
        GlStateManager._depthMask(true);
        GlStateManager._colorMask(ColorTargetState.WRITE_ALL);
    }
}
