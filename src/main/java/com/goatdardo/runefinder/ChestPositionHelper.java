
        package com.goatdardo.runefinder;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ChestPositionHelper {

    /**
     * Returns one consistent coordinate for a chest.
     *
     * SINGLE:
     *     Uses the chest's own coordinate.
     *
     * LEFT / RIGHT:
     *     Uses Minecraft's chest type and facing direction
     *     to find the other half and always returns the
     *     LEFT half as the canonical coordinate.
     */
    public static BlockPos getCanonicalPosition(
            MinecraftClient client,
            BlockPos pos
    ) {

        if (client == null ||
                client.world == null ||
                pos == null) {

            return pos;
        }

        BlockState state =
                client.world.getBlockState(pos);

        if (!(state.getBlock() instanceof ChestBlock)) {
            return pos;
        }

        ChestType type =
                state.get(ChestBlock.CHEST_TYPE);

        // ------------------------------------------------------------
        // SINGLE CHEST
        // ------------------------------------------------------------

        if (type == ChestType.SINGLE) {
            return pos;
        }

        // ------------------------------------------------------------
        // DOUBLE CHEST
        // ------------------------------------------------------------

        Direction facing =
                state.get(ChestBlock.FACING);

        /*
         * Minecraft defines LEFT and RIGHT relative
         * to the direction the chest is facing.
         *
         * Find the position of the LEFT half.
         */
        if (type == ChestType.LEFT) {
            return pos;
        }

        Direction leftDirection =
                facing.rotateYCounterclockwise();

        return pos.offset(leftDirection);
    }
}
