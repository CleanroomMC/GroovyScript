package com.cleanroommc.groovyscript.compat.mods.techreborn;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import reborncore.api.praescriptum.ingredients.input.FluidStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.InputIngredient;
import reborncore.api.praescriptum.ingredients.input.ItemStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.OreDictionaryInputIngredient;

public class Helper {

    public static ItemStack getStackFromIIngredient(IIngredient ingredient) {
        if (ingredient instanceof OreDictIngredient oreDictIngredient) {
            if (OreDictionary.doesOreNameExist(oreDictIngredient.getOreDict())) {
                return OreDictionary.getOres(oreDictIngredient.getOreDict()).get(0).copy();
            }
        }
        if (IngredientHelper.isItem(ingredient)) {
            return IngredientHelper.toItemStack(ingredient);
        }
        if (ingredient.getMatchingStacks().length > 0) return ingredient.getMatchingStacks()[0];
        return ItemStack.EMPTY;
    }

    public static InputIngredient<?> toInputIngredient(IIngredient ingredient) {
        if (ingredient instanceof OreDictIngredient ore) return OreDictionaryInputIngredient.of(ore.getOreDict(), ore.getAmount());
        if (ingredient instanceof FluidStack fluid) return FluidStackInputIngredient.of(fluid);
        if (IngredientHelper.isItem(ingredient)) return ItemStackInputIngredient.of(IngredientHelper.toItemStack(ingredient));
        return ItemStackInputIngredient.of(ItemStack.EMPTY);
    }
}
