package drones.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.TextureManager;

import drones.client.texture.DronePovTexture;
import drones.ext.MinecraftClientExt;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements MinecraftClientExt {

    @Shadow
    public abstract TextureManager getTextureManager();

    @Mutable @Shadow @Final private Framebuffer framebuffer;

    @Inject(method = "<init>(Lnet/minecraft/client/RunArgs;)V", at = @At("RETURN"))
    private void onInit(RunArgs args, CallbackInfo ci) {
        getTextureManager().registerTexture(DronePovTexture.ID, DronePovTexture.getInstance());
    }

    @Override
    public void setFramebuffer(Framebuffer fb) {
        framebuffer = fb;
    }

}
