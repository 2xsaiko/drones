package drones.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

import drones.client.options.KeyBindings;
import drones.client.render.entity.DroneRenderer;
import drones.init.EntityTypes;

public class Main implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        KeyBindings.initialize();
        EntityRendererRegistry.INSTANCE.register(EntityTypes.DRONE, (dispatcher, ctx) -> new DroneRenderer(dispatcher));
    }

}
