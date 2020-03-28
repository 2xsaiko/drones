package drones.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

import drones.Main;
import drones.init.EntityTypes;
import drones.init.Items;
import drones.item.RemoteControlItem;
import drones.util.DroneSettings;

public class DroneEntity extends Entity {

    private UUID linkId = null;

    public DroneEntity(EntityType<? extends DroneEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {

    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        if (tag.contains("DroneSettings")) {
            setSettings(DroneSettings.fromTag((CompoundTag) tag.get("DroneSettings")));
        }
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        tag.put("DroneSettings", DroneSettings.of(linkId).toTag());
    }

    private void setSettings(DroneSettings ds) {
        this.linkId = ds.linkId;
    }

    public DroneSettings getSettings() {
        return DroneSettings.of(linkId);
    }

    @Override
    public void tick() {
        super.tick();

    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
    }

    @Override
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        if (world.isClient()) return super.interactAt(player, hitPos, hand);
        if (!player.isSneaking()) return super.interactAt(player, hitPos, hand);

        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() == Items.REMOTE_CONTROL) {
            linkId = RemoteControlItem.getLinkId(stack);
            player.addChatMessage(new TranslatableText(String.format("status.%s.drone_linked", Main.MODID)), true);
            return ActionResult.SUCCESS;
        } else if (stack.isEmpty()) {
            player.setStackInHand(hand, Items.DRONE.fromEntity(this));
            kill();
            return ActionResult.SUCCESS;
        } else {
            return super.interactAt(player, hitPos, hand);
        }
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    public static DroneEntity create(World world, DroneSettings settings) {
        DroneEntity de = new DroneEntity(EntityTypes.DRONE, world);
        de.setSettings(settings);
        return de;
    }

}
