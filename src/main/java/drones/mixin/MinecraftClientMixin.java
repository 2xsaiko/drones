package drones.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.texture.TextureManager;

import drones.client.texture.DronePovTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow
    public abstract TextureManager getTextureManager();

    @Inject(method = "<init>(Lnet/minecraft/client/RunArgs;)V", at = @At("RETURN"))
    private void onInit(RunArgs args, CallbackInfo ci) {
        getTextureManager().registerTexture(DronePovTexture.ID, DronePovTexture.getInstance());
    }

}
