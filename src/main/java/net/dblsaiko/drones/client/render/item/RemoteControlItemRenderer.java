package net.dblsaiko.drones.client.render.item;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;

import net.dblsaiko.drones.client.texture.DronePovTexture;

public class RemoteControlItemRenderer {

    public static final RemoteControlItemRenderer INSTANCE = new RemoteControlItemRenderer();

    private RemoteControlItemRenderer() {}

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int overlay) {
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(DronePovTexture.ID));
        Entry tos = matrices.peek();
        Matrix3f normal = tos.getNormal();
        Matrix4f tr = tos.getModel();
        buffer.vertex(tr, 4 / 16f, 4 / 16f, 3.75f / 16f).color(1f, 1f, 1f, 1f).texture(0f, 0f).overlay(overlay).light(0xF000F0).normal(normal, 0f, 0f, 1f).next();
        buffer.vertex(tr, 12 / 16f, 4 / 16f, 3.75f / 16f).color(1f, 1f, 1f, 1f).texture(1f, 0f).overlay(overlay).light(0xF000F0).normal(normal, 0f, 0f, 1f).next();
        buffer.vertex(tr, 12 / 16f, 10 / 16f, 3.75f / 16f).color(1f, 1f, 1f, 1f).texture(1f, 1f).overlay(overlay).light(0xF000F0).normal(normal, 0f, 0f, 1f).next();
        buffer.vertex(tr, 4 / 16f, 10 / 16f, 3.75f / 16f).color(1f, 1f, 1f, 1f).texture(0f, 1f).overlay(overlay).light(0xF000F0).normal(normal, 0f, 0f, 1f).next();
    }

}
