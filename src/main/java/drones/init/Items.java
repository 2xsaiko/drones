package drones.init;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import drones.Main;
import drones.item.DroneItem;
import drones.item.RemoteControlItem;

public class Items {

    public static DroneItem DRONE;
    public static RemoteControlItem REMOTE_CONTROL;

    public static void initialize() {
        DRONE = Registry.register(Registry.ITEM, new Identifier(Main.MODID, "drone"), new DroneItem(new Item.Settings().group(ItemGroups.ALL).maxCount(1)));
        REMOTE_CONTROL = Registry.register(Registry.ITEM, new Identifier(Main.MODID, "remote_control"), new RemoteControlItem(new Item.Settings().group(ItemGroups.ALL)));
    }

}
