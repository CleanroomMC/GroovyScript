package com.cleanroommc.groovyscript.compat.mods.jei;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
import com.cleanroommc.groovyscript.helper.ingredient.ItemsIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;

import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.plugins.jei.info.IngredientInfoRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class Description extends VirtualizedRegistry<Description.DescriptionEntry<?>> {

    static class DescriptionEntry<T> {
        T representative;
        List<T> ingredients;
        IIngredientType<T> ingredientType;
        String[] descriptionKeys;

        DescriptionEntry(@Nonnull List<T> ingredients, @Nonnull IIngredientType<T> ingredientType, @Nullable List<String> descriptionKeys) {
            if (ingredients.isEmpty()) {
                throw new IllegalArgumentException("JEI Description Entries must not have an empty list of ingredients, got " + ingredients);
            }
            Objects.requireNonNull(ingredientType);

            this.ingredients = ingredients;
            this.ingredientType = ingredientType;
            this.representative = this.ingredients.get(0);
            Objects.requireNonNull(representative);

            this.descriptionKeys = descriptionKeys == null ? new String[0] : descriptionKeys.toArray(new String[0]);
        }
    }

    /**
     * Called by {@link JeiPlugin#register}
     */
    @SuppressWarnings("unchecked")
    @GroovyBlacklist
    public void applyAdditions(IModRegistry modRegistry) {
        for (DescriptionEntry<?> entry : this.getScriptedRecipes()) {
            System.out.println("adding entry: " + entry);
            if (entry.representative instanceof ItemStack) {
                modRegistry.addIngredientInfo((List<ItemStack>) entry.ingredients, (IIngredientType<ItemStack>) entry.ingredientType, entry.descriptionKeys);
            } else if (entry.representative instanceof FluidStack) {
                modRegistry.addIngredientInfo((List<FluidStack>) entry.ingredients, (IIngredientType<FluidStack>) entry.ingredientType, entry.descriptionKeys);
            } else {
                GroovyLog.msg("Got a Description Entry of an invalid type, this is probably a bug in Groovyscript").error().post();
            }
        }
    }

    private boolean compareObjects(Object inEntry, Object inRegistry) {
        if (inEntry instanceof ItemStack ieStack) {
            return inRegistry instanceof ItemStack irStack && new ItemsIngredient(ieStack).test(irStack);
        } else if (inEntry instanceof FluidStack ieStack) {
            return inRegistry instanceof FluidStack irStack && ieStack.getFluid().equals(irStack.getFluid());
        } else {
            GroovyLog.msg("Got a Description Entry of type {} which is not supported, this is probably a bug in Groovyscript", inEntry.getClass().getName())
                .error()
                .post();
            return false;
        }
    }

    /**
     * Called by {@link JeiPlugin#afterRuntimeAvailable()}
     */
    @SuppressWarnings("unchecked")
    @GroovyBlacklist
    public void applyRemovals(IRecipeRegistry recipeRegistry) {
        IngredientInfoRecipeCategory category = (IngredientInfoRecipeCategory) recipeRegistry.getRecipeCategory(VanillaRecipeCategoryUid.INFORMATION);
        if (category != null) {
            recipeRegistry.getRecipeWrappers(category).forEach(wrapper -> {
                IngredientInfoRecipeAccessor<?> accessor = (IngredientInfoRecipeAccessor<?>) wrapper;

                for (DescriptionEntry<?> entry : this.getBackupRecipes()) {
                    System.out.println("removing entry: " + entry);
                    if (!entry.ingredientType.equals(accessor.getIngredientType())) continue;
                    if (entry.ingredients
                            .stream()
                            .anyMatch(x -> accessor.getIngredients().stream().anyMatch(a -> compareObjects(x, a)))) {
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
        addScripted(new DescriptionEntry<>(
            Stream.of(target).flatMap(x -> Stream.of(x.getMatchingStacks())).collect(Collectors.toList()),
            VanillaTypes.ITEM,
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

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:gold_ingot'), 'groovyscript.recipe.fluid_recipe'"))
    public void add(IIngredient target, String... description) {
        add(new IIngredient[]{target}, description);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(FluidStack[] target, String... description) {
        addScripted(new DescriptionEntry<>(
            Arrays.asList(target),
            VanillaTypes.FLUID,
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
        addBackup(new DescriptionEntry<>(Arrays.asList(target.getMatchingStacks()), VanillaTypes.ITEM, null));
    }

    @MethodDescription(example = @Example(value = "fluid('water')", commented = true))
    public void remove(FluidStack target) {
        addBackup(new DescriptionEntry<>(Arrays.asList(target), VanillaTypes.FLUID, null));
    }
}
