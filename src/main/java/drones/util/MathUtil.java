package drones.util;

import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public final class MathUtil {

    private MathUtil() {}

    public static EulerAngle toEulerAngles(Quaternion q) {
        float w = q.getA();
        float x = q.getB();
        float y = q.getC();
        float z = q.getD();
        float roll, pitch, yaw;

        // roll (x-axis rotation)
        double sinr_cosp = 2 * (w * x + y * z);
        double cosr_cosp = 1 - 2 * (x * x + y * y);
        roll = (float) Math.atan2(sinr_cosp, cosr_cosp);

        // pitch (y-axis rotation)
        double sinp = 2 * (w * y - z * x);
        if (Math.abs(sinp) >= 1)
            pitch = (float) Math.copySign(Math.PI / 2, sinp); // use 90 degrees if out of range
        else
            pitch = (float) Math.asin(sinp);

        // yaw (z-axis rotation)
        double siny_cosp = 2 * (w * z + x * y);
        double cosy_cosp = 1 - 2 * (y * y + z * z);
        yaw = (float) Math.atan2(siny_cosp, cosy_cosp);

        return new EulerAngle(pitch, yaw, roll);
    }

    public static Vec3d rotate(Vec3d self, Quaternion q) {
        // Extract the vector part of the quaternion
        Vec3d u = new Vec3d(q.getB(), q.getC(), q.getD());

        // Extract the scalar part of the quaternion
        float s = q.getA();

        // Do the math
        return u.multiply(2.0f * u.dotProduct(self))
            .add(self.multiply(s * s - u.dotProduct(u)))
            .add(u.crossProduct(self).multiply(2.0f * s));
    }

}
