package drones.client.render.entity.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;

public class LinkedModelPart extends ModelPart {

    public LinkedModelPart parent;

    public LinkedModelPart(Model model) {
        super(model);
    }

    public LinkedModelPart(Model model, int textureOffsetU, int textureOffsetV) {
        super(model, textureOffsetU, textureOffsetV);
    }

    public LinkedModelPart(int textureWidth, int textureHeight, int textureOffsetU, int textureOffsetV) {
        super(textureWidth, textureHeight, textureOffsetU, textureOffsetV);
    }

    public float getTotalPivotX() {
        return parent == null ? pivotX : parent.getTotalPivotX() + pivotX;
    }

    public float getTotalPivotY() {
        return parent == null ? pivotY : parent.getTotalPivotY() + pivotY;
    }

    public float getTotalPivotZ() {
        return parent == null ? pivotZ : parent.getTotalPivotZ() + pivotZ;
    }

}
