// Derived from Ponder's DefaultSuperRenderTypeBuffer.
//
// Iris 1.10 (26.1) removed its batchedentityrendering package (FullyBufferedMultiBufferSource,
// TransparencyType, BlendingStateHolder), which the previous implementation relied on for
// transparency-sorted buffering. This is a self-contained reimplementation modelled on
// Create-Fly's catnip DefaultSuperRenderTypeBuffer: a buffered MultiBufferSource per phase with a
// simple solid/translucent split (RenderType.hasBlending()). The fine-grained transparency
// categories are intentionally dropped for now and can be refined later.

package dev.djefrey.colorwheel.mod_compat;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zurrtum.create.client.catnip.render.SuperRenderTypeBuffer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.SequencedMap;

public class ClrwlSuperRenderTypeBuffer implements SuperRenderTypeBuffer
{
    private static final ClrwlSuperRenderTypeBuffer INSTANCE = new ClrwlSuperRenderTypeBuffer();

    public static ClrwlSuperRenderTypeBuffer getInstance()
    {
        return INSTANCE;
    }

    protected final Phase earlyBuffer = new Phase();
    protected final Phase defaultBuffer = new Phase();
    protected final Phase lateBuffer = new Phase();

    public ClrwlSuperRenderTypeBuffer()
    {
    }

    @Override
    public VertexConsumer getEarlyBuffer(RenderType type)
    {
        return earlyBuffer.bufferSource.getBuffer(type);
    }

    @Override
    public VertexConsumer getBuffer(RenderType type)
    {
        return defaultBuffer.bufferSource.getBuffer(type);
    }

    @Override
    public VertexConsumer getLateBuffer(RenderType type)
    {
        return lateBuffer.bufferSource.getBuffer(type);
    }

    @Override
    public void draw()
    {
        earlyBuffer.bufferSource.endBatch();
        defaultBuffer.bufferSource.endBatch();
        lateBuffer.bufferSource.endBatch();
    }

    @Override
    public void draw(RenderType type)
    {
        earlyBuffer.bufferSource.endBatch(type);
        defaultBuffer.bufferSource.endBatch(type);
        lateBuffer.bufferSource.endBatch(type);
    }

    public void drawSolid()
    {
        earlyBuffer.drawSolid();
        defaultBuffer.drawSolid();
        lateBuffer.drawSolid();
    }

    public void drawTranslucent()
    {
        earlyBuffer.drawTranslucent();
        defaultBuffer.drawTranslucent();
        lateBuffer.drawTranslucent();
    }

    private static class Phase
    {
        private final ArrayList<RenderType> solidTypes = new ArrayList<>();
        private final ArrayList<RenderType> translucentTypes = new ArrayList<>();
        private final SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers = Util.make(
            new Object2ObjectLinkedOpenHashMap<>(), map ->
            {
                put(map, Sheets.cutoutBlockSheet());
                put(map, Sheets.cutoutBlockItemSheet());
                put(map, Sheets.cutoutItemSheet());
                put(map, Sheets.translucentBlockSheet());
                put(map, Sheets.translucentBlockItemSheet());
                put(map, Sheets.translucentItemSheet());
                put(map, RenderTypes.armorEntityGlint());
                put(map, RenderTypes.glint());
                put(map, RenderTypes.glintTranslucent());
                put(map, RenderTypes.entityGlint());
                put(map, RenderTypes.waterMask());
                ModelBakery.DESTROY_TYPES.forEach(renderType -> put(map, renderType));
            });
        private final BufferSource bufferSource = MultiBufferSource.immediateWithBuffers(
            fixedBuffers,
            new ByteBufferBuilder(256)
        );

        private void put(Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder> map, RenderType type)
        {
            map.put(type, new ByteBufferBuilder(type.bufferSize()));
            if (type.hasBlending())
            {
                translucentTypes.add(type);
            }
            else
            {
                solidTypes.add(type);
            }
        }

        private void drawSolid()
        {
            bufferSource.endLastBatch();
            solidTypes.forEach(bufferSource::endBatch);
        }

        private void drawTranslucent()
        {
            drawSolid();
            translucentTypes.forEach(bufferSource::endBatch);
        }
    }
}
