package com.cleanroommc.groovyscript.compat.mods.enderio;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.enderio.recipe.RecipeUtils;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.RecipeName;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.enderio.core.common.util.stackable.Things;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.tank.TankMachineRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class Tank extends VirtualizedRegistry<TankMachineRecipe> {

    public Tank() {
        super();
    }

    public void addFill(IIngredient input, FluidStack inputFluid, ItemStack output) {
        addFill(RecipeName.generate(), input, inputFluid, output);
    }

    public void addFill(String recipeName, IIngredient input, FluidStack inputFluid, ItemStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding EnderIO Tank filling recipe").error();
        msg.add(IngredientHelper.isEmpty(input), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(inputFluid), () -> "fluid must not be empty");
        msg.add(output == null, () -> "output must not be null");
        if (msg.postIfNotEmpty()) return;

        Things in = RecipeUtils.toThings(input);
        Things out = new Things().add(output);
        TankMachineRecipe rec = new TankMachineRecipe(recipeName, true, in, inputFluid, out, TankMachineRecipe.Logic.NONE, RecipeLevel.IGNORE);
        MachineRecipeRegistry.instance.registerRecipe(rec);
        addScripted(rec);
    }

    public void addDrain(IIngredient input, FluidStack outputFluid, ItemStack output) {
        addDrain(RecipeName.generate(), input, outputFluid, output);
    }

    public void addDrain(String recipeName, IIngredient input, FluidStack outputFluid, ItemStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding EnderIO Tank draining recipe").error();
        msg.add(IngredientHelper.isEmpty(input), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(outputFluid), () -> "fluid must not be empty");
        msg.add(output == null, () -> "output must not be null");
        if (msg.postIfNotEmpty()) return;

        Things in = RecipeUtils.toThings(input);
        Things out = new Things().add(output);
        TankMachineRecipe rec = new TankMachineRecipe(recipeName, false, in, outputFluid, out, TankMachineRecipe.Logic.NONE, RecipeLevel.IGNORE);
        MachineRecipeRegistry.instance.registerRecipe(rec);
        addScripted(rec);
    }

    public void removeFill(FluidStack fluid, ItemStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing EnderIO Tank filling recipe").error();
        msg.add(IngredientHelper.isEmpty(fluid), () -> "fluid must not be empty");
        msg.add(output == null, () -> "output must not be null");
        if (msg.postIfNotEmpty()) return;

        List<TankMachineRecipe> recipes = new ArrayList<>();
        for (IMachineRecipe recipe : MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.TANK_FILLING).values()) {
            TankMachineRecipe tankMachineRecipe = (TankMachineRecipe) recipe;
            if (tankMachineRecipe.getFluid().isFluidEqual(fluid) && tankMachineRecipe.getOutput().contains(output)) {
                recipes.add(tankMachineRecipe);
            }
        }
        if (recipes.isEmpty()) {
            GroovyLog.get().error("Could not find EnderIO Tank filling recipes for fluid {} and output {}", fluid.getFluid().getName(), output);
        } else {
            recipes.forEach(this::addBackup);
            recipes.forEach(MachineRecipeRegistry.instance::removeRecipe);
        }
    }

    public void removeDrain(FluidStack fluid, ItemStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing EnderIO Tank draining recipe").error();
        msg.add(IngredientHelper.isEmpty(fluid), () -> "fluid must not be empty");
        msg.add(output == null, () -> "output must not be null");
        if (msg.postIfNotEmpty()) return;

        List<TankMachineRecipe> recipes = new ArrayList<>();
        for (IMachineRecipe recipe : MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.TANK_EMPTYING).values()) {
            TankMachineRecipe tankMachineRecipe = (TankMachineRecipe) recipe;
            if (tankMachineRecipe.getFluid().isFluidEqual(fluid) && tankMachineRecipe.getOutput().contains(output)) {
                recipes.add(tankMachineRecipe);
            }
        }
        if (recipes.isEmpty()) {
            GroovyLog.get().error("Could not find EnderIO Tank draining recipes for fluid {} and output {}", fluid.getFluid().getName(), output);
        } else {
            recipes.forEach(this::addBackup);
            recipes.forEach(MachineRecipeRegistry.instance::removeRecipe);
        }
    }

    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(MachineRecipeRegistry.instance::removeRecipe);
        restoreFromBackup().forEach(MachineRecipeRegistry.instance::registerRecipe);
    }
}
