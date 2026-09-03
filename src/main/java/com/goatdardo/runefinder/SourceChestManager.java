
        package com.goatdardo.runefinder;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class SourceChestManager {

    public static void tick(MinecraftClient client) {

        if (RuneKeys.sourceChestKey == null) {
            return;
        }

        if (!RuneKeys.sourceChestKey.wasPressed()) {
            return;
        }

        if (client.player == null ||
                client.world == null) {
            return;
        }

        if (!(client.crosshairTarget instanceof BlockHitResult hit)) {

            client.player.sendMessage(
                    Text.literal(
                            "Rune Finder: Look directly at a chest."
                    ),
                    true
            );

            return;
        }

        BlockPos pos = hit.getBlockPos();

        Block block =
                client.world.getBlockState(pos).getBlock();

        // Make sure the block is actually a chest.
        if (block != Blocks.CHEST &&
                block != Blocks.TRAPPED_CHEST) {

            client.player.sendMessage(
                    Text.literal(
                            "Rune Finder: That block is not a chest."
                    ),
                    true
            );

            return;
        }

        /*
         * Convert a double chest into one canonical
         * coordinate so both halves count as the
         * same chest.
         */
        pos = ChestPositionHelper.getCanonicalPosition(
                client,
                pos
        );

        String position =
                pos.getX() +
                        "," +
                        pos.getY() +
                        "," +
                        pos.getZ();

        /*
         * Do not allow a destination chest
         * to also be a source chest.
         */
        if (RuneFinderClient.CONFIG
                .getDestinationChests()
                .contains(position)) {

            client.player.sendMessage(
                    Text.literal(
                            "§cRune Finder: This chest is already selected as a DESTINATION."
                    ),
                    true
            );

            return;
        }

        if (RuneFinderClient.CONFIG
                .getSourceChests()
                .contains(position)) {

            RuneFinderClient.CONFIG
                    .removeSourceChest(position);

            client.player.sendMessage(
                    Text.literal(
                            "§cSOURCE CHEST REMOVED §7" +
                                    position
                    ),
                    true
            );

        } else {

            RuneFinderClient.CONFIG
                    .addSourceChest(position);

            client.player.sendMessage(
                    Text.literal(
                            "§aSOURCE CHEST ADDED §7" +
                                    position
                    ),
                    true
            );
        }
    }
}
