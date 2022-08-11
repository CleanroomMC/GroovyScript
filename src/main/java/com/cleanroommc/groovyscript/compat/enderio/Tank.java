package com.cleanroommc.groovyscript.compat.enderio;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.enderio.recipe.RecipeUtils;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.RecipeName;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import com.enderio.core.common.util.stackable.Things;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.tank.TankMachineRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class Tank {

    public void addFill(IIngredient input, FluidStack inputFluid, ItemStack output) {
        GroovyLog.Msg msg = new GroovyLog.Msg("Error adding EnderIO Tank filling recipe").error();
        msg.add(IngredientHelper.isEmpty(input), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(inputFluid), () -> "fluid must not be empty");
        msg.add(output == null, () -> "output must not be null");
        if (msg.hasSubMessages()) {
            GroovyLog.LOG.log(msg);
            return;
        }

        Things in = RecipeUtils.toThings(input);
        Things out = new Things().add(output);
        TankMachineRecipe rec = new TankMachineRecipe(RecipeName.generate(), true, in, inputFluid, out, TankMachineRecipe.Logic.NONE, RecipeLevel.IGNORE);
        MachineRecipeRegistry.instance.registerRecipe(rec);
    }

    public void addDrain(IIngredient input, FluidStack outputFluid, ItemStack output) {
        GroovyLog.Msg msg = new GroovyLog.Msg("Error adding EnderIO Tank draining recipe").error();
        msg.add(IngredientHelper.isEmpty(input), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(outputFluid), () -> "fluid must not be empty");
        msg.add(output == null, () -> "output must not be null");
        if (msg.hasSubMessages()) {
            GroovyLog.LOG.log(msg);
            return;
        }

        Things in = RecipeUtils.toThings(input);
        Things out = new Things().add(output);
        TankMachineRecipe rec = new TankMachineRecipe(RecipeName.generate(), false, in, outputFluid, out, TankMachineRecipe.Logic.NONE, RecipeLevel.IGNORE);
        MachineRecipeRegistry.instance.registerRecipe(rec);
    }

    public void removeFill(FluidStack fluid, ItemStack output) {
        GroovyLog.Msg msg = new GroovyLog.Msg("Error removing EnderIO Tank filling recipe").error();
        msg.add(IngredientHelper.isEmpty(fluid), () -> "fluid must not be empty");
        msg.add(output == null, () -> "output must not be null");
        if (msg.hasSubMessages()) {
            GroovyLog.LOG.log(msg);
            return;
        }

        List<IMachineRecipe> recipes = new ArrayList<>();
        for (IMachineRecipe recipe : MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.TANK_FILLING).values()) {
            TankMachineRecipe tankMachineRecipe = (TankMachineRecipe) recipe;
            if (tankMachineRecipe.getFluid().isFluidEqual(fluid) && tankMachineRecipe.getOutput().contains(output)) {
                recipes.add(recipe);
            }
        }
        if (recipes.isEmpty()) {
            GroovyLog.LOG.error("Could not find EnderIO Tank filling recipes for fluid %s and output %s", fluid.getFluid().getName(), output);
        } else {
            recipes.forEach(MachineRecipeRegistry.instance::removeRecipe);
        }
    }

    public void removeDrain(FluidStack fluid, ItemStack output) {
        GroovyLog.Msg msg = new GroovyLog.Msg("Error removing EnderIO Tank draining recipe").error();
        msg.add(IngredientHelper.isEmpty(fluid), () -> "fluid must not be empty");
        msg.add(output == null, () -> "output must not be null");
        if (msg.hasSubMessages()) {
            GroovyLog.LOG.log(msg);
            return;
        }

        List<IMachineRecipe> recipes = new ArrayList<>();
        for (IMachineRecipe recipe : MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.TANK_EMPTYING).values()) {
            TankMachineRecipe tankMachineRecipe = (TankMachineRecipe) recipe;
            if (tankMachineRecipe.getFluid().isFluidEqual(fluid) && tankMachineRecipe.getOutput().contains(output)) {
                recipes.add(recipe);
            }
        }
        if (recipes.isEmpty()) {
            GroovyLog.LOG.error("Could not find EnderIO Tank draining recipes for fluid %s and output %s", fluid.getFluid().getName(), output);
        } else {
            recipes.forEach(MachineRecipeRegistry.instance::removeRecipe);
        }
    }

    @GroovyBlacklist
    public void onReload() {

    }

}
