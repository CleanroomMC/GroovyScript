package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class Crafting {

    public void addShaped(String name, ItemStack output, List<List<IIngredient>> input) {
        List<IIngredient> inputs = new ArrayList<>();
        int w = 0;
        for (List<IIngredient> row : input) {
            if (w != 0 && w != row.size()) {
                throw new IllegalArgumentException("All rows must be the same size");
            }
            w = row.size();
            inputs.addAll(row);
        }
        ReloadableRegistryManager.addRegistryEntry(ForgeRegistries.RECIPES, name, new ShapedCraftingRecipe(output.copy(), inputs, w, input.size()));
    }

    public void addShapeless(String name, ItemStack output, List<IIngredient> input) {
        ReloadableRegistryManager.addRegistryEntry(ForgeRegistries.RECIPES, name, new ShapelessCraftingRecipe(output.copy(), input));
    }

    public void remove(String name) {
        ReloadableRegistryManager.removeRegistryEntry(ForgeRegistries.RECIPES, name);
    }

}
