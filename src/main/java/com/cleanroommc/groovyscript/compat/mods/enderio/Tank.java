package com.cleanroommc.groovyscript.compat.mods.enderio;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.enderio.recipe.RecipeUtils;
import com.cleanroommc.groovyscript.core.mixin.enderio.SimpleRecipeGroupHolderAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.helper.recipe.RecipeName;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.enderio.core.common.util.stackable.Things;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.tank.TankMachineRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Tank extends VirtualizedRegistry<TankMachineRecipe> {

    public Tank() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void add(TankMachineRecipe recipe) {
        MachineRecipeRegistry.instance.registerRecipe(recipe);
        addScripted(recipe);
    }

    public void addFill(IIngredient input, FluidStack inputFluid, ItemStack output) {
        addFill(GroovyScript.getRunConfig().getPackId() + ":" + RecipeName.generate("groovyscript_enderio_tank_"), input, inputFluid, output);
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
        add(rec);
    }

    public void addDrain(IIngredient input, FluidStack outputFluid, ItemStack output) {
        addDrain(GroovyScript.getRunConfig().getPackId() + ":" + RecipeName.generate("groovyscript_enderio_tank_"), input, outputFluid, output);
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
        add(rec);
    }

    public boolean remove(TankMachineRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        MachineRecipeRegistry.instance.removeRecipe(recipe);
        return true;
    }

    public void removeFill(ItemStack input, FluidStack fluid) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing EnderIO Tank filling recipe").error();
        msg.add(IngredientHelper.isEmpty(fluid), () -> "fluid must not be empty");
        msg.add(IngredientHelper.isEmpty(input), () -> "input must not be empty");
        if (msg.postIfNotEmpty()) return;

        List<TankMachineRecipe> recipes = new ArrayList<>();
        for (IMachineRecipe recipe : MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.TANK_FILLING).values()) {
            TankMachineRecipe tankMachineRecipe = (TankMachineRecipe) recipe;
            if (tankMachineRecipe.getFluid().isFluidEqual(fluid) && tankMachineRecipe.getInput().contains(input)) {
                recipes.add(tankMachineRecipe);
            }
        }
        if (recipes.isEmpty()) {
            GroovyLog.get().error("Could not find EnderIO Tank filling recipes for fluid {} and input {}", fluid.getFluid().getName(), input);
        } else {
            recipes.forEach(this::addBackup);
            recipes.forEach(MachineRecipeRegistry.instance::removeRecipe);
        }
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

    public void removeDrain(ItemStack input, FluidStack fluid) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing EnderIO Tank draining recipe").error();
        msg.add(IngredientHelper.isEmpty(fluid), () -> "fluid must not be empty");
        msg.add(IngredientHelper.isEmpty(input), () -> "input must not be empty");
        if (msg.postIfNotEmpty()) return;

        List<TankMachineRecipe> recipes = new ArrayList<>();
        for (IMachineRecipe recipe : MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.TANK_EMPTYING).values()) {
            TankMachineRecipe tankMachineRecipe = (TankMachineRecipe) recipe;
            if (tankMachineRecipe.getFluid().isFluidEqual(fluid) && tankMachineRecipe.getInput().contains(input)) {
                recipes.add(tankMachineRecipe);
            }
        }
        if (recipes.isEmpty()) {
            GroovyLog.get().error("Could not find EnderIO Tank draining recipes for fluid {} and input {}", fluid.getFluid().getName(), input);
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

    public SimpleObjectStream<TankMachineRecipe> streamRecipes() {
        List<TankMachineRecipe> list = new ArrayList<>();
        list.addAll((Collection<? extends TankMachineRecipe>) MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.TANK_FILLING).values());
        list.addAll((Collection<? extends TankMachineRecipe>) MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.TANK_EMPTYING).values());
        return new SimpleObjectStream<>(list).setRemover(this::remove);
    }

    public void removeAll() {
        MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.TANK_FILLING).forEach((r, l) -> addBackup((TankMachineRecipe) l));
        ((SimpleRecipeGroupHolderAccessor) MachineRecipeRegistry.instance.getRecipeHolderssForMachine(MachineRecipeRegistry.TANK_FILLING)).getRecipes().clear();
        MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.TANK_EMPTYING).forEach((r, l) -> addBackup((TankMachineRecipe) l));
        ((SimpleRecipeGroupHolderAccessor) MachineRecipeRegistry.instance.getRecipeHolderssForMachine(MachineRecipeRegistry.TANK_EMPTYING)).getRecipes().clear();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<TankMachineRecipe> {

        private boolean isFilling;

        public RecipeBuilder fill() {
            this.isFilling = true;
            return this;
        }

        public RecipeBuilder drain() {
            this.isFilling = false;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding EnderIO Tank recipe";
        }

        public String getRecipeNamePrefix() {
            return "groovyscript_enderio_tank_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 1, 1, 0, 1);
            if (isFilling) validateFluids(msg, 0, 0, 1, 1);
            else validateFluids(msg, 1, 1, 0, 0);
            msg.add(msg.hasSubMessages(), "The Tank Recipe Builder requires an input fluid stack if it is draining and " +
                                          "an output fluid stack if it is filling. This recipe was {}.", isFilling ? "filling" : "draining");
        }

        @Override
        public @Nullable TankMachineRecipe register() {
            if (!validate()) return null;
            Things in = RecipeUtils.toThings(input.get(0));
            Things out = new Things().add(output.getOrEmpty(0));
            TankMachineRecipe recipe = new TankMachineRecipe(name.toString(), isFilling, in, isFilling ? fluidOutput.get(0) : fluidInput.get(0), out, TankMachineRecipe.Logic.NONE, RecipeLevel.IGNORE);

            ModSupport.ENDER_IO.get().tank.add(recipe);
            return recipe;
        }
    }
}
