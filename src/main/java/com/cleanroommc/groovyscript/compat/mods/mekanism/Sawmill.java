package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.SawmillRecipe;
import mekanism.common.recipe.outputs.ChanceOutput;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Sawmill extends VirtualizedMekanismRegistry<SawmillRecipe> {

    public Sawmill() {
        super(RecipeHandler.Recipe.PRECISION_SAWMILL);
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond_block')).output(item('minecraft:diamond') * 9).extra(item('minecraft:clay_ball'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(description = "groovyscript.wiki.mekanism.sawmill.add0", type = MethodDescription.Type.ADDITION)
    public SawmillRecipe add(IIngredient ingredient, ItemStack output) {
        return add(ingredient, output, null, 0.0);
    }

    @MethodDescription(description = "groovyscript.wiki.mekanism.sawmill.add1", type = MethodDescription.Type.ADDITION)
    public SawmillRecipe add(IIngredient ingredient, ItemStack output, ItemStack secondary) {
        return add(ingredient, output, secondary, 1.0);
    }

    @MethodDescription(description = "groovyscript.wiki.mekanism.sawmill.add2", type = MethodDescription.Type.ADDITION, example = @Example(value = "item('minecraft:diamond_block'), item('minecraft:diamond') * 9, item('minecraft:clay_ball'), 0.7", commented = true))
    public SawmillRecipe add(IIngredient ingredient, ItemStack output, ItemStack secondary, double chance) {
        return recipeBuilder().extra(secondary).chance(chance).output(output).input(ingredient).register();
    }

    @MethodDescription(example = @Example("item('minecraft:ladder')"))
    public boolean removeByInput(IIngredient ingredient) {
        if (IngredientHelper.isEmpty(ingredient)) {
            removeError("input must not be empty");
            return false;
        }
        boolean found = false;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            SawmillRecipe recipe = recipeRegistry.get().remove(new ItemStackInput(itemStack));
            if (recipe != null) {
                addBackup(recipe);
                found = true;
            }
        }
        if (!found) {
            removeError("could not find recipe for {}", ingredient);
        }
        return found;
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<SawmillRecipe> {

        @Property(defaultValue = "ItemStack.EMPTY")
        private ItemStack extra = ItemStack.EMPTY;
        @Property(defaultValue = "1.0", comp = @Comp(gte = 0, lte = 1))
        private double chance = 1.0;

        @RecipeBuilderMethodDescription
        public RecipeBuilder extra(ItemStack extra) {
            this.extra = extra;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder chance(double chance) {
            this.chance = chance;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Sawmill recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(chance < 0 || chance > 1, "chance must be between 0 and 1.0, yet it was {}", chance);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable SawmillRecipe register() {
            if (!validate()) return null;
            ChanceOutput chanceOutput = IngredientHelper.isEmpty(extra)
                    ? new ChanceOutput(output.get(0))
                    : new ChanceOutput(output.get(0), extra, chance);
            SawmillRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                SawmillRecipe r = new SawmillRecipe(new ItemStackInput(itemStack), chanceOutput);
                if (recipe == null) recipe = r;
                ModSupport.MEKANISM.get().sawmill.add(r);
            }
            return recipe;
        }
    }
}
