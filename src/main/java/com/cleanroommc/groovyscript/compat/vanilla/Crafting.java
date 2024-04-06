package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class Crafting extends ForgeRegistryWrapper<IRecipe> {

    private static final Char2ObjectOpenHashMap<IIngredient> fallbackChars = new Char2ObjectOpenHashMap<>();

    public Crafting() {
        super(ForgeRegistries.RECIPES);
    }

    @GroovyBlacklist
    public static IIngredient getFallback(char c) {
        return fallbackChars.get(c);
    }

    public void setFallback(char key, IIngredient ingredient) {
        fallbackChars.put(key, ingredient);
    }

    public void setFallback(String key, IIngredient ingredient) {
        if (key == null || key.length() != 1) {
            GroovyLog.get().error("Fallback key must be a single character");
            return;
        }
        fallbackChars.put(key.charAt(0), ingredient);
    }

    public void addShaped(ItemStack output, List<List<IIngredient>> input) {
        shapedBuilder()
                .matrix(input)
                .output(output)
                .register();
    }

    public void addShaped(String name, ItemStack output, List<List<IIngredient>> input) {
        shapedBuilder()
                .matrix(input)
                .output(output)
                .name(name)
                .register();
    }

    public void addShaped(ResourceLocation name, ItemStack output, List<List<IIngredient>> input) {
        shapedBuilder()
                .matrix(input)
                .output(output)
                .name(name)
                .register();
    }

    public void addShapeless(ItemStack output, List<IIngredient> input) {
        shapelessBuilder()
                .input(input)
                .output(output)
                .register();
    }

    public void addShapeless(String name, ItemStack output, List<IIngredient> input) {
        shapelessBuilder()
                .input(input)
                .output(output)
                .name(name)
                .register();
    }

    public void addShapeless(ResourceLocation name, ItemStack output, List<IIngredient> input) {
        shapelessBuilder()
                .input(input)
                .output(output)
                .name(name)
                .register();
    }

    public void replaceShapeless(ItemStack output, List<IIngredient> input) {
        shapelessBuilder()
                .input(input)
                .output(output)
                .replace()
                .register();
    }

    public void replaceShapeless(String name, ItemStack output, List<IIngredient> input) {
        shapelessBuilder()
                .input(input)
                .output(output)
                .name(name)
                .replaceByName()
                .register();
    }

    public void replaceShapeless(ResourceLocation name, ItemStack output, List<IIngredient> input) {
        shapelessBuilder()
                .input(input)
                .output(output)
                .name(name)
                .replaceByName()
                .register();
    }

    public void replaceShaped(ItemStack output, List<List<IIngredient>> input) {
        shapedBuilder()
                .matrix(input)
                .output(output)
                .replace()
                .register();
    }

    public void replaceShaped(String name, ItemStack output, List<List<IIngredient>> input) {
        shapedBuilder()
                .matrix(input)
                .output(output)
                .name(name)
                .replaceByName()
                .register();
    }

    public void replaceShaped(ResourceLocation name, ItemStack output, List<List<IIngredient>> input) {
        shapedBuilder()
                .matrix(input)
                .output(output)
                .name(name)
                .replaceByName()
                .register();
    }

    public void removeByOutput(IIngredient output) {
        removeByOutput(output, true);
    }

    public void removeByOutput(IIngredient output, boolean log) {
        if (IngredientHelper.isEmpty(output)) {
            if (log) {
                GroovyLog.msg("Error removing Minecraft Crafting recipe")
                        .add("Output must not be empty")
                        .error()
                        .post();
            }
            return;
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
                        .add("No recipes found for {}", output)
                        .error()
                        .post();
            }
            return;
        }
        for (ResourceLocation rl : recipesToRemove) {
            ReloadableRegistryManager.removeRegistryEntry(ForgeRegistries.RECIPES, rl);
        }
    }

    public void removeByInput(IIngredient input) {
        removeByInput(input, true);
    }

    public void removeByInput(IIngredient input, boolean log) {
        if (IngredientHelper.isEmpty(input)) {
            if (log) {
                GroovyLog.msg("Error removing Minecraft Crafting recipe")
                        .add("Input must not be empty")
                        .error()
                        .post();
            }
            return;
        }
        List<ResourceLocation> recipesToRemove = new ArrayList<>();
        for (IRecipe recipe : ForgeRegistries.RECIPES) {
            if (recipe.getRegistryName() != null && !recipe.getIngredients().isEmpty() && recipe.getIngredients().stream().anyMatch(i -> i.getMatchingStacks().length > 0 && input.test(i.getMatchingStacks()[0]))) {
                recipesToRemove.add(recipe.getRegistryName());
            }
        }
        if (recipesToRemove.isEmpty()) {
            if (log) {
                GroovyLog.msg("Error removing Minecraft Crafting recipe")
                        .add("No recipes found for {}", input)
                        .error()
                        .post();
            }
            return;
        }
        for (ResourceLocation location : recipesToRemove) {
            ReloadableRegistryManager.removeRegistryEntry(ForgeRegistries.RECIPES, location);
        }
    }

    public CraftingRecipeBuilder.Shaped shapedBuilder() {
        return new CraftingRecipeBuilder.Shaped();
    }

    public CraftingRecipeBuilder.Shapeless shapelessBuilder() {
        return new CraftingRecipeBuilder.Shapeless();
    }
}
