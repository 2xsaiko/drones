package net.dblsaiko.drones.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;

import net.dblsaiko.drones.Main;

public class ItemGroups {

    public static final ItemGroup ALL = FabricItemGroupBuilder.build(new Identifier(Main.MODID, "all"), () -> new ItemStack(Items.DRONE));

}
