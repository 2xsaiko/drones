package drones.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class RemoteControlItem extends Item {

    public RemoteControlItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        UUID linkId = RemoteControlItem.getLinkId(stack);
        if (linkId != null && context.isAdvanced()) {
            tooltip.add(new LiteralText(linkId.toString().substring(0, 7)).formatted(Formatting.GRAY));
        }
    }

    public static UUID getLinkId(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return null;
        return !tag.containsUuid("LinkId") ? null : tag.getUuid("LinkId");
    }

    public static UUID getOrCreateLinkId(ItemStack stack) {
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
