package com.elfmcys.ysm.mixin.client;

import com.elfmcys.ysm.natives.render.ProjectionMatrixCarrier;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GpuBufferSlice.class)
public abstract class GpuBufferSliceMixin implements ProjectionMatrixCarrier {
    @Unique
    private Matrix4f ysm$projectionMatrix;

    @Override
    public Matrix4f ysm$getProjectionMatrix() {
        return ysm$projectionMatrix;
    }

    @Override
    public void ysm$setProjectionMatrix(Matrix4f matrix) {
        if (ysm$projectionMatrix == null) {
            ysm$projectionMatrix = new Matrix4f();
        }
        ysm$projectionMatrix.set(matrix);
    }
}
