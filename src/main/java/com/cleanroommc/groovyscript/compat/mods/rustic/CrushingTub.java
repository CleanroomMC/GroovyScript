package com.cleanroommc.groovyscript.compat.mods.rustic;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import rustic.common.crafting.CrushingTubRecipe;
import rustic.common.crafting.ICrushingTubRecipe;
import rustic.common.crafting.Recipes;

import java.util.Collection;

@RegistryDescription
public class CrushingTub extends StandardListRegistry<ICrushingTubRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:stone')).fluidOutput(fluid('lava') * 50)"),
            @Example(".input(item('minecraft:clay')).fluidOutput(fluid('lava') * 20).byproduct(item('minecraft:gold_ingot') * 4)"),
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<ICrushingTubRecipe> getRecipes() {
        return Recipes.crushingTubRecipes;
    }

    @MethodDescription(example = {
            @Example("fluid('ironberryjuice')"), @Example("item('minecraft:sugar')")
    })
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(entry -> {
            if (output.test(entry.getResult()) || output.test(entry.getByproduct())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('rustic:wildberries')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(entry -> {
            if (input.test(entry.getInput())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "fluidOutput", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ICrushingTubRecipe> {

        @Property
        private ItemStack byproduct = ItemStack.EMPTY;

        @RecipeBuilderMethodDescription
        public RecipeBuilder byproduct(ItemStack byproduct) {
            this.byproduct = byproduct;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Rustic Crushing Tub recipe";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg, 0, 0, 1, 1);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ICrushingTubRecipe register() {
            if (!validate()) return null;
            ICrushingTubRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                ICrushingTubRecipe recipe1 = new CrushingTubRecipe(fluidOutput.get(0), itemStack, byproduct);
                ModSupport.RUSTIC.get().crushingTub.add(recipe1);
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }

    }

}
