package com.cleanroommc.groovyscript.compat.mods.ic2.classic;

import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import ic2.api.classic.recipe.ClassicRecipes;
import ic2.api.classic.recipe.custom.ILiquidFuelGeneratorRegistry;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class LiquidFuelGenerator extends VirtualizedRegistry<LiquidFuelGenerator.LFGRecipe> {

    public LiquidFuelGenerator() {
        super("FluidGenerator", "fluidgenerator");
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> ClassicRecipes.fluidGenerator.getBurnMap().remove(recipe.fluid));
        restoreFromBackup().forEach(recipe -> ClassicRecipes.fluidGenerator.addEntry(recipe.fluid, recipe.time, recipe.power));
    }
    
    public void add(LFGRecipe recipe) {
        ClassicRecipes.fluidGenerator.addEntry(recipe.fluid, recipe.time, recipe.power);
        addScripted(recipe);
    }
    
    public LFGRecipe add(FluidStack fluid, int burnTicks, float euPerTick) {
        LFGRecipe recipe = new LFGRecipe(fluid, burnTicks, euPerTick);
        add(recipe);
        return recipe;
    }
    
    public boolean remove(FluidStack fluid) {
        ILiquidFuelGeneratorRegistry.BurnEntry entry = ClassicRecipes.fluidGenerator.getBurnEntry(fluid.getFluid());
        if (entry == null) {
            GroovyLog.msg("Error removing Liquid Fuel Generator recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return false;
        }
        remove(new LFGRecipe(fluid, entry.getTicksLast(), entry.getProduction()));
        return true;
    }

    public void remove(LFGRecipe recipe) {
        ClassicRecipes.fluidGenerator.getBurnMap().remove(recipe.fluid);
        addBackup(recipe);
    }

    public void removeAll() {
        for (Fluid fluid : ClassicRecipes.fluidGenerator.getBurnMap().keySet()) {
            ILiquidFuelGeneratorRegistry.BurnEntry entry = ClassicRecipes.fluidGenerator.getBurnEntry(fluid);
            remove(new LFGRecipe(fluid, entry.getTicksLast(), entry.getProduction()));
        }
    }

    public static class LFGRecipe {
        public Fluid fluid;
        public int time;
        public float power;
        
        public LFGRecipe(FluidStack fluid, int time, float power) {
            this.fluid = fluid.getFluid();
            this.time = time;
            this.power = power;
        }

        public LFGRecipe(Fluid fluid, int time, float power) {
            this.fluid = fluid;
            this.time = time;
            this.power = power;
        }
    }
}
