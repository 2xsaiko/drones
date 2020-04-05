package net.dblsaiko.drones.init;

import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;

import net.dblsaiko.drones.Main;
import net.dblsaiko.drones.entity.DroneEntity;

public class EntityTypes {

    public static EntityType<DroneEntity> DRONE;

    public static void initialize() {
        DRONE = Registry.register(Registry.ENTITY_TYPE, new Identifier(Main.MODID, "drone"),
            FabricEntityTypeBuilder.create(EntityCategory.MISC, DroneEntity::new)
                .size(EntityDimensions.fixed(0.75f, 0.3f))
                .trackable(64, 4).build());
    }

}
