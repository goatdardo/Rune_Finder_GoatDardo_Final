package com.goatdardo.runefinder;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.Optional;

public class RuneDetector {

    public static Optional<String> getRuneType(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return Optional.empty();
        }

        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);

        if (customData == null) {
            return Optional.empty();
        }

        NbtCompound root = customData.copyNbt();

        NbtCompound publicValues =
                root.getCompoundOrEmpty("PublicBukkitValues");

        String runeEnchant =
                publicValues.getString("scrptr:rune_enchant", "");

        if (runeEnchant.isBlank()) {
            return Optional.empty();
        }

        return Optional.of(runeEnchant);
    }
}