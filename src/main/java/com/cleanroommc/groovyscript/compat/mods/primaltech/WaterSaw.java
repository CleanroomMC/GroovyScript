package com.cleanroommc.groovyscript.compat.mods.primaltech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.primal_tech.WaterSawRecipesAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import primal_tech.recipes.WaterSawRecipes;

import java.util.Collection;

@RegistryDescription
public class WaterSaw extends StandardListRegistry<WaterSawRecipes> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay')).choppingTime(50)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond') * 4).choppingTime(100)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<WaterSawRecipes> getRecipes() {
        return WaterSawRecipesAccessor.getRecipes();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public WaterSawRecipes add(ItemStack output, IIngredient input, int choppingTime) {
        return recipeBuilder()
                .choppingTime(choppingTime)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(example = @Example("item('minecraft:log')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(recipe -> {
            if (input.test(recipe.getInput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:planks:1')"))
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
    public static class RecipeBuilder extends AbstractRecipeBuilder<WaterSawRecipes> {

        @Property(comp = @Comp(gte = 0))
        private int choppingTime;

        @RecipeBuilderMethodDescription
        public RecipeBuilder choppingTime(int choppingTime) {
            this.choppingTime = choppingTime;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Primal Tech Water Saw recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(choppingTime < 0, "choppingTime must be greater than or equal to 0, yet it was {}", choppingTime);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable WaterSawRecipes register() {
            if (!validate()) return null;
            WaterSawRecipes recipe = null;
            for (ItemStack matchingStack : input.get(0).getMatchingStacks()) {
                recipe = WaterSawRecipesAccessor.createWaterSawRecipes(output.get(0), matchingStack, choppingTime);
                ModSupport.PRIMAL_TECH.get().waterSaw.add(recipe);
            }
            return recipe;
        }
    }
}
