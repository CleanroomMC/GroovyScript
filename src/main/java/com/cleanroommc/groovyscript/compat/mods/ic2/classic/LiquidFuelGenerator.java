package com.cleanroommc.groovyscript.compat.mods.ic2.classic;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ic2.api.classic.recipe.ClassicRecipes;
import ic2.api.classic.recipe.custom.ILiquidFuelGeneratorRegistry;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

public class LiquidFuelGenerator extends VirtualizedRegistry<LiquidFuelGenerator.LFGRecipe> {

    public LiquidFuelGenerator() {
        super(Alias.generateOf("FluidGenerator"));
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
        if (GroovyLog.msg("Error adding Fluid Generator recipe")
                .add(IngredientHelper.isEmpty(fluid), () -> "fluid must not be empty")
                .add(burnTicks <= 0, () -> "burnTicks must be higher than zero")
                .add(euPerTick <= 0, () -> "euPerTick must be higher than zero")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        LFGRecipe recipe = new LFGRecipe(fluid, burnTicks, euPerTick);
        add(recipe);
        return recipe;
    }

    public boolean remove(FluidStack fluid) {
        if (IngredientHelper.isEmpty(fluid)) {
            GroovyLog.msg("Error removing Liquid Fuel Generator recipe")
                    .add("fluid must not be empty")
                    .error()
                    .post();
            return false;
        }
        return remove(fluid.getFluid());
    }

    public boolean remove(Fluid fluid) {
        if (fluid == null) {
            GroovyLog.msg("Error removing Liquid Fuel Generator recipe")
                    .add("fluid must not be empty")
                    .error()
                    .post();
            return false;
        }
        ILiquidFuelGeneratorRegistry.BurnEntry entry = ClassicRecipes.fluidGenerator.getBurnEntry(fluid);
        if (entry == null) {
            GroovyLog.msg("Error removing Liquid Fuel Generator recipe")
                    .add("no recipes found for {}", fluid)
                    .error()
                    .post();
            return false;
        }
        remove(new LFGRecipe(fluid, entry.getTicksLast(), entry.getProduction()));
        return true;
    }

    public SimpleObjectStream<Map.Entry<Fluid, ILiquidFuelGeneratorRegistry.BurnEntry>> streamRecipes() {
        return new SimpleObjectStream<>(ClassicRecipes.fluidGenerator.getBurnMap().entrySet()).setRemover(r -> remove(r.getKey()));
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
