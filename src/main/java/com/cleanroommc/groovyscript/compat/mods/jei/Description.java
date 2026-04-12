package com.cleanroommc.groovyscript.compat.mods.jei;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.jei.IngredientInfoRecipeAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;

import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.plugins.jei.info.IngredientInfoRecipeCategory;
import net.minecraftforge.fluids.FluidStack;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class Description extends VirtualizedRegistry<Description.DescriptionEntry> {

    public static class DescriptionEntry {
        List<Object> ingredients;
        String[] descriptionKeys;

        @SuppressWarnings("unchecked")
        DescriptionEntry(@Nonnull List<?> ingredients, @Nullable List<String> descriptionKeys) {
            if (ingredients.isEmpty()) {
                throw new IllegalArgumentException("JEI Description Entries must not have an empty list of ingredients, got " + ingredients);
            }

            // This cast is not exactly correct, but Java/JVM allows it due to type erasure
            this.ingredients = (List<Object>) ingredients;

            this.descriptionKeys = descriptionKeys == null ? new String[0] : descriptionKeys.toArray(new String[0]);
        }

        @Nullable
        IIngredientType<Object> validateIngredientType(IModRegistry modRegistry) {
            Object first = ingredients.get(0);
            IIngredientType<Object> ingredientType = modRegistry.getIngredientRegistry().getIngredientType(first);
            if (ingredientType == null) {
                return null;
            }
            for (Object o : ingredients) {
                if (!ingredientType.equals(modRegistry.getIngredientRegistry().getIngredientType(o))) {
                    return null;
                }
            }
            return ingredientType;
        }
    }

    private <T> void addIngredientInfo(IModRegistry modRegistry, List<T> ingredients, IIngredientType<T> ingredientType, String[] description) {
        // Force Java to pick the correct overload we use
        // This is needed because the method has overloads in a way T = Object cannot be substituted
        modRegistry.addIngredientInfo(ingredients, ingredientType, description);
    }

    /**
     * Called by {@link JeiPlugin#register}
     */
    @GroovyBlacklist
    public void applyAdditions(IModRegistry modRegistry) {
        for (DescriptionEntry entry : this.getScriptedRecipes()) {
            IIngredientType<Object> ingredientType = entry.validateIngredientType(modRegistry);
            if (ingredientType != null) {
                addIngredientInfo(modRegistry, entry.ingredients, ingredientType, entry.descriptionKeys);
            } else {
                GroovyLog.msg("Unable to obtain an ingredient type for items {}", entry.ingredients.toArray())
                    .error()
                    .post();
            }
        }
    }

    /**
     * Called by {@link JeiPlugin#afterRuntimeAvailable()}
     */
    @SuppressWarnings("unchecked")
    @GroovyBlacklist
    public void applyRemovals(IModRegistry modRegistry, IRecipeRegistry recipeRegistry) {
        IIngredientRegistry ingredientRegistry = modRegistry.getIngredientRegistry();
        IngredientInfoRecipeCategory category = (IngredientInfoRecipeCategory) recipeRegistry.getRecipeCategory(VanillaRecipeCategoryUid.INFORMATION);
        if (category != null) {
            recipeRegistry.getRecipeWrappers(category).forEach(wrapper -> {
                IngredientInfoRecipeAccessor<?> accessor = (IngredientInfoRecipeAccessor<?>) wrapper;

                for (DescriptionEntry entry : this.getBackupRecipes()) {
                    IIngredientType<Object> ingredientType = entry.validateIngredientType(modRegistry);
                    IIngredientHelper<Object> helper = ingredientRegistry.getIngredientHelper(ingredientType);
                    if (ingredientType == null || !ingredientType.equals(accessor.getIngredientType())) continue;
                    if (entry.ingredients
                            .stream()
                            .anyMatch(x -> accessor.getIngredients().stream().anyMatch(a -> helper.getUniqueId(a).equals(helper.getUniqueId(x))))) {
                        // the API seems to be broken hence the cast
                        entry.descriptionKeys = ((List<String>)wrapper.getDescription()).toArray(new String[0]);
                        recipeRegistry.hideRecipe(wrapper, VanillaRecipeCategoryUid.INFORMATION);
                    }
                }
            });
        }
    }

    @Override
    public void onReload() {
        restoreFromBackup();
        removeScripted();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(IIngredient[] target, String... description) {
        addScripted(new DescriptionEntry(
            Stream.of(target).flatMap(x -> Stream.of(x.getMatchingStacks())).collect(Collectors.toList()),
            Arrays.asList(description)));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(IIngredient[] target, List<String> description) {
        add(target, description.toArray(new String[0]));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:clay'), ['wow', 'this', 'is', 'neat']"))
    public void add(IIngredient target, List<String> description) {
        add(new IIngredient[]{target}, description);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:gold_ingot') | item('minecraft:iron_block'), 'groovyscript.recipe.fluid_recipe'"))
    public void add(IIngredient target, String... description) {
        add(new IIngredient[]{target}, description);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(Object[] target, String... description) {
        addScripted(new DescriptionEntry(
            Arrays.asList(target),
            Arrays.asList(description)));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(Object[] target, List<String> description) {
        add(target, description.toArray(new String[0]));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(Object target, List<String> description) {
        add(new Object[]{target}, description);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(Object target, String... description) {
        add(new Object[]{target}, description);
    }

    // This overload set is the same as Object one. It exists because FluidStack implements IIngredient, but does not have any matching stacks,
    // which causes a JEI startup crash when trying to add that description
    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(FluidStack[] target, String... description) {
        addScripted(new DescriptionEntry(
            Arrays.asList(target),
            Arrays.asList(description)));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(FluidStack[] target, List<String> description) {
        add(target, description.toArray(new String[0]));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("fluid('lava') * 500, ['very', 'hot', 'fluid']"))
    public void add(FluidStack target, List<String> description) {
        add(new FluidStack[]{target}, description);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("fluid('water') * 1000, 'groovyscript.recipe.fluid_recipe'"))
    public void add(FluidStack target, String... description) {
        add(new FluidStack[]{target}, description);
    }

    @MethodDescription(example = @Example(value = "item('thaumcraft:triple_meat_treat')", commented = true))
    public void remove(IIngredient target) {
        addBackup(new DescriptionEntry(Arrays.asList(target.getMatchingStacks()), null));
    }

    @MethodDescription(example = @Example(value = "fluid('water')", commented = true))
    public void remove(Object target) {
        addBackup(new DescriptionEntry(Arrays.asList(target), null));
    }
}
