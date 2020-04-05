package net.dblsaiko.drones.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;

import java.util.UUID;

import io.netty.buffer.Unpooled;

import net.dblsaiko.drones.Main;
import net.dblsaiko.drones.init.Items;
import net.dblsaiko.drones.item.RemoteControlItem;
import net.dblsaiko.drones.util.RcAction;
import net.dblsaiko.drones.util.RcHandler;

public class RcActionPacket {

    public static final Identifier ID = new Identifier(Main.MODID, "rc_action");

    private final RcAction action;

    private RcActionPacket(RcAction action) {
        this.action = action;
    }

    public void handle(PacketContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        ItemStack stack = player.getMainHandStack();
        if (stack.getItem() != Items.REMOTE_CONTROL) return;
        UUID linkId = RemoteControlItem.getLinkId(stack);
        if (linkId == null) return;
        ctx.getTaskQueue().execute(() -> {
            RcHandler.getInstance((ServerWorld) player.world).sender(linkId, (ServerPlayerEntity) player).send(action);
        });
    }

    public void send() {
        ClientSidePacketRegistry.INSTANCE.sendToServer(ID, this.intoBuffer());
    }

    public PacketByteBuf intoBuffer() {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeByte(action.getId());
        return buffer;
    }

    public static RcActionPacket of(RcAction action) {
        return new RcActionPacket(action);
    }

    public static RcActionPacket from(PacketByteBuf buffer) {
        RcAction action = RcAction.byId(buffer.readByte());
        return RcActionPacket.of(action);
    }

    public static void register() {
        ServerSidePacketRegistry.INSTANCE.register(ID, (context, buffer) -> RcActionPacket.from(buffer).handle(context));
    }

}
