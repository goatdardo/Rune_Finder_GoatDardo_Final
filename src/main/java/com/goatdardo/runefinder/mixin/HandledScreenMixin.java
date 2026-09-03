
        package com.goatdardo.runefinder.mixin;

import com.goatdardo.runefinder.RuneDetector;
import com.goatdardo.runefinder.RuneFinderClient;
import com.goatdardo.runefinder.RuneSelection;
import com.goatdardo.runefinder.RuneSorterTest;
import com.goatdardo.runefinder.RuneSorterDepositTest;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.slot.Slot;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Inject(
            method = "mouseClicked",
            at = @At("HEAD"),
            cancellable = true
    )
    private void runefinder$selectRune(
            Click click,
            boolean doubleClick,
            CallbackInfoReturnable<Boolean> cir
    ) {
        MinecraftClient client =
                MinecraftClient.getInstance();

        if (!InputUtil.isKeyPressed(
                client.getWindow(),
                GLFW.GLFW_KEY_RIGHT_SHIFT
        )) {
            return;
        }

        HandledScreen<?> screen =
                (HandledScreen<?>) (Object) this;

        Slot slot =
                ((HandledScreenAccessor) screen)
                        .runefinder$getSlotAt(
                                click.x(),
                                click.y()
                        );

        if (slot == null) {
            return;
        }

        RuneSelection.selectSlot(slot);

        cir.setReturnValue(true);
    }

    /*
     * P = temporary sorter pickup test.
     *
     * Minecraft 1.21.11 uses KeyInput here.
     */
    @Inject(
            method = "keyPressed",
            at = @At("HEAD"),
            cancellable = true
    )
    private void runefinder$sorterPreview(
            KeyInput input,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (input.key() != GLFW.GLFW_KEY_P) {
            return;
        }

        if (RuneFinderClient.CONFIG == null) {
            return;
        }

        RuneSorterTest.testFirstRune(
                MinecraftClient.getInstance()
        );

        cir.setReturnValue(true);
    }

    /*
     * O = temporary sorter deposit test.
     *
     * This will be used only while a chest GUI is open.
     */
    @Inject(
            method = "keyPressed",
            at = @At("HEAD"),
            cancellable = true
    )
    private void runefinder$sorterDeposit(
            KeyInput input,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (input.key() != GLFW.GLFW_KEY_O) {
            return;
        }

        if (RuneFinderClient.CONFIG == null) {
            return;
        }

        RuneSorterDepositTest.depositCursorRune(
                MinecraftClient.getInstance()
        );

        cir.setReturnValue(true);
    }

    @Inject(
            method = "drawSlot",
            at = @At("TAIL")
    )
    private void runefinder$highlightRune(
            DrawContext context,
            Slot slot,
            int mouseX,
            int mouseY,
            CallbackInfo ci
    ) {
        if (RuneFinderClient.CONFIG == null) {
            return;
        }

        if (!RuneFinderClient.CONFIG.highlightEnabled) {
            return;
        }

        RuneDetector.getRuneType(
                slot.getStack()
        ).ifPresent(rune -> {

            if (!RuneFinderClient.CONFIG.isSelected(rune)) {
                return;
            }

            int alpha = Math.round(
                    RuneFinderClient.CONFIG.highlightOpacity
                            * 255.0f
            );

            int color =
                    (alpha << 24)
                            | (RuneFinderClient.CONFIG.highlightColor
                            & 0xFFFFFF);

            int x = slot.x;
            int y = slot.y;

            context.fill(
                    x,
                    y,
                    x + 18,
                    y + 1,
                    color
            );

            context.fill(
                    x,
                    y + 17,
                    x + 18,
                    y + 18,
                    color
            );

            context.fill(
                    x,
                    y,
                    x + 1,
                    y + 18,
                    color
            );

            context.fill(
                    x + 17,
                    y,
                    x + 18,
                    y + 18,
                    color
            );
        });
    }
}

