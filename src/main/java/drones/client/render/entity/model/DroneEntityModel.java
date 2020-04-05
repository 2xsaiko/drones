package drones.client.render.entity.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;

import java.util.Collections;

import drones.entity.DroneEntity;

public class DroneEntityModel extends CompositeEntityModel<DroneEntity> {
    private final LinkedModelPart body;
    private final LinkedModelPart rotorSocket1;
    private final LinkedModelPart rotorSocket2;
    private final LinkedModelPart rotorSocket3;
    private final LinkedModelPart rotorSocket4;
    private final LinkedModelPart rotor1;
    private final LinkedModelPart rotor2;
    private final LinkedModelPart rotor3;
    private final LinkedModelPart rotor4;

    public DroneEntityModel() {
        body = createPart(this, 0, 0,
            0f, 1.5f, 0f,
            6f, 3f, 10f,
            0f, 1.5f, 0f);
        rotorSocket1 = createPart(this, body, 0, 0,
            -3f, 3.5f, -4f,
            2f, 1f, 2f,
            -3f, 3.5f, -4f);
        rotorSocket2 = createPart(this, body, 0, 0,
            -3f, 3.5f, 4f,
            2f, 1f, 2f,
            -3f, 3.5f, 4f);
        rotorSocket3 = createPart(this, body, 0, 0,
            3f, 3.5f, 4f,
            2f, 1f, 2f,
            3f, 3.5f, 4f);
        rotorSocket4 = createPart(this, body, 0, 0,
            3f, 3.5f, -4f,
            2f, 1f, 2f,
            3f, 3.5f, -4f);
        rotor1 = createPart(this, rotorSocket1, 0, 13,
            -3f, 4.5f, -4f,
            8f, 1f, 2f,
            -3f, 4.5f, -4f);
        rotor2 = createPart(this, rotorSocket2, 0, 16,
            -3f, 4.5f, 4f,
            8f, 1f, 2f,
            -3f, 4.5f, 4f);
        rotor3 = createPart(this, rotorSocket3, 0, 13,
            3f, 4.5f, 4f,
            8f, 1f, 2f,
            3f, 4.5f, 4f);
        rotor4 = createPart(this, rotorSocket4, 0, 16,
            3f, 4.5f, -4f,
            8f, 1f, 2f,
            3f, 4.5f, -4f);
    }

    private static LinkedModelPart createPart(Model model, int u, int v, float x, float y, float z, float sx, float sy, float sz, float px, float py, float pz) {
        LinkedModelPart part = new LinkedModelPart(model, u, v);
        part.addCuboid(x - sx / 2 - px, y - sy / 2 - py, z - sz / 2 - pz, sx, sy, sz);
        part.setPivot(px, py, pz);
        return part;
    }

    private static LinkedModelPart createPart(Model model, LinkedModelPart parent, int u, int v, float x, float y, float z, float sx, float sy, float sz, float px, float py, float pz) {
        LinkedModelPart part = createPart(model, u, v,
            x - parent.getTotalPivotX(), y - parent.getTotalPivotY(), z - parent.getTotalPivotZ(),
            sx, sy, sz,
            px - parent.getTotalPivotX(), py - parent.getTotalPivotY(), pz - parent.getTotalPivotZ());
        parent.addChild(part);
        part.parent = parent;
        return part;
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return Collections.singletonList(body);
    }

    @Override
    public void setAngles(DroneEntity entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {
        // EulerAngle ang = new EulerAngle(0, 0, 0);//MathUtil.toEulerAngles(entity.getRotation());
        // body.yaw = ang.getYaw();
        // body.pitch = ang.getPitch();
        // body.roll = ang.getRoll();
        float rotation = System.nanoTime() % 36000 / 100f;
        setRotorAngles(rotation);
    }

    public void setRotorAngles(float rotation) {
        float toRadians = (float) (Math.PI / 180);
        rotor1.yaw = rotation * toRadians;
        rotor2.yaw = (-rotation + 90) * toRadians;
        rotor3.yaw = (rotation + 180) * toRadians;
        rotor4.yaw = (-rotation + 270) * toRadians;
    }

}
