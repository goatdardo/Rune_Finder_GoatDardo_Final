
        package com.goatdardo.runefinder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.Optional;

public class RuneSorterTest {

    /**
     * MULTI-RUNE PICKUP TEST
     *
     * Scans the currently open source chest and moves
     * every selected rune with an assigned destination
     * directly into the player's inventory.
     *
     * It does NOT:
     * - move the player
     * - open another chest
     * - deposit anything
     * - sort items at destinations
     */
    public static void testFirstRune(
            MinecraftClient client
    ) {

        if (client == null ||
                client.player == null ||
                client.world == null) {

            return;
        }

        ClientPlayerEntity player =
                client.player;

        ScreenHandler handler =
                player.currentScreenHandler;

        if (handler == null ||
                handler.slots.size() <= 36) {

            player.sendMessage(
                    Text.literal(
                            "§cRune Finder: Open a source chest first."
                    ),
                    false
            );

            return;
        }

        int containerSlots =
                handler.slots.size() - 36;

        int movedRunes = 0;

        /*
         * Scan every chest slot.
         */
        for (
                int slotIndex = 0;
                slotIndex < containerSlots;
                slotIndex++
        ) {

            ItemStack stack =
                    handler.slots
                            .get(slotIndex)
                            .getStack();

            if (stack == null ||
                    stack.isEmpty()) {

                continue;
            }

            /*
             * Check whether the item is a rune.
             */
            Optional<String> runeType =
                    RuneDetector.getRuneType(stack);

            if (runeType.isEmpty()) {
                continue;
            }

            String rune =
                    runeType.get();

            /*
             * Only collect runes that the user has
             * selected in the Rune Finder GUI.
             *
             * Selection uses the FULL rune ID.
             *
             * Example:
             *
             * Hitstreak:5
             * Slam:2
             * Bleed:3
             */
            if (!RuneFinderClient.CONFIG.isSelected(rune)) {

                continue;
            }

            /*
             * Convert the full rune into its family.
             *
             * Example:
             *
             * Hitstreak:5 → Hitstreak
             * Slam:2      → Slam
             * Bleed:3     → Bleed
             */
            String family =
                    getRuneFamily(rune);

            /*
             * Find the destination assigned to
             * this rune family.
             */
            String destination =
                    RuneFinderClient.CONFIG
                            .getRuneDestination(family);

            /*
             * Ignore runes that do not have a
             * destination assigned.
             */
            if (destination == null ||
                    destination.isBlank()) {

                continue;
            }

            /*
             * Move the entire stack directly into
             * the player's inventory.
             */
            client.interactionManager.clickSlot(
                    handler.syncId,
                    slotIndex,
                    0,
                    SlotActionType.QUICK_MOVE,
                    player
            );

            movedRunes++;

            player.sendMessage(
                    Text.literal(
                            "§aCollected §b" +
                                    rune +
                                    " §7→ §f" +
                                    destination
                    ),
                    false
            );
        }

        /*
         * Nothing was collected.
         */
        if (movedRunes == 0) {

            player.sendMessage(
                    Text.literal(
                            "§eRune Finder: No selected runes with assigned destinations found."
                    ),
                    false
            );

            return;
        }

        /*
         * Summary.
         */
        player.sendMessage(
                Text.literal(
                        "§aCollected §b" +
                                movedRunes +
                                " §arune stack(s) into your inventory."
                ),
                false
        );

        player.sendMessage(
                Text.literal(
                        "§eMulti-rune pickup test complete."
                ),
                false
        );
    }

    /**
     * Converts a full rune ID into its family.
     *
     * Examples:
     *
     * Hitstreak:5 → Hitstreak
     * Slam:2      → Slam
     * Bleed:3     → Bleed
     */
    private static String getRuneFamily(
            String rune
    ) {

        if (rune == null ||
                rune.isBlank()) {

            return "";
        }

        int separator =
                rune.indexOf(':');

        if (separator > 0) {

            return rune.substring(
                    0,
                    separator
            );
        }

        return rune;
    }
}

