package drones;

import net.fabricmc.api.ModInitializer;

import drones.entity.data.TrackedDataHandlers;
import drones.init.EntityTypes;
import drones.init.Items;
import drones.network.RcInputPacket;

public class Main implements ModInitializer {

    public static final String MODID = "drones";

    @Override
    public void onInitialize() {
        Items.initialize();
        EntityTypes.initialize();
        TrackedDataHandlers.register();

        RcInputPacket.register();
    }

}
