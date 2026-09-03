
        package com.goatdardo.runefinder;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class DestinationChestManager {

    public static void tick(MinecraftClient client) {

        if (RuneKeys.destinationChestKey == null) {
            return;
        }

        if (!RuneKeys.destinationChestKey.wasPressed()) {
            return;
        }

        if (client.player == null ||
                client.world == null) {
            return;
        }

        // ============================================================
        // CHECK WHAT PLAYER IS LOOKING AT
        // ============================================================

        if (!(client.crosshairTarget instanceof BlockHitResult hit)) {

            client.player.sendMessage(
                    Text.literal(
                            "§cRune Finder: Look directly at a chest."
                    ),
                    true
            );

            return;
        }

        BlockPos pos =
                hit.getBlockPos();

        Block block =
                client.world
                        .getBlockState(pos)
                        .getBlock();

        // ============================================================
        // CHECK CHEST
        // ============================================================

        if (block != Blocks.CHEST &&
                block != Blocks.TRAPPED_CHEST) {

            client.player.sendMessage(
                    Text.literal(
                            "§cRune Finder: That block is not a chest."
                    ),
                    true
            );

            return;
        }

        // ============================================================
        // CONVERT DOUBLE CHEST TO CANONICAL POSITION
        // ============================================================

        pos = ChestPositionHelper.getCanonicalPosition(
                client,
                pos
        );

        // ============================================================
        // CREATE COORDINATE STRING
        // ============================================================

        String position =
                pos.getX() + "," +
                        pos.getY() + "," +
                        pos.getZ();

        // ============================================================
        // DO NOT ALLOW SOURCE + DESTINATION
        // ============================================================

        if (RuneFinderClient.CONFIG
                .getSourceChests()
                .contains(position)) {

            client.player.sendMessage(
                    Text.literal(
                            "§cRune Finder: This chest is already selected as a SOURCE."
                    ),
                    true
            );

            return;
        }

        // ============================================================
        // REMOVE IF ALREADY REGISTERED
        // ============================================================

        if (RuneFinderClient.CONFIG
                .getDestinationChests()
                .contains(position)) {

            RuneFinderClient.CONFIG
                    .removeDestinationChest(
                            position
                    );

            client.player.sendMessage(
                    Text.literal(
                            "§cDESTINATION CHEST REMOVED §7" +
                                    position
                    ),
                    true
            );

            return;
        }

        // ============================================================
        // ADD DESTINATION CHEST
        // ============================================================

        RuneFinderClient.CONFIG
                .addDestinationChest(
                        position
                );

        client.player.sendMessage(
                Text.literal(
                        "§bDESTINATION CHEST ADDED §7" +
                                position
                ),
                true
        );
    }
}

