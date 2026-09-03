
        package com.goatdardo.runefinder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

import java.util.Optional;

public class RuneSorterPreview {

    /**
     * Preview only.
     *
     * This method does NOT move any items.
     * It only reports what the sorter would do.
     */
    public static void previewCurrentChest(
            MinecraftClient client
    ) {

        if (client == null ||
                client.player == null ||
                client.world == null) {

            return;
        }

        ClientPlayerEntity player =
                client.player;

        /*
         * Make sure a container screen is open.
         *
         * We check the handler instead of relying
         * on a specific screen class.
         */
        ScreenHandler handler =
                player.currentScreenHandler;

        if (handler == null ||
                handler.slots.size() <= 36) {

            player.sendMessage(
                    Text.literal(
                            "§cRune Finder: Open a chest first."
                    ),
                    false
            );

            return;
        }

        /*
         * Normal chest:
         * 27 container slots + 36 player slots = 63
         *
         * Double chest:
         * 54 container slots + 36 player slots = 90
         *
         * Therefore the first (size - 36)
         * slots belong to the chest/container.
         */
        int containerSlots =
                handler.slots.size() - 36;

        player.sendMessage(
                Text.literal(
                        "§6========== RUNE SORTER PREVIEW =========="
                ),
                false
        );

        boolean foundRune = false;

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

            Optional<String> runeType =
                    RuneDetector.getRuneType(stack);

            /*
             * Not a rune.
             */
            if (runeType.isEmpty()) {
                continue;
            }

            foundRune = true;

            String rune =
                    runeType.get();

            String family =
                    getRuneFamily(rune);

            String destination =
                    RuneFinderClient.CONFIG
                            .getRuneDestination(
                                    family
                            );

            if (destination == null ||
                    destination.isBlank()) {

                player.sendMessage(
                        Text.literal(
                                "§c" +
                                        stack.getName()
                                                .getString() +
                                        " §7→ §e" +
                                        family +
                                        " §7→ §cNO DESTINATION"
                        ),
                        false
                );

                continue;
            }

            player.sendMessage(
                    Text.literal(
                            "§a" +
                                    stack.getName()
                                            .getString() +
                                    " §7→ §b" +
                                    family +
                                    " §7→ §d" +
                                    destination
                    ),
                    false
            );
        }

        if (!foundRune) {

            player.sendMessage(
                    Text.literal(
                            "§eRune Finder: No runes found in this chest."
                    ),
                    false
            );
        }

        player.sendMessage(
                Text.literal(
                        "§6=========================================="
                ),
                false
        );

        player.sendMessage(
                Text.literal(
                        "§ePreview only — no items were moved."
                ),
                false
        );
    }

    /**
     * Converts:
     *
     * Hitstreak:4 → Hitstreak
     * Slam:2      → Slam
     * Lifesteal:1 → Lifesteal
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

