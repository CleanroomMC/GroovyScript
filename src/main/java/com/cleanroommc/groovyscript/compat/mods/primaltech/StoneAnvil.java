package com.cleanroommc.groovyscript.compat.mods.primaltech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.api.jeiremoval.IJEIRemoval;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.IOperation;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.primal_tech.StoneAnvilRecipesAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import primal_tech.jei.stone_anvil.StoneAnvilCategory;
import primal_tech.recipes.StoneAnvilRecipes;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RegistryDescription
public class StoneAnvil extends VirtualizedRegistry<StoneAnvilRecipes> implements IJEIRemoval.Default {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay'))"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond') * 4)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        StoneAnvilRecipesAccessor.getRecipes().removeAll(removeScripted());
        StoneAnvilRecipesAccessor.getRecipes().addAll(restoreFromBackup());
    }

    public void add(StoneAnvilRecipes recipe) {
        if (recipe != null) {
            addScripted(recipe);
            StoneAnvilRecipesAccessor.getRecipes().add(recipe);
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public StoneAnvilRecipes add(ItemStack output, IIngredient input) {
        return recipeBuilder()
                .input(input)
                .output(output)
                .register();
    }

    public boolean remove(StoneAnvilRecipes recipe) {
        if (StoneAnvilRecipesAccessor.getRecipes().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example(value = "item('primal_tech:flint_block')", commented = true))
    public boolean removeByInput(IIngredient input) {
        return StoneAnvilRecipesAccessor.getRecipes().removeIf(recipe -> {
            if (input.test(recipe.getInput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:flint')"))
    public boolean removeByOutput(IIngredient output) {
        return StoneAnvilRecipesAccessor.getRecipes().removeIf(recipe -> {
            if (output.test(recipe.getOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<StoneAnvilRecipes> streamRecipes() {
        return new SimpleObjectStream<>(StoneAnvilRecipesAccessor.getRecipes()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        StoneAnvilRecipesAccessor.getRecipes().forEach(this::addBackup);
        StoneAnvilRecipesAccessor.getRecipes().clear();
    }

    @Override
    public @NotNull Collection<String> getCategories() {
        return Collections.singletonList(StoneAnvilCategory.UID);
    }

    @Override
    public @NotNull List<IOperation> getJEIOperations() {
        return Default.excludeSlots(2);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<StoneAnvilRecipes> {

        @Override
        public String getErrorMsg() {
            return "Error adding Primal Tech Stone Anvil recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable StoneAnvilRecipes register() {
            if (!validate()) return null;
            StoneAnvilRecipes recipe = null;
            for (ItemStack matchingStack : input.get(0).getMatchingStacks()) {
                recipe = StoneAnvilRecipesAccessor.createStoneAnvilRecipes(output.get(0), matchingStack);
                ModSupport.PRIMAL_TECH.get().stoneAnvil.add(recipe);
            }
            return recipe;
        }
    }
}
