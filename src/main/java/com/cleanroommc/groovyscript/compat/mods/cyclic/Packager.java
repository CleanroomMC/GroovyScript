package com.cleanroommc.groovyscript.compat.mods.cyclic;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.lothrazar.cyclicmagic.CyclicContent;
import com.lothrazar.cyclicmagic.block.packager.RecipePackager;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

@RegistryDescription
public class Packager extends StandardListRegistry<RecipePackager> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay'))"),
            @Example(".input(ore('logWood'), ore('sand'), ore('gravel'), item('minecraft:diamond'), item('minecraft:diamond_block'), item('minecraft:gold_block')).output(item('minecraft:clay') * 4)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public boolean isEnabled() {
        return CyclicContent.packager.enabled();
    }

    @Override
    public Collection<RecipePackager> getRecipes() {
        return RecipePackager.recipes;
    }

    @MethodDescription(example = @Example("item('minecraft:grass')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(recipe -> {
            if (recipe.getInput().stream().anyMatch(input)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:melon_block')"))
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
    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipePackager> {

        @Override
        public String getErrorMsg() {
            return "Error adding Cyclic Packager recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 6, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipePackager register() {
            if (!validate()) return null;
            RecipePackager recipe = null;
            List<List<ItemStack>> cartesian = IngredientHelper.cartesianProductItemStacks(input);
            for (List<ItemStack> stacks : cartesian) {
                recipe = new RecipePackager(output.get(0), stacks.toArray(new ItemStack[0]));
                ModSupport.CYCLIC.get().packager.add(recipe);
            }
            return recipe;
        }
    }
}
