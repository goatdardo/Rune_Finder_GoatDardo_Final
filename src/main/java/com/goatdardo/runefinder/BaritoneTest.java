package com.goatdardo.runefinder;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class BaritoneTest {

    public static void goToTestLocation(
            MinecraftClient client
    ) {

        if (client == null ||
                client.player == null) {
            return;
        }

        IBaritone baritone =
                BaritoneAPI.getProvider()
                        .getPrimaryBaritone();

        if (baritone == null) {

            client.player.sendMessage(
                    Text.literal(
                            "§cRune Finder: Baritone API not found."
                    ),
                    false
            );

            return;
        }

        int x = 100;
        int y = 70;
        int z = 200;

        baritone.getCustomGoalProcess()
                .setGoalAndPath(
                        new GoalBlock(x, y, z)
                );

        client.player.sendMessage(
                Text.literal(
                        "§aRune Finder: Baritone API → going to §b" +
                                x + " " +
                                y + " " +
                                z
                ),
                false
        );
    }
}