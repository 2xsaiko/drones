package drones;

import net.fabricmc.api.ModInitializer;

import drones.init.EntityTypes;
import drones.init.Items;

public class Main implements ModInitializer {

	public static final String MODID = "drones";

	@Override
	public void onInitialize() {
		Items.initialize();
		EntityTypes.initialize();
	}

}
