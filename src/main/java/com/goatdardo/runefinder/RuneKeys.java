
        package com.goatdardo.runefinder;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class RuneKeys {

    public static KeyBinding selectRuneKey;
    public static KeyBinding autoPickupKey;
    public static KeyBinding clearRunesKey;
    public static KeyBinding sorterStartKey;

    // Chest registration keys
    public static KeyBinding sourceChestKey;
    public static KeyBinding destinationChestKey;

    // Temporary sorter preview key
    public static KeyBinding sorterPreviewKey;

    // Temporary Baritone API test key
    public static KeyBinding baritoneTestKey;

    public static void register() {

        // Right Shift = select/remove rune
        selectRuneKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.runefinder.select",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_RIGHT_SHIFT,
                        KeyBinding.Category.MISC
                )
        );

        // ; = toggle auto pickup
        autoPickupKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.runefinder.autopickup",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_SEMICOLON,
                        KeyBinding.Category.MISC
                )
        );

        // ' = clear selected runes
        clearRunesKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.runefinder.clear",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_APOSTROPHE,
                        KeyBinding.Category.MISC
                )
        );

        // [ = register/remove source chest
        sourceChestKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.runefinder.source_chest",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_LEFT_BRACKET,
                        KeyBinding.Category.MISC
                )
        );

        // ] = register/remove destination chest
        destinationChestKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.runefinder.destination_chest",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_RIGHT_BRACKET,
                        KeyBinding.Category.MISC
                )
        );

        // P = temporary sorter preview
        sorterPreviewKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.runefinder.sorter_preview",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_P,
                        KeyBinding.Category.MISC
                )
        );

        // O = temporary Baritone API test
        baritoneTestKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.runefinder.baritone_test",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_O,
                        KeyBinding.Category.MISC
                )
        );
        sorterStartKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.runefinder.sorter_start",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_K,
                        KeyBinding.Category.MISC
                )
        );

    }
}

