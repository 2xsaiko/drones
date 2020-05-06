package net.dblsaiko.drones.entity.data;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Quaternion;

import net.dblsaiko.drones.util.MathUtil;
import net.dblsaiko.drones.util.RcInputState;

public final class TrackedDataHandlers {

    private TrackedDataHandlers() {}

    public static final TrackedDataHandler<Quaternion> QUATERNION = new TrackedDataHandler<Quaternion>() {
        @Override
        public void write(PacketByteBuf data, Quaternion q) {
            data.writeFloat(q.getW());
            data.writeFloat(q.getX());
            data.writeFloat(q.getY());
            data.writeFloat(q.getZ());
        }

        @Override
        public Quaternion read(PacketByteBuf buffer) {
            float w = buffer.readFloat();
            float x = buffer.readFloat();
            float y = buffer.readFloat();
            float z = buffer.readFloat();
            return new Quaternion(x, y, z, w);
        }

        @Override
        public Quaternion copy(Quaternion q) {
            return MathUtil.copy(q);
        }
    };

    public static final TrackedDataHandler<RcInputState> RC_INPUT_STATE = new TrackedDataHandler<RcInputState>() {
        @Override
        public void write(PacketByteBuf data, RcInputState object) {
            data.writeByte(object.toByte());
        }

        @Override
        public RcInputState read(PacketByteBuf packetByteBuf) {
            return RcInputState.from(packetByteBuf.readByte());
        }

        @Override
        public RcInputState copy(RcInputState object) {
            return object;
        }
    };

    public static void register() {
        TrackedDataHandlerRegistry.register(QUATERNION);
        TrackedDataHandlerRegistry.register(RC_INPUT_STATE);
    }

}
