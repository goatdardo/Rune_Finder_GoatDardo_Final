
        package com.goatdardo.runefinder;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class SorterPreviewKeyHandler {

    public static void handleKey(
            MinecraftClient client,
            int key,
            int action
    ) {

        if (key != GLFW.GLFW_KEY_P) {
            return;
        }

        if (action != GLFW.GLFW_PRESS) {
            return;
        }

        RuneSorterPreview.previewCurrentChest(client);
    }
}
