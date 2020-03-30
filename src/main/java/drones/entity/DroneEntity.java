package drones.entity;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

import drones.Main;
import drones.entity.data.TrackedDataHandlers;
import drones.init.EntityTypes;
import drones.init.Items;
import drones.item.RemoteControlItem;
import drones.util.DroneSettings;
import drones.util.MathUtil;
import drones.util.RcHandler;
import drones.util.RcInputState;
import drones.util.RcReceiver;

public class DroneEntity extends Entity {

    private static final TrackedData<Quaternion> ROTATION = DataTracker.registerData(DroneEntity.class, TrackedDataHandlers.QUATERNION);
    private static final TrackedData<RcInputState> INPUTS = DataTracker.registerData(DroneEntity.class, TrackedDataHandlers.RC_INPUT_STATE);

    private static final Vec3d UP = new Vec3d(0, 1, 0);

    private UUID linkId = null;
    private RcReceiver recv = null;

    private int interactionCooldown; // fuck you

    public DroneEntity(EntityType<? extends DroneEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();

        RcInputState inputs;
        if (!world.isClient()) {
            RcReceiver recv = getReceiver();
            inputs = recv == null ? RcInputState.ofDefault() : recv.inputs();
            setInputs(inputs);
        } else {
            inputs = getInputs();
        }

        Quaternion rotation = getRotation().copy();
        float x = -inputs.getZTilt();
        float y = -inputs.getYTurn();
        float z = -inputs.getXTilt();
        float l = MathHelper.sqrt(x * x + y * y + z * z);
        if (l != 0) {
            x /= l;
            y /= l;
            z /= l;
            rotation.hamiltonProduct(new Quaternion(new Vector3f(x, y, z), 0.2f, false));
            rotation.normalize();
        }

        MathUtil.rotateTowards(rotation, UP, 0.2f, null, null);
        setRotation(rotation);

        EulerAngle eulerAngle = MathUtil.toEulerAngles(rotation);
        yaw = eulerAngle.getYaw() * MathUtil.toDegreesf;
        pitch = eulerAngle.getPitch() * MathUtil.toDegreesf;

        Vec3d up = MathUtil.rotate(UP, rotation);
        Vec3d g = new Vec3d(0, -0.1, 0);
        move(MovementType.SELF, g.add(MathUtil.rotate(g.negate(), rotation)).add(up.multiply(0.2 * inputs.getSpeed())));
    }

    @Override
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        if (interactionCooldown > 0) {
            interactionCooldown -= 1;
            return ActionResult.PASS;
        }

        if (!isAlive()) return ActionResult.FAIL;
        if (world.isClient()) return super.interactAt(player, hitPos, hand);
        if (!player.isSneaking()) return super.interactAt(player, hitPos, hand);

        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() == Items.REMOTE_CONTROL) {
            setLinkId(RemoteControlItem.getOrCreateLinkId(stack));
            player.addChatMessage(new TranslatableText(String.format("status.%s.drone_linked", Main.MODID)), true);
            interactionCooldown = 5;
            return ActionResult.SUCCESS;
        } else if (stack.isEmpty()) {
            player.setStackInHand(hand, Items.DRONE.fromEntity(this));
            kill();
            interactionCooldown = 5;
            return ActionResult.SUCCESS;
        } else {
            return super.interactAt(player, hitPos, hand);
        }
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
        setLinkId(ds.linkId);
    }

    private void setLinkId(UUID linkId) {
        this.linkId = linkId;
        this.recv = null;
    }

    private RcReceiver getReceiver() {
        if (world.isClient()) return null;
        if (linkId == null) return null;
        if (recv == null) {
            recv = RcHandler.getInstance((ServerWorld) world).receiver(linkId, this);
        }
        return recv;
    }

    public DroneSettings getSettings() {
        return DroneSettings.of(linkId);
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
    }

    public Quaternion getRotation() {
        return getDataTracker().get(ROTATION);
    }

    public void setRotation(Quaternion q) {
        getDataTracker().set(ROTATION, q);
    }

    public RcInputState getInputs() {
        return getDataTracker().get(INPUTS);
    }

    public void setInputs(RcInputState inputs) {
        getDataTracker().set(INPUTS, inputs);
    }

    @Override
    public boolean collides() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected void initDataTracker() {
        getDataTracker().startTracking(ROTATION, Quaternion.IDENTITY.copy());
        getDataTracker().startTracking(INPUTS, RcInputState.ofDefault());
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
