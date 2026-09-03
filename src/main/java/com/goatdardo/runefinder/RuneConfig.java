package com.goatdardo.runefinder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RuneConfig {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Path CONFIG_PATH =
            Path.of("config", "runefinder.json");

    /*
     * ============================================================
     * RUNE FINDER SELECTIONS
     * ============================================================
     *
     * These are FULL rune IDs.
     *
     * Example:
     *
     * Hitstreak:1
     * Hitstreak:2
     * Slam:1
     *
     * Rune Finder can freely clear these.
     */

    private List<String> selectedRunes =
            new ArrayList<>();


    /*
     * ============================================================
     * AUTO SORT SELECTIONS
     * ============================================================
     *
     * These are RUNE FAMILIES.
     *
     * Example:
     *
     * Hitstreak
     * Slam
     * Lifesteal
     *
     * This list is completely independent from selectedRunes.
     *
     * Once a family is added here, clearing Rune Finder does NOT
     * remove it.
     */

    private List<String> autoSortFamilies =
            new ArrayList<>();


    /*
     * ============================================================
     * RUNE FINDER SETTINGS
     * ============================================================
     */

    public boolean highlightEnabled = true;

    public int highlightColor =
            0xFFFFD700;

    public float highlightOpacity =
            0.35f;

    public boolean autoPickupEnabled =
            false;

    public int pickupDelay =
            150;

    public String sortRarity =
            "Rare";


    /*
     * ============================================================
     * CHESTS
     * ============================================================
     */

    private List<String> sourceChests =
            new ArrayList<>();

    private List<String> destinationChests =
            new ArrayList<>();


    /*
     * ============================================================
     * RUNE DESTINATIONS
     * ============================================================
     *
     * family -> chest coordinates
     *
     * Example:
     *
     * Hitstreak -> 100,70,200
     * Slam      -> 110,70,200
     */

    private Map<String, String> runeDestinations =
            new LinkedHashMap<>();


    /*
     * ============================================================
     * SORTER SETTINGS
     * ============================================================
     */

    public String sorterMovementMode =
            "WALK";

    public String sorterHomeName =
            "base";

    public transient boolean sorterRunning =
            false;


    /*
     * ============================================================
     * RUNE FINDER
     * ============================================================
     */

    public List<String> getSelectedRunes() {
        return selectedRunes;
    }

    public boolean isSelected(String rune) {

        return selectedRunes.contains(rune);
    }


    /*
     * Select/deselect a Rune Finder rune.
     *
     * IMPORTANT:
     *
     * Selecting a rune ALSO adds its family to Auto Sort.
     *
     * Example:
     *
     * Hitstreak:1
     *
     * becomes:
     *
     * Rune Finder:
     * Hitstreak:1
     *
     * Auto Sort:
     * Hitstreak
     *
     * If Hitstreak already exists in Auto Sort,
     * nothing is added a second time.
     */

    public void toggleRune(String rune) {

        if (rune == null ||
                rune.isBlank()) {

            return;
        }

        if (selectedRunes.contains(rune)) {

            selectedRunes.remove(rune);

        } else {

            selectedRunes.add(rune);

            addAutoSortFamilyFromRune(rune);
        }

        save();
    }


    /*
     * ============================================================
     * ADD RUNE FINDER RUNE WITHOUT TOGGLING
     * ============================================================
     */

    public void addRune(String rune) {

        if (rune == null ||
                rune.isBlank()) {

            return;
        }

        if (!selectedRunes.contains(rune)) {

            selectedRunes.add(rune);

            addAutoSortFamilyFromRune(rune);

            save();
        }
    }


    /*
     * ============================================================
     * AUTO SORT FAMILY
     * ============================================================
     */

    private void addAutoSortFamilyFromRune(
            String rune
    ) {

        if (rune == null ||
                rune.isBlank()) {

            return;
        }

        int separator =
                rune.indexOf(':');

        String family;

        if (separator > 0) {

            family =
                    rune.substring(
                            0,
                            separator
                    );

        } else {

            family = rune;
        }

        addAutoSortFamily(family);
    }


    public List<String> getAutoSortFamilies() {

        return autoSortFamilies;
    }


    public boolean isAutoSortFamilySelected(
            String family
    ) {

        if (family == null ||
                family.isBlank()) {

            return false;
        }

        return autoSortFamilies.contains(
                family
        );
    }


    public void addAutoSortFamily(
            String family
    ) {

        if (family == null ||
                family.isBlank()) {

            return;
        }

        if (!autoSortFamilies.contains(family)) {

            autoSortFamilies.add(family);

            save();
        }
    }


    /*
     * Auto Sort families are intentionally NOT cleared by
     * clearRunes().
     */

    public void clearAutoSortFamilies() {

        autoSortFamilies.clear();

        save();
    }


    /*
     * ============================================================
     * CLEAR RUNE FINDER ONLY
     * ============================================================
     */

    public void clearRunes() {

        /*
         * IMPORTANT:
         *
         * Only Rune Finder selections are cleared.
         *
         * Auto Sort families remain untouched.
         */

        selectedRunes.clear();

        save();
    }


    /*
     * ============================================================
     * SOURCE CHESTS
     * ============================================================
     */

    public List<String> getSourceChests() {

        return sourceChests;
    }


    public void addSourceChest(
            String position
    ) {

        if (position == null ||
                position.isBlank()) {

            return;
        }

        if (!sourceChests.contains(position)) {

            sourceChests.add(position);

            save();
        }
    }


    public void removeSourceChest(
            String position
    ) {

        if (position == null) {
            return;
        }

        sourceChests.remove(position);

        save();
    }


    public void clearSourceChests() {

        sourceChests.clear();

        save();
    }


    /*
     * ============================================================
     * DESTINATION CHESTS
     * ============================================================
     */

    public List<String> getDestinationChests() {

        return destinationChests;
    }


    public void addDestinationChest(
            String position
    ) {

        if (position == null ||
                position.isBlank()) {

            return;
        }

        if (!destinationChests.contains(position)) {

            destinationChests.add(position);

            save();
        }
    }


    public void removeDestinationChest(
            String position
    ) {

        if (position == null) {
            return;
        }

        destinationChests.remove(position);

        runeDestinations.entrySet()
                .removeIf(
                        entry ->
                                position.equals(
                                        entry.getValue()
                                )
                );

        save();
    }


    public void clearDestinationChests() {

        destinationChests.clear();

        runeDestinations.clear();

        save();
    }


    /*
     * ============================================================
     * RUNE DESTINATIONS
     * ============================================================
     */

    public Map<String, String> getRuneDestinations() {

        return runeDestinations;
    }


    public String getRuneDestination(
            String runeFamily
    ) {

        if (runeFamily == null) {
            return null;
        }

        return runeDestinations.get(
                runeFamily
        );
    }


    public void setRuneDestination(
            String runeFamily,
            String position
    ) {

        if (runeFamily == null ||
                runeFamily.isBlank()) {

            return;
        }

        if (position == null ||
                position.isBlank()) {

            runeDestinations.remove(
                    runeFamily
            );

        } else {

            runeDestinations.put(
                    runeFamily,
                    position
            );

            /*
             * Assigning a family to a destination also makes
             * sure Auto Sort knows about that family.
             */

            addAutoSortFamily(
                    runeFamily
            );
        }

        save();
    }


    public void removeRuneDestination(
            String runeFamily
    ) {

        if (runeFamily == null) {
            return;
        }

        runeDestinations.remove(
                runeFamily
        );

        save();
    }


    public void clearRuneDestinations() {

        runeDestinations.clear();

        save();
    }


    /*
     * ============================================================
     * MOVEMENT
     * ============================================================
     */

    public boolean isWalkMode() {

        return "WALK".equalsIgnoreCase(
                sorterMovementMode
        );
    }


    public boolean isTeleportMode() {

        return "TELEPORT".equalsIgnoreCase(
                sorterMovementMode
        );
    }


    public void setMovementMode(
            String mode
    ) {

        if (mode == null ||
                mode.isBlank()) {

            sorterMovementMode =
                    "WALK";

        } else {

            sorterMovementMode =
                    mode.toUpperCase();
        }

        save();
    }


    /*
     * ============================================================
     * SAVE
     * ============================================================
     */

    public void save() {

        try {

            Files.createDirectories(
                    CONFIG_PATH.getParent()
            );

            Files.writeString(
                    CONFIG_PATH,
                    GSON.toJson(this)
            );

        } catch (IOException e) {

            System.err.println(
                    "Rune Finder: Could not save config."
            );

            e.printStackTrace();
        }
    }


    /*
     * ============================================================
     * LOAD
     * ============================================================
     */

    public static RuneConfig load() {

        try {

            if (Files.exists(CONFIG_PATH)) {

                String json =
                        Files.readString(
                                CONFIG_PATH
                        );

                RuneConfig config =
                        GSON.fromJson(
                                json,
                                RuneConfig.class
                        );

                if (config != null) {

                    if (config.selectedRunes == null) {

                        config.selectedRunes =
                                new ArrayList<>();
                    }

                    if (config.autoSortFamilies == null) {

                        config.autoSortFamilies =
                                new ArrayList<>();
                    }

                    if (config.sourceChests == null) {

                        config.sourceChests =
                                new ArrayList<>();
                    }

                    if (config.destinationChests == null) {

                        config.destinationChests =
                                new ArrayList<>();
                    }

                    if (config.runeDestinations == null) {

                        config.runeDestinations =
                                new LinkedHashMap<>();
                    }

                    if (config.sortRarity == null ||
                            config.sortRarity.isBlank()) {

                        config.sortRarity =
                                "Rare";
                    }

                    if (config.sorterMovementMode == null ||
                            config.sorterMovementMode.isBlank()) {

                        config.sorterMovementMode =
                                "WALK";
                    }

                    if (config.sorterHomeName == null ||
                            config.sorterHomeName.isBlank()) {

                        config.sorterHomeName =
                                "base";
                    }

                    /*
                     * Make sure any existing assignments are also
                     * represented in Auto Sort.
                     *
                     * This is important when upgrading from the
                     * old config system.
                     */

                    for (String family :
                            config.runeDestinations.keySet()) {

                        if (family != null &&
                                !family.isBlank() &&
                                !config.autoSortFamilies
                                        .contains(family)) {

                            config.autoSortFamilies
                                    .add(family);
                        }
                    }

                    config.sorterRunning =
                            false;

                    return config;
                }
            }

        } catch (Exception e) {

            System.err.println(
                    "Rune Finder: Could not load config."
            );

            e.printStackTrace();
        }


        RuneConfig config =
                new RuneConfig();

        config.save();

        return config;
    }
}