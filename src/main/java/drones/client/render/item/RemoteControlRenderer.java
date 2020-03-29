package drones.client.render.item;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.item.ItemStack;

import drones.client.texture.DronePovTexture;

public class RemoteControlRenderer {

    public static final RemoteControlRenderer INSTANCE = new RemoteControlRenderer();

    private RemoteControlRenderer() {}

    public void render(ItemStack stack, Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model) {
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(DronePovTexture.ID));
        Entry tos = matrices.peek();
        Matrix3f normal = tos.getNormal();
        Matrix4f tr = tos.getModel();
        buffer.vertex(tr, 4 / 16f, 4 / 16f, 3.75f / 16f).color(1f, 1f, 1f, 1f).texture(0f, 1f).overlay(overlay).light(light).normal(normal, 0f, 0f, 1f).next();
        buffer.vertex(tr, 12 / 16f, 4 / 16f, 3.75f / 16f).color(1f, 1f, 1f, 1f).texture(1f, 1f).overlay(overlay).light(light).normal(normal, 0f, 0f, 1f).next();
        buffer.vertex(tr, 12 / 16f, 10 / 16f, 3.75f / 16f).color(1f, 1f, 1f, 1f).texture(1f, 0f).overlay(overlay).light(light).normal(normal, 0f, 0f, 1f).next();
        buffer.vertex(tr, 4 / 16f, 10 / 16f, 3.75f / 16f).color(1f, 1f, 1f, 1f).texture(0f, 0f).overlay(overlay).light(light).normal(normal, 0f, 0f, 1f).next();
    }

}
