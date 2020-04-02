package drones.util;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

import drones.init.Items;
import drones.item.RemoteControlItem;

public class RcHandler {

    private static final Map<ServerWorld, RcHandler> instances = new WeakHashMap<>();

    private final Map<UUID, RcChannel> channels = new HashMap<>();

    private RcHandler(ServerWorld world) { }

    public RcSender sender(UUID uuid, ServerPlayerEntity self) {
        RcChannel chan = channels.computeIfAbsent(uuid, RcChannel::new);
        return chan.signals.keySet().parallelStream().filter(e -> e.player == self).findAny().orElseGet(() -> {
            RcSenderImpl send = new RcSenderImpl(chan, self);
            send.send(RcInputState.ofDefault());
            return send;
        });
    }

    public RcReceiver receiver(UUID uuid, Entity self) {
        RcChannel chan = channels.computeIfAbsent(uuid, RcChannel::new);
        RcReceiverImpl recv = new RcReceiverImpl(chan, self);
        chan.receivers.add(new WeakReference<>(recv));
        return recv;
    }

    public static RcHandler getInstance(ServerWorld world) {
        return instances.computeIfAbsent(world, RcHandler::new);
    }

    private static final class RcChannel {
        public final UUID linkId;
        public final Set<WeakReference<RcReceiverImpl>> receivers = new HashSet<>();
        public final Map<RcSenderImpl, RcInputState> signals = new LinkedHashMap<>();

        private RcChannel(UUID linkId) {
            this.linkId = linkId;
        }

        public RcInputState signal(Vec3d from) {
            double dist = Double.POSITIVE_INFINITY;
            RcInputState closest = RcInputState.ofDefault();
            for (Iterator<Entry<RcSenderImpl, RcInputState>> iterator = signals.entrySet().iterator(); iterator.hasNext(); ) {
                Entry<RcSenderImpl, RcInputState> entry = iterator.next();
                if (!entry.getKey().isValid()) {
                    iterator.remove();
                    continue;
                }
                double v = from.squaredDistanceTo(entry.getKey().pos());
                if (v < dist) {
                    dist = v;
                    closest = entry.getValue();
                }
            }
            return closest;
        }

        public void post(RcSenderImpl source, RcInputState state) {
            signals.remove(source);
            signals.put(source, state);
        }

        public void postAction(RcAction action) {
            for (WeakIter<RcReceiverImpl> iter = WeakIter.wrap(receivers.iterator()); iter.hasNext(); ) {
                RcReceiverImpl next = iter.next();
                next.pushAction(action);
            }
        }
    }

    private static final class RcReceiverImpl implements RcReceiver {

        public final RcChannel chan;
        public final Entity entity;
        private final List<RcAction> pendingActions = new LinkedList<>();

        private RcReceiverImpl(RcChannel chan, Entity entity) {
            this.chan = chan;
            this.entity = entity;
        }

        @Override
        public RcInputState inputs() {
            return chan.signal(entity.getPos());
        }

        public void pushAction(RcAction action) {
            pendingActions.add(action);
        }

        @Override
        public RcAction nextAction() {
            if (pendingActions.isEmpty()) return null;
            return pendingActions.remove(0);
        }

    }

    private static final class RcSenderImpl implements RcSender {

        public final RcChannel chan;
        public final ServerPlayerEntity player;

        private RcSenderImpl(RcChannel chan, ServerPlayerEntity player) {
            this.chan = chan;
            this.player = player;
        }

        @Override
        public void send(RcInputState inputs) {
            chan.post(this, inputs);
        }

        @Override
        public void send(RcAction action) {
            chan.postAction(action);
        }

        public boolean isValid() {
            ItemStack stack = player.getMainHandStack();
            return stack.getItem() == Items.REMOTE_CONTROL && chan.linkId.equals(RemoteControlItem.getLinkId(stack));
        }

        public Vec3d pos() {
            return player.getPos();
        }

    }

}
