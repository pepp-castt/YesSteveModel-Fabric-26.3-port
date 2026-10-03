package com.elfmcys.ysm.mixin.client;

import com.elfmcys.ysm.natives.render.ProjectionMatrixCarrier;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectionMatrixBuffer.class)
public abstract class ProjectionMatrixBufferMixin {
    @Inject(method = "writeBuffer(Lorg/joml/Matrix4f;)Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;",
            at = @At("RETURN"))
    private void ysm$attachMatrix(Matrix4f matrix, CallbackInfoReturnable<GpuBufferSlice> cir) {
        GpuBufferSlice slice = cir.getReturnValue();
        if (((Object) slice) instanceof ProjectionMatrixCarrier carrier) {
            carrier.ysm$setProjectionMatrix(matrix);
        }
    }
}
