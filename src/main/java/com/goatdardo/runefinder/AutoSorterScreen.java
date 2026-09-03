package com.goatdardo.runefinder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AutoSorterScreen extends Screen {

    private final Screen parent;

    private ButtonWidget runeFinderTab;
    private ButtonWidget autoSorterTab;

    private ButtonWidget addSourceButton;
    private ButtonWidget removeSourceButton;
    private ButtonWidget sourceUpButton;
    private ButtonWidget sourceDownButton;

    private ButtonWidget addDestinationButton;
    private ButtonWidget removeDestinationButton;
    private ButtonWidget destinationUpButton;
    private ButtonWidget destinationDownButton;

    private ButtonWidget assignmentUpButton;
    private ButtonWidget assignmentDownButton;

    private ButtonWidget assignRuneButton;
    private ButtonWidget alignButton;

    private ButtonWidget sortAllButton;
    private ButtonWidget movementButton;
    private ButtonWidget doneButton;

    private TextFieldWidget homeNameField;

    private int sourceScroll = 0;
    private int destinationScroll = 0;
    private int familyScroll = 0;

    private int selectedSource = -1;
    private int selectedDestination = -1;
    private int selectedFamily = -1;

    private double pageScroll = 0;

    private static final int TAB_Y = 10;
    private static final int TAB_WIDTH = 100;
    private static final int TAB_HEIGHT = 20;

    private static final int TITLE_Y = 55;

    private static final int SOURCE_TITLE_Y = 95;
    private static final int SOURCE_PANEL_Y = 115;
    private static final int SOURCE_PANEL_BOTTOM = 250;

    private static final int DESTINATION_TITLE_Y = 285;
    private static final int DESTINATION_PANEL_Y = 305;
    private static final int DESTINATION_PANEL_BOTTOM = 440;

    private static final int ASSIGNMENT_TITLE_Y = 475;
    private static final int ASSIGNMENT_PANEL_Y = 495;
    private static final int ASSIGNMENT_PANEL_BOTTOM = 620;

    private static final int ASSIGN_BUTTON_Y = 625;
    private static final int ALIGN_BUTTON_Y = 650;

    private static final int SORT_LABEL_Y = 685;
    private static final int SORT_Y = 700;

    private static final int MOVEMENT_LABEL_Y = 740;
    private static final int MOVEMENT_Y = 755;

    private static final int HOME_LABEL_Y = 790;
    private static final int HOME_Y = 810;

    private static final int DONE_Y = 855;

    private static final int PANEL_LEFT_OFFSET = 125;
    private static final int PANEL_WIDTH = 250;

    private static final int ARROW_WIDTH = 20;
    private static final int ARROW_HEIGHT = 20;

    private static final int ROW_HEIGHT = 22;

    /*
     * Each destination uses two lines:
     *
     * Line 1 = coordinates
     * Line 2 = assigned rune
     */
    private static final int DESTINATION_ROW_HEIGHT = 36;

    private AutoSorterScreen(Screen parent) {
        super(Text.literal("Auto Sorter"));
        this.parent = parent;
    }

    public static AutoSorterScreen create(Screen parent) {
        return new AutoSorterScreen(parent);
    }

    @Override
    protected void init() {

        int centerX = this.width / 2;

        /*
         * ========================================================
         * TABS
         * ========================================================
         */

        runeFinderTab = ButtonWidget.builder(
                Text.literal("Rune Finder"),
                button -> {

                    if (this.client != null) {

                        this.client.setScreen(
                                RuneSettingsScreen.create(parent)
                        );
                    }
                }
        ).dimensions(
                10,
                TAB_Y,
                TAB_WIDTH,
                TAB_HEIGHT
        ).build();

        this.addDrawableChild(runeFinderTab);

        autoSorterTab = ButtonWidget.builder(
                Text.literal("Auto Sorter"),
                button -> {
                }
        ).dimensions(
                115,
                TAB_Y,
                TAB_WIDTH,
                TAB_HEIGHT
        ).build();

        autoSorterTab.active = false;

        this.addDrawableChild(autoSorterTab);


        /*
         * ========================================================
         * SOURCE BUTTONS
         * ========================================================
         */

        addSourceButton = ButtonWidget.builder(
                Text.literal("Add Source"),
                button -> addSourceChest()
        ).dimensions(
                centerX - 125,
                SOURCE_PANEL_BOTTOM + 5,
                120,
                20
        ).build();

        this.addDrawableChild(addSourceButton);

        removeSourceButton = ButtonWidget.builder(
                Text.literal("Remove Selected"),
                button -> removeSelectedSource()
        ).dimensions(
                centerX + 5,
                SOURCE_PANEL_BOTTOM + 5,
                120,
                20
        ).build();

        this.addDrawableChild(removeSourceButton);

        sourceUpButton = ButtonWidget.builder(
                Text.literal("▲"),
                button -> {

                    if (sourceScroll > 0) {
                        sourceScroll--;
                    }

                    updateArrowVisibility();
                }
        ).dimensions(
                centerX + PANEL_LEFT_OFFSET - ARROW_WIDTH,
                SOURCE_PANEL_Y,
                ARROW_WIDTH,
                ARROW_HEIGHT
        ).build();

        this.addDrawableChild(sourceUpButton);

        sourceDownButton = ButtonWidget.builder(
                Text.literal("▼"),
                button -> {

                    int max =
                            getSourceMaxScroll();

                    if (sourceScroll < max) {
                        sourceScroll++;
                    }

                    updateArrowVisibility();
                }
        ).dimensions(
                centerX + PANEL_LEFT_OFFSET - ARROW_WIDTH,
                SOURCE_PANEL_BOTTOM - ARROW_HEIGHT,
                ARROW_WIDTH,
                ARROW_HEIGHT
        ).build();

        this.addDrawableChild(sourceDownButton);


        /*
         * ========================================================
         * DESTINATION BUTTONS
         * ========================================================
         */

        addDestinationButton = ButtonWidget.builder(
                Text.literal("Add Destination"),
                button -> addDestinationChest()
        ).dimensions(
                centerX - 125,
                DESTINATION_PANEL_BOTTOM + 5,
                120,
                20
        ).build();

        this.addDrawableChild(addDestinationButton);

        removeDestinationButton = ButtonWidget.builder(
                Text.literal("Remove Selected"),
                button -> removeSelectedDestination()
        ).dimensions(
                centerX + 5,
                DESTINATION_PANEL_BOTTOM + 5,
                120,
                20
        ).build();

        this.addDrawableChild(removeDestinationButton);


        /*
         * DESTINATION UP
         */

        destinationUpButton = ButtonWidget.builder(
                Text.literal("▲"),
                button -> {

                    if (destinationScroll > 0) {
                        destinationScroll--;
                    }

                    updateArrowVisibility();
                }
        ).dimensions(
                centerX + PANEL_LEFT_OFFSET - ARROW_WIDTH,
                DESTINATION_PANEL_Y,
                ARROW_WIDTH,
                ARROW_HEIGHT
        ).build();

        this.addDrawableChild(destinationUpButton);


        /*
         * DESTINATION DOWN
         */

        destinationDownButton = ButtonWidget.builder(
                Text.literal("▼"),
                button -> {

                    int max =
                            getDestinationMaxScroll();

                    if (destinationScroll < max) {
                        destinationScroll++;
                    }

                    if (destinationScroll < 0) {
                        destinationScroll = 0;
                    }

                    if (destinationScroll > max) {
                        destinationScroll = max;
                    }

                    updateArrowVisibility();
                }
        ).dimensions(
                centerX + PANEL_LEFT_OFFSET - ARROW_WIDTH,
                DESTINATION_PANEL_BOTTOM - ARROW_HEIGHT,
                ARROW_WIDTH,
                ARROW_HEIGHT
        ).build();

        this.addDrawableChild(destinationDownButton);


        /*
         * ========================================================
         * ASSIGNMENT SCROLL
         * ========================================================
         */

        assignmentUpButton = ButtonWidget.builder(
                Text.literal("▲"),
                button -> {

                    if (familyScroll > 0) {
                        familyScroll--;
                    }

                    updateArrowVisibility();
                }
        ).dimensions(
                centerX + PANEL_LEFT_OFFSET - ARROW_WIDTH,
                ASSIGNMENT_PANEL_Y,
                ARROW_WIDTH,
                ARROW_HEIGHT
        ).build();

        this.addDrawableChild(assignmentUpButton);

        assignmentDownButton = ButtonWidget.builder(
                Text.literal("▼"),
                button -> {

                    int max =
                            getFamilyMaxScroll();

                    if (familyScroll < max) {
                        familyScroll++;
                    }

                    updateArrowVisibility();
                }
        ).dimensions(
                centerX + PANEL_LEFT_OFFSET - ARROW_WIDTH,
                ASSIGNMENT_PANEL_BOTTOM - ARROW_HEIGHT,
                ARROW_WIDTH,
                ARROW_HEIGHT
        ).build();

        this.addDrawableChild(assignmentDownButton);


        /*
         * ========================================================
         * MANUAL ASSIGN
         * ========================================================
         */

        assignRuneButton = ButtonWidget.builder(
                Text.literal("ASSIGN SELECTED"),
                button -> assignSelectedFamily()
        ).dimensions(
                centerX - 100,
                ASSIGN_BUTTON_Y,
                200,
                20
        ).build();

        this.addDrawableChild(assignRuneButton);


        /*
         * ========================================================
         * ALIGN 1 → 1
         * ========================================================
         *
         * Destination #1 → Rune #1
         * Destination #2 → Rune #2
         * Destination #3 → Rune #3
         * etc.
         */

        alignButton = ButtonWidget.builder(
                Text.literal("ALIGN 1 → 1"),
                button -> alignDestinationsAndRunes()
        ).dimensions(
                centerX - 100,
                ALIGN_BUTTON_Y,
                200,
                20
        ).build();

        this.addDrawableChild(alignButton);


        /*
         * ========================================================
         * SORT ALL
         * ========================================================
         */

        sortAllButton = ButtonWidget.builder(
                Text.literal("SORT ALL RUNES"),
                button -> {

                    if (this.client != null &&
                            this.client.player != null) {

                        RuneSorterManager.start(
                                this.client
                        );
                    }
                }
        ).dimensions(
                centerX - 100,
                SORT_Y,
                200,
                20
        ).build();

        this.addDrawableChild(sortAllButton);


        /*
         * ========================================================
         * MOVEMENT
         * ========================================================
         */

        movementButton = ButtonWidget.builder(
                Text.literal(
                        getMovementDisplayName()
                ),
                button -> {

                    cycleMovementMode();

                    button.setMessage(
                            Text.literal(
                                    getMovementDisplayName()
                            )
                    );
                }
        ).dimensions(
                centerX - 100,
                MOVEMENT_Y,
                200,
                20
        ).build();

        this.addDrawableChild(movementButton);


        /*
         * ========================================================
         * HOME
         * ========================================================
         */

        homeNameField = new TextFieldWidget(
                this.textRenderer,
                centerX - 100,
                HOME_Y,
                200,
                20,
                Text.literal("Home name")
        );

        String savedHome =
                RuneFinderClient.CONFIG.sorterHomeName;

        if (savedHome == null ||
                savedHome.isBlank()) {

            savedHome = "base";
        }

        homeNameField.setText(savedHome);

        homeNameField.setMaxLength(64);

        homeNameField.setSuggestion(
                "e.g. base"
        );

        homeNameField.setChangedListener(
                text -> {

                    String value =
                            text == null
                                    ? ""
                                    : text.trim();

                    RuneFinderClient.CONFIG
                            .sorterHomeName = value;

                    RuneFinderClient.CONFIG.save();
                }
        );

        this.addDrawableChild(homeNameField);


        /*
         * ========================================================
         * DONE
         * ========================================================
         */

        doneButton = ButtonWidget.builder(
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
                200,
                20
        ).build();

        this.addDrawableChild(doneButton);

        updateAllPositions();
    }


    /*
     * ========================================================
     * ADD SOURCE
     * ========================================================
     */

    private void addSourceChest() {

        MinecraftClient client =
                MinecraftClient.getInstance();

        if (client.player == null) {
            return;
        }

        if (!(client.crosshairTarget
                instanceof BlockHitResult hit)) {

            client.player.sendMessage(
                    Text.literal(
                            "§cRune Finder: Look at a chest first."
                    ),
                    false
            );

            return;
        }

        BlockPos pos =
                hit.getBlockPos();

        if (!isChest(
                client,
                pos
        )) {

            client.player.sendMessage(
                    Text.literal(
                            "§cRune Finder: You are not looking at a chest."
                    ),
                    false
            );

            return;
        }

        String coordinate =
                pos.getX() +
                        "," +
                        pos.getY() +
                        "," +
                        pos.getZ();

        List<String> sources =
                RuneFinderClient.CONFIG
                        .getSourceChests();

        if (sources.contains(coordinate)) {

            client.player.sendMessage(
                    Text.literal(
                            "§eRune Finder: Source chest already registered."
                    ),
                    false
            );

            return;
        }

        RuneFinderClient.CONFIG
                .addSourceChest(
                        coordinate
                );

        selectedSource =
                sources.indexOf(coordinate);

        client.player.sendMessage(
                Text.literal(
                        "§aRune Finder: Source chest added: " +
                                coordinate
                ),
                false
        );

        updateArrowVisibility();
    }


    /*
     * ========================================================
     * ADD DESTINATION
     * ========================================================
     */

    private void addDestinationChest() {

        MinecraftClient client =
                MinecraftClient.getInstance();

        if (client.player == null) {
            return;
        }

        if (!(client.crosshairTarget
                instanceof BlockHitResult hit)) {

            client.player.sendMessage(
                    Text.literal(
                            "§cRune Finder: Look at a chest first."
                    ),
                    false
            );

            return;
        }

        BlockPos pos =
                hit.getBlockPos();

        if (!isChest(
                client,
                pos
        )) {

            client.player.sendMessage(
                    Text.literal(
                            "§cRune Finder: You are not looking at a chest."
                    ),
                    false
            );

            return;
        }

        String coordinate =
                pos.getX() +
                        "," +
                        pos.getY() +
                        "," +
                        pos.getZ();

        List<String> destinations =
                RuneFinderClient.CONFIG
                        .getDestinationChests();

        if (destinations.contains(coordinate)) {

            client.player.sendMessage(
                    Text.literal(
                            "§eRune Finder: Destination chest already registered."
                    ),
                    false
            );

            return;
        }

        RuneFinderClient.CONFIG
                .addDestinationChest(
                        coordinate
                );

        selectedDestination =
                destinations.indexOf(
                        coordinate
                );

        int max =
                getDestinationMaxScroll();

        if (selectedDestination > max) {
            destinationScroll = max;
        }

        updateArrowVisibility();

        client.player.sendMessage(
                Text.literal(
                        "§aRune Finder: Destination chest added: " +
                                coordinate
                ),
                false
        );
    }


    /*
     * ========================================================
     * CHEST CHECK
     * ========================================================
     */

    private boolean isChest(
            MinecraftClient client,
            BlockPos pos
    ) {

        if (client.world == null) {
            return false;
        }

        return client.world
                .getBlockState(pos)
                .getBlock()
                .getName()
                .getString()
                .toLowerCase()
                .contains("chest");
    }


    /*
     * ========================================================
     * REMOVE SOURCE
     * ========================================================
     */

    private void removeSelectedSource() {

        List<String> sources =
                RuneFinderClient.CONFIG
                        .getSourceChests();

        if (selectedSource < 0 ||
                selectedSource >= sources.size()) {

            return;
        }

        String coordinate =
                sources.get(
                        selectedSource
                );

        RuneFinderClient.CONFIG
                .removeSourceChest(
                        coordinate
                );

        selectedSource = -1;

        if (sourceScroll >
                getSourceMaxScroll()) {

            sourceScroll =
                    getSourceMaxScroll();
        }

        updateArrowVisibility();
    }


    /*
     * ========================================================
     * REMOVE DESTINATION
     * ========================================================
     */

    private void removeSelectedDestination() {

        List<String> destinations =
                RuneFinderClient.CONFIG
                        .getDestinationChests();

        if (selectedDestination < 0 ||
                selectedDestination >= destinations.size()) {

            return;
        }

        String coordinate =
                destinations.get(
                        selectedDestination
                );

        RuneFinderClient.CONFIG
                .removeDestinationChest(
                        coordinate
                );

        selectedDestination = -1;

        if (destinationScroll >
                getDestinationMaxScroll()) {

            destinationScroll =
                    getDestinationMaxScroll();
        }

        updateArrowVisibility();
    }


    /*
     * ========================================================
     * RUNE FAMILIES
     * ========================================================
     *
     * IMPORTANT:
     *
     * Auto Sorter uses its OWN independent family list.
     *
     * Rune Finder selections are NOT used here.
     *
     * Example:
     *
     * Rune Finder:
     * Hitstreak:1
     * Slam:1
     *
     * Auto Sort:
     * Hitstreak
     * Slam
     *
     * If Rune Finder is cleared, these Auto Sort families
     * remain available.
     */

    private List<String> getRuneFamilies() {

        List<String> families =
                RuneFinderClient.CONFIG
                        .getAutoSortFamilies();

        if (families == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(
                families
        );
    }


    /*
     * ========================================================
     * ASSIGNED FAMILIES
     * ========================================================
     */

    private List<String> getAssignedFamilies(
            String destination
    ) {

        List<String> families =
                new ArrayList<>();

        if (destination == null) {
            return families;
        }

        Map<String, String> assignments =
                RuneFinderClient.CONFIG
                        .getRuneDestinations();

        if (assignments == null) {
            return families;
        }

        for (
                Map.Entry<String, String> entry :
                assignments.entrySet()
        ) {

            if (destination.equals(
                    entry.getValue()
            )) {

                families.add(
                        entry.getKey()
                );
            }
        }

        return families;
    }


    /*
     * ========================================================
     * MANUAL ASSIGN
     * ========================================================
     */

    private void assignSelectedFamily() {

        MinecraftClient client =
                MinecraftClient.getInstance();

        List<String> destinations =
                RuneFinderClient.CONFIG
                        .getDestinationChests();

        List<String> families =
                getRuneFamilies();

        if (selectedDestination < 0 ||
                selectedDestination >=
                        destinations.size()) {

            if (client.player != null) {

                client.player.sendMessage(
                        Text.literal(
                                "§cRune Finder: Select a destination chest first."
                        ),
                        false
                );
            }

            return;
        }

        if (selectedFamily < 0 ||
                selectedFamily >=
                        families.size()) {

            if (client.player != null) {

                client.player.sendMessage(
                        Text.literal(
                                "§cRune Finder: Select a rune family first."
                        ),
                        false
                );
            }

            return;
        }

        String family =
                families.get(
                        selectedFamily
                );

        String destination =
                destinations.get(
                        selectedDestination
                );

        RuneFinderClient.CONFIG
                .setRuneDestination(
                        family,
                        destination
                );

        if (client.player != null) {

            client.player.sendMessage(
                    Text.literal(
                            "§aRune Finder: " +
                                    family +
                                    " → " +
                                    destination
                    ),
                    false
            );
        }

        selectedFamily = -1;
        familyScroll = 0;

        updateArrowVisibility();
    }


    /*
     * ========================================================
     * ALIGN 1 → 1
     * ========================================================
     */

    private void alignDestinationsAndRunes() {

        MinecraftClient client =
                MinecraftClient.getInstance();

        List<String> destinations =
                RuneFinderClient.CONFIG
                        .getDestinationChests();

        List<String> families =
                getRuneFamilies();

        int destinationCount =
                destinations == null
                        ? 0
                        : destinations.size();

        int familyCount =
                families == null
                        ? 0
                        : families.size();


        /*
         * --------------------------------------------------------
         * NO DESTINATIONS
         * --------------------------------------------------------
         */

        if (destinationCount == 0) {

            sendPlayerMessage(
                    client,
                    "§cRune Finder: No destination chests registered."
            );

            return;
        }


        /*
         * --------------------------------------------------------
         * NO RUNES
         * --------------------------------------------------------
         */

        if (familyCount == 0) {

            sendPlayerMessage(
                    client,
                    "§cRune Finder: No rune families selected."
            );

            return;
        }


        /*
         * --------------------------------------------------------
         * COUNTS MUST MATCH
         * --------------------------------------------------------
         */

        if (destinationCount != familyCount) {

            sendPlayerMessage(
                    client,
                    "§cRune Finder: Cannot align! §f" +
                            destinationCount +
                            " §cdestinations but §f" +
                            familyCount +
                            " §crune families."
            );

            return;
        }


        /*
         * --------------------------------------------------------
         * APPLY ALIGNMENT
         * --------------------------------------------------------
         */

        for (int i = 0;
             i < destinationCount;
             i++) {

            String destination =
                    destinations.get(i);

            String family =
                    families.get(i);

            RuneFinderClient.CONFIG
                    .setRuneDestination(
                            family,
                            destination
                    );
        }


        /*
         * --------------------------------------------------------
         * RESET UI
         * --------------------------------------------------------
         */

        selectedDestination = -1;
        selectedFamily = -1;

        destinationScroll = 0;
        familyScroll = 0;

        updateArrowVisibility();


        /*
         * --------------------------------------------------------
         * SUCCESS MESSAGE
         * --------------------------------------------------------
         */

        sendPlayerMessage(
                client,
                "§aRune Finder: Successfully aligned §f" +
                        destinationCount +
                        " §arunes with §f" +
                        destinationCount +
                        " §adestination chests!"
        );
    }


    /*
     * ========================================================
     * PLAYER MESSAGE
     * ========================================================
     */

    private void sendPlayerMessage(
            MinecraftClient client,
            String message
    ) {

        if (client != null &&
                client.player != null) {

            client.player.sendMessage(
                    Text.literal(message),
                    false
            );
        }
    }


    /*
     * ========================================================
     * MOVEMENT
     * ========================================================
     */

    private void cycleMovementMode() {

        String current =
                RuneFinderClient.CONFIG
                        .sorterMovementMode;

        if ("WALK".equalsIgnoreCase(
                current
        )) {

            RuneFinderClient.CONFIG
                    .setMovementMode(
                            "TELEPORT"
                    );

        } else if ("TELEPORT".equalsIgnoreCase(
                current
        )) {

            RuneFinderClient.CONFIG
                    .setMovementMode(
                            "TELEPORT_WALK"
                    );

        } else {

            RuneFinderClient.CONFIG
                    .setMovementMode(
                            "WALK"
                    );
        }
    }

    private String getMovementDisplayName() {

        String mode =
                RuneFinderClient.CONFIG
                        .sorterMovementMode;

        if ("TELEPORT".equalsIgnoreCase(
                mode
        )) {

            return "Movement: TELEPORT";
        }

        if ("TELEPORT_WALK".equalsIgnoreCase(
                mode
        )) {

            return "Movement: TELEPORT + WALK";
        }

        return "Movement: WALK";
    }


    /*
     * ========================================================
     * SOURCE SCROLL
     * ========================================================
     */

    private int getSourceMaxScroll() {

        int visibleRows =
                getVisibleRows(
                        SOURCE_PANEL_BOTTOM -
                                SOURCE_PANEL_Y
                );

        return Math.max(
                0,
                RuneFinderClient.CONFIG
                        .getSourceChests()
                        .size() -
                        visibleRows
        );
    }


    /*
     * ========================================================
     * DESTINATION SCROLL
     * ========================================================
     */

    private int getDestinationVisibleRows() {

        int panelHeight =
                DESTINATION_PANEL_BOTTOM -
                        DESTINATION_PANEL_Y;

        return Math.max(
                1,
                (panelHeight - 12) /
                        DESTINATION_ROW_HEIGHT
        );
    }

    private int getDestinationMaxScroll() {

        List<String> destinations =
                RuneFinderClient.CONFIG
                        .getDestinationChests();

        int visibleRows =
                getDestinationVisibleRows();

        return Math.max(
                0,
                destinations.size() -
                        visibleRows
        );
    }


    /*
     * ========================================================
     * FAMILY SCROLL
     * ========================================================
     */

    private int getFamilyMaxScroll() {

        int visibleRows =
                getVisibleRows(
                        ASSIGNMENT_PANEL_BOTTOM -
                                ASSIGNMENT_PANEL_Y
                );

        return Math.max(
                0,
                getRuneFamilies().size() -
                        visibleRows
        );
    }


    private int getVisibleRows(
            int height
    ) {

        return Math.max(
                1,
                (height - 10) /
                        ROW_HEIGHT
        );
    }


    /*
     * ========================================================
     * PAGE SCROLL
     * ========================================================
     */

    private int getContentHeight() {

        return DONE_Y + 30;
    }

    private int getMaxPageScroll() {

        int visibleHeight =
                this.height - 25;

        return Math.max(
                0,
                getContentHeight() -
                        visibleHeight
        );
    }


    /*
     * ========================================================
     * POSITIONING
     * ========================================================
     */

    private void updateAllPositions() {

        int scroll =
                (int) pageScroll;

        sourceUpButton.setY(
                SOURCE_PANEL_Y -
                        scroll
        );

        sourceDownButton.setY(
                SOURCE_PANEL_BOTTOM -
                        ARROW_HEIGHT -
                        scroll
        );

        addSourceButton.setY(
                SOURCE_PANEL_BOTTOM +
                        5 -
                        scroll
        );

        removeSourceButton.setY(
                SOURCE_PANEL_BOTTOM +
                        5 -
                        scroll
        );

        destinationUpButton.setY(
                DESTINATION_PANEL_Y -
                        scroll
        );

        destinationDownButton.setY(
                DESTINATION_PANEL_BOTTOM -
                        ARROW_HEIGHT -
                        scroll
        );

        addDestinationButton.setY(
                DESTINATION_PANEL_BOTTOM +
                        5 -
                        scroll
        );

        removeDestinationButton.setY(
                DESTINATION_PANEL_BOTTOM +
                        5 -
                        scroll
        );

        assignmentUpButton.setY(
                ASSIGNMENT_PANEL_Y -
                        scroll
        );

        assignmentDownButton.setY(
                ASSIGNMENT_PANEL_BOTTOM -
                        ARROW_HEIGHT -
                        scroll
        );

        assignRuneButton.setY(
                ASSIGN_BUTTON_Y -
                        scroll
        );

        alignButton.setY(
                ALIGN_BUTTON_Y -
                        scroll
        );

        sortAllButton.setY(
                SORT_Y -
                        scroll
        );

        movementButton.setY(
                MOVEMENT_Y -
                        scroll
        );

        homeNameField.setY(
                HOME_Y -
                        scroll
        );

        doneButton.setY(
                DONE_Y -
                        scroll
        );

        updateArrowVisibility();
    }


    /*
     * ========================================================
     * ARROW VISIBILITY
     * ========================================================
     */

    private void updateArrowVisibility() {

        sourceUpButton.active =
                sourceScroll > 0;

        sourceDownButton.active =
                sourceScroll <
                        getSourceMaxScroll();

        destinationUpButton.active =
                destinationScroll > 0;

        destinationDownButton.active =
                destinationScroll <
                        getDestinationMaxScroll();

        assignmentUpButton.active =
                familyScroll > 0;

        assignmentDownButton.active =
                familyScroll <
                        getFamilyMaxScroll();

        removeSourceButton.active =
                selectedSource >= 0 &&
                        selectedSource <
                                RuneFinderClient.CONFIG
                                        .getSourceChests()
                                        .size();

        removeDestinationButton.active =
                selectedDestination >= 0 &&
                        selectedDestination <
                                RuneFinderClient.CONFIG
                                        .getDestinationChests()
                                        .size();

        assignRuneButton.active =
                selectedDestination >= 0 &&
                        selectedDestination <
                                RuneFinderClient.CONFIG
                                        .getDestinationChests()
                                        .size() &&
                        selectedFamily >= 0 &&
                        selectedFamily <
                                getRuneFamilies().size();

        alignButton.active =
                !RuneFinderClient.CONFIG
                        .getDestinationChests()
                        .isEmpty() &&
                        !getRuneFamilies().isEmpty();
    }


    /*
     * ========================================================
     * PANEL
     * ========================================================
     */

    private void drawPanel(
            DrawContext context,
            int x,
            int y,
            int width,
            int height
    ) {

        context.fill(
                x,
                y,
                x + width,
                y + height,
                0x60000000
        );
    }


    /*
     * ========================================================
     * SOURCE LIST
     * ========================================================
     */

    private void drawSourceChestList(
            DrawContext context,
            List<String> list,
            int x,
            int y,
            int height,
            int scroll,
            int selected
    ) {

        if (list == null ||
                list.isEmpty()) {

            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.literal(
                            "None registered"
                    ),
                    x,
                    y,
                    0xFFAAAAAA
            );

            return;
        }

        int visibleRows =
                getVisibleRows(height);

        int maxScroll =
                Math.max(
                        0,
                        list.size() -
                                visibleRows
                );

        int start =
                Math.min(
                        scroll,
                        maxScroll
                );

        int end =
                Math.min(
                        list.size(),
                        start +
                                visibleRows
                );

        for (int i = start;
             i < end;
             i++) {

            int rowY =
                    y +
                            (i - start) *
                                    ROW_HEIGHT;

            if (i == selected) {

                context.fill(
                        x - 3,
                        rowY - 2,
                        x + 215,
                        rowY + 18,
                        0x80555555
                );
            }

            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.literal(
                            (i + 1) +
                                    ". " +
                                    list.get(i)
                    ),
                    x,
                    rowY,
                    0xFFFFFFFF
            );
        }
    }


    /*
     * ========================================================
     * DESTINATION LIST
     * ========================================================
     */

    private void drawDestinationChestList(
            DrawContext context,
            List<String> list,
            int x,
            int y,
            int height,
            int scroll,
            int selected
    ) {

        if (list == null ||
                list.isEmpty()) {

            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.literal(
                            "None registered"
                    ),
                    x,
                    y,
                    0xFFAAAAAA
            );

            return;
        }

        int visibleRows =
                getDestinationVisibleRows();

        int maxScroll =
                Math.max(
                        0,
                        list.size() -
                                visibleRows
                );

        int start =
                Math.min(
                        scroll,
                        maxScroll
                );

        int end =
                Math.min(
                        list.size(),
                        start +
                                visibleRows
                );

        context.enableScissor(
                x - 5,
                y - 2,
                x + 225,
                y +
                        height -
                        2
        );

        for (int i = start;
             i < end;
             i++) {

            int rowY =
                    y +
                            (i - start) *
                                    DESTINATION_ROW_HEIGHT;

            if (i == selected) {

                context.fill(
                        x - 3,
                        rowY - 2,
                        x + 220,
                        rowY + 32,
                        0x80555555
                );
            }

            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.literal(
                            (i + 1) +
                                    ". " +
                                    list.get(i)
                    ),
                    x,
                    rowY,
                    0xFFFFFFFF
            );

            List<String> families =
                    getAssignedFamilies(
                            list.get(i)
                    );

            String assignmentText;

            if (families.isEmpty()) {

                assignmentText =
                        "Rune: UNASSIGNED";

            } else {

                assignmentText =
                        "Rune: " +
                                String.join(
                                        ", ",
                                        families
                                );
            }

            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.literal(
                            assignmentText
                    ),
                    x + 14,
                    rowY + 14,
                    families.isEmpty()
                            ? 0xFFAAAAAA
                            : 0xFF55FF55
            );
        }

        context.disableScissor();
    }


    /*
     * ========================================================
     * RUNE FAMILY LIST
     * ========================================================
     */

    private void drawRuneFamilies(
            DrawContext context,
            int x,
            int y,
            int height
    ) {

        List<String> families =
                getRuneFamilies();

        if (families.isEmpty()) {

            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.literal(
                            "No Auto Sort families"
                    ),
                    x,
                    y,
                    0xFFAAAAAA
            );

            return;
        }

        int visibleRows =
                getVisibleRows(height);

        int maxScroll =
                Math.max(
                        0,
                        families.size() -
                                visibleRows
                );

        int start =
                Math.min(
                        familyScroll,
                        maxScroll
                );

        int end =
                Math.min(
                        families.size(),
                        start +
                                visibleRows
                );

        for (int i = start;
             i < end;
             i++) {

            int rowY =
                    y +
                            (i - start) *
                                    ROW_HEIGHT;

            if (i == selectedFamily) {

                context.fill(
                        x - 3,
                        rowY - 2,
                        x + 215,
                        rowY + 18,
                        0x80555555
                );
            }

            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.literal(
                            (i + 1) +
                                    ". " +
                                    families.get(i)
                    ),
                    x,
                    rowY,
                    0xFFFFFFFF
            );
        }
    }


    /*
     * ========================================================
     * CLICK LISTS
     * ========================================================
     */

    @Override
    public boolean mouseClicked(
            Click click,
            boolean doubleClick
    ) {

        double mouseX =
                click.x();

        double mouseY =
                click.y();

        double adjustedY =
                mouseY +
                        pageScroll;

        int centerX =
                this.width / 2;


        /*
         * Let ButtonWidgets receive clicks first.
         */

        if (super.mouseClicked(
                click,
                doubleClick
        )) {

            return true;
        }


        /*
         * ========================================================
         * SOURCE
         * ========================================================
         */

        if (
                mouseX >= centerX - 125 &&
                        mouseX <= centerX + 125 &&
                        adjustedY >= SOURCE_PANEL_Y &&
                        adjustedY < SOURCE_PANEL_BOTTOM
        ) {

            int row =
                    (int) (
                            (adjustedY -
                                    (SOURCE_PANEL_Y + 8)) /
                                    ROW_HEIGHT
                    );

            int index =
                    sourceScroll +
                            row;

            List<String> sources =
                    RuneFinderClient.CONFIG
                            .getSourceChests();

            if (
                    index >= 0 &&
                            index < sources.size()
            ) {

                selectedSource =
                        index;

                updateArrowVisibility();

                return true;
            }
        }


        /*
         * ========================================================
         * DESTINATION
         * ========================================================
         */

        if (
                mouseX >= centerX - 125 &&
                        mouseX < centerX + 105 &&
                        adjustedY >= DESTINATION_PANEL_Y &&
                        adjustedY < DESTINATION_PANEL_BOTTOM
        ) {

            int row =
                    (int) (
                            (adjustedY -
                                    (DESTINATION_PANEL_Y + 8)) /
                                    DESTINATION_ROW_HEIGHT
                    );

            int index =
                    destinationScroll +
                            row;

            List<String> destinations =
                    RuneFinderClient.CONFIG
                            .getDestinationChests();

            if (
                    index >= 0 &&
                            index < destinations.size()
            ) {

                selectedDestination =
                        index;

                updateArrowVisibility();

                return true;
            }
        }


        /*
         * ========================================================
         * RUNE FAMILY
         * ========================================================
         */

        if (
                mouseX >= centerX - 125 &&
                        mouseX <= centerX + 125 &&
                        adjustedY >= ASSIGNMENT_PANEL_Y &&
                        adjustedY < ASSIGNMENT_PANEL_BOTTOM
        ) {

            int row =
                    (int) (
                            (adjustedY -
                                    (ASSIGNMENT_PANEL_Y + 8)) /
                                    ROW_HEIGHT
                    );

            List<String> families =
                    getRuneFamilies();

            int index =
                    familyScroll +
                            row;

            if (
                    index >= 0 &&
                            index < families.size()
            ) {

                selectedFamily =
                        index;

                updateArrowVisibility();

                return true;
            }
        }

        return false;
    }


    /*
     * ========================================================
     * PAGE SCROLL
     * ========================================================
     */

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double horizontalAmount,
            double verticalAmount
    ) {

        pageScroll -=
                verticalAmount * 20;

        int max =
                getMaxPageScroll();

        if (pageScroll < 0) {
            pageScroll = 0;
        }

        if (pageScroll > max) {
            pageScroll = max;
        }

        updateAllPositions();

        return true;
    }


    /*
     * ========================================================
     * RENDER
     * ========================================================
     */

    @Override
    public void render(
            DrawContext context,
            int mouseX,
            int mouseY,
            float delta
    ) {

        updateAllPositions();

        context.fill(
                0,
                0,
                this.width,
                this.height,
                0xC0101010
        );

        super.render(
                context,
                mouseX,
                mouseY,
                delta
        );

        int centerX =
                this.width / 2;

        int scroll =
                (int) pageScroll;


        /*
         * TITLE
         */

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal(
                        "AUTO SORTER"
                ),
                centerX,
                TITLE_Y -
                        scroll,
                0xFFFFFFFF
        );


        /*
         * SOURCE
         */

        context.drawTextWithShadow(
                this.textRenderer,
                Text.literal(
                        "Source Chests (" +
                                RuneFinderClient.CONFIG
                                        .getSourceChests()
                                        .size() +
                                ")"
                ),
                centerX - 125,
                SOURCE_TITLE_Y -
                        scroll,
                0xFFFFFFFF
        );

        drawPanel(
                context,
                centerX - 125,
                SOURCE_PANEL_Y -
                        scroll,
                PANEL_WIDTH,
                SOURCE_PANEL_BOTTOM -
                        SOURCE_PANEL_Y
        );

        drawSourceChestList(
                context,
                RuneFinderClient.CONFIG
                        .getSourceChests(),
                centerX - 115,
                SOURCE_PANEL_Y +
                        8 -
                        scroll,
                SOURCE_PANEL_BOTTOM -
                        SOURCE_PANEL_Y,
                sourceScroll,
                selectedSource
        );


        /*
         * DESTINATION
         */

        context.drawTextWithShadow(
                this.textRenderer,
                Text.literal(
                        "Destination Chests (" +
                                RuneFinderClient.CONFIG
                                        .getDestinationChests()
                                        .size() +
                                ")"
                ),
                centerX - 125,
                DESTINATION_TITLE_Y -
                        scroll,
                0xFFFFFFFF
        );

        drawPanel(
                context,
                centerX - 125,
                DESTINATION_PANEL_Y -
                        scroll,
                PANEL_WIDTH,
                DESTINATION_PANEL_BOTTOM -
                        DESTINATION_PANEL_Y
        );

        drawDestinationChestList(
                context,
                RuneFinderClient.CONFIG
                        .getDestinationChests(),
                centerX - 115,
                DESTINATION_PANEL_Y +
                        8 -
                        scroll,
                DESTINATION_PANEL_BOTTOM -
                        DESTINATION_PANEL_Y,
                destinationScroll,
                selectedDestination
        );


        /*
         * RUNE FAMILIES
         */

        context.drawTextWithShadow(
                this.textRenderer,
                Text.literal(
                        "Rune Families"
                ),
                centerX - 125,
                ASSIGNMENT_TITLE_Y -
                        scroll,
                0xFFFFFFFF
        );

        drawPanel(
                context,
                centerX - 125,
                ASSIGNMENT_PANEL_Y -
                        scroll,
                PANEL_WIDTH,
                ASSIGNMENT_PANEL_BOTTOM -
                        ASSIGNMENT_PANEL_Y
        );

        drawRuneFamilies(
                context,
                centerX - 115,
                ASSIGNMENT_PANEL_Y +
                        8 -
                        scroll,
                ASSIGNMENT_PANEL_BOTTOM -
                        ASSIGNMENT_PANEL_Y
        );


        /*
         * SORT
         */

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal(
                        "Sort Everything"
                ),
                centerX,
                SORT_LABEL_Y -
                        scroll,
                0xFFAAAAAA
        );


        /*
         * MOVEMENT
         */

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal(
                        "Movement Mode"
                ),
                centerX,
                MOVEMENT_LABEL_Y -
                        scroll,
                0xFFAAAAAA
        );


        /*
         * HOME
         */

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal(
                        "Home Name"
                ),
                centerX,
                HOME_LABEL_Y -
                        scroll,
                0xFFAAAAAA
        );


        /*
         * PAGE SCROLL BAR
         */

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
                                    (double) barHeight *
                                            visibleHeight /
                                            getContentHeight()
                            )
                    );

            int thumbTravel =
                    barHeight -
                            thumbHeight;

            int thumbY =
                    barTop +
                            (int) (
                                    thumbTravel *
                                            (pageScroll /
                                                    maxScroll)
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
    }
}