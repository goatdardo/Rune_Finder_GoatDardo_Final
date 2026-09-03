package com.goatdardo.runefinder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;

public class RuneSelection {

    public static void register() {
        // Selection is handled by the screen mixin.
    }

    public static void selectSlot(Slot slot) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) {
            return;
        }

        String rune = RuneDetector.getRuneType(slot.getStack()).orElse(null);

        if (rune == null) {
            return;
        }

        RuneFinderClient.CONFIG.toggleRune(rune);

        System.out.println(
                "Rune Finder: " +
                        (RuneFinderClient.CONFIG.isSelected(rune)
                                ? "Selected "
                                : "Removed ") +
                        rune
        );
    }
}