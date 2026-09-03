
        package com.goatdardo.runefinder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class RuneAutoPickup {

    private static long lastPickupTime = 0;
    private static boolean inventoryFullMessageShown = false;

    public static void tick(MinecraftClient client) {

        if (!RuneFinderClient.CONFIG.autoPickupEnabled) {
            return;
        }

        if (!(client.currentScreen instanceof HandledScreen<?> screen)) {
            inventoryFullMessageShown = false;
            return;
        }

        if (client.player == null || client.interactionManager == null) {
            return;
        }

        long now = System.currentTimeMillis();

        if (now - lastPickupTime < RuneFinderClient.CONFIG.pickupDelay) {
            return;
        }

        PlayerInventory inventory = client.player.getInventory();

        for (Slot slot : screen.getScreenHandler().slots) {

            // Ignore the player's own inventory slots.
            if (slot.inventory == inventory) {
                continue;
            }

            ItemStack stack = slot.getStack();

            if (stack.isEmpty()) {
                continue;
            }

            String rune = RuneDetector
                    .getRuneType(stack)
                    .orElse(null);

            if (rune == null) {
                continue;
            }

            // Only pick up runes the user selected.
            if (!RuneFinderClient.CONFIG.isSelected(rune)) {
                continue;
            }

            boolean hasEmptySlot =
                    inventory.getEmptySlot() != -1;

            boolean hasRoomInExistingStack =
                    inventory.getOccupiedSlotWithRoomForStack(stack) != -1;

            /*
             * Inventory is completely full.
             * Don't attempt the pickup.
             */
            if (!hasEmptySlot && !hasRoomInExistingStack) {

                if (!inventoryFullMessageShown) {

                    client.player.sendMessage(
                            Text.literal("Rune Finder: Inventory Full"),
                            false
                    );

                    inventoryFullMessageShown = true;
                }

                return;
            }

            /*
             * Space is available again, so allow the
             * inventory-full message to appear again later.
             */
            inventoryFullMessageShown = false;

            client.interactionManager.clickSlot(
                    screen.getScreenHandler().syncId,
                    slot.id,
                    0,
                    SlotActionType.QUICK_MOVE,
                    client.player
            );

            lastPickupTime = now;

            return;
        }
    }
}

