package com.cleanroommc.groovyscript.compat.mods.primaltech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.primal_tech.ClayKilnRecipesAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import primal_tech.recipes.ClayKilnRecipes;

import java.util.Collection;

@RegistryDescription
public class ClayKiln extends StandardListRegistry<ClayKilnRecipes> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay')).cookTime(50)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond') * 4).cookTime(100)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<ClayKilnRecipes> getRecipes() {
        return ClayKilnRecipesAccessor.getRecipes();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public ClayKilnRecipes add(ItemStack output, IIngredient input, int cookTime) {
        return recipeBuilder()
                .cookTime(cookTime)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(example = @Example("item('minecraft:gravel')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(recipe -> {
            if (input.test(recipe.getInput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('primal_tech:charcoal_block')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(recipe -> {
            if (output.test(recipe.getOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ClayKilnRecipes> {

        @Property(comp = @Comp(gte = 0))
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
