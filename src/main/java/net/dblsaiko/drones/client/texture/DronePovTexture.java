package net.dblsaiko.drones.client.texture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import com.mojang.blaze3d.systems.RenderSystem;

import net.dblsaiko.drones.Main;

public class DronePovTexture extends AbstractTexture {

    public static final Identifier ID = new Identifier(Main.MODID, "textures/special/drone_cam.png");

    private static final DronePovTexture INSTANCE = new DronePovTexture();

    private Framebuffer fb;

    @Override
    public void load(ResourceManager manager) {

    }

    public Framebuffer getFramebuffer() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (fb == null) {
            fb = new Framebuffer(256, 256, true, MinecraftClient.IS_SYSTEM_MAC);
            this.glId = fb.getColorAttachment(); // just in case
        }

        return fb;
    }

    @Override
    public int getGlId() {
        return getFramebuffer().getColorAttachment();
    }

    @Override
    public void clearGlId() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(this::clearGlId0);
        } else {
            clearGlId0();
        }
    }

    private void clearGlId0() {
        if (fb != null) {
            fb.delete();
            fb = null;
        }
        glId = -1;
    }

    public static DronePovTexture getInstance() {
        return INSTANCE;
    }

}
