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
import mekanism.common.recipe.machines.CrusherRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Crusher extends VirtualizedMekanismRegistry<CrusherRecipe> {

    public Crusher() {
        super(RecipeHandler.Recipe.CRUSHER);
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay_ball')).output(item('minecraft:gold_ingot'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "item('minecraft:clay_ball'), item('minecraft:gold_ingot')", commented = true))
    public CrusherRecipe add(IIngredient ingredient, ItemStack output) {
        return recipeBuilder().output(output).input(ingredient).register();
    }

    @MethodDescription(example = @Example("ore('ingotTin')"))
    public boolean removeByInput(IIngredient ingredient) {
        if (IngredientHelper.isEmpty(ingredient)) {
            removeError("input must not be empty");
            return false;
        }
        boolean found = false;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            CrusherRecipe recipe = recipeRegistry.get().remove(new ItemStackInput(itemStack));
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
    public static class RecipeBuilder extends AbstractRecipeBuilder<CrusherRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Crusher recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CrusherRecipe register() {
            if (!validate()) return null;
            CrusherRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                CrusherRecipe r = new CrusherRecipe(itemStack, output.get(0));
                if (recipe == null) recipe = r;
                ModSupport.MEKANISM.get().crusher.add(r);
            }
            return recipe;
        }
    }
}
