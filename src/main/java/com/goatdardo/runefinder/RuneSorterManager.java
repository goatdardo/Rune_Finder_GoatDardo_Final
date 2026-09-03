
        package com.goatdardo.runefinder;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class RuneSorterManager {

    private enum State {

        IDLE,

        /*
         * SOURCE SWEEP
         */
        WALKING_TO_SOURCE,
        OPENING_SOURCE,
        COLLECTING_SOURCE,

        /*
         * DESTINATIONS
         */
        WALKING_TO_DESTINATION,
        OPENING_DESTINATION,
        DEPOSITING_DESTINATION,

        /*
         * HOME
         */
        RETURNING_HOME
    }

    private static State state = State.IDLE;

    /*
     * --------------------------------------------------------
     * SOURCE SWEEP TRACKING
     * --------------------------------------------------------
     *
     * currentSourceIndex tells us which source we are checking.
     *
     * A source is checked only once because we ALWAYS increment
     * this index after finishing that source.
     *
     * When we reach the end, a new sweep can begin.
     */

    private static int currentSourceIndex = 0;

    /*
     * Number of complete source sweeps that have happened.
     */
    private static int sourceSweepNumber = 0;

    /*
     * --------------------------------------------------------
     * CURRENT POSITIONS
     * --------------------------------------------------------
     */

    private static String currentSourcePosition = null;

    private static String currentDestination = null;

    /*
     * --------------------------------------------------------
     * TIMERS
     * --------------------------------------------------------
     */

    private static int openAttempts = 0;

    private static long nextActionTime = 0;


    /*
     * ========================================================
     * START
     * ========================================================
     */

    public static void start(
            MinecraftClient client
    ) {

        if (client == null ||
                client.player == null ||
                client.world == null) {

            return;
        }

        if (RuneFinderClient.CONFIG.sorterRunning) {

            client.player.sendMessage(
                    Text.literal(
                            "§eRune Finder: Sorter is already running."
                    ),
                    true
            );

            return;
        }

        if (RuneFinderClient.CONFIG
                .getSourceChests()
                .isEmpty()) {

            client.player.sendMessage(
                    Text.literal(
                            "§cRune Finder: No source chests registered."
                    ),
                    true
            );

            return;
        }

        if (RuneFinderClient.CONFIG
                .getRuneDestinations()
                .isEmpty()) {

            client.player.sendMessage(
                    Text.literal(
                            "§cRune Finder: No rune destinations configured."
                    ),
                    true
            );

            return;
        }

        String home =
                RuneFinderClient.CONFIG.sorterHomeName;

        if (home == null ||
                home.isBlank()) {

            client.player.sendMessage(
                    Text.literal(
                            "§cRune Finder: No sorter home configured."
                    ),
                    true
            );

            return;
        }

        /*
         * Start sorter.
         */

        RuneFinderClient.CONFIG.sorterRunning = true;

        currentSourceIndex = 0;

        sourceSweepNumber = 1;

        currentSourcePosition = null;

        currentDestination = null;

        openAttempts = 0;

        nextActionTime = 0;

        state = State.WALKING_TO_SOURCE;

        client.player.sendMessage(
                Text.literal(
                        "§aRune Finder: Source sweep #1 started."
                ),
                true
        );

        walkToCurrentSource(client);
    }


    /*
     * ========================================================
     * STOP
     * ========================================================
     */

    public static void stop(
            MinecraftClient client
    ) {

        RuneFinderClient.CONFIG.sorterRunning = false;

        state = State.IDLE;

        currentSourceIndex = 0;

        sourceSweepNumber = 0;

        currentSourcePosition = null;

        currentDestination = null;

        openAttempts = 0;

        nextActionTime = 0;

        stopBaritone();

        if (client != null &&
                client.player != null) {

            client.player.sendMessage(
                    Text.literal(
                            "§cRune Finder: Sorter stopped."
                    ),
                    true
            );
        }
    }


    /*
     * ========================================================
     * MAIN TICK
     * ========================================================
     */

    public static void tick(
            MinecraftClient client
    ) {

        if (client == null ||
                client.player == null ||
                client.world == null) {

            return;
        }

        if (!RuneFinderClient.CONFIG.sorterRunning) {
            return;
        }

        switch (state) {

            /*
             * =================================================
             * WALKING TO SOURCE
             * =================================================
             */

            case WALKING_TO_SOURCE -> {

                if (hasReachedPosition(
                        client,
                        currentSourcePosition
                )) {

                    stopBaritone();

                    state = State.OPENING_SOURCE;

                    openAttempts = 0;

                    nextActionTime =
                            System.currentTimeMillis() + 300;

                    client.player.sendMessage(
                            Text.literal(
                                    "§aRune Finder: Reached source §7" +
                                            (currentSourceIndex + 1) +
                                            "§a."
                            ),
                            true
                    );
                }
            }


            /*
             * =================================================
             * OPENING SOURCE
             * =================================================
             */

            case OPENING_SOURCE -> {

                /*
                 * Chest opened.
                 */

                if (client.currentScreen
                        instanceof HandledScreen<?>) {

                    state = State.COLLECTING_SOURCE;

                    nextActionTime =
                            System.currentTimeMillis() + 400;

                    return;
                }

                if (System.currentTimeMillis() <
                        nextActionTime) {

                    return;
                }

                /*
                 * Try a few times.
                 */

                if (openAttempts >= 5) {

                    client.player.sendMessage(
                            Text.literal(
                                    "§cRune Finder: Could not open source chest."
                            ),
                            true
                    );

                    /*
                     * Even if a source cannot open, move to the
                     * next source instead of checking this source
                     * again.
                     */

                    finishCurrentSource(
                            client
                    );

                    return;
                }

                openChest(
                        client,
                        currentSourcePosition
                );

                openAttempts++;

                nextActionTime =
                        System.currentTimeMillis() + 600;
            }


            /*
             * =================================================
             * COLLECTING CURRENT SOURCE
             * =================================================
             */

            case COLLECTING_SOURCE -> {

                /*
                 * Chest closed unexpectedly.
                 */

                if (!(client.currentScreen
                        instanceof HandledScreen<?> screen)) {

                    state = State.OPENING_SOURCE;

                    openAttempts = 0;

                    nextActionTime =
                            System.currentTimeMillis() + 300;

                    return;
                }

                if (System.currentTimeMillis() <
                        nextActionTime) {

                    return;
                }

                /*
                 * Find the next matching rune.
                 */

                Slot runeSlot =
                        findMatchingRuneSlot(
                                client,
                                screen
                        );

                /*
                 * ------------------------------------------------
                 * NO MORE RUNES IN THIS SOURCE
                 * ------------------------------------------------
                 */

                if (runeSlot == null) {

                    client.player.sendMessage(
                            Text.literal(
                                    "§aRune Finder: Source §7" +
                                            (currentSourceIndex + 1) +
                                            " §acomplete."
                            ),
                            true
                    );

                    client.player.closeHandledScreen();

                    nextActionTime =
                            System.currentTimeMillis() + 400;

                    /*
                     * IMPORTANT:
                     *
                     * We NEVER check this source again during
                     * this sweep.
                     */

                    finishCurrentSource(
                            client
                    );

                    return;
                }

                ItemStack stack =
                        runeSlot.getStack();

                /*
                 * ------------------------------------------------
                 * INVENTORY FULL
                 * ------------------------------------------------
                 */

                if (!canFitRuneStack(
                        client,
                        stack
                )) {

                    client.player.sendMessage(
                            Text.literal(
                                    "§eRune Finder: Inventory full — moving to destinations."
                            ),
                            true
                    );

                    client.player.closeHandledScreen();

                    nextActionTime =
                            System.currentTimeMillis() + 400;

                    /*
                     * DO NOT check any more source chests.
                     *
                     * Go directly to destinations.
                     */

                    String destination =
                            findNextInventoryDestination(
                                    client
                            );

                    if (destination != null) {

                        currentDestination =
                                destination;

                        state =
                                State.WALKING_TO_DESTINATION;

                        walkToDestination(
                                client
                        );

                    } else {

                        /*
                         * No destination runes in inventory.
                         *
                         * Continue to next source.
                         */

                        finishCurrentSource(
                                client
                        );
                    }

                    return;
                }

                String rune =
                        RuneDetector
                                .getRuneType(stack)
                                .orElse(null);

                if (rune == null) {
                    return;
                }

                client.player.sendMessage(
                        Text.literal(
                                "§aRune Finder: Collecting §e" +
                                        rune +
                                        " §7× " +
                                        stack.getCount()
                        ),
                        true
                );

                client.interactionManager.clickSlot(
                        screen.getScreenHandler().syncId,
                        runeSlot.id,
                        0,
                        SlotActionType.QUICK_MOVE,
                        client.player
                );

                nextActionTime =
                        System.currentTimeMillis() + 150;
            }


            /*
             * =================================================
             * WALKING TO DESTINATION
             * =================================================
             */

            case WALKING_TO_DESTINATION -> {

                if (currentDestination == null) {

                    /*
                     * No destination selected.
                     *
                     * Go back to source processing.
                     */

                    finishCurrentSource(
                            client
                    );

                    return;
                }

                if (hasReachedPosition(
                        client,
                        currentDestination
                )) {

                    stopBaritone();

                    state =
                            State.OPENING_DESTINATION;

                    openAttempts = 0;

                    nextActionTime =
                            System.currentTimeMillis() + 300;

                    client.player.sendMessage(
                            Text.literal(
                                    "§aRune Finder: Reached destination."
                            ),
                            true
                    );
                }
            }


            /*
             * =================================================
             * OPENING DESTINATION
             * =================================================
             */

            case OPENING_DESTINATION -> {

                if (client.currentScreen
                        instanceof HandledScreen<?>) {

                    state =
                            State.DEPOSITING_DESTINATION;

                    nextActionTime =
                            System.currentTimeMillis() + 400;

                    return;
                }

                if (System.currentTimeMillis() <
                        nextActionTime) {

                    return;
                }

                if (openAttempts >= 5) {

                    client.player.sendMessage(
                            Text.literal(
                                    "§cRune Finder: Could not open destination chest."
                            ),
                            true
                    );

                    /*
                     * Don't go back to sources while we may still
                     * have destination runes.
                     */

                    currentDestination = null;

                    String nextDestination =
                            findNextInventoryDestination(
                                    client
                            );

                    if (nextDestination != null) {

                        currentDestination =
                                nextDestination;

                        state =
                                State.WALKING_TO_DESTINATION;

                        walkToDestination(
                                client
                        );

                    } else {

                        finishCurrentSource(
                                client
                        );
                    }

                    return;
                }

                openChest(
                        client,
                        currentDestination
                );

                openAttempts++;

                nextActionTime =
                        System.currentTimeMillis() + 600;
            }


            /*
             * =================================================
             * DEPOSITING DESTINATION
             * =================================================
             */

            case DEPOSITING_DESTINATION -> {

                if (!(client.currentScreen
                        instanceof HandledScreen<?> screen)) {

                    state =
                            State.OPENING_DESTINATION;

                    openAttempts = 0;

                    nextActionTime =
                            System.currentTimeMillis() + 300;

                    return;
                }

                if (System.currentTimeMillis() <
                        nextActionTime) {

                    return;
                }

                Slot runeSlot =
                        findInventoryRuneForDestination(
                                client,
                                screen,
                                currentDestination
                        );

                /*
                 * ------------------------------------------------
                 * DESTINATION FINISHED
                 * ------------------------------------------------
                 */

                if (runeSlot == null) {

                    client.player.sendMessage(
                            Text.literal(
                                    "§aRune Finder: Destination batch deposited."
                            ),
                            true
                    );

                    client.player.closeHandledScreen();

                    currentDestination = null;

                    nextActionTime =
                            System.currentTimeMillis() + 400;

                    /*
                     * VERY IMPORTANT:
                     *
                     * Before returning to sources, check whether
                     * ANY rune with a destination is still in the
                     * inventory.
                     */

                    String nextDestination =
                            findNextInventoryDestination(
                                    client
                            );

                    if (nextDestination != null) {

                        /*
                         * More destination runes remain.
                         *
                         * Stay in destination mode.
                         */

                        currentDestination =
                                nextDestination;

                        state =
                                State.WALKING_TO_DESTINATION;

                        walkToDestination(
                                client
                        );

                        return;
                    }

                    /*
                     * Inventory contains no more runes with
                     * configured destinations.
                     *
                     * NOW we are allowed to return to sources.
                     */

                    finishCurrentSource(
                            client
                    );

                    return;
                }

                ItemStack stack =
                        runeSlot.getStack();

                String rune =
                        RuneDetector
                                .getRuneType(stack)
                                .orElse(null);

                if (rune == null) {
                    return;
                }

                int beforeCount =
                        stack.getCount();

                client.player.sendMessage(
                        Text.literal(
                                "§aRune Finder: Depositing §e" +
                                        rune +
                                        " §7× " +
                                        beforeCount
                        ),
                        true
                );

                client.interactionManager.clickSlot(
                        screen.getScreenHandler().syncId,
                        runeSlot.id,
                        0,
                        SlotActionType.QUICK_MOVE,
                        client.player
                );

                nextActionTime =
                        System.currentTimeMillis() + 150;
            }


            /*
             * =================================================
             * RETURNING HOME
             * =================================================
             */

            case RETURNING_HOME -> {

                /*
                 * Wait a short moment so the server has time to
                 * process the instant /home teleport.
                 */

                if (System.currentTimeMillis() <
                        nextActionTime) {

                    return;
                }

                /*
                 * Start the next source sweep.
                 */

                startNextSourceSweep(
                        client
                );
            }


            /*
             * =================================================
             * IDLE
             * =================================================
             */

            case IDLE -> {
                // Nothing.
            }
        }
    }


    /*
     * ========================================================
     * FINISH CURRENT SOURCE
     * ========================================================
     *
     * This method is called when the CURRENT source is finished.
     *
     * It ALWAYS advances to the next source.
     *
     * It NEVER returns to the same source.
     */

    private static void finishCurrentSource(
            MinecraftClient client
    ) {

        client.player.closeHandledScreen();

        currentDestination = null;

        /*
         * Move to the NEXT source.
         */

        currentSourceIndex++;

        /*
         * --------------------------------------------------------
         * THERE ARE MORE SOURCES IN THIS SWEEP
         * --------------------------------------------------------
         */

        if (currentSourceIndex <
                RuneFinderClient.CONFIG
                        .getSourceChests()
                        .size()) {

            currentSourcePosition =
                    RuneFinderClient.CONFIG
                            .getSourceChests()
                            .get(currentSourceIndex);

            client.player.sendMessage(
                    Text.literal(
                            "§bRune Finder: Moving to next source §7" +
                                    (currentSourceIndex + 1)
                    ),
                    true
            );

            state =
                    State.WALKING_TO_SOURCE;

            walkToCurrentSource(
                    client
            );

            return;
        }

        /*
         * --------------------------------------------------------
         * COMPLETE SOURCE SWEEP
         * --------------------------------------------------------
         */

        client.player.sendMessage(
                Text.literal(
                        "§bRune Finder: Source sweep #" +
                                sourceSweepNumber +
                                " complete."
                ),
                true
        );

        /*
         * We have checked EVERY source exactly once.
         *
         * Now check inventory.
         */

        String destination =
                findNextInventoryDestination(
                        client
                );

        if (destination != null) {

            /*
             * There are still runes to deposit.
             *
             * DO NOT start another source sweep.
             */

            currentDestination =
                    destination;

            client.player.sendMessage(
                    Text.literal(
                            "§aRune Finder: Runes found in inventory — going to destinations."
                    ),
                    true
            );

            state =
                    State.WALKING_TO_DESTINATION;

            walkToDestination(
                    client
            );

            return;
        }

        /*
         * --------------------------------------------------------
         * NO DESTINATION RUNES
         * --------------------------------------------------------
         *
         * We completed a full source sweep and found nothing
         * in inventory that has a configured destination.
         *
         * Therefore there is nothing more to sort.
         */

        finishSorting(
                client
        );
    }


    /*
     * ========================================================
     * START NEXT SOURCE SWEEP
     * ========================================================
     */

    private static void startNextSourceSweep(
            MinecraftClient client
    ) {

        /*
         * Before starting a new source sweep, make absolutely
         * sure the inventory has no rune that still has a
         * configured destination.
         */

        String remainingDestination =
                findNextInventoryDestination(
                        client
                );

        if (remainingDestination != null) {

            /*
             * We still have something to deposit.
             *
             * Do NOT check sources yet.
             */

            currentDestination =
                    remainingDestination;

            state =
                    State.WALKING_TO_DESTINATION;

            walkToDestination(
                    client
            );

            return;
        }

        /*
         * --------------------------------------------------------
         * NEW SOURCE SWEEP
         * --------------------------------------------------------
         */

        sourceSweepNumber++;

        currentSourceIndex = 0;

        currentSourcePosition = null;

        currentDestination = null;

        openAttempts = 0;

        nextActionTime = 0;

        client.player.sendMessage(
                Text.literal(
                        "§bRune Finder: Starting source sweep #" +
                                sourceSweepNumber +
                                "."
                ),
                true
        );

        state =
                State.WALKING_TO_SOURCE;

        walkToCurrentSource(
                client
        );
    }


    /*
     * ========================================================
     * FIND MATCHING RUNE IN SOURCE
     * ========================================================
     */

    private static Slot findMatchingRuneSlot(
            MinecraftClient client,
            HandledScreen<?> screen
    ) {

        for (Slot slot :
                screen.getScreenHandler().slots) {

            /*
             * Ignore player inventory.
             */

            if (slot.inventory ==
                    client.player.getInventory()) {

                continue;
            }

            if (!slot.hasStack()) {
                continue;
            }

            ItemStack stack =
                    slot.getStack();

            String rune =
                    RuneDetector
                            .getRuneType(stack)
                            .orElse(null);

            if (rune == null) {
                continue;
            }

            /*
             * Only selected runes.
             */

            if (!RuneFinderClient.CONFIG
                    .isSelected(rune)) {

                continue;
            }

            String family =
                    getRuneFamily(rune);

            if (family == null) {
                continue;
            }

            /*
             * Only collect runes that actually have a
             * destination configured.
             */

            String destination =
                    RuneFinderClient.CONFIG
                            .getRuneDestination(
                                    family
                            );

            if (destination == null) {
                continue;
            }

            return slot;
        }

        return null;
    }


    /*
     * ========================================================
     * CAN INVENTORY ACCEPT STACK
     * ========================================================
     */

    private static boolean canFitRuneStack(
            MinecraftClient client,
            ItemStack stack
    ) {

        if (stack == null ||
                stack.isEmpty()) {

            return false;
        }

        String rune =
                RuneDetector
                        .getRuneType(stack)
                        .orElse(null);

        if (rune == null) {
            return false;
        }

        /*
         * 0-8  = hotbar
         * 9-35 = main inventory
         */

        for (int i = 0; i < 36; i++) {

            ItemStack inventoryStack =
                    client.player
                            .getInventory()
                            .getStack(i);

            /*
             * Empty slot.
             */

            if (inventoryStack.isEmpty()) {

                return true;
            }

            /*
             * Existing compatible rune stack.
             */

            String existingRune =
                    RuneDetector
                            .getRuneType(
                                    inventoryStack
                            )
                            .orElse(null);

            if (rune.equals(existingRune) &&
                    inventoryStack.getCount() <
                            inventoryStack.getMaxCount()) {

                return true;
            }
        }

        return false;
    }


    /*
     * ========================================================
     * FIND NEXT INVENTORY DESTINATION
     * ========================================================
     *
     * Returns the destination of ANY rune currently in the
     * player's inventory that is selected and configured.
     */

    private static String findNextInventoryDestination(
            MinecraftClient client
    ) {

        for (int i = 0; i < 36; i++) {

            ItemStack stack =
                    client.player
                            .getInventory()
                            .getStack(i);

            if (stack.isEmpty()) {
                continue;
            }

            String rune =
                    RuneDetector
                            .getRuneType(stack)
                            .orElse(null);

            if (rune == null) {
                continue;
            }

            if (!RuneFinderClient.CONFIG
                    .isSelected(rune)) {

                continue;
            }

            String family =
                    getRuneFamily(rune);

            if (family == null) {
                continue;
            }

            String destination =
                    RuneFinderClient.CONFIG
                            .getRuneDestination(
                                    family
                            );

            if (destination == null) {
                continue;
            }

            return destination;
        }

        return null;
    }


    /*
     * ========================================================
     * FIND INVENTORY RUNE FOR DESTINATION
     * ========================================================
     */

    private static Slot findInventoryRuneForDestination(
            MinecraftClient client,
            HandledScreen<?> screen,
            String destination
    ) {

        if (destination == null) {
            return null;
        }

        for (Slot slot :
                screen.getScreenHandler().slots) {

            /*
             * Only player inventory slots.
             */

            if (slot.inventory !=
                    client.player.getInventory()) {

                continue;
            }

            if (!slot.hasStack()) {
                continue;
            }

            ItemStack stack =
                    slot.getStack();

            String rune =
                    RuneDetector
                            .getRuneType(stack)
                            .orElse(null);

            if (rune == null) {
                continue;
            }

            if (!RuneFinderClient.CONFIG
                    .isSelected(rune)) {

                continue;
            }

            String family =
                    getRuneFamily(rune);

            if (family == null) {
                continue;
            }

            String runeDestination =
                    RuneFinderClient.CONFIG
                            .getRuneDestination(
                                    family
                            );

            if (runeDestination == null) {
                continue;
            }

            if (!destination.equals(
                    runeDestination
            )) {

                continue;
            }

            return slot;
        }

        return null;
    }


    /*
     * ========================================================
     * WALK TO CURRENT SOURCE
     * ========================================================
     */

    private static void walkToCurrentSource(
            MinecraftClient client
    ) {

        if (RuneFinderClient.CONFIG
                .getSourceChests()
                .isEmpty()) {

            finishSorting(
                    client
            );

            return;
        }

        if (currentSourceIndex >=
                RuneFinderClient.CONFIG
                        .getSourceChests()
                        .size()) {

            finishSorting(
                    client
            );

            return;
        }

        currentSourcePosition =
                RuneFinderClient.CONFIG
                        .getSourceChests()
                        .get(currentSourceIndex);

        state =
                State.WALKING_TO_SOURCE;

        walkToPosition(
                client,
                currentSourcePosition
        );
    }


    /*
     * ========================================================
     * WALK TO DESTINATION
     * ========================================================
     */

    private static void walkToDestination(
            MinecraftClient client
    ) {

        if (currentDestination == null) {
            return;
        }

        walkToPosition(
                client,
                currentDestination
        );
    }


    /*
     * ========================================================
     * WALK USING BARITONE
     * ========================================================
     */

    private static void walkToPosition(
            MinecraftClient client,
            String position
    ) {

        BlockPos pos =
                parsePosition(position);

        if (pos == null) {

            client.player.sendMessage(
                    Text.literal(
                            "§cRune Finder: Invalid position: " +
                                    position
                    ),
                    true
            );

            finishSorting(
                    client
            );

            return;
        }

        IBaritone baritone =
                BaritoneAPI.getProvider()
                        .getPrimaryBaritone();

        if (baritone == null) {

            client.player.sendMessage(
                    Text.literal(
                            "§cRune Finder: Baritone API unavailable."
                    ),
                    true
            );

            finishSorting(
                    client
            );

            return;
        }

        baritone.getCustomGoalProcess()
                .setGoalAndPath(
                        new GoalBlock(
                                pos.getX(),
                                pos.getY(),
                                pos.getZ()
                        )
                );

        client.player.sendMessage(
                Text.literal(
                        "§bRune Finder: Walking to §7" +
                                position
                ),
                true
        );
    }


    /*
     * ========================================================
     * OPEN CHEST
     * ========================================================
     */

    private static void openChest(
            MinecraftClient client,
            String position
    ) {

        BlockPos target =
                parsePosition(position);

        if (target == null) {
            return;
        }

        if (!isChest(
                client,
                target
        )) {

            client.player.sendMessage(
                    Text.literal(
                            "§cRune Finder: Position is not a chest: " +
                                    position
                    ),
                    true
            );

            return;
        }

        /*
         * Look at chest BEFORE creating BlockHitResult.
         */

        lookAtBlock(
                client.player,
                target
        );

        Vec3d hitPos =
                Vec3d.ofCenter(target);

        BlockHitResult hit =
                new BlockHitResult(
                        hitPos,
                        Direction.UP,
                        target,
                        false
                );

        if (client.interactionManager != null) {

            client.interactionManager.interactBlock(
                    client.player,
                    Hand.MAIN_HAND,
                    hit
            );
        }
    }


    /*
     * ========================================================
     * RETURN HOME
     * ========================================================
     */

    private static void returnHome(
            MinecraftClient client
    ) {

        String home =
                RuneFinderClient.CONFIG
                        .sorterHomeName;

        if (home == null ||
                home.isBlank()) {

            client.player.sendMessage(
                    Text.literal(
                            "§cRune Finder: Home name is empty."
                    ),
                    true
            );

            finishSorting(
                    client
            );

            return;
        }

        stopBaritone();

        client.player.sendMessage(
                Text.literal(
                        "§bRune Finder: Returning home → §f" +
                                home
                ),
                true
        );

        /*
         * Server command:
         *
         * /home <name>
         */

        client.player.networkHandler.sendChatCommand(
                "home " + home
        );

        state =
                State.RETURNING_HOME;

        nextActionTime =
                System.currentTimeMillis() + 500;
    }


    /*
     * ========================================================
     * FINISH SORTING
     * ========================================================
     */

    private static void finishSorting(
            MinecraftClient client
    ) {

        RuneFinderClient.CONFIG.sorterRunning = false;

        state = State.IDLE;

        currentSourceIndex = 0;

        sourceSweepNumber = 0;

        currentSourcePosition = null;

        currentDestination = null;

        openAttempts = 0;

        nextActionTime = 0;

        stopBaritone();

        if (client != null &&
                client.player != null) {

            client.player.sendMessage(
                    Text.literal(
                            "§aRune Finder: Sorting complete."
                    ),
                    true
            );
        }
    }


    /*
     * ========================================================
     * ARRIVAL CHECK
     * ========================================================
     */

    private static boolean hasReachedPosition(
            MinecraftClient client,
            String position
    ) {

        BlockPos target =
                parsePosition(position);

        if (target == null) {
            return false;
        }

        double distance =
                client.player
                        .getBlockPos()
                        .getSquaredDistance(
                                target
                        );

        return distance <= 4.0;
    }


    /*
     * ========================================================
     * LOOK AT BLOCK
     * ========================================================
     */

    private static void lookAtBlock(
            ClientPlayerEntity player,
            BlockPos pos
    ) {

        double x =
                pos.getX() + 0.5;

        double y =
                pos.getY() + 0.5;

        double z =
                pos.getZ() + 0.5;

        double dx =
                x - player.getX();

        double dy =
                y - (
                        player.getY() +
                                player.getStandingEyeHeight()
                );

        double dz =
                z - player.getZ();

        double horizontal =
                Math.sqrt(
                        dx * dx +
                                dz * dz
                );

        float yaw =
                (float) (
                        Math.toDegrees(
                                Math.atan2(
                                        dz,
                                        dx
                                )
                        ) - 90.0
                );

        float pitch =
                (float) (
                        -Math.toDegrees(
                                Math.atan2(
                                        dy,
                                        horizontal
                                )
                        )
                );

        player.setYaw(yaw);

        player.setPitch(pitch);
    }


    /*
     * ========================================================
     * CHECK CHEST
     * ========================================================
     */

    private static boolean isChest(
            MinecraftClient client,
            BlockPos pos
    ) {

        if (client.world == null) {
            return false;
        }

        Block block =
                client.world
                        .getBlockState(pos)
                        .getBlock();

        return block == Blocks.CHEST ||
                block == Blocks.TRAPPED_CHEST;
    }


    /*
     * ========================================================
     * PARSE POSITION
     * ========================================================
     */

    private static BlockPos parsePosition(
            String position
    ) {

        if (position == null ||
                position.isBlank()) {

            return null;
        }

        try {

            String[] parts =
                    position.split(",");

            if (parts.length != 3) {
                return null;
            }

            int x =
                    Integer.parseInt(
                            parts[0].trim()
                    );

            int y =
                    Integer.parseInt(
                            parts[1].trim()
                    );

            int z =
                    Integer.parseInt(
                            parts[2].trim()
                    );

            return new BlockPos(
                    x,
                    y,
                    z
            );

        } catch (NumberFormatException e) {

            return null;
        }
    }


    /*
     * ========================================================
     * RUNE FAMILY
     * ========================================================
     */

    private static String getRuneFamily(
            String rune
    ) {

        if (rune == null ||
                rune.isBlank()) {

            return null;
        }

        int separator =
                rune.indexOf(':');

        if (separator <= 0) {
            return rune;
        }

        return rune.substring(
                0,
                separator
        );
    }


    /*
     * ========================================================
     * STOP BARITONE
     * ========================================================
     */

    private static void stopBaritone() {

        IBaritone baritone =
                BaritoneAPI.getProvider()
                        .getPrimaryBaritone();

        if (baritone != null) {

            baritone.getCustomGoalProcess()
                    .onLostControl();
        }
    }
}

