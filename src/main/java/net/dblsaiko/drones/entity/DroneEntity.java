package net.dblsaiko.drones.entity;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
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

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import net.dblsaiko.drones.Main;
import net.dblsaiko.drones.entity.data.TrackedDataHandlers;
import net.dblsaiko.drones.init.EntityTypes;
import net.dblsaiko.drones.init.Items;
import net.dblsaiko.drones.item.RemoteControlItem;
import net.dblsaiko.drones.util.DroneSettings;
import net.dblsaiko.drones.util.MathUtil;
import net.dblsaiko.drones.util.RcAction;
import net.dblsaiko.drones.util.RcHandler;
import net.dblsaiko.drones.util.RcInputState;
import net.dblsaiko.drones.util.RcReceiver;
import net.dblsaiko.drones.util.WeakIter;

public class DroneEntity extends Entity {

    private static final TrackedData<Quaternion> ROTATION = DataTracker.registerData(DroneEntity.class, TrackedDataHandlers.QUATERNION);
    private static final TrackedData<RcInputState> INPUTS = DataTracker.registerData(DroneEntity.class, TrackedDataHandlers.RC_INPUT_STATE);
    private static final TrackedData<Optional<UUID>> LINK_ID = DataTracker.registerData(DroneEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    private static final Vec3d GRAVITY = new Vec3d(0, -0.2, 0);
    public static final Vec3d UP = GRAVITY.multiply(-1.0).normalize();

    private static final Set<WeakReference<DroneEntity>> INSTANCES = new HashSet<>();

    private RcReceiver recv = null;

    private int interactionCooldown; // fuck you

    public DroneEntity(EntityType<? extends DroneEntity> type, World world) {
        super(type, world);
        if (world.isClient()) INSTANCES.add(new WeakReference<>(this));
    }

    @Override
    public void tick() {
        super.tick();

        RcInputState inputs;
        if (!world.isClient()) {
            RcReceiver recv = getReceiver();
            inputs = recv == null ? RcInputState.ofDefault() : recv.inputs();
            setInputs(inputs);

            if (recv != null) {
                RcAction next;
                while ((next = recv.nextAction()) != null) {
                    switch (next) {
                        case PICK_UP:
                            pickUpEntity();
                            break;
                    }
                }
            }
        } else {
            inputs = getInputs();
        }

        // react to inputs

        Quaternion rotation = MathUtil.copy(getRotation());
        float x = -inputs.getZTilt();
        float y = -inputs.getYTurn();
        float z = -inputs.getXTilt();
        float l = MathHelper.sqrt(x * x + y * y + z * z);
        if (l != 0) {
            x /= l;
            y /= l;
            z /= l;
            rotation.hamiltonProduct(new Quaternion(new Vector3f(x, y, z), 0.075f, false));
            MathUtil.normalize(rotation);
        }

        // try rotating towards normal orientation again

        // FIXME quaternions can go fucking die
        // MathUtil.rotateTowards(rotation, UP.add(MathUtil.reject(getVelocity().negate(), UP)).normalize(), 0.1f, null, null);
        MathUtil.rotateTowards(rotation, UP, (float) Math.max(0.1, -MathUtil.rotate(UP, rotation).dotProduct(UP)), null, null);
        setRotation(rotation);
        updateEulerRotation(rotation);

        // calculate total new acceleration without taking air resistance into account

        Vec3d up = MathUtil.rotate(UP, rotation);
        Vec3d upAccel = up.multiply(-GRAVITY.y + 0.5 * inputs.getSpeed());
        Vec3d totalAccel = GRAVITY.add(upAccel);

        // calculate current actual acceleration

        float airResistanceDown = 0.4f;
        float airResistanceSide = 0.9f;
        Vec3d unadjustedAccel = getVelocity().add(totalAccel);
        Vec3d actualAccel = MathUtil.project(unadjustedAccel, up).multiply(airResistanceDown)
            .add(MathUtil.reject(unadjustedAccel, up).multiply(airResistanceSide));

        // actually move the entity!

        setVelocity(actualAccel);
        move(MovementType.SELF, actualAccel);

        getPassengerList().forEach(e -> e.fallDistance = 0);
    }

    private void pickUpEntity() {
        Entity e = getPrimaryPassenger();
        if (e != null) {
            e.stopRiding();
        } else {
            world.getOtherEntities(this, getBoundingBox().offset(0.0, -1, 0.0).expand(0.2, 1.0, 0.2)).stream().findFirst().ifPresent(e1 -> {
                e1.startRiding(this);
            });
        }
    }

    @Override
    public Entity getPrimaryPassenger() {
        return this.getPassengerList().isEmpty() ? null : this.getPassengerList().get(0);
    }

    @Override
    public void updatePassengerPosition(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            passenger.updatePosition(this.getX(), this.getY() - passenger.getHeight() + getHeightOffset(passenger.getType()), this.getZ());
        }
    }

    private double getHeightOffset(EntityType<?> entity) {
        if (entity == EntityType.PLAYER) return -0.2;
        else if (entity == EntityType.ITEM) return -0.5;
        else return 0.0;
    }

    private void updateEulerRotation(Quaternion rotation) {
        EulerAngle eulerAngle = MathUtil.toEulerAngles(rotation);
        yaw = eulerAngle.getYaw() * MathUtil.toDegreesf;
        pitch = eulerAngle.getPitch() * MathUtil.toDegreesf;
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
            player.sendMessage(new TranslatableText(String.format("status.%s.drone_linked", Main.MODID)), true);
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
        tag.put("DroneSettings", DroneSettings.of(getLinkId()).toTag());
    }

    public UUID getLinkId() {
        return getDataTracker().get(LINK_ID).orElse(null);
    }

    private void setSettings(DroneSettings ds) {
        setLinkId(ds.linkId);
    }

    private void setLinkId(UUID linkId) {
        getDataTracker().set(LINK_ID, Optional.ofNullable(linkId));
        this.recv = null;
    }

    private RcReceiver getReceiver() {
        if (world.isClient()) return null;
        if (getLinkId() == null) return null;
        if (recv == null) {
            recv = RcHandler.getInstance((ServerWorld) world).receiver(getLinkId(), this);
        }
        return recv;
    }

    public DroneSettings getSettings() {
        return DroneSettings.of(getLinkId());
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
        getDataTracker().startTracking(ROTATION, MathUtil.copy(Quaternion.IDENTITY));
        getDataTracker().startTracking(INPUTS, RcInputState.ofDefault());
        getDataTracker().startTracking(LINK_ID, Optional.empty());
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

    public static Iterator<DroneEntity> getInstances() {
        return WeakIter.wrap(INSTANCES.iterator());
    }

}
