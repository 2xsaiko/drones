package net.dblsaiko.drones;

import net.fabricmc.api.ModInitializer;

import net.dblsaiko.drones.entity.data.TrackedDataHandlers;
import net.dblsaiko.drones.init.EntityTypes;
import net.dblsaiko.drones.init.Items;
import net.dblsaiko.drones.network.RcActionPacket;
import net.dblsaiko.drones.network.RcInputPacket;

public class Main implements ModInitializer {

    public static final String MODID = "drones";

    @Override
    public void onInitialize() {
        Items.initialize();
        EntityTypes.initialize();
        TrackedDataHandlers.register();

        RcInputPacket.register();
        RcActionPacket.register();
    }

}
