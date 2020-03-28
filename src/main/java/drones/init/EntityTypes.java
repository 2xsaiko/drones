package drones.init;

import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;

import drones.Main;
import drones.entity.DroneEntity;

public class EntityTypes {

    public static EntityType<DroneEntity> DRONE;

    public static void initialize() {
        DRONE = Registry.register(Registry.ENTITY_TYPE, new Identifier(Main.MODID, "drone"),
            FabricEntityTypeBuilder.create(EntityCategory.MISC, DroneEntity::new)
                .size(EntityDimensions.fixed(1f, 1f))
                .trackable(64, 4)
                .build());
    }

}