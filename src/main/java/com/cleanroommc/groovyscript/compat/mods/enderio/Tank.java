package com.cleanroommc.groovyscript.compat.mods.enderio;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
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

@RegistryDescription
public class Tank extends VirtualizedRegistry<TankMachineRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".drain().input(item('minecraft:clay')).output(item('minecraft:diamond')).fluidInput(fluid('water') * 500)"),
            @Example(".fill().input(item('minecraft:diamond')).output(item('minecraft:clay')).fluidOutput(fluid('water') * 500)"),
            @Example(".drain().input(item('minecraft:diamond')).fluidInput(fluid('fire_water') * 8000)"),
            @Example(".fill().input(item('minecraft:diamond')).fluidOutput(fluid('fire_water') * 8000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void add(TankMachineRecipe recipe) {
        MachineRecipeRegistry.instance.registerRecipe(recipe);
        addScripted(recipe);
    }

    @MethodDescription(description = "groovyscript.wiki.enderio.tank.addFill0", type = MethodDescription.Type.ADDITION)
    public void addFill(IIngredient input, FluidStack inputFluid, ItemStack output) {
        addFill(GroovyScript.getRunConfig().getPackId() + ":" + RecipeName.generate("groovyscript_enderio_tank_"), input, inputFluid, output);
    }

    @MethodDescription(description = "groovyscript.wiki.enderio.tank.addFill1", type = MethodDescription.Type.ADDITION)
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

    @MethodDescription(description = "groovyscript.wiki.enderio.tank.addDrain0", type = MethodDescription.Type.ADDITION)
    public void addDrain(IIngredient input, FluidStack outputFluid, ItemStack output) {
        addDrain(GroovyScript.getRunConfig().getPackId() + ":" + RecipeName.generate("groovyscript_enderio_tank_"), input, outputFluid, output);
    }

    @MethodDescription(description = "groovyscript.wiki.enderio.tank.addDrain1", type = MethodDescription.Type.ADDITION)
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

    @MethodDescription(description = "groovyscript.wiki.enderio.tank.removeFill0", example = @Example("item('minecraft:glass_bottle'), fluid('xpjuice')"))
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

    @MethodDescription(description = "groovyscript.wiki.enderio.tank.removeFill1")
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

    @MethodDescription(description = "groovyscript.wiki.enderio.tank.removeDrain0", example = @Example("item('minecraft:experience_bottle'), fluid('xpjuice')"))
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

    @MethodDescription(description = "groovyscript.wiki.enderio.tank.removeDrain1")
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

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<TankMachineRecipe> streamRecipes() {
        List<TankMachineRecipe> list = new ArrayList<>();
        list.addAll((Collection<? extends TankMachineRecipe>) MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.TANK_FILLING).values());
        list.addAll((Collection<? extends TankMachineRecipe>) MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.TANK_EMPTYING).values());
        return new SimpleObjectStream<>(list).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.TANK_FILLING).forEach((r, l) -> addBackup((TankMachineRecipe) l));
        ((SimpleRecipeGroupHolderAccessor) MachineRecipeRegistry.instance.getRecipeHolderssForMachine(MachineRecipeRegistry.TANK_FILLING)).getRecipes().clear();
        MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.TANK_EMPTYING).forEach((r, l) -> addBackup((TankMachineRecipe) l));
        ((SimpleRecipeGroupHolderAccessor) MachineRecipeRegistry.instance.getRecipeHolderssForMachine(MachineRecipeRegistry.TANK_EMPTYING)).getRecipes().clear();
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "fluidInput", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
    @Property(property = "output", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
    @Property(property = "fluidOutput", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
    public static class RecipeBuilder extends AbstractRecipeBuilder<TankMachineRecipe> {

        @Property
        private boolean isFilling;

        @RecipeBuilderMethodDescription(field = "isFilling")
        public RecipeBuilder fill() {
            this.isFilling = true;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "isFilling")
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
        @RecipeBuilderRegistrationMethod
        public @Nullable TankMachineRecipe register() {
            if (!validate()) return null;
            Things in = RecipeUtils.toThings(input.get(0));
            Things out = new Things().add(output.getOrEmpty(0));
            TankMachineRecipe recipe = new TankMachineRecipe(name.toString(), isFilling, in, isFilling ? fluidOutput.get(0)
                                                                                                       : fluidInput.get(0), out, TankMachineRecipe.Logic.NONE, RecipeLevel.IGNORE);

            ModSupport.ENDER_IO.get().tank.add(recipe);
            return recipe;
        }
    }
}
