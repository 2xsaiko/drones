package net.dblsaiko.drones.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dblsaiko.drones.client.render.DronePovRenderer;
import net.dblsaiko.drones.entity.DroneEntity;
import net.dblsaiko.drones.ext.GameRendererExt;
import net.dblsaiko.drones.util.MathUtil;

@Mixin(GameRenderer.class)
public class GameRendererMixin implements GameRendererExt {

    @Shadow @Final private MinecraftClient client;

    @Shadow private float viewDistance;
    private DroneEntity dronePov;

    private float lastTickDelta;

    @Inject(method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "HEAD"))
    private void renderLights(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        if (getDronePov() != null) return;
        DronePovRenderer.getInstance().render(tickDelta, limitTime);
    }

    @Inject(method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V", at = @At("HEAD"))
    private void saveTickDelta(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        lastTickDelta = tickDelta;
    }

    @Inject(method = "updateTargetedEntity(F)V", at = @At("HEAD"), cancellable = true)
    private void updateTargetedEntity(float tickDelta, CallbackInfo ci) {
        if (getDronePov() != null) ci.cancel();
    }

    @ModifyVariable(method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "STORE"), ordinal = 0, name = "camera")
    private Camera getCamera(Camera cam) {
        if (getDronePov() == null) return cam;

        Camera camera = new Camera();
        camera.update(client.world, client.getCameraEntity() == null ? client.player : client.getCameraEntity(), false, false, lastTickDelta);
        return camera;
    }

    @Inject(method = "method_22973(Lnet/minecraft/client/render/Camera;FZ)Lnet/minecraft/client/util/math/Matrix4f;", at = @At("HEAD"), cancellable = true)
    private void getProjectionMatrix(Camera camera, float f, boolean bl, CallbackInfoReturnable<Matrix4f> cir) {
        if (getDronePov() == null) return;

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.peek().getModel().loadIdentity();

        matrixStack.peek().getModel().multiply(Matrix4f.viewboxMatrix(90.0f, 4 / 3f, 0.05f, viewDistance * 4.0f));
        cir.setReturnValue(matrixStack.peek().getModel());
    }

    @Inject(method = "bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V", at = @At("HEAD"), cancellable = true)
    private void bobView(MatrixStack matrixStack, float f, CallbackInfo ci) {
        if (getDronePov() != null) ci.cancel();
    }

    @Inject(method = "bobViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V", at = @At("HEAD"), cancellable = true)
    private void bobViewWhenHurt(MatrixStack matrixStack, float f, CallbackInfo ci) {
        if (getDronePov() != null) ci.cancel();
    }

    @Inject(method = "renderHand(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/Camera;F)V", at = @At("HEAD"), cancellable = true)
    private void renderHand(MatrixStack matrixStack, Camera camera, float f, CallbackInfo ci) {
        if (getDronePov() != null) ci.cancel();
    }

    @Redirect(method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F", ordinal = 0))
    private float getNauseaStrength(float delta, float first, float second) {
        return getDronePov() == null ? MathHelper.lerp(delta, first, second) : 0f;
    }

    @Redirect(method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lnet/minecraft/util/math/Quaternion;)V"))
    private void multiply(MatrixStack matrixStack, Quaternion quaternion) {
        if (getDronePov() != null) return;

        matrixStack.multiply(quaternion);
    }

    @Inject(
        method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/client/util/math/Matrix4f;)V",
            shift = Shift.BEFORE
        )
    )
    private void applyRotation(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        if (getDronePov() == null) return;

        matrix.translate(0.0, -0.2, 0.0);
        Quaternion rotation = MathUtil.copy(getDronePov().getRotation());
        MathUtil.invert(rotation);
        matrix.multiply(rotation);
    }

    @Redirect(method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clear(IZ)V"))
    private void clear(int i, boolean bl) {
        if (getDronePov() != null) return;

        RenderSystem.clear(i, bl);
    }

    @Override
    public void setDronePov(DroneEntity entity) {
        this.dronePov = entity;
    }

    @Override
    public DroneEntity getDronePov() {
        return dronePov;
    }

}
