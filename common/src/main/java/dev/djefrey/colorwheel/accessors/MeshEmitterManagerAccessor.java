package dev.djefrey.colorwheel.accessors;

// Implemented (via mixin) on Create-Fly's MeshEmitterManager so BakedModelBuffererMixin can hand it
// the current block's Iris block-sensitive metadata before its geometry is buffered.
public interface MeshEmitterManagerAccessor
{
    void colorwheel$prepareBlock(int block, byte renderType, byte emission, boolean isTerrain, int posX, int posY, int posZ);
}
