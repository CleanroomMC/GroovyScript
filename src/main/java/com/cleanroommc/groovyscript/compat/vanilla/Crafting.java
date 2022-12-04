package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.api.GroovyLog;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class Crafting {

    public void addShaped(ItemStack output, List<List<IIngredient>> input) {
        addShaped(null, output, input);
    }

    public void addShaped(String name, ItemStack output, List<List<IIngredient>> input) {
        shapedBuilder()
                .matrix(input)
                .output(output)
                .name(name)
                .register();
    }

    public void addShapeless(ItemStack output, List<IIngredient> input) {
        addShapeless(null, output, input);
    }

    public void addShapeless(String name, ItemStack output, List<IIngredient> input) {
        shapelessBuilder()
                .input(input)
                .output(output)
                .name(name)
                .register();
    }

    public void replaceShapeless(ItemStack output, List<IIngredient> input) {
        replaceShapeless(null, output, input);
    }

    public void replaceShapeless(String name, ItemStack output, List<IIngredient> input) {
        shapelessBuilder()
                .input(input)
                .output(output)
                .name(name)
                .replace()
                .register();
    }

    public void replaceShaped(ItemStack output, List<List<IIngredient>> input) {
        replaceShaped(null, output, input);
    }

    public void replaceShaped(String name, ItemStack output, List<List<IIngredient>> input) {
        shapedBuilder()
                .matrix(input)
                .output(output)
                .name(name)
                .replaceByName()
                .register();
    }

    public void remove(String name) {
        ReloadableRegistryManager.removeRegistryEntry(ForgeRegistries.RECIPES, name);
    }

    public void removeByOutput(IIngredient output) {
        removeByOutput(output, true);
    }

    @GroovyBlacklist
    public void removeByOutput(IIngredient output, boolean log) {
        if (IngredientHelper.isEmpty(output)) {
            if (log) {
                GroovyLog.msg("Error removing Minecraft Crafting recipe")
                        .add("Output must not be empty")
                        .error()
                        .post();
                return;
            }
        }
        List<ResourceLocation> recipesToRemove = new ArrayList<>();
        for (IRecipe recipe : ForgeRegistries.RECIPES) {
            if (recipe.getRegistryName() != null && output.test(recipe.getRecipeOutput())) {
                recipesToRemove.add(recipe.getRegistryName());
            }
        }
        if (recipesToRemove.isEmpty()) {
            if (log) {
                GroovyLog.msg("Error removing Minecraft Crafting recipe")
                        .add("No recipes found for %s", output)
                        .error()
                        .post();
            }
            return;
        }
        for (ResourceLocation rl : recipesToRemove) {
            ReloadableRegistryManager.removeRegistryEntry(ForgeRegistries.RECIPES, rl);
        }
    }

    public CraftingRecipeBuilder.Shaped shapedBuilder() {
        return new CraftingRecipeBuilder.Shaped(3, 3);
    }

    public CraftingRecipeBuilder.Shapeless shapelessBuilder() {
        return new CraftingRecipeBuilder.Shapeless(3, 3);
    }
}
