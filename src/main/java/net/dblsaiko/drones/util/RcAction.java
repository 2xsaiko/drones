package net.dblsaiko.drones.util;

public enum RcAction {
    PICK_UP;

    public byte getId() {
        switch (this) {
            case PICK_UP:
                return 0;
            default:
                throw new IllegalStateException("unreachable");
        }
    }

    public static RcAction byId(byte id) {
        switch (id) {
            case 0:
                return RcAction.PICK_UP;
            default:
                throw new IllegalStateException("invalid action id " + id);
        }
    }
}
