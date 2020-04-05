package drones.entity.data;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Quaternion;

import drones.util.MathUtil;
import drones.util.RcInputState;

public final class TrackedDataHandlers {

    private TrackedDataHandlers() {}

    public static final TrackedDataHandler<Quaternion> QUATERNION = new TrackedDataHandler<Quaternion>() {
        @Override
        public void write(PacketByteBuf data, Quaternion q) {
            data.writeFloat(q.getA());
            data.writeFloat(q.getB());
            data.writeFloat(q.getC());
            data.writeFloat(q.getD());
        }

        @Override
        public Quaternion read(PacketByteBuf buffer) {
            float a = buffer.readFloat();
            float b = buffer.readFloat();
            float c = buffer.readFloat();
            float d = buffer.readFloat();
            return new Quaternion(b, c, d, a);
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
