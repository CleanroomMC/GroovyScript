package com.cleanroommc.groovyscript.compat.mods.primaltech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.api.jeiremoval.IJEIRemoval;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.IOperation;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.primal_tech.ClayKilnRecipesAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import primal_tech.jei.clay_kiln.KilnCategory;
import primal_tech.recipes.ClayKilnRecipes;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RegistryDescription
public class ClayKiln extends VirtualizedRegistry<ClayKilnRecipes> implements IJEIRemoval.Default {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay')).cookTime(50)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond') * 4).cookTime(100)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        ClayKilnRecipesAccessor.getRecipes().removeAll(removeScripted());
        ClayKilnRecipesAccessor.getRecipes().addAll(restoreFromBackup());
    }

    public void add(ClayKilnRecipes recipe) {
        if (recipe != null) {
            addScripted(recipe);
            ClayKilnRecipesAccessor.getRecipes().add(recipe);
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public ClayKilnRecipes add(ItemStack output, IIngredient input, int cookTime) {
        return recipeBuilder()
                .cookTime(cookTime)
                .input(input)
                .output(output)
                .register();
    }

    public boolean remove(ClayKilnRecipes recipe) {
        if (ClayKilnRecipesAccessor.getRecipes().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("item('minecraft:gravel')"))
    public boolean removeByInput(IIngredient input) {
        return ClayKilnRecipesAccessor.getRecipes().removeIf(recipe -> {
            if (input.test(recipe.getInput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('primal_tech:charcoal_block')"))
    public boolean removeByOutput(IIngredient output) {
        return ClayKilnRecipesAccessor.getRecipes().removeIf(recipe -> {
            if (output.test(recipe.getOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ClayKilnRecipes> streamRecipes() {
        return new SimpleObjectStream<>(ClayKilnRecipesAccessor.getRecipes()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ClayKilnRecipesAccessor.getRecipes().forEach(this::addBackup);
        ClayKilnRecipesAccessor.getRecipes().clear();
    }

    @Override
    public @NotNull Collection<String> getCategories() {
        return Collections.singletonList(KilnCategory.UID);
    }

    @Override
    public @NotNull List<IOperation> getJEIOperations() {
        return Default.excludeSlots(2);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ClayKilnRecipes> {

        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int cookTime;

        @RecipeBuilderMethodDescription
        public RecipeBuilder cookTime(int cookTime) {
            this.cookTime = cookTime;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Primal Tech Clay Kiln recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(cookTime < 0, "cookTime must be greater than or equal to 0, yet it was {}", cookTime);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ClayKilnRecipes register() {
            if (!validate()) return null;
            ClayKilnRecipes recipe = null;
            for (ItemStack matchingStack : input.get(0).getMatchingStacks()) {
                recipe = ClayKilnRecipesAccessor.createClayKilnRecipes(output.get(0), matchingStack, cookTime);
                ModSupport.PRIMAL_TECH.get().clayKiln.add(recipe);
            }
            return recipe;
        }
    }
}
