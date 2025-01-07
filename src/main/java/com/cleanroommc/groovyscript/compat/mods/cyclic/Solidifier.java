package com.cleanroommc.groovyscript.compat.mods.cyclic;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.lothrazar.cyclicmagic.CyclicContent;
import com.lothrazar.cyclicmagic.block.solidifier.RecipeSolidifier;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

@RegistryDescription
public class Solidifier extends StandardListRegistry<RecipeSolidifier> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).fluidInput(fluid('water') * 175).output(item('minecraft:gold_ingot') * 3)"),
            @Example(".input(ore('logWood'), ore('sand'), ore('gravel'), item('minecraft:diamond')).fluidInput(fluid('lava') * 500).output(item('minecraft:clay') * 2)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public boolean isEnabled() {
        return CyclicContent.solidifier.enabled();
    }

    @Override
    public Collection<RecipeSolidifier> getRecipes() {
        return RecipeSolidifier.recipes;
    }

    @MethodDescription(example = {
            @Example("item('minecraft:bucket')"), @Example("fluid('water')"),
    })
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(recipe -> {
            if (input.test(recipe.getFluidIngredient()) || recipe.getRecipeInput().stream().anyMatch(input)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('cyclicmagic:crystallized_obsidian')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(recipe -> {
            if (output.test(recipe.getRecipeOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(gte = 1, lte = 4))
    @Property(property = "fluidInput", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipeSolidifier> {

        @Override
        public String getErrorMsg() {
            return "Error adding Cyclic Solidifier recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 4, 1, 1);
            validateFluids(msg, 1, 1, 0, 0);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipeSolidifier register() {
            if (!validate()) return null;
            RecipeSolidifier recipe = null;
            List<List<ItemStack>> cartesian = IngredientHelper.cartesianProductItemStacks(input);
            for (List<ItemStack> stacks : cartesian) {
                recipe = new RecipeSolidifier(stacks.toArray(new ItemStack[0]), output.get(0), fluidInput.get(0).getFluid().getName(), fluidInput.get(0).amount);
                ModSupport.CYCLIC.get().solidifier.add(recipe);
            }
            return recipe;
        }
    }
}
