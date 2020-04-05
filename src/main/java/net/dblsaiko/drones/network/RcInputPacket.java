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
import net.dblsaiko.drones.util.RcHandler;
import net.dblsaiko.drones.util.RcInputState;

public class RcInputPacket {

    public static final Identifier ID = new Identifier(Main.MODID, "rc_input");

    private final RcInputState inputs;

    private RcInputPacket(RcInputState inputs) {
        this.inputs = inputs;
    }

    public void handle(PacketContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        ItemStack stack = player.getMainHandStack();
        if (stack.getItem() != Items.REMOTE_CONTROL) return;
        UUID linkId = RemoteControlItem.getLinkId(stack);
        if (linkId == null) return;
        ctx.getTaskQueue().execute(() -> {
            RcHandler.getInstance((ServerWorld) player.world).sender(linkId, (ServerPlayerEntity) player).send(inputs);
        });
    }

    public void send() {
        ClientSidePacketRegistry.INSTANCE.sendToServer(ID, this.intoBuffer());
    }

    public PacketByteBuf intoBuffer() {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeByte(inputs.toByte());
        return buffer;
    }

    public static RcInputPacket of(RcInputState inputs) {
        return new RcInputPacket(inputs);
    }

    public static RcInputPacket from(PacketByteBuf buffer) {
        RcInputState inputs = RcInputState.from(buffer.readByte());
        return RcInputPacket.of(inputs);
    }

    public static void register() {
        ServerSidePacketRegistry.INSTANCE.register(ID, (context, buffer) -> RcInputPacket.from(buffer).handle(context));
    }

}
