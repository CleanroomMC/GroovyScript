package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class ItemStackExpansion {

    public static ItemStack setRarity(ItemStack self, TextFormatting color) {
        VanillaModule.rarity.set(color, self);
        return self;
    }

    public static void addOreDict(ItemStack self, OreDictIngredient ingredient) {
        VanillaModule.oreDict.add(ingredient.getOreDict(), self);
    }

    public static void removeOreDict(ItemStack self, OreDictIngredient ingredient) {
        VanillaModule.oreDict.remove(ingredient.getOreDict(), self);
    }

    public static boolean leftShift(ItemStack self, IIngredient ingredient) {
        return ingredient.isCase(self) && ingredient.getAmount() >= self.getCount();
    }

    public static boolean isSameExact(ItemStack self, ItemStack itemStack) {
        return ItemStack.areItemStacksEqual(self, itemStack);
    }

    public static boolean isSame(ItemStack self, ItemStack itemStack, boolean ignoreNbt) {
        return ItemStack.areItemsEqual(self, itemStack) && (ignoreNbt || ItemStack.areItemStackTagsEqual(self, itemStack));
    }
}
