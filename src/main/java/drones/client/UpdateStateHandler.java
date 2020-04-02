package drones.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

import drones.client.options.KeyBindings;
import drones.init.Items;
import drones.network.RcActionPacket;
import drones.network.RcInputPacket;
import drones.util.RcAction;
import drones.util.RcInputState;

public class UpdateStateHandler {

    private RcInputState lastTransmitted;

    public void update(ClientPlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        if (stack.getItem() != Items.REMOTE_CONTROL) {
            lastTransmitted = null;
            return;
        }
        RcInputState state = getInputs();
        if (!state.equals(lastTransmitted)) {
            RcInputPacket.of(state).send();
            lastTransmitted = state;
        }

        while (KeyBindings.DRONE_PICK_UP.wasPressed()) {
            RcActionPacket.of(RcAction.PICK_UP).send();
        }
    }

    private static RcInputState getInputs() {
        return RcInputState.of(
            KeyBindings.DRONE_TILT_FORWARD.isPressed(),
            KeyBindings.DRONE_TILT_BACKWARD.isPressed(),
            KeyBindings.DRONE_TILT_LEFT.isPressed(),
            KeyBindings.DRONE_TILT_RIGHT.isPressed(),
            KeyBindings.DRONE_TURN_LEFT.isPressed(),
            KeyBindings.DRONE_TURN_RIGHT.isPressed(),
            KeyBindings.DRONE_ACCEL.isPressed(),
            KeyBindings.DRONE_DECEL.isPressed()
        );
    }

}
