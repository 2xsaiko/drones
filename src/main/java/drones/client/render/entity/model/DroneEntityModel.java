package drones.client.render.entity.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;

import java.util.Arrays;

import drones.entity.DroneEntity;

public class DroneEntityModel extends CompositeEntityModel<DroneEntity> {
    private final ModelPart body;
    private final ModelPart rotorSocket1;
    private final ModelPart rotorSocket2;
    private final ModelPart rotorSocket3;
    private final ModelPart rotorSocket4;
    private final ModelPart rotor1;
    private final ModelPart rotor2;
    private final ModelPart rotor3;
    private final ModelPart rotor4;

    public DroneEntityModel() {
        body = new ModelPart(this, 0, 0);
        body.addCuboid(0f, 0f, 0f, 6f, 3f, 10f);
        body.setPivot(0f, 1.5f, 0f);
        rotorSocket1 = new ModelPart(this, 0, 0);
        rotorSocket1.addCuboid(5f, 3.5f, 4f, 2f, 1f, 2f);
        rotorSocket1.setPivot(5f, 3.5f, 4f);
        rotorSocket2 = new ModelPart(this, 0, 0);
        rotorSocket2.addCuboid(5f, 3.5f, 12f, 2f, 1f, 2f);
        rotorSocket2.setPivot(5f, 3.5f, 12f);
        rotorSocket3 = new ModelPart(this, 0, 0);
        rotorSocket3.addCuboid(11f, 3.5f, 12f, 2f, 1f, 2f);
        rotorSocket3.setPivot(11f, 3.5f, 12f);
        rotorSocket4 = new ModelPart(this, 0, 0);
        rotorSocket4.addCuboid(11f, 3.5f, 4f, 2f, 1f, 2f);
        rotorSocket4.setPivot(11f, 3.5f, 4f);
        rotor1 = new ModelPart(this, 0, 0);
        rotor1.addCuboid(5f, 4.5f, 4f, 8f, 1f, 2f);
        rotor1.setPivot(5f, 4.5f, 4f);
        rotor2 = new ModelPart(this, 0, 0);
        rotor2.addCuboid(5f, 4.5f, 12f, 2f, 1f, 8f);
        rotor2.setPivot(5f, 4.5f, 12f);
        rotor3 = new ModelPart(this, 0, 0);
        rotor3.addCuboid(11f, 4.5f, 12f, 8f, 1f, 2f);
        rotor3.setPivot(11f, 4.5f, 12f);
        rotor4 = new ModelPart(this, 0, 0);
        rotor4.addCuboid(11f, 4.5f, 4f, 2f, 1f, 8f);
        rotor4.setPivot(11f, 4.5f, 4f);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return Arrays.asList(body, rotorSocket1, rotorSocket2, rotorSocket3, rotorSocket4, rotor1, rotor2, rotor3, rotor4);
    }

    @Override
    public void setAngles(DroneEntity entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {

    }

}
