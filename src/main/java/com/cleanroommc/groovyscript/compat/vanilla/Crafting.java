package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.jeiremoval.IJEIRemoval;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.FluidOperation;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.IOperation;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.ItemOperation;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.WrapperOperation;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.OperationHandler;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Crafting extends ForgeRegistryWrapper<IRecipe> implements IJEIRemoval.Default {

    private static final Char2ObjectOpenHashMap<IIngredient> fallbackChars = new Char2ObjectOpenHashMap<>();

    public Crafting() {
        super(ForgeRegistries.RECIPES);
    }

    @GroovyBlacklist
    public static IIngredient getFallback(char c) {
        return fallbackChars.get(c);
    }

    private static IOperation registryNameOperation() {
        return new WrapperOperation<>(ICraftingRecipeWrapper.class, wrapper ->
                wrapper.getRegistryName() == null
                ? Collections.emptyList()
                : Collections.singletonList(OperationHandler.format("remove", GroovyScriptCodeConverter.asGroovyCode(wrapper.getRegistryName(), true))));
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

    @Override
    public @NotNull Collection<String> getCategories() {
        return Collections.singletonList(VanillaRecipeCategoryUid.CRAFTING);
    }

    @Override
    public @NotNull List<IOperation> getJEIOperations() {
        return ImmutableList.of(registryNameOperation(), ItemOperation.defaultOperation().include(0), FluidOperation.defaultOperation());
    }

}
