package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.GasRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.OxidationRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class ChemicalOxidizer extends VirtualizedMekanismRegistry<OxidationRecipe> {

    public ChemicalOxidizer() {
        super(RecipeHandler.Recipe.CHEMICAL_OXIDIZER, Alias.generateOfClassAnd(ChemicalOxidizer.class, "Oxidizer"));
    }

    @RecipeBuilderDescription(example = @Example(".input(ore('dustGold')).gasOutput(gas('gold'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "ore('dustGold'), gas('gold')", commented = true))
    public OxidationRecipe add(IIngredient ingredient, GasStack output) {
        return recipeBuilder().gasOutput(output).input(ingredient).register();
    }

    @MethodDescription(example = @Example("ore('dustSulfur')"))
    public boolean removeByInput(IIngredient ingredient) {
        if (IngredientHelper.isEmpty(ingredient)) {
            removeError("input must not be empty");
            return false;
        }
        boolean found = false;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            OxidationRecipe recipe = recipeRegistry.get().remove(new ItemStackInput(itemStack));
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
    @Property(property = "gasOutput", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends GasRecipeBuilder<OxidationRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Chemical Oxidizer recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg);
            validateGases(msg, 0, 0, 1, 1);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable OxidationRecipe register() {
            if (!validate()) return null;
            OxidationRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                OxidationRecipe r = new OxidationRecipe(itemStack, gasOutput.get(0));
                if (recipe == null) recipe = r;
                ModSupport.MEKANISM.get().chemicalOxidizer.add(r);
            }
            return recipe;
        }
    }
}
