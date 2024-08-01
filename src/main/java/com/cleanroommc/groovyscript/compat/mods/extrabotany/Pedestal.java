package com.cleanroommc.groovyscript.compat.mods.extrabotany;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.IJEIRemoval;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.OperationHandler;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.meteor.extrabotany.api.ExtraBotanyAPI;
import com.meteor.extrabotany.client.integration.jei.pedestal.HammerRecipeCategory;
import com.meteor.extrabotany.common.crafting.recipe.RecipePedestal;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RegistryDescription
public class Pedestal extends VirtualizedRegistry<RecipePedestal> implements IJEIRemoval.Default {

    @Override
    public void onReload() {
        ExtraBotanyAPI.pedestalRecipes.removeAll(removeScripted());
        ExtraBotanyAPI.pedestalRecipes.addAll(restoreFromBackup());
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:gold_ingot'))"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond') * 2)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public RecipePedestal add(IIngredient input, ItemStack output) {
        return recipeBuilder().input(input).output(output).register();
    }

    public RecipePedestal add(RecipePedestal recipe) {
        if (recipe != null) {
            addScripted(recipe);
            ExtraBotanyAPI.pedestalRecipes.add(recipe);
        }
        return recipe;
    }

    public boolean remove(RecipePedestal recipe) {
        if (ExtraBotanyAPI.pedestalRecipes.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("item('minecraft:cobblestone')"))
    public boolean removeByInput(IIngredient input) {
        return ExtraBotanyAPI.pedestalRecipes.removeIf(r -> {
            if (input.test(r.getInput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:flint')"))
    public boolean removeByOutput(IIngredient output) {
        return ExtraBotanyAPI.pedestalRecipes.removeIf(r -> {
            if (output.test(r.getOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<RecipePedestal> streamRecipes() {
        return new SimpleObjectStream<>(ExtraBotanyAPI.pedestalRecipes).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ExtraBotanyAPI.pedestalRecipes.forEach(this::addBackup);
        ExtraBotanyAPI.pedestalRecipes.clear();
    }

    @Override
    public @NotNull Collection<String> getCategories() {
        return Collections.singletonList(HammerRecipeCategory.UID);
    }

    @Override
    public @NotNull List<OperationHandler.IOperation> getJEIOperations() {
        return Default.excludeSlots(1);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipePedestal> {

        @Override
        public String getErrorMsg() {
            return "Error adding Extra Botania Pedestal recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Nullable
        @Override
        @RecipeBuilderRegistrationMethod
        public RecipePedestal register() {
            if (!validate()) return null;
            RecipePedestal recipe = null;
            for (ItemStack matchingStack : input.get(0).getMatchingStacks()) {
                recipe = new RecipePedestal(output.get(0), matchingStack);
                ModSupport.EXTRA_BOTANY.get().pedestal.add(recipe);
            }
            return recipe;
        }
    }
}
