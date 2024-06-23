package com.cleanroommc.groovyscript.compat.mods.essentialcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import essentialcraft.api.WindImbueRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class WindRune extends VirtualizedRegistry<WindImbueRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:gold_block')).output(item('minecraft:diamond_block')).espe(500)"))
    public WindRune.RecipeBuilder recipeBuilder() {
        return new WindRune.RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(WindImbueRecipe::removeRecipe);
        WindImbueRecipe.RECIPES.addAll(restoreFromBackup());
    }

    @MethodDescription(example = @Example("item('minecraft:diamond')"))
    public boolean removeByInput(IIngredient x) {
        ItemStack[] stacks = x.getMatchingStacks();
        if (stacks.length == 0) return false;
        return WindImbueRecipe.RECIPES.removeIf(r -> {
            if (r.input.test(stacks[0])) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('essentialcraft:air_potion')"))
    public boolean removeByOutput(IIngredient x) {
        return WindImbueRecipe.RECIPES.removeIf(r -> {
            if (x.test(r.result)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        WindImbueRecipe.RECIPES.forEach(this::addBackup);
        WindImbueRecipe.RECIPES.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<WindImbueRecipe> streamRecipes() {
        return new SimpleObjectStream<>(WindImbueRecipe.RECIPES).setRemover(r -> {
            addBackup(r);
            return WindImbueRecipe.RECIPES.remove(r);
        });
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<WindImbueRecipe> {
        @Property(valid = @Comp(type = Comp.Type.GTE, value = "1"))
        private int espe;

        @RecipeBuilderMethodDescription
        public RecipeBuilder espe(int cost) {
            espe = cost;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Wind Rune Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(espe < 1, "espe cost must be 1 or greater, got {}", espe);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable WindImbueRecipe register() {
            if (!validate()) return null;
            Ingredient inputItem = input.get(0).toMcIngredient();
            WindImbueRecipe recipe = new WindImbueRecipe(inputItem, output.get(0), espe);  // also adds the recipe
            ModSupport.ESSENTIALCRAFT.get().windRune.addScripted(recipe);
            return recipe;
        }
    }
}
