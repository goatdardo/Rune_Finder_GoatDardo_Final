
        package com.goatdardo.runefinder;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class RuneSettingsScreen extends Screen {

    private final Screen parent;

    private double pageScroll = 0;
    private int runeScroll = 0;

    private final List<ButtonWidget> runeButtons =
            new ArrayList<>();

    private ButtonWidget runeFinderTab;
    private ButtonWidget autoSorterTab;

    private ButtonWidget highlightButton;
    private ButtonWidget autoPickupButton;
    private ButtonWidget delayMinusButton;
    private ButtonWidget delayValueButton;
    private ButtonWidget delayPlusButton;
    private ButtonWidget sortRarityButton;
    private ButtonWidget clearAllButton;
    private ButtonWidget addRuneButton;

    private ButtonWidget rangeRuneButton;
    private ButtonWidget addRangeButton;

    private ButtonWidget runeUpButton;
    private ButtonWidget runeDownButton;
    private ButtonWidget doneButton;

    private TextFieldWidget runeInput;
    private TextFieldWidget fromInput;
    private TextFieldWidget toInput;

    private int rangeRuneIndex = 0;

    private static final int CENTER_WIDTH = 200;

    private static final int TAB_Y = 10;
    private static final int TAB_WIDTH = 100;
    private static final int TAB_HEIGHT = 20;

    private static final int HIGHLIGHT_Y = 50;
    private static final int AUTO_PICKUP_Y = 80;
    private static final int DELAY_Y = 110;
    private static final int SORT_RARITY_Y = 140;

    private static final int CLEAR_ALL_Y = 195;

    private static final int TITLE_Y = 225;

    private static final int PANEL_TOP = 250;
    private static final int PANEL_BOTTOM = 360;

    private static final int ADD_RUNE_Y = 385;

    private static final int RANGE_TITLE_Y = 420;
    private static final int RANGE_RUNE_Y = 445;
    private static final int RANGE_NUMBER_Y = 470;
    private static final int RANGE_BUTTON_Y = 495;

    private static final int DONE_Y = 535;

    private static final int ROW_HEIGHT = 25;

    private RuneSettingsScreen(Screen parent) {

        super(
                Text.literal(
                        "Rune Finder GoatDardo"
                )
        );

        this.parent = parent;
    }

    public static RuneSettingsScreen create(
            Screen parent
    ) {

        return new RuneSettingsScreen(
                parent
        );
    }

    @Override
    protected void init() {

        int centerX =
                this.width / 2;

        runeButtons.clear();

        /*
         * ============================================================
         * RUNE FINDER TAB
         * ============================================================
         */

        runeFinderTab =
                ButtonWidget.builder(
                        Text.literal(
                                "Rune Finder"
                        ),
                        button -> {
                        }
                ).dimensions(
                        10,
                        TAB_Y,
                        TAB_WIDTH,
                        TAB_HEIGHT
                ).build();

        runeFinderTab.active =
                false;

        this.addDrawableChild(
                runeFinderTab
        );

        /*
         * ============================================================
         * AUTO SORTER TAB
         * ============================================================
         */

        autoSorterTab =
                ButtonWidget.builder(
                        Text.literal(
                                "Auto Sorter"
                        ),
                        button -> {

                            if (this.client != null) {

                                this.client.setScreen(
                                        AutoSorterScreen.create(
                                                this
                                        )
                                );
                            }
                        }
                ).dimensions(
                        115,
                        TAB_Y,
                        TAB_WIDTH,
                        TAB_HEIGHT
                ).build();

        this.addDrawableChild(
                autoSorterTab
        );

        /*
         * ============================================================
         * HIGHLIGHT
         * ============================================================
         */

        highlightButton =
                ButtonWidget.builder(
                        Text.literal(
                                "Highlight: " +
                                        (
                                                RuneFinderClient
                                                        .CONFIG
                                                        .highlightEnabled
                                                        ? "ON"
                                                        : "OFF"
                                        )
                        ),
                        button -> {

                            RuneFinderClient
                                    .CONFIG
                                    .highlightEnabled =
                                    !RuneFinderClient
                                            .CONFIG
                                            .highlightEnabled;

                            RuneFinderClient
                                    .CONFIG
                                    .save();

                            button.setMessage(
                                    Text.literal(
                                            "Highlight: " +
                                                    (
                                                            RuneFinderClient
                                                                    .CONFIG
                                                                    .highlightEnabled
                                                                    ? "ON"
                                                                    : "OFF"
                                                    )
                                    )
                            );
                        }
                ).dimensions(
                        centerX - 100,
                        HIGHLIGHT_Y,
                        CENTER_WIDTH,
                        20
                ).build();

        this.addDrawableChild(
                highlightButton
        );

        /*
         * ============================================================
         * AUTO PICKUP
         * ============================================================
         */

        autoPickupButton =
                ButtonWidget.builder(
                        Text.literal(
                                "Auto Pickup: " +
                                        (
                                                RuneFinderClient
                                                        .CONFIG
                                                        .autoPickupEnabled
                                                        ? "ON"
                                                        : "OFF"
                                        )
                        ),
                        button -> {

                            RuneFinderClient
                                    .CONFIG
                                    .autoPickupEnabled =
                                    !RuneFinderClient
                                            .CONFIG
                                            .autoPickupEnabled;

                            RuneFinderClient
                                    .CONFIG
                                    .save();

                            button.setMessage(
                                    Text.literal(
                                            "Auto Pickup: " +
                                                    (
                                                            RuneFinderClient
                                                                    .CONFIG
                                                                    .autoPickupEnabled
                                                                    ? "ON"
                                                                    : "OFF"
                                                    )
                                    )
                            );
                        }
                ).dimensions(
                        centerX - 100,
                        AUTO_PICKUP_Y,
                        CENTER_WIDTH,
                        20
                ).build();

        this.addDrawableChild(
                autoPickupButton
        );

        /*
         * ============================================================
         * DELAY -
         * ============================================================
         */

        delayMinusButton =
                ButtonWidget.builder(
                        Text.literal("-"),
                        button -> {

                            RuneFinderClient
                                    .CONFIG
                                    .pickupDelay -= 50;

                            if (RuneFinderClient
                                    .CONFIG
                                    .pickupDelay < 50) {

                                RuneFinderClient
                                        .CONFIG
                                        .pickupDelay = 50;
                            }

                            RuneFinderClient
                                    .CONFIG
                                    .save();

                            this.clearAndInit();
                        }
                ).dimensions(
                        centerX - 100,
                        DELAY_Y,
                        40,
                        20
                ).build();

        this.addDrawableChild(
                delayMinusButton
        );

        /*
         * ============================================================
         * DELAY VALUE
         * ============================================================
         */

        delayValueButton =
                ButtonWidget.builder(
                        Text.literal(
                                RuneFinderClient
                                        .CONFIG
                                        .pickupDelay +
                                        " ms"
                        ),
                        button -> {
                        }
                ).dimensions(
                        centerX - 55,
                        DELAY_Y,
                        110,
                        20
                ).build();

        this.addDrawableChild(
                delayValueButton
        );

        /*
         * ============================================================
         * DELAY +
         * ============================================================
         */

        delayPlusButton =
                ButtonWidget.builder(
                        Text.literal("+"),
                        button -> {

                            RuneFinderClient
                                    .CONFIG
                                    .pickupDelay += 50;

                            if (RuneFinderClient
                                    .CONFIG
                                    .pickupDelay > 1000) {

                                RuneFinderClient
                                        .CONFIG
                                        .pickupDelay = 1000;
                            }

                            RuneFinderClient
                                    .CONFIG
                                    .save();

                            this.clearAndInit();
                        }
                ).dimensions(
                        centerX + 60,
                        DELAY_Y,
                        40,
                        20
                ).build();

        this.addDrawableChild(
                delayPlusButton
        );

        /*
         * ============================================================
         * SORT RARITY
         * ============================================================
         */

        sortRarityButton =
                ButtonWidget.builder(
                        Text.literal(
                                "Sort Rarity: " +
                                        RuneFinderClient
                                                .CONFIG
                                                .sortRarity
                        ),
                        button -> {

                            String current =
                                    RuneFinderClient
                                            .CONFIG
                                            .sortRarity;

                            String next;

                            switch (current) {

                                case "Common" ->
                                        next = "Rare";

                                case "Rare" ->
                                        next = "Legendary";

                                case "Legendary" ->
                                        next = "Mythical";

                                case "Mythical" ->
                                        next = "Dojo";

                                default ->
                                        next = "Common";
                            }

                            RuneFinderClient
                                    .CONFIG
                                    .sortRarity =
                                    next;

                            RuneFinderClient
                                    .CONFIG
                                    .save();

                            button.setMessage(
                                    Text.literal(
                                            "Sort Rarity: " +
                                                    next
                                    )
                            );
                        }
                ).dimensions(
                        centerX - 100,
                        SORT_RARITY_Y,
                        CENTER_WIDTH,
                        20
                ).build();

        this.addDrawableChild(
                sortRarityButton
        );

        /*
         * ============================================================
         * CLEAR ALL
         * ============================================================
         */

        clearAllButton =
                ButtonWidget.builder(
                        Text.literal(
                                "Clear All"
                        ),
                        button -> {

                            RuneFinderClient
                                    .CONFIG
                                    .clearRunes();

                            runeScroll = 0;
                            pageScroll = 0;
                            rangeRuneIndex = 0;

                            this.clearAndInit();
                        }
                ).dimensions(
                        centerX - 100,
                        CLEAR_ALL_Y,
                        CENTER_WIDTH,
                        20
                ).build();

        this.addDrawableChild(
                clearAllButton
        );

        /*
         * ============================================================
         * RUNE PANEL UP
         * ============================================================
         */

        runeUpButton =
                ButtonWidget.builder(
                        Text.literal("▲"),
                        button -> {

                            runeScroll -=
                                    ROW_HEIGHT;

                            if (runeScroll < 0) {
                                runeScroll = 0;
                            }

                            updateRunePositions();
                        }
                ).dimensions(
                        centerX + 105,
                        PANEL_TOP,
                        20,
                        20
                ).build();

        this.addDrawableChild(
                runeUpButton
        );

        /*
         * ============================================================
         * RUNE PANEL DOWN
         * ============================================================
         */

        runeDownButton =
                ButtonWidget.builder(
                        Text.literal("▼"),
                        button -> {

                            runeScroll +=
                                    ROW_HEIGHT;

                            if (runeScroll >
                                    getMaxRuneScroll()) {

                                runeScroll =
                                        getMaxRuneScroll();
                            }

                            updateRunePositions();
                        }
                ).dimensions(
                        centerX + 105,
                        PANEL_BOTTOM - 20,
                        20,
                        20
                ).build();

        this.addDrawableChild(
                runeDownButton
        );

        /*
         * ============================================================
         * SELECTED RUNES
         * ============================================================
         */

        createRuneButtons();

        /*
         * ============================================================
         * ADD RUNE
         * ============================================================
         */

        runeInput =
                new TextFieldWidget(
                        this.textRenderer,
                        centerX - 100,
                        ADD_RUNE_Y,
                        140,
                        20,
                        Text.literal("Rune")
                );

        runeInput.setMaxLength(100);

        runeInput.setPlaceholder(
                Text.literal(
                        "e.g. Hitstreak:1"
                )
        );

        this.addDrawableChild(
                runeInput
        );

        addRuneButton =
                ButtonWidget.builder(
                        Text.literal("Add"),
                        button ->
                                addManualRune()
                ).dimensions(
                        centerX + 45,
                        ADD_RUNE_Y,
                        55,
                        20
                ).build();

        this.addDrawableChild(
                addRuneButton
        );

        /*
         * ============================================================
         * ADD RUNE RANGE
         * ============================================================
         */

        List<String> selectedRunes =
                RuneFinderClient.CONFIG
                        .getSelectedRunes();

        if (rangeRuneIndex >=
                selectedRunes.size()) {

            rangeRuneIndex = 0;
        }

        String rangeRuneText =
                selectedRunes.isEmpty()
                        ? "No selected runes"
                        : selectedRunes.get(
                        rangeRuneIndex
                );

        rangeRuneButton =
                ButtonWidget.builder(
                        Text.literal(
                                "Rune: " +
                                        rangeRuneText
                        ),
                        button -> {

                            List<String> runes =
                                    RuneFinderClient
                                            .CONFIG
                                            .getSelectedRunes();

                            if (runes.isEmpty()) {
                                return;
                            }

                            rangeRuneIndex++;

                            if (rangeRuneIndex >=
                                    runes.size()) {

                                rangeRuneIndex = 0;
                            }

                            button.setMessage(
                                    Text.literal(
                                            "Rune: " +
                                                    runes.get(
                                                            rangeRuneIndex
                                                    )
                                    )
                            );
                        }
                ).dimensions(
                        centerX - 100,
                        RANGE_RUNE_Y,
                        CENTER_WIDTH,
                        20
                ).build();

        rangeRuneButton.active =
                !selectedRunes.isEmpty();

        this.addDrawableChild(
                rangeRuneButton
        );

        /*
         * ============================================================
         * FROM
         * ============================================================
         */

        fromInput =
                new TextFieldWidget(
                        this.textRenderer,
                        centerX - 100,
                        RANGE_NUMBER_Y,
                        95,
                        20,
                        Text.literal("From")
                );

        fromInput.setMaxLength(6);

        fromInput.setPlaceholder(
                Text.literal("From")
        );

        this.addDrawableChild(
                fromInput
        );

        /*
         * ============================================================
         * TO
         * ============================================================
         */

        toInput =
                new TextFieldWidget(
                        this.textRenderer,
                        centerX + 5,
                        RANGE_NUMBER_Y,
                        95,
                        20,
                        Text.literal("To")
                );

        toInput.setMaxLength(6);

        toInput.setPlaceholder(
                Text.literal("To")
        );

        this.addDrawableChild(
                toInput
        );

        /*
         * ============================================================
         * ADD RANGE
         * ============================================================
         */

        addRangeButton =
                ButtonWidget.builder(
                        Text.literal(
                                "Add Range"
                        ),
                        button ->
                                addRuneRange()
                ).dimensions(
                        centerX - 100,
                        RANGE_BUTTON_Y,
                        CENTER_WIDTH,
                        20
                ).build();

        addRangeButton.active =
                !selectedRunes.isEmpty();

        this.addDrawableChild(
                addRangeButton
        );

        /*
         * ============================================================
         * DONE
         * ============================================================
         */

        doneButton =
                ButtonWidget.builder(
                        Text.literal("Done"),
                        button -> {

                            if (this.client != null) {

                                this.client.setScreen(
                                        parent
                                );
                            }
                        }
                ).dimensions(
                        centerX - 100,
                        DONE_Y,
                        CENTER_WIDTH,
                        20
                ).build();

        this.addDrawableChild(
                doneButton
        );

        updateAllPositions();
    }

    /*
     * ============================================================
     * ADD MANUAL RUNE
     * ============================================================
     */

    private void addManualRune() {

        String rune =
                runeInput.getText().trim();

        if (rune.isEmpty()) {
            return;
        }

        RuneFinderClient.CONFIG
                .addRune(rune);

        runeInput.setText("");

        runeScroll = 0;

        this.clearAndInit();
    }

    /*
     * ============================================================
     * ADD RUNE RANGE
     * ============================================================
     */

    private void addRuneRange() {

        List<String> selectedRunes =
                RuneFinderClient.CONFIG
                        .getSelectedRunes();

        if (selectedRunes.isEmpty()) {
            return;
        }

        if (rangeRuneIndex >=
                selectedRunes.size()) {

            rangeRuneIndex = 0;
        }

        String baseRune =
                selectedRunes.get(
                        rangeRuneIndex
                );

        String fromText =
                fromInput.getText().trim();

        String toText =
                toInput.getText().trim();

        if (fromText.isEmpty() ||
                toText.isEmpty()) {

            return;
        }

        try {

            int from =
                    Integer.parseInt(
                            fromText
                    );

            int to =
                    Integer.parseInt(
                            toText
                    );

            if (from > to) {
                return;
            }

            int colonIndex =
                    baseRune.lastIndexOf(':');

            if (colonIndex == -1) {
                return;
            }

            String prefix =
                    baseRune.substring(
                            0,
                            colonIndex + 1
                    );

            long amount =
                    (long) to -
                            from +
                            1;

            if (amount > 1000) {
                return;
            }

            for (int number = from;
                 number <= to;
                 number++) {

                String rune =
                        prefix +
                                number;

                RuneFinderClient.CONFIG
                        .addRune(rune);
            }

            fromInput.setText("");
            toInput.setText("");

            runeScroll = 0;

            this.clearAndInit();

        } catch (NumberFormatException ignored) {
        }
    }

    /*
     * ============================================================
     * CREATE RUNE BUTTONS
     * ============================================================
     */

    private void createRuneButtons() {

        int centerX =
                this.width / 2;

        List<String> runes =
                RuneFinderClient.CONFIG
                        .getSelectedRunes();

        for (String rune : runes) {

            ButtonWidget button =
                    ButtonWidget.builder(
                            Text.literal(
                                    rune +
                                            "  [Remove]"
                            ),
                            pressed -> {

                                RuneFinderClient.CONFIG
                                        .toggleRune(rune);

                                if (runeScroll >
                                        getMaxRuneScroll()) {

                                    runeScroll =
                                            getMaxRuneScroll();
                                }

                                List<String> currentRunes =
                                        RuneFinderClient
                                                .CONFIG
                                                .getSelectedRunes();

                                if (rangeRuneIndex >=
                                        currentRunes.size()) {

                                    rangeRuneIndex = 0;
                                }

                                this.clearAndInit();
                            }
                    ).dimensions(
                            centerX - 100,
                            0,
                            CENTER_WIDTH,
                            20
                    ).build();

            runeButtons.add(button);

            this.addDrawableChild(
                    button
            );
        }
    }

    private int getMaxRuneScroll() {

        int runeCount =
                RuneFinderClient.CONFIG
                        .getSelectedRunes()
                        .size();

        int visibleRows =
                Math.max(
                        1,
                        (
                                PANEL_BOTTOM -
                                        PANEL_TOP -
                                        30
                        ) /
                                ROW_HEIGHT
                );

        return Math.max(
                0,
                (
                        runeCount -
                                visibleRows
                ) *
                        ROW_HEIGHT
        );
    }

    private int getContentHeight() {

        int runeCount =
                RuneFinderClient.CONFIG
                        .getSelectedRunes()
                        .size();

        return Math.max(
                DONE_Y + 30,
                575 +
                        Math.max(
                                0,
                                runeCount - 4
                        ) *
                                ROW_HEIGHT
        );
    }

    private int getMaxPageScroll() {

        int visibleHeight =
                this.height - 20;

        return Math.max(
                0,
                getContentHeight() -
                        visibleHeight
        );
    }

    private void updateAllPositions() {

        int scroll =
                (int) pageScroll;

        highlightButton.setY(
                HIGHLIGHT_Y - scroll
        );

        autoPickupButton.setY(
                AUTO_PICKUP_Y - scroll
        );

        delayMinusButton.setY(
                DELAY_Y - scroll
        );

        delayValueButton.setY(
                DELAY_Y - scroll
        );

        delayPlusButton.setY(
                DELAY_Y - scroll
        );

        sortRarityButton.setY(
                SORT_RARITY_Y - scroll
        );

        clearAllButton.setY(
                CLEAR_ALL_Y - scroll
        );

        runeInput.setY(
                ADD_RUNE_Y - scroll
        );

        addRuneButton.setY(
                ADD_RUNE_Y - scroll
        );

        rangeRuneButton.setY(
                RANGE_RUNE_Y - scroll
        );

        fromInput.setY(
                RANGE_NUMBER_Y - scroll
        );

        toInput.setY(
                RANGE_NUMBER_Y - scroll
        );

        addRangeButton.setY(
                RANGE_BUTTON_Y - scroll
        );

        doneButton.setY(
                DONE_Y - scroll
        );

        runeUpButton.setY(
                PANEL_TOP - scroll
        );

        runeDownButton.setY(
                PANEL_BOTTOM -
                        20 -
                        scroll
        );

        updateRunePositions();
    }

    private void updateRunePositions() {

        int centerX =
                this.width / 2;

        int pageOffset =
                (int) pageScroll;

        for (int i = 0;
             i < runeButtons.size();
             i++) {

            ButtonWidget button =
                    runeButtons.get(i);

            int y =
                    PANEL_TOP +
                            5 +
                            (
                                    i *
                                            ROW_HEIGHT
                            ) -
                            runeScroll -
                            pageOffset;

            button.setPosition(
                    centerX - 100,
                    y
            );

            boolean insidePanel =
                    y >=
                            PANEL_TOP -
                                    pageOffset
                            &&
                            y + 20 <=
                                    PANEL_BOTTOM -
                                            pageOffset;

            button.visible =
                    insidePanel;

            button.active =
                    insidePanel;
        }

        boolean hasRunes =
                !RuneFinderClient.CONFIG
                        .getSelectedRunes()
                        .isEmpty();

        runeUpButton.visible =
                hasRunes;

        runeDownButton.visible =
                hasRunes;

        runeUpButton.active =
                true;

        runeDownButton.active =
                true;
    }

    private void updatePageScroll() {

        int maxScroll =
                getMaxPageScroll();

        if (pageScroll < 0) {
            pageScroll = 0;
        }

        if (pageScroll > maxScroll) {
            pageScroll = maxScroll;
        }
    }

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double horizontalAmount,
            double verticalAmount
    ) {

        int maxScroll =
                getMaxPageScroll();

        if (maxScroll > 0) {

            pageScroll -=
                    verticalAmount * 20;

            updatePageScroll();

            updateAllPositions();

            return true;
        }

        return super.mouseScrolled(
                mouseX,
                mouseY,
                horizontalAmount,
                verticalAmount
        );
    }

    @Override
    public boolean keyPressed(
            net.minecraft.client.input.KeyInput input
    ) {

        if (runeInput != null &&
                runeInput.isFocused()) {

            if (input.key() == 257) {

                addManualRune();

                return true;
            }
        }

        if (fromInput != null &&
                fromInput.isFocused()) {

            if (input.key() == 257) {

                addRuneRange();

                return true;
            }
        }

        if (toInput != null &&
                toInput.isFocused()) {

            if (input.key() == 257) {

                addRuneRange();

                return true;
            }
        }

        return super.keyPressed(input);
    }

    @Override
    public void render(
            DrawContext context,
            int mouseX,
            int mouseY,
            float delta
    ) {

        updatePageScroll();

        updateAllPositions();

        context.fill(
                0,
                0,
                this.width,
                this.height,
                0xC0101010
        );

        int scroll =
                (int) pageScroll;

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.title,
                this.width / 2,
                20 - scroll,
                0xFFFFFF
        );

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal(
                        "Selected Runes"
                ),
                this.width / 2,
                TITLE_Y -
                        25 -
                        scroll,
                0xFFFFFF
        );

        int panelLeft =
                this.width / 2 - 105;

        int panelRight =
                this.width / 2 + 105;

        context.fill(
                panelLeft,
                PANEL_TOP - scroll,
                panelRight,
                PANEL_BOTTOM - scroll,
                0x60000000
        );

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal(
                        "Add Rune Range"
                ),
                this.width / 2,
                RANGE_TITLE_Y -
                        scroll,
                0xFFFFFF
        );

        int maxScroll =
                getMaxPageScroll();

        if (maxScroll > 0) {

            int barX =
                    this.width - 10;

            int barTop = 5;

            int barBottom =
                    this.height - 5;

            int barHeight =
                    barBottom -
                            barTop;

            int visibleHeight =
                    this.height - 20;

            int thumbHeight =
                    Math.max(
                            25,
                            (int) (
                                    (
                                            (double)
                                                    barHeight *
                                                    visibleHeight
                                    ) /
                                            getContentHeight()
                            )
                    );

            int thumbTravel =
                    barHeight -
                            thumbHeight;

            /*
             * FIXED:
             * The original code had:
             *
             * int
             * (
             *
             * which is invalid Java syntax.
             *
             * It must be:
             *
             * (int) (...)
             */

            int thumbY =
                    barTop +
                            (int) (
                                    thumbTravel *
                                            (
                                                    pageScroll /
                                                            maxScroll
                                            )
                            );

            context.fill(
                    barX,
                    barTop,
                    barX + 6,
                    barBottom,
                    0x60303030
            );

            context.fill(
                    barX,
                    thumbY,
                    barX + 6,
                    thumbY +
                            thumbHeight,
                    0xFFFFFFFF
            );
        }

        super.render(
                context,
                mouseX,
                mouseY,
                delta
        );
    }
}

