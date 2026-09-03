
        package com.goatdardo.runefinder;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class RuneFinderClient implements ClientModInitializer {

    public static RuneConfig CONFIG;

    @Override
    public void onInitializeClient() {

        CONFIG = RuneConfig.load();

        RuneKeys.register();

        ClientTickEvents.END_CLIENT_TICK.register(
                RuneFinderClient::onClientTick
        );
    }

    private static void onClientTick(
            MinecraftClient client
    ) {

        if (client == null) {
            return;
        }

        /*
         * ------------------------------------------------
         * STOP / START SORTER
         * ------------------------------------------------
         *
         * K is now a toggle:
         *
         * K while stopped  -> START
         * K while running  -> STOP
         */
        if (RuneKeys.sorterStartKey != null &&
                RuneKeys.sorterStartKey.wasPressed()) {

            if (CONFIG.sorterRunning) {

                RuneSorterManager.stop(client);

            } else {

                RuneSorterManager.start(client);
            }
        }

        /*
         * ------------------------------------------------
         * AUTO PICKUP
         * ------------------------------------------------
         */

        if (RuneKeys.autoPickupKey != null &&
                RuneKeys.autoPickupKey.wasPressed()) {

            CONFIG.autoPickupEnabled =
                    !CONFIG.autoPickupEnabled;

            CONFIG.save();

            if (client.player != null) {

                client.player.sendMessage(
                        Text.literal(
                                CONFIG.autoPickupEnabled
                                        ? "§aRune Finder: Auto pickup ON"
                                        : "§cRune Finder: Auto pickup OFF"
                        ),
                        true
                );
            }
        }

        /*
         * ------------------------------------------------
         * CLEAR SELECTED RUNES
         * ------------------------------------------------
         */

        if (RuneKeys.clearRunesKey != null &&
                RuneKeys.clearRunesKey.wasPressed()) {

            CONFIG.clearRunes();

            if (client.player != null) {

                client.player.sendMessage(
                        Text.literal(
                                "§eRune Finder: Selected runes cleared."
                        ),
                        true
                );
            }
        }

        /*
         * ------------------------------------------------
         * TEST FIRST RUNE
         * ------------------------------------------------
         */

        if (RuneKeys.sorterPreviewKey != null &&
                RuneKeys.sorterPreviewKey.wasPressed()) {

            RuneSorterTest.testFirstRune(client);
        }

        /*
         * ------------------------------------------------
         * BARITONE TEST
         * ------------------------------------------------
         */

        if (RuneKeys.baritoneTestKey != null &&
                RuneKeys.baritoneTestKey.wasPressed()) {

            BaritoneTest.goToTestLocation(client);
        }

        /*
         * ------------------------------------------------
         * RUNE AUTO PICKUP
         * ------------------------------------------------
         */

        RuneAutoPickup.tick(client);

        /*
         * ------------------------------------------------
         * SOURCE CHESTS
         * ------------------------------------------------
         */

        SourceChestManager.tick(client);

        /*
         * ------------------------------------------------
         * DESTINATION CHESTS
         * ------------------------------------------------
         */

        DestinationChestManager.tick(client);

        /*
         * ------------------------------------------------
         * SORTER
         * ------------------------------------------------
         */

        RuneSorterManager.tick(client);
    }
}

