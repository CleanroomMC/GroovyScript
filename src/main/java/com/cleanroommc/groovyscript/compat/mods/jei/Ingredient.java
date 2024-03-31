package com.cleanroommc.groovyscript.compat.mods.jei;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IIngredientType;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.*;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES,
                     admonition = @Admonition("groovyscript.wiki.jei.ingredient.note0"))
public class Ingredient extends VirtualizedRegistry<Void> {

    private final Map<IIngredientType<?>, List<Object>> hiddenIngredients = new Object2ObjectOpenHashMap<>();
    private final Map<IIngredientType<?>, List<Object>> addedIngredients = new Object2ObjectOpenHashMap<>();
    private final List<IIngredientType<?>> hiddenTypes = new ArrayList<>();
    private boolean hideAllIngredients;

    public Ingredient() {
        super(Alias.generateOfClass(Ingredient.class).andGenerate("Sidebar"));
    }

    /**
     * Called by {@link JeiPlugin#onRuntimeAvailable}
     */
    @GroovyBlacklist
    public void applyChanges(IIngredientRegistry ingredientRegistry) {
        if (hideAllIngredients) {
            ingredientRegistry.getRegisteredIngredientTypes().forEach(hiddenTypes::add);
        }
        for (var type : hiddenTypes) {
            hiddenIngredients.computeIfAbsent(type, k -> new ArrayList<>()).addAll(ingredientRegistry.getAllIngredients(type));
        }
        for (var entry : hiddenIngredients.entrySet()) {
            if (entry.getValue().isEmpty()) continue;
            //noinspection unchecked,rawtypes
            ingredientRegistry.removeIngredientsAtRuntime(entry.getKey(), ingredientRegistry.getIngredientHelper(entry.getKey()).expandSubtypes((List) entry.getValue()));
        }

        for (var entry : addedIngredients.entrySet()) {
            //noinspection unchecked,rawtypes
            ingredientRegistry.addIngredientsAtRuntime(entry.getKey(), ingredientRegistry.getIngredientHelper(entry.getKey()).expandSubtypes((List) entry.getValue()));
        }

    }

    @Override
    public void onReload() {
        addedIngredients.clear();
        hiddenIngredients.clear();
        hiddenTypes.clear();
        hideAllIngredients = false;
    }

