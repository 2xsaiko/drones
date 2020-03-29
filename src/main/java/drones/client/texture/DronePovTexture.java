package drones.client.texture;

import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import drones.Main;

public class DronePovTexture extends AbstractTexture {

    public static final Identifier ID = new Identifier(Main.MODID, "textures/special/drone_cam.png");

    private static final DronePovTexture INSTANCE = new DronePovTexture();

    @Override
    public void load(ResourceManager manager) {

    }

    public static DronePovTexture getInstance() {
        return INSTANCE;
    }

}
