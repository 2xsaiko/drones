package drones.util;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
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

    public static Quaternion getRotationTowards(Vec3d from, Vec3d to) {
        Quaternion q = Quaternion.IDENTITY.copy();
        Vec3d cross = from.crossProduct(to);
        float w = (float) (Math.sqrt(from.lengthSquared() * to.lengthSquared()) + from.dotProduct(to));
        q.set((float) cross.x, (float) cross.y, (float) cross.z, w);
        q.normalize();
        return q;
    }

    //
//    public static float dot(Quaternion v0, Quaternion v1) {
//        return v0.getA() * v1.getA() + v0.getA() * v1.getA() + v0.getB() * v1.getB() + v0.getC() * v1.getC() + v0.getD() * v1.getD();
//    }
//
//    public static void negate(Quaternion self) {
//        self.set(-self.getA(), -self.getB(), -self.getC(), -self.getD());
//    }
//
//    public static void add(Quaternion self, Quaternion other) {
//        self.set(self.getA() + other.getA(), self.getB() + other.getB(), self.getC() + other.getC(), self.getD() + other.getD());
//    }
//
//    public static void sub(Quaternion self, Quaternion other) {
//        self.set(self.getA() - other.getA(), self.getB() - other.getB(), self.getC() - other.getC(), self.getD() - other.getD());
//    }
//
    public static void set(Quaternion self, Quaternion other) {
        self.set(other.getB(), other.getC(), other.getD(), other.getA());
    }
//
//    public static Quaternion slerp(Quaternion v0, Quaternion v1, float t) {
//        // Only unit quaternions are valid rotations.
//        // Normalize to avoid undefined behavior.
//        v0.normalize();
//        v1.normalize();
//
//        // Compute the cosine of the angle between the two vectors.
//        float dot = dot(v0, v1);
//
//        // If the dot product is negative, slerp won't take
//        // the shorter path. Note that v1 and -v1 are equivalent when
//        // the negation is applied to all four components. Fix by
//        // reversing one quaternion.
//        if (dot < 0.0f) {
//            negate(v1);
//            dot = -dot;
//        }
//
//        double DOT_THRESHOLD = 0.9995;
//        if (dot > DOT_THRESHOLD) {
//            // If the inputs are too close for comfort, linearly interpolate
//            // and normalize the result.
//
//            Quaternion result = v0.copy();
//            Quaternion v2 = v1.copy();
//            sub(v2, v0);
//            v2.scale(t);
//            add(result, v2);
//            result.normalize();
//            return result;
//        }
//
//        // Since dot is in range [0, DOT_THRESHOLD], acos is safe
//        double theta_0 = Math.acos(dot);        // theta_0 = angle between input vectors
//        double theta = theta_0 * t;          // theta = angle between v0 and result
//        double sin_theta = Math.sin(theta);     // compute this value only once
//        double sin_theta_0 = Math.sin(theta_0); // compute this value only once
//
//        double s0 = Math.cos(theta) - dot * sin_theta / sin_theta_0;  // == sin(theta_0 - theta) / sin(theta_0)
//        double s1 = sin_theta / sin_theta_0;
//
//        Quaternion v2 = v0.copy();
//        v2.scale((float) s0);
//        Quaternion v3 = v1.copy();
//        v3.scale((float) s1);
//        add(v2, v3);
//        return v2;
//    }

//    public static void rotateTowards(Quaternion self, Vec3d orientation, float speed) {
//        Vec3d vec2 = rotate(orientation, self);
//        Quaternion q1 = getRotationTowards(vec2, orientation);
//        q1 = slerp(self, q1, speed);
//        set(self, q1);
//    }

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

    public static Vec3d interp(Vec3d from, Vec3d to, double delta) {
        return from.multiply(1 - delta).add(to.multiply(delta));
    }

    public static void rotateTowards1(Quaternion self, Vec3d orientation, float speed, Matrix4f mat, VertexConsumer debugConsumer) {
        Vec3d vec2 = rotate(orientation, self).normalize();
        Vec3d cross = orientation.crossProduct(vec2);
        double partial = cross.length() * speed;
        Vec3d target = interp(vec2, orientation, partial).normalize();
        if (debugConsumer != null) {
            debugConsumer.vertex(mat, 0.0f, 0.0f, 0.0f).color(1f, 0f, 0f, 1f).next();
            debugConsumer.vertex(mat, (float) orientation.x, (float) orientation.y, (float) orientation.z).color(1f, 0f, 0f, 1f).next();

            debugConsumer.vertex(mat, 0.0f, 0.0f, 0.0f).color(0f, 0f, 1f, 1f).next();
            debugConsumer.vertex(mat, (float) vec2.x, (float) vec2.y, (float) vec2.z).color(0f, 0f, 1f, 1f).next();

            debugConsumer.vertex(mat, 0.0f, 0.0f, 0.0f).color(0f, 1f, 0f, 1f).next();
            debugConsumer.vertex(mat, (float) cross.x, (float) cross.y, (float) cross.z).color(0f, 1f, 0f, 1f).next();

            debugConsumer.vertex(mat, 0.0f, 0.0f, 0.0f).color(1f, 0f, 1f, 1f).next();
            debugConsumer.vertex(mat, (float) target.x, (float) target.y, (float) target.z).color(1f, 0f, 1f, 1f).next();
        }
        Quaternion other = getRotationTowards(vec2, target);
        other.hamiltonProduct(self);
        set(self, other);
    }

}
