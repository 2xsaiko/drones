package net.dblsaiko.drones.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;

import net.dblsaiko.drones.client.options.KeyBindings;
import net.dblsaiko.drones.client.render.entity.DroneRenderer;
import net.dblsaiko.drones.init.EntityTypes;

public class Main implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        KeyBindings.initialize();
        EntityRendererRegistry.INSTANCE.register(EntityTypes.DRONE, (dispatcher, ctx) -> new DroneRenderer(dispatcher));

        UpdateStateHandler ush = new UpdateStateHandler();
        ClientTickCallback.EVENT.register(client -> {
            ClientPlayerEntity player = client.player;
            if (player != null) {
                ush.update(player);
            }
        });
    }

}
