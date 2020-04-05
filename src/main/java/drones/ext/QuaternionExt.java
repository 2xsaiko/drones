package drones.ext;

import net.minecraft.util.math.Quaternion;

public interface QuaternionExt {

    void set0(float a, float b, float c, float d);

    @SuppressWarnings("ConstantConditions")
    static QuaternionExt from(Quaternion self) {
        return (QuaternionExt) (Object) self;
    }

}
