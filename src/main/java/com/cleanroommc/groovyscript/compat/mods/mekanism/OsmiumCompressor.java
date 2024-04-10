package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.GasRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.common.MekanismFluids;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.AdvancedMachineInput;
import mekanism.common.recipe.machines.OsmiumCompressorRecipe;
import mekanism.common.recipe.outputs.ItemStackOutput;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class OsmiumCompressor extends VirtualizedMekanismRegistry<OsmiumCompressorRecipe> {

    public OsmiumCompressor() {
        super(RecipeHandler.Recipe.OSMIUM_COMPRESSOR);
    }

    @RecipeBuilderDescription(example = @Example(value = ".input(item('minecraft:diamond')).gasInput(gas('hydrogen'))/*()!*/.output(item('minecraft:nether_star'))", annotations = "groovyscript.wiki.mekanism.osmium_compressor.annotation"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "item('minecraft:diamond'), gas('hydrogen'), item('minecraft:nether_star')", commented = true))
    public OsmiumCompressorRecipe add(IIngredient ingredient, GasStack gasInput, ItemStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Osmium Compressor recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        output = output.copy();
        OsmiumCompressorRecipe recipe1 = null;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            OsmiumCompressorRecipe recipe = new OsmiumCompressorRecipe(new AdvancedMachineInput(itemStack.copy(), gasInput.getGas()), new ItemStackOutput(output));
            if (recipe1 == null) recipe1 = recipe;
            recipeRegistry.put(recipe);
            addScripted(recipe);
        }
        return recipe1;
    }

    @MethodDescription(example = @Example("ore('dustRefinedObsidian'), gas('liquidosmium')"))
    public boolean removeByInput(IIngredient ingredient, GasStack gasInput) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Mekanism Osmium Compressor recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(Mekanism.isEmpty(gasInput), () -> "gas input must not be empty");
        if (msg.postIfNotEmpty()) return false;

        boolean found = false;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            OsmiumCompressorRecipe recipe = recipeRegistry.get().remove(new AdvancedMachineInput(itemStack, gasInput.getGas()));
            if (recipe != null) {
                addBackup(recipe);
                found = true;
            }
        }
        if (!found) {
            removeError("could not find recipe for {} and {}", ingredient, gasInput);
        }
        return found;
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    @Property(property = "gasInput", defaultValue = "MekanismFluids.LiquidOsmium", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
    public static class RecipeBuilder extends GasRecipeBuilder<OsmiumCompressorRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Osmium Compressor recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            validateGases(msg, 0, 1, 0, 0);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable OsmiumCompressorRecipe register() {
            if (!validate()) return null;
            Gas gas = gasInput.isEmpty() ? MekanismFluids.LiquidOsmium : gasInput.get(0).getGas();
            OsmiumCompressorRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                OsmiumCompressorRecipe r = new OsmiumCompressorRecipe(new AdvancedMachineInput(itemStack.copy(), gas), new ItemStackOutput(output.get(0)));
                if (recipe == null) recipe = r;
                ModSupport.MEKANISM.get().osmiumCompressor.add(r);
            }
            return recipe;
        }
    }
}
