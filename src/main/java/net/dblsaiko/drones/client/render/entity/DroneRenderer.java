package net.dblsaiko.drones.client.render.entity;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import net.dblsaiko.drones.Main;
import net.dblsaiko.drones.client.render.entity.model.DroneEntityModel;
import net.dblsaiko.drones.entity.DroneEntity;

public class DroneRenderer extends EntityRenderer<DroneEntity> {
    public static final Identifier TEXTURE = new Identifier(Main.MODID, "textures/entity/drone.png");

    private final DroneEntityModel model;

    public DroneRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
        model = new DroneEntityModel();
        shadowRadius = 0.375f;
    }

    @Override
    public void render(DroneEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        this.model.setAngles(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.model.getLayer(this.getTexture(entity)));
        matrices.push();
        matrices.multiply(entity.getRotation());
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();

        // VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getLines());
        // MathUtil.rotateTowards(entity.getRotation(), DroneEntity.UP.add(MathUtil.reject(entity.getVelocity().negate(), DroneEntity.UP)).normalize(), 0.1f, matrices.peek().getModel(), buffer);
    }

    @Override
    public Identifier getTexture(DroneEntity entity) {
        return TEXTURE;
    }

}
