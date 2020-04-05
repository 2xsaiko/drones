package drones.mixin;

import net.minecraft.util.math.Quaternion;

import drones.ext.QuaternionExt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Quaternion.class)
public class QuaternionMixin implements QuaternionExt {

    @Shadow private float a;
    @Shadow private float b;
    @Shadow private float c;
    @Shadow private float d;

    @Override
    public void set0(float a, float b, float c, float d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

}
