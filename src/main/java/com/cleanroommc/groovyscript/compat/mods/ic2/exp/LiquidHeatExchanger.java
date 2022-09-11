package com.cleanroommc.groovyscript.compat.mods.ic2.exp;

import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import ic2.api.recipe.ILiquidHeatExchangerManager;
import ic2.api.recipe.Recipes;
import ic2.core.block.machine.tileentity.TileEntityLiquidHeatExchanger;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

public class LiquidHeatExchanger extends VirtualizedRegistry<LiquidHeatExchanger.HeatExchangerRecipe> {

    private static final Map<String, ILiquidHeatExchangerManager.HeatExchangeProperty> heatupMap = Recipes.liquidHeatupManager.getHeatExchangeProperties();
    private static final Map<String, ILiquidHeatExchangerManager.HeatExchangeProperty> cooldownMap = Recipes.liquidCooldownManager.getHeatExchangeProperties();

    public LiquidHeatExchanger() {
        super("LiquidHeatExchanger", "liquidheatexchanger", "HeatExchanger", "heatexchanger");
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> {
            if (recipe.type == 0) heatupMap.remove(recipe.cold.getName());
            else cooldownMap.remove(recipe.hot.getName());
        });
        restoreFromBackup().forEach(recipe -> {
            if (recipe.type == 0) TileEntityLiquidHeatExchanger.addHeatupRecipe(recipe.hot.getName(), recipe.cold.getName(), recipe.huPerMB);
            else TileEntityLiquidHeatExchanger.addCooldownRecipe(recipe.hot.getName(), recipe.cold.getName(), recipe.huPerMB);
        });
    }

    public HeatExchangerRecipe add(FluidStack hotFluid, FluidStack coldFluid, int huPerMB) {
        addCooldown(hotFluid, coldFluid, huPerMB);
        return addHeatup(coldFluid, hotFluid, huPerMB);
    }

    public HeatExchangerRecipe addHeatup(FluidStack coldFluid, FluidStack hotFluid, int huPerMB) {
        Recipes.liquidHeatupManager.getHeatExchangeProperties().put(coldFluid.getFluid().getName(), new ILiquidHeatExchangerManager.HeatExchangeProperty(hotFluid.getFluid(), huPerMB));
        HeatExchangerRecipe recipe = new HeatExchangerRecipe(0, hotFluid, coldFluid, huPerMB);
        addScripted(recipe);
        return recipe;
    }

    public HeatExchangerRecipe addCooldown(FluidStack hotFluid, FluidStack coldFluid, int huPerMB) {
        Recipes.liquidCooldownManager.getHeatExchangeProperties().put(hotFluid.getFluid().getName(), new ILiquidHeatExchangerManager.HeatExchangeProperty(coldFluid.getFluid(), huPerMB));
        HeatExchangerRecipe recipe = new HeatExchangerRecipe(1, hotFluid, coldFluid, huPerMB);
        addScripted(recipe);
        return recipe;
    }

    public boolean removeHeatup(FluidStack coldFluid) {
        if (IngredientHelper.isEmpty(coldFluid)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Liquid Heat Exchanger heatup recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return false;
        }
        return removeHeatup(coldFluid.getFluid());
    }

    public boolean removeCooldown(FluidStack hotFluid) {
        if (IngredientHelper.isEmpty(hotFluid)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Liquid Heat Exchanger cooldown recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return false;
        }
        return removeCooldown(hotFluid.getFluid());
    }

    public boolean removeHeatup(Fluid coldFluid) {
        ILiquidHeatExchangerManager.HeatExchangeProperty property = heatupMap.remove(coldFluid.getName());
        if (property != null) {
            addBackup(new HeatExchangerRecipe(0, property.outputFluid, coldFluid, property.huPerMB));
            return true;
        }

        return false;
    }

    public boolean removeCooldown(Fluid hotFluid) {
        ILiquidHeatExchangerManager.HeatExchangeProperty property = cooldownMap.remove(hotFluid.getName());
        if (property != null) {
            addBackup(new HeatExchangerRecipe(1, hotFluid, property.outputFluid, property.huPerMB));
            return true;
        }

        return false;
    }

    public void removeAll() {
        for (String fluid : heatupMap.keySet()) {
            removeHeatup(FluidRegistry.getFluid(fluid));
        }

        for (String fluid : cooldownMap.keySet()) {
            removeCooldown(FluidRegistry.getFluid(fluid));
        }
    }

    public static class HeatExchangerRecipe {
        public int type; // 0 = heatup | 1 = cooldown
        public Fluid hot, cold;
        public int huPerMB;

        public HeatExchangerRecipe(int type, FluidStack in, FluidStack out, int huPerMB) {
            this.type = type;
            this.hot = in.getFluid();
            this.cold = out.getFluid();
            this.huPerMB = huPerMB;
        }

        public HeatExchangerRecipe(int type, Fluid in, Fluid out, int huPerMB) {
            this.type = type;
            this.hot = in;
            this.cold = out;
            this.huPerMB = huPerMB;
        }
    }
}
