package com.cleanroommc.groovyscript.compat.mods.lightningcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import sblectric.lightningcraft.api.recipes.LightningCrusherRecipe;
import sblectric.lightningcraft.recipes.LightningCrusherRecipes;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RegistryDescription
public class Crusher extends StandardListRegistry<LightningCrusherRecipe> {

    @Override
    public Collection<LightningCrusherRecipe> getRecipes() {
        return LightningCrusherRecipes.instance().getRecipeList();
    }

    @MethodDescription(example = @Example("item('minecraft:saddle')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> r.getInput().stream().anyMatch(input) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('minecraft:redstone')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.getOutput()) && doAddBackup(r));
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond_block')).output(item('minecraft:nether_star'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<LightningCrusherRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding LightningCraft Crusher recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable LightningCrusherRecipe register() {
            if (!validate()) return null;
            List<ItemStack> inputs = Arrays.asList(input.get(0).getMatchingStacks());
            LightningCrusherRecipe recipe = new LightningCrusherRecipe(output.get(0), inputs);
            ModSupport.LIGHTNING_CRAFT.get().crusher.add(recipe);
            return recipe;
        }
    }
}
