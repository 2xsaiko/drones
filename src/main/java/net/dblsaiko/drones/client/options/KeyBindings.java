package net.dblsaiko.drones.client.options;

import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding.Builder;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;

import org.lwjgl.glfw.GLFW;

import net.dblsaiko.drones.Main;

public class KeyBindings {

    public static FabricKeyBinding DRONE_TILT_FORWARD;
    public static FabricKeyBinding DRONE_TILT_BACKWARD;
    public static FabricKeyBinding DRONE_TILT_LEFT;
    public static FabricKeyBinding DRONE_TILT_RIGHT;
    public static FabricKeyBinding DRONE_ACCEL;
    public static FabricKeyBinding DRONE_DECEL;
    public static FabricKeyBinding DRONE_TURN_LEFT;
    public static FabricKeyBinding DRONE_TURN_RIGHT;
    public static FabricKeyBinding DRONE_PICK_UP;

    private static FabricKeyBinding create(String name, int code) {
        FabricKeyBinding kb = Builder.create(new Identifier(Main.MODID, name), Type.KEYSYM, code, String.format("%s.drone_controls", Main.MODID)).build();
        KeyBindingRegistry.INSTANCE.register(kb);
        return kb;
    }

    public static void initialize() {
        KeyBindingRegistry.INSTANCE.addCategory(String.format("%s.drone_controls", Main.MODID));
        DRONE_TILT_FORWARD = create("drone_tilt_forward", GLFW.GLFW_KEY_KP_8);
        DRONE_TILT_BACKWARD = create("drone_tilt_backward", GLFW.GLFW_KEY_KP_2);
        DRONE_TILT_LEFT = create("drone_tilt_left", GLFW.GLFW_KEY_KP_4);
        DRONE_TILT_RIGHT = create("drone_tilt_right", GLFW.GLFW_KEY_KP_6);
        DRONE_ACCEL = create("drone_accel", GLFW.GLFW_KEY_KP_ADD);
        DRONE_DECEL = create("drone_decel", GLFW.GLFW_KEY_KP_ENTER);
        DRONE_TURN_LEFT = create("drone_turn_left", GLFW.GLFW_KEY_KP_1);
        DRONE_TURN_RIGHT = create("drone_turn_right", GLFW.GLFW_KEY_KP_3);
        DRONE_PICK_UP = create("drone_pick_up", GLFW.GLFW_KEY_KP_MULTIPLY);
    }

}
