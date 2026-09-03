
        package com.goatdardo.runefinder;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public class RuneRarityDetector {

    public enum Rarity {
        COMMON,
        RARE,
        LEGENDARY,
        MYTHICAL,
        DOJO
    }

    public static Optional<Rarity> getRarity(ItemStack stack) {

        if (stack == null || stack.isEmpty()) {
            return Optional.empty();
        }

        CustomModelDataComponent customModelData =
                stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);

        if (customModelData == null) {
            return Optional.empty();
        }

        if (customModelData.floats().isEmpty()) {
            return Optional.empty();
        }

        float value = customModelData.floats().get(0);

        if (Float.compare(value, 1542.0f) == 0) {
            return Optional.of(Rarity.COMMON);
        }

        if (Float.compare(value, 1543.0f) == 0) {
            return Optional.of(Rarity.RARE);
        }

        if (Float.compare(value, 1544.0f) == 0) {
            return Optional.of(Rarity.LEGENDARY);
        }

        if (Float.compare(value, 1540.0f) == 0) {
            return Optional.of(Rarity.MYTHICAL);
        }

        if (Float.compare(value, 1545.0f) == 0) {
            return Optional.of(Rarity.DOJO);
        }

        return Optional.empty();
    }

    public static String getDisplayName(Rarity rarity) {

        return switch (rarity) {
            case COMMON -> "Common";
            case RARE -> "Rare";
            case LEGENDARY -> "Legendary";
            case MYTHICAL -> "Mythical";
            case DOJO -> "Dojo";
        };
    }
}

