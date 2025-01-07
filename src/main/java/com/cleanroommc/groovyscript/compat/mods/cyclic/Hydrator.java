package com.cleanroommc.groovyscript.compat.mods.cyclic;


import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.lothrazar.cyclicmagic.CyclicContent;
import com.lothrazar.cyclicmagic.block.hydrator.RecipeHydrate;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

@RegistryDescription
public class Hydrator extends StandardListRegistry<RecipeHydrate> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay'))"),
            @Example(".input(ore('logWood'), ore('sand'), ore('gravel'), item('minecraft:diamond')).output(item('minecraft:clay') * 8).water(100)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public boolean isEnabled() {
        return CyclicContent.hydrator.enabled();
    }

    @Override
    public Collection<RecipeHydrate> getRecipes() {
        return RecipeHydrate.recipes;
    }

    @MethodDescription(example = @Example("item('minecraft:dirt')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(recipe -> {
            if (recipe.getRecipeInput().stream().anyMatch(input)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:clay_ball')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(recipe -> {
            if (output.test(recipe.getRecipeOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(gte = 1, lte = 6))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipeHydrate> {

        @Property(defaultValue = "25", comp = @Comp(gte = 0))
        private int water = 25;

        @RecipeBuilderMethodDescription
        public RecipeBuilder water(int water) {
            this.water = water;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Cyclic Hydrator recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 6, 1, 1);
            validateFluids(msg);
            msg.add(water < 0, "water must be a non-negative integer, yet it was {}", water);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipeHydrate register() {
            if (!validate()) return null;
            RecipeHydrate recipe = null;
            List<List<ItemStack>> cartesian = IngredientHelper.cartesianProductItemStacks(input);
            for (List<ItemStack> stacks : cartesian) {
                recipe = new RecipeHydrate(stacks.toArray(new ItemStack[0]), output.get(0), water);
                ModSupport.CYCLIC.get().hydrator.add(recipe);
            }
            return recipe;
        }
    }
}
