package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.GasRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.GasInput;
import mekanism.common.recipe.machines.CrystallizerRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Crystallizer extends VirtualizedMekanismRegistry<CrystallizerRecipe> {

    public Crystallizer() {
        super(RecipeHandler.Recipe.CHEMICAL_CRYSTALLIZER);
    }

    @RecipeBuilderDescription(example = @Example(".gasInput(gas('cleanGold')).output(item('minecraft:gold_ingot'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "gas('cleanGold'), item('minecraft:gold_ingot')", commented = true))
    public CrystallizerRecipe add(GasStack input, ItemStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Crystallizer recipe").error();
        msg.add(Mekanism.isEmpty(input), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        CrystallizerRecipe recipe = new CrystallizerRecipe(input.copy(), output.copy());
        recipeRegistry.put(recipe);
        addScripted(recipe);
        return recipe;
    }

    @MethodDescription(example = @Example("gas('cleanGold')"))
    public boolean removeByInput(GasStack input) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Mekanism Crystallizer recipe").error();
        msg.add(Mekanism.isEmpty(input), () -> "input must not be empty");
        if (msg.postIfNotEmpty()) return false;

        CrystallizerRecipe recipe = recipeRegistry.get().remove(new GasInput(input));
        if (recipe != null) {
            addBackup(recipe);
            return true;
        }
        removeError("could not find recipe for %", input);
        return false;
    }

    @Property(property = "output", valid = @Comp("1"))
    @Property(property = "gasInput", valid = @Comp("1"))
    public static class RecipeBuilder extends GasRecipeBuilder<CrystallizerRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Crystallizer recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg);
            validateGases(msg, 1, 1, 0, 0);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CrystallizerRecipe register() {
            if (!validate()) return null;
            CrystallizerRecipe recipe = new CrystallizerRecipe(gasInput.get(0), output.get(0));
            ModSupport.MEKANISM.get().crystallizer.add(recipe);
            return recipe;
        }
    }
}
