package drones.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class RemoteControlItem extends Item {

    public RemoteControlItem(Settings settings) {
        super(settings);
    }

    public static UUID getLinkId(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.containsUuid("LinkId")) {
            UUID id = UUID.randomUUID();
            tag.putUuid("LinkId", id);
            return id;
        } else {
            return tag.getUuid("LinkId");
        }
    }

}
