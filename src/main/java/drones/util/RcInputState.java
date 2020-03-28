package drones.util;

import java.util.Objects;

public class RcInputState {

    private static final RcInputState DEFAULT = RcInputState.from((byte) 0);

    public final boolean tiltForward;
    public final boolean tiltBackward;
    public final boolean tiltLeft;
    public final boolean tiltRight;
    public final boolean turnLeft;
    public final boolean turnRight;
    public final boolean accel;
    public final boolean decel;

    private RcInputState(boolean tiltForward, boolean tiltBackward, boolean tiltLeft, boolean tiltRight, boolean turnLeft, boolean turnRight, boolean accel, boolean decel) {
        this.tiltForward = tiltForward;
        this.tiltBackward = tiltBackward;
        this.tiltLeft = tiltLeft;
        this.tiltRight = tiltRight;
        this.turnLeft = turnLeft;
        this.turnRight = turnRight;
        this.accel = accel;
        this.decel = decel;
    }

    public int getZTilt() {
        return (tiltForward ? 1 : 0) + (tiltBackward ? -1 : 0);
    }

    public int getXTilt() {
        return (tiltRight ? 1 : 0) + (tiltLeft ? -1 : 0);
    }

    public int getYTurn() {
        return (turnRight ? 1 : 0) + (turnLeft ? -1 : 0);
    }

    public int getSpeed() {
        return (accel ? 1 : 0) + (decel ? -1 : 0);
    }

    public byte toByte() {
        return (byte) ((tiltForward ? 1 : 0) |
            (tiltBackward ? 2 : 0) |
            (tiltLeft ? 4 : 0) |
            (tiltRight ? 8 : 0) |
            (turnLeft ? 16 : 0) |
            (turnRight ? 32 : 0) |
            (accel ? 64 : 0) |
            (decel ? 128 : 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RcInputState that = (RcInputState) o;
        return tiltForward == that.tiltForward &&
            tiltBackward == that.tiltBackward &&
            tiltLeft == that.tiltLeft &&
            tiltRight == that.tiltRight &&
            turnLeft == that.turnLeft &&
            turnRight == that.turnRight &&
            accel == that.accel &&
            decel == that.decel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tiltForward, tiltBackward, tiltLeft, tiltRight, turnLeft, turnRight, accel, decel);
    }

    @Override
    public String toString() {
        return String.format("RcInputState { tiltForward: %s, tiltBackward: %s, tiltLeft: %s, tiltRight: %s, turnLeft: %s, turnRight: %s, accel: %s, decel: %s }", tiltForward, tiltBackward, tiltLeft, tiltRight, turnLeft, turnRight, accel, decel);
    }

    public static RcInputState ofDefault() {
        return DEFAULT;
    }

    public static RcInputState of(boolean tiltForward, boolean tiltBackward, boolean tiltLeft, boolean tiltRight, boolean turnLeft, boolean turnRight, boolean accel, boolean decel) {
        return new RcInputState(tiltForward, tiltBackward, tiltLeft, tiltRight, turnLeft, turnRight, accel, decel);
    }

    public static RcInputState from(byte b) {
        return RcInputState.of(
            (b & 1) != 0,
            (b & 2) != 0,
            (b & 4) != 0,
            (b & 8) != 0,
            (b & 16) != 0,
            (b & 32) != 0,
            (b & 64) != 0,
            (b & 128) != 0
        );
    }

}
