package drones.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import java.util.Iterator;
import java.util.UUID;

import drones.client.ext.GameRendererExt;
import drones.client.ext.MinecraftClientExt;
import drones.client.texture.DronePovTexture;
import drones.entity.DroneEntity;
import drones.init.Items;
import drones.item.RemoteControlItem;

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
        MinecraftClient mc = MinecraftClient.getInstance();
        ItemStack heldItem = mc.player.getMainHandStack();
        if (heldItem.getItem() != Items.REMOTE_CONTROL) return null;
        UUID linkId = RemoteControlItem.getLinkId(heldItem);
        if (linkId == null) return null;
        for (Iterator<DroneEntity> iter = DroneEntity.getInstances(); iter.hasNext(); ) {
            DroneEntity next = iter.next();
            if (!next.isAlive()) {
                iter.remove();
                continue;
            }
            if (next.world == mc.player.world && linkId.equals(next.getLinkId())) return next;
        }
        return null;
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

        mc.cameraEntity = povEntity;

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
