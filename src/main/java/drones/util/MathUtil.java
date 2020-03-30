package drones.util;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public final class MathUtil {

    private MathUtil() {}

    public static final double toDegrees = 180.0 / Math.PI;
    public static final float toDegreesf = (float) toDegrees;

    public static EulerAngle toEulerAngles(Quaternion q) {
        float w = q.getA();
        float x = q.getB();
        float y = q.getC();
        float z = q.getD();
        float roll, yaw, pitch;

        // roll (x-axis rotation)
        double sinr_cosp = 2 * (w * x + y * z);
        double cosr_cosp = 1 - 2 * (x * x + y * y);
        roll = (float) Math.atan2(sinr_cosp, cosr_cosp);

        // yaw (y-axis rotation)
        double sinp = 2 * (w * y - z * x);
        if (Math.abs(sinp) >= 1)
            yaw = (float) Math.copySign(Math.PI / 2, sinp); // use 90 degrees if out of range
        else
            yaw = (float) Math.asin(sinp);

        // pitch (z-axis rotation)
        double siny_cosp = 2 * (w * z + x * y);
        double cosy_cosp = 1 - 2 * (y * y + z * z);
        pitch = (float) Math.atan2(siny_cosp, cosy_cosp);

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

    public static Quaternion getRotationTowards(Vec3d from, Vec3d to) {
        Quaternion q = Quaternion.IDENTITY.copy();
        Vec3d cross = from.crossProduct(to);
        float w = (float) (Math.sqrt(from.lengthSquared() * to.lengthSquared()) + from.dotProduct(to));
        q.set((float) cross.x, (float) cross.y, (float) cross.z, w);
        q.normalize();
        return q;
    }

    public static void set(Quaternion self, Quaternion other) {
        self.set(other.getB(), other.getC(), other.getD(), other.getA());
    }

    public static void rotateTowards(Quaternion self, Vec3d orientation, float speed, Matrix4f mat, VertexConsumer debugConsumer) {
        self.normalize();
        Vec3d vec2 = rotate(orientation, self);
        Vec3d cross = orientation.crossProduct(vec2).negate();
        Vec3d axis = cross.normalize();
        float f1 = (float) cross.length();
        if (debugConsumer != null) {
            debugConsumer.vertex(mat, 0.0f, 0.0f, 0.0f).color(1f, 0f, 0f, 1f).next();
            debugConsumer.vertex(mat, (float) orientation.x, (float) orientation.y, (float) orientation.z).color(1f, 0f, 0f, 1f).next();

            debugConsumer.vertex(mat, 0.0f, 0.0f, 0.0f).color(0f, 0f, 1f, 1f).next();
            debugConsumer.vertex(mat, (float) vec2.x, (float) vec2.y, (float) vec2.z).color(0f, 0f, 1f, 1f).next();

            debugConsumer.vertex(mat, 0.0f, 0.0f, 0.0f).color(0f, 1f, 0f, 1f).next();
            debugConsumer.vertex(mat, (float) cross.x, (float) cross.y, (float) cross.z).color(0f, 1f, 0f, 1f).next();
        }
        Quaternion other = new Quaternion(new Vector3f(axis), speed * f1, false);
        other.hamiltonProduct(self);
        set(self, other);
    }

    public static void invert(Quaternion self) {
        float l = self.getA() * self.getA() + self.getB() * self.getB() + self.getC() * self.getC() + self.getD() * self.getD();
        self.set(-self.getB() / l, -self.getC() / l, -self.getD() / l, self.getA() / l);
    }

}
