
        package com.goatdardo.runefinder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class RuneSorterDepositTest {

    /**
     * MULTI-RUNE DEPOSIT TEST
     *
     * Scans the player's inventory and deposits every
     * rune whose assigned destination matches the
     * currently open destination chest.
     *
     * It does NOT:
     * - move the player
     * - open another chest
     * - deposit runes belonging to other destinations
     * - move normal items
     */
    public static void depositCursorRune(
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
                            "§cRune Finder: Open the destination chest first."
                    ),
                    false
            );

            return;
        }

        /*
         * Make sure we are actually looking at a chest.
         */
        if (!(client.crosshairTarget instanceof BlockHitResult hit)) {

            player.sendMessage(
                    Text.literal(
                            "§cRune Finder: Look directly at the destination chest."
                    ),
                    false
            );

            return;
        }

        BlockPos chestPos =
                hit.getBlockPos();

        /*
         * Use the same canonical position logic that
         * source/destination registration uses.
         */
        chestPos =
                ChestPositionHelper.getCanonicalPosition(
                        client,
                        chestPos
                );

        String destinationPosition =
                chestPos.getX() + "," +
                        chestPos.getY() + "," +
                        chestPos.getZ();

        /*
         * Make sure this chest is actually registered
         * as a destination.
         */
        if (!RuneFinderClient.CONFIG
                .getDestinationChests()
                .contains(destinationPosition)) {

            player.sendMessage(
                    Text.literal(
                            "§cRune Finder: This chest is not a registered destination."
                    ),
                    false
            );

            return;
        }

        int containerSlots =
                handler.slots.size() - 36;

        int movedRunes = 0;

        /*
         * Scan the player's inventory.
         *
         * Inventory slots come after the container slots.
         */
        for (
                int inventorySlot = containerSlots;
                inventorySlot < handler.slots.size();
                inventorySlot++
        ) {

            Slot slot =
                    handler.slots.get(inventorySlot);

            ItemStack stack =
                    slot.getStack();

            if (stack == null ||
                    stack.isEmpty()) {

                continue;
            }

            Optional<String> runeType =
                    RuneDetector.getRuneType(stack);

            if (runeType.isEmpty()) {
                continue;
            }

            String rune =
                    runeType.get();

            String family =
                    getRuneFamily(rune);

            /*
             * Find where this rune family belongs.
             */
            String assignedDestination =
                    RuneFinderClient.CONFIG
                            .getRuneDestination(family);

            if (assignedDestination == null ||
                    assignedDestination.isBlank()) {

                continue;
            }

            /*
             * This rune belongs somewhere else.
             */
            if (!destinationPosition.equals(
                    assignedDestination
            )) {

                continue;
            }

            /*
             * Find a suitable chest slot.
             *
             * First try to merge with an existing
             * stack of the same rune family.
             */
            int targetSlot =
                    findCompatibleChestSlot(
                            handler,
                            containerSlots,
                            stack,
                            family
                    );

            /*
             * If no compatible stack exists,
             * find an empty chest slot.
             */
            if (targetSlot == -1) {

                targetSlot =
                        findEmptyChestSlot(
                                handler,
                                containerSlots
                        );
            }

            if (targetSlot == -1) {

                player.sendMessage(
                        Text.literal(
                                "§cRune Finder: Destination chest is full."
                        ),
                        false
                );

                break;
            }

            /*
             * Pick up the inventory stack.
             */
            client.interactionManager.clickSlot(
                    handler.syncId,
                    inventorySlot,
                    0,
                    SlotActionType.PICKUP,
                    player
            );

            /*
             * Place the stack into the destination chest.
             */
            client.interactionManager.clickSlot(
                    handler.syncId,
                    targetSlot,
                    0,
                    SlotActionType.PICKUP,
                    player
            );

            movedRunes++;

            player.sendMessage(
                    Text.literal(
                            "§aDeposited §b" +
                                    rune +
                                    " §7→ §d" +
                                    destinationPosition
                    ),
                    false
            );
        }

        /*
         * Safety check.
         *
         * If something somehow remains on the cursor,
         * report it instead of pretending everything worked.
         */
        if (!handler.getCursorStack().isEmpty()) {

            player.sendMessage(
                    Text.literal(
                            "§cRune Finder: Something remains on the cursor. Stop and check the inventory."
                    ),
                    false
            );

            return;
        }

        if (movedRunes == 0) {

            player.sendMessage(
                    Text.literal(
                            "§eRune Finder: No runes for this destination were found."
                    ),
                    false
            );

            return;
        }

        player.sendMessage(
                Text.literal(
                        "§aDeposited §b" +
                                movedRunes +
                                " §arune stack(s)."
                ),
                false
        );

        player.sendMessage(
                Text.literal(
                        "§eMulti-rune deposit test complete."
                ),
                false
        );
    }

    private static int findCompatibleChestSlot(
            ScreenHandler handler,
            int containerSlots,
            ItemStack inventoryStack,
            String family
    ) {

        for (
                int slotIndex = 0;
                slotIndex < containerSlots;
                slotIndex++
        ) {

            ItemStack chestStack =
                    handler.slots
                            .get(slotIndex)
                            .getStack();

            if (chestStack == null ||
                    chestStack.isEmpty()) {

                continue;
            }

            Optional<String> chestRune =
                    RuneDetector.getRuneType(chestStack);

            if (chestRune.isEmpty()) {
                continue;
            }

            String chestFamily =
                    getRuneFamily(
                            chestRune.get()
                    );

            if (!family.equals(chestFamily)) {
                continue;
            }

            if (!chestStack.isStackable()) {
                continue;
            }

            if (chestStack.getCount() >=
                    chestStack.getMaxCount()) {

                continue;
            }

            /*
             * Only return the slot if Minecraft itself
             * considers the items stack-compatible.
             */
            if (!ItemStack.areItemsAndComponentsEqual(
                    inventoryStack,
                    chestStack
            )) {
                continue;
            }

            return slotIndex;
        }

        return -1;
    }

    private static int findEmptyChestSlot(
            ScreenHandler handler,
            int containerSlots
    ) {

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

                return slotIndex;
            }
        }

        return -1;
    }

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

