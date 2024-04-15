package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.DoubleMachineInput;
import mekanism.common.recipe.machines.CombinerRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Combiner extends VirtualizedMekanismRegistry<CombinerRecipe> {

    public Combiner() {
        super(RecipeHandler.Recipe.COMBINER);
    }

    @RecipeBuilderDescription(example = @Example(".input(ore('gemQuartz') * 8).extra(item('minecraft:netherrack')).output(item('minecraft:quartz_ore'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "ore('gemQuartz') * 8, item('minecraft:netherrack'), item('minecraft:quartz_ore')", commented = true))
    public CombinerRecipe add(IIngredient ingredient, ItemStack extra, ItemStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Crusher recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(extra), () -> "extra input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        extra = extra.copy();
        output = output.copy();
        CombinerRecipe recipe1 = null;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            CombinerRecipe recipe = new CombinerRecipe(itemStack.copy(), extra, output);
            if (recipe1 == null) recipe1 = recipe;
            recipeRegistry.put(recipe);
            addScripted(recipe);
        }
        return recipe1;
    }

    @MethodDescription(example = @Example("item('minecraft:flint'), item('minecraft:cobblestone')"))
    public boolean removeByInput(IIngredient ingredient, ItemStack extra) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Mekanism Combiner recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(extra), () -> "extra input must not be empty");
        if (msg.postIfNotEmpty()) return false;

        boolean found = false;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            CombinerRecipe recipe = recipeRegistry.get().remove(new DoubleMachineInput(itemStack, extra));
            if (recipe != null) {
                addBackup(recipe);
                found = true;
            }
        }
        if (!found) {
            removeError("could not find recipe for {} and {}", ingredient, extra);
        }
        return found;
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CombinerRecipe> {

        @Property(defaultValue = "new ItemStack(Blocks.COBBLESTONE)")
        private ItemStack extra;

        public RecipeBuilder extra(ItemStack extra) {
            this.extra = extra;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Combiner recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CombinerRecipe register() {
            if (!validate()) return null;
            CombinerRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                CombinerRecipe r = new CombinerRecipe(itemStack.copy(), extra, output.get(0));
                if (recipe == null) recipe = r;
                ModSupport.MEKANISM.get().combiner.add(r);
            }
            return recipe;
        }
    }

}
