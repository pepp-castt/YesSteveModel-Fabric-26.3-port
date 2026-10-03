package com.elfmcys.ysm.mixin.client;

import com.elfmcys.ysm.natives.render.ProjectionMatrixHolder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 26.3 still exposes the projection upload through
 * ProjectionMatrixBuffer#getBuffer(Matrix4f), but the returned GPU slice moved
 * to RenderPearl's public buffer API.
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererProjectionMixin {
    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ProjectionMatrixBuffer;getBuffer(Lorg/joml/Matrix4f;)Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;"))
    private GpuBufferSlice ysm$captureProjection(ProjectionMatrixBuffer buffer, Matrix4f matrix,
                                                 Operation<GpuBufferSlice> original) {
        ProjectionMatrixHolder.set(matrix);
        return original.call(buffer, matrix);
    }
}
