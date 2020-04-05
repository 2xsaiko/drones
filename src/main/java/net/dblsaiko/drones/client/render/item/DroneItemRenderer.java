package net.dblsaiko.drones.client.render.item;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;

import net.dblsaiko.drones.client.render.entity.DroneRenderer;
import net.dblsaiko.drones.client.render.entity.model.DroneEntityModel;

public class DroneItemRenderer {

    public static final DroneItemRenderer INSTANCE = new DroneItemRenderer();

    private final DroneEntityModel model = Util.make(new DroneEntityModel(), it -> {
        it.setRotorAngles(0.0f);
    });

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5, 0.25, 0.5);
        model.setRotorAngles(0f);
        model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(DroneRenderer.TEXTURE)), light, overlay, 1f, 1f, 1f, 1f);
        matrices.pop();
    }

}