    @MethodDescription(
            type = MethodDescription.Type.ADDITION,
            example = @Example(value = "VanillaTypes.ITEM, item('minecraft:bed').withNbt([display:[Name:'Beds come in 16 different colors!']])", imports = "mezz.jei.api.ingredients.VanillaTypes")
    )
    public void add(IIngredientType<?> type, Collection<Object> entries) {
        addedIngredients.computeIfAbsent(type, k -> new ArrayList<>()).addAll(entries);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(IIngredientType<?> type, Object... entries) {
        add(type, Arrays.asList(entries));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:stone:1').withNbt([display:[Name:'Special Granite']])"))
    public void add(IIngredient ingredient) {
        if (IngredientHelper.isEmpty(ingredient)) {
            GroovyLog.msg("Error adding items {}", ingredient)
                    .add("Items must not be empty")
                    .error()
                    .post();
            return;
        }
        if (IngredientHelper.isItem(ingredient)) {
            addedIngredients.computeIfAbsent(VanillaTypes.ITEM, k -> new ArrayList<>()).addAll(Arrays.asList(ingredient.getMatchingStacks()));
        } else if (IngredientHelper.isFluid(ingredient)) {
            addedIngredients.computeIfAbsent(VanillaTypes.FLUID, k -> new ArrayList<>()).add(IngredientHelper.toFluidStack(ingredient));
        } else {
            GroovyLog.msg("Error adding items {}", ingredient)
                    .add("Could not identify the type")
                    .error()
                    .post();
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(IIngredient... ingredients) {
        for (IIngredient ingredient : ingredients) {
            add(ingredient);
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(Iterable<IIngredient> ingredients) {
        for (IIngredient ingredient : ingredients) {
            add(ingredient);
        }
    }

    @MethodDescription(example = @Example(value = "VanillaTypes.ITEM, item('minecraft:bed:*')", imports = "mezz.jei.api.ingredients.VanillaTypes"))
    public void hide(IIngredientType<?> type, Collection<Object> entries) {
        hiddenIngredients.computeIfAbsent(type, k -> new ArrayList<>()).addAll(entries);
    }

    @MethodDescription(example = @Example(value = "mekanism.client.jei.MekanismJEI.TYPE_GAS, gas('tritium')", commented = true))
    public void hide(IIngredientType<?> type, Object... entries) {
        hide(type, Arrays.asList(entries));
    }

    @MethodDescription(example = {
            @Example("fluid('water')"),
            @Example("item('minecraft:stone:1'), item('minecraft:stone:3')")
    })
    public void hide(IIngredient ingredient) {
        if (IngredientHelper.isEmpty(ingredient)) {
            GroovyLog.msg("Error hiding items {}", ingredient)
                    .add("Items must not be empty")
                    .error()
                    .post();
            return;
        }
        if (IngredientHelper.isItem(ingredient)) {
            hiddenIngredients.computeIfAbsent(VanillaTypes.ITEM, k -> new ArrayList<>()).addAll(Arrays.asList(ingredient.getMatchingStacks()));
        } else if (IngredientHelper.isFluid(ingredient)) {
            hiddenIngredients.computeIfAbsent(VanillaTypes.FLUID, k -> new ArrayList<>()).add(IngredientHelper.toFluidStack(ingredient));
        } else {
            GroovyLog.msg("Error hiding items {}", ingredient)
                    .add("Could not identify the type")
                    .error()
                    .post();
        }
    }

    @MethodDescription
    public void hide(IIngredient... ingredients) {
        for (IIngredient ingredient : ingredients) {
            hide(ingredient);
        }
    }

    @MethodDescription
    public void hide(Iterable<IIngredient> ingredients) {
        for (IIngredient ingredient : ingredients) {
            hide(ingredient);
        }
    }

    @MethodDescription(example = {
            @Example(value = "com.buuz135.thaumicjei.ThaumcraftJEIPlugin.ASPECT_LIST", commented = true),
            @Example(value = "mekanism.client.jei.MekanismJEI.TYPE_GAS", commented = true),
            @Example(value = "VanillaTypes.ITEM", imports = "mezz.jei.api.ingredients.VanillaTypes", commented = true),
            @Example(value = "VanillaTypes.ENCHANT", imports = "mezz.jei.api.ingredients.VanillaTypes", commented = true),
            @Example(value = "VanillaTypes.FLUID", imports = "mezz.jei.api.ingredients.VanillaTypes", commented = true),
    })
    public void hideByType(IIngredientType<?> type) {
        hiddenTypes.add(type);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void hideAll() {
        hideAllIngredients = true;
    }

    @MethodDescription
    public void removeAndHide(IIngredient ingredient) {
        if (IngredientHelper.isEmpty(ingredient)) {
            GroovyLog.msg("Error remove and hide items {}", ingredient)
                    .add("Items must not be empty")
                    .error()
                    .post();
            return;
        }
        hide(ingredient);
        VanillaModule.crafting.removeByOutput(ingredient, false);
    }

    @MethodDescription
    public void removeAndHide(IIngredient... ingredients) {
        for (IIngredient ingredient : ingredients) {
            if (IngredientHelper.isEmpty(ingredient)) {
                GroovyLog.msg("Error remove and hide items {}", ingredient)
                        .add("Items must not be empty")
                        .error()
                        .post();
                return;
            }
            hide(ingredient);
        }
        for (IRecipe recipe : ForgeRegistries.RECIPES) {
            if (recipe.getRegistryName() != null) {
                for (IIngredient ingredient : ingredients) {
                    if (ingredient.test(recipe.getRecipeOutput())) {
                        ReloadableRegistryManager.removeRegistryEntry(ForgeRegistries.RECIPES, recipe.getRegistryName());
                        break;
                    }
                }
            }
        }
    }

    @MethodDescription
    public void removeAndHide(Iterable<IIngredient> ingredients) {
        for (IIngredient ingredient : ingredients) {
            if (IngredientHelper.isEmpty(ingredient)) {
                GroovyLog.msg("Error remove and hide items {}", ingredient)
                        .add("Items must not be empty")
                        .error()
                        .post();
                return;
            }
            hide(ingredient);
        }
        for (IRecipe recipe : ForgeRegistries.RECIPES) {
            if (recipe.getRegistryName() != null) {
                for (IIngredient ingredient : ingredients) {
                    if (ingredient.test(recipe.getRecipeOutput())) {
                        ReloadableRegistryManager.removeRegistryEntry(ForgeRegistries.RECIPES, recipe.getRegistryName());
                        break;
                    }
                }
            }
        }
    }

    @MethodDescription
    public void yeet(IIngredient ingredient) {
        removeAndHide(ingredient);
    }

    @MethodDescription
    public void yeet(IIngredient... ingredients) {
        removeAndHide(ingredients);
    }

    @MethodDescription
    public void yeet(Iterable<IIngredient> ingredients) {
        removeAndHide(ingredients);
    }

}
