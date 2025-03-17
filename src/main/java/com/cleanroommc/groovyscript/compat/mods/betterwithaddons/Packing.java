package com.cleanroommc.groovyscript.compat.mods.betterwithaddons;

import betterwithaddons.crafting.manager.CraftingManagerPacking;
import betterwithaddons.crafting.recipes.PackingRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Packing extends StandardListRegistry<PackingRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:gold_ingot')).compress(blockstate('minecraft:clay'))"),
            @Example(".input(item('minecraft:clay') * 10).compress(blockstate('minecraft:diamond_block'))"),
            @Example(".input(item('minecraft:diamond')).compress(blockstate('minecraft:dirt')).jeiOutput(item('minecraft:diamond') * 64)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<PackingRecipe> getRecipes() {
        return CraftingManagerPacking.getInstance().getRecipes();
    }

    @MethodDescription(example = @Example("blockstate('minecraft:gravel')"))
    public boolean removeByOutput(IBlockState output) {
        return getRecipes().removeIf(r -> output.equals(r.output) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('minecraft:clay_ball')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> {
            for (var itemstack : r.getRecipeInputs()) {
                if (input.test(itemstack)) {
                    return doAddBackup(r);
                }
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<PackingRecipe> {

        @Property(comp = @Comp(not = "null"))
        private IBlockState compress;
        @Property(defaultValue = "compress as ItemStack", comp = @Comp(not = "null"))
        private ItemStack jeiOutput;

        @RecipeBuilderMethodDescription
        public RecipeBuilder compress(IBlockState compress) {
            this.compress = compress;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder jeiOutput(ItemStack jeiOutput) {
            this.jeiOutput = jeiOutput;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Better With Addons Packing recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg);
            msg.add(compress == null, "compress cannot be null, yet it was");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable PackingRecipe register() {
            if (!validate()) return null;
            PackingRecipe recipe = new PackingRecipe(BetterWithAddons.FromIngredient.fromIIngredient(input.get(0)), compress);
            recipe.setJeiOutput(IngredientHelper.isEmpty(jeiOutput) ? IngredientHelper.toItemStack(compress) : jeiOutput);
            ModSupport.BETTER_WITH_ADDONS.get().packing.add(recipe);
            return recipe;
        }
    }
}
