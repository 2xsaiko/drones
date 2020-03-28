package drones.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

import drones.entity.DroneEntity;
import drones.util.DroneSettings;

public class DroneItem extends Item {

    public DroneItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getSide() != Direction.UP) return ActionResult.FAIL;
        DroneEntity drone = DroneEntity.create(context.getWorld(), getDroneSettings(context.getStack()).orElse(DroneSettings.ofDefault()));
        Vec3d pos = context.getHitPos();
        drone.refreshPositionAndAngles(pos.x, pos.y, pos.z, context.getPlayerYaw(), 0.0f);
        boolean collides = context.getWorld().getBlockCollisions(drone, drone.getBoundingBox()).findAny().isPresent();
        if (collides) return ActionResult.FAIL;
        if (!context.getWorld().isClient()) {
            context.getWorld().spawnEntity(drone);
        }
        context.getStack().decrement(1);
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        getDroneSettings(stack).ifPresent(ds -> {
            if (context.isAdvanced()) {
                if (ds.linkId != null) tooltip.add(new LiteralText(ds.linkId.toString()).formatted(Formatting.GRAY));
            }
        });
    }

    public ItemStack fromEntity(DroneEntity droneEntity) {
        ItemStack stack = new ItemStack(this);
        setDroneSettings(stack, droneEntity.getSettings());
        return stack;
    }

    @SuppressWarnings("ConstantConditions")
    public static Optional<DroneSettings> getDroneSettings(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return Optional.empty();
        if (!tag.contains("DroneSettings")) return Optional.empty();
        return Optional.of(DroneSettings.fromTag((CompoundTag) tag.get("DroneSettings")));
    }

    public static void setDroneSettings(ItemStack stack, DroneSettings ds) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.put("DroneSettings", ds.toTag());
    }

}
