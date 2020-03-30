package drones.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import java.util.stream.StreamSupport;

import drones.client.ext.GameRendererExt;
import drones.client.ext.MinecraftClientExt;
import drones.client.texture.DronePovTexture;
import drones.entity.DroneEntity;

import static com.mojang.blaze3d.systems.RenderSystem.disableCull;
import static com.mojang.blaze3d.systems.RenderSystem.loadIdentity;
import static com.mojang.blaze3d.systems.RenderSystem.popMatrix;
import static com.mojang.blaze3d.systems.RenderSystem.pushMatrix;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glMatrixMode;

public class DronePovRenderer {

    private static final DronePovRenderer INSTANCE = new DronePovRenderer();

    private DronePovRenderer() {}

    public static DronePovRenderer getInstance() {
        return INSTANCE;
    }

    public DroneEntity getPovEntity() {
        return (DroneEntity) StreamSupport.stream(MinecraftClient.getInstance().world.getEntities().spliterator(), true).filter($ -> $ instanceof DroneEntity).findAny().orElse(null);
    }

    public void render(float tickDelta, long limitTime) {
        MinecraftClient mc = MinecraftClient.getInstance();
        DroneEntity povEntity = getPovEntity();
        if (povEntity == null) return;

        boolean oldHudHidden = mc.options.hudHidden;
        Entity oldCam = mc.cameraEntity;
        int oldPerspective = mc.options.perspective;
        mc.options.hudHidden = true;
        mc.options.perspective = 0;
        Framebuffer window = mc.getFramebuffer();
        Framebuffer dronePovTexture = DronePovTexture.getInstance().getFramebuffer();
        dronePovTexture.beginWrite(true);
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        MinecraftClientExt.from(mc).setFramebuffer(dronePovTexture);

        glMatrixMode(GL_PROJECTION);
        pushMatrix();
        loadIdentity();
        glMatrixMode(GL_MODELVIEW);
        pushMatrix();
        loadIdentity();

        // setupCamera(lc)

        mc.cameraEntity = povEntity;
        disableCull();

        try {
            GameRendererExt.from(mc.gameRenderer).setDronePov(povEntity);
            mc.gameRenderer.renderWorld(tickDelta, limitTime, new MatrixStack());
        } finally {
            GameRendererExt.from(mc.gameRenderer).setDronePov(null);
        }

        glMatrixMode(GL_PROJECTION);
        popMatrix();
        glMatrixMode(GL_MODELVIEW);
        popMatrix();

        MinecraftClientExt.from(mc).setFramebuffer(window);
        window.beginWrite(true);

        mc.cameraEntity = oldCam;
        mc.options.hudHidden = oldHudHidden;
        mc.options.perspective = oldPerspective;
    }
}
