package com.cleanroommc.groovyscript.compat.mods.ic2.exp;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ic2.api.recipe.ILiquidHeatExchangerManager;
import ic2.api.recipe.Recipes;
import ic2.core.block.machine.tileentity.TileEntityLiquidHeatExchanger;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

public class LiquidHeatExchanger extends VirtualizedRegistry<LiquidHeatExchanger.HeatExchangerRecipe> {

    public LiquidHeatExchanger() {
        super(Alias.generateOf("HeatExchanger"));
    }

    @Override
    public void onReload() {
        Map<String, ILiquidHeatExchangerManager.HeatExchangeProperty> heatupMap = Recipes.liquidHeatupManager.getHeatExchangeProperties();
        Map<String, ILiquidHeatExchangerManager.HeatExchangeProperty> cooldownMap = Recipes.liquidCooldownManager.getHeatExchangeProperties();
        removeScripted().forEach(recipe -> {
            if (recipe.type == 0) heatupMap.remove(recipe.cold.getName());
            else cooldownMap.remove(recipe.hot.getName());
        });
        restoreFromBackup().forEach(recipe -> {
            if (recipe.type == 0)
                TileEntityLiquidHeatExchanger.addHeatupRecipe(recipe.hot.getName(), recipe.cold.getName(), recipe.huPerMB);
            else
                TileEntityLiquidHeatExchanger.addCooldownRecipe(recipe.hot.getName(), recipe.cold.getName(), recipe.huPerMB);
        });
    }

    public HeatExchangerRecipe add(FluidStack hotFluid, FluidStack coldFluid, int huPerMB) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Liquid Heat Exchanger recipe")
                .add(IngredientHelper.isEmpty(hotFluid), () -> "hot fluid must not be empty")
                .add(IngredientHelper.isEmpty(coldFluid), () -> "cold fluid must not be empty")
                .add(huPerMB <= 0, () -> "heat per mb must be higher than zero")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        addCooldown(hotFluid, coldFluid, huPerMB);
        return addHeatup(coldFluid, hotFluid, huPerMB);
    }

    public HeatExchangerRecipe addHeatup(FluidStack coldFluid, FluidStack hotFluid, int huPerMB) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Liquid Heat Exchanger recipe")
                .add(IngredientHelper.isEmpty(hotFluid), () -> "hot fluid must not be empty")
                .add(IngredientHelper.isEmpty(coldFluid), () -> "cold fluid must not be empty")
                .add(huPerMB <= 0, () -> "heat per mb must be higher than zero")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        Recipes.liquidHeatupManager.getHeatExchangeProperties().put(coldFluid.getFluid().getName(), new ILiquidHeatExchangerManager.HeatExchangeProperty(hotFluid.getFluid(), huPerMB));
        HeatExchangerRecipe recipe = new HeatExchangerRecipe(0, hotFluid, coldFluid, huPerMB);
        addScripted(recipe);
        return recipe;
    }

    public HeatExchangerRecipe addCooldown(FluidStack hotFluid, FluidStack coldFluid, int huPerMB) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Liquid Heat Exchanger recipe")
                .add(IngredientHelper.isEmpty(hotFluid), () -> "hot fluid must not be empty")
                .add(IngredientHelper.isEmpty(coldFluid), () -> "cold fluid must not be empty")
                .add(huPerMB <= 0, () -> "heat per mb must be higher than zero")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
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
        ILiquidHeatExchangerManager.HeatExchangeProperty property = Recipes.liquidHeatupManager.getHeatExchangeProperties().remove(coldFluid.getName());
        if (property != null) {
            addBackup(new HeatExchangerRecipe(0, property.outputFluid, coldFluid, property.huPerMB));
            return true;
        }

        return false;
    }

    public boolean removeCooldown(Fluid hotFluid) {
        ILiquidHeatExchangerManager.HeatExchangeProperty property = Recipes.liquidCooldownManager.getHeatExchangeProperties().remove(hotFluid.getName());
        if (property != null) {
            addBackup(new HeatExchangerRecipe(1, hotFluid, property.outputFluid, property.huPerMB));
            return true;
        }

        return false;
    }

    public SimpleObjectStream<Map.Entry<String, ILiquidHeatExchangerManager.HeatExchangeProperty>> streamCooldownRecipes() {
        Map<String, ILiquidHeatExchangerManager.HeatExchangeProperty> cooldownMap = Recipes.liquidCooldownManager.getHeatExchangeProperties();
        return new SimpleObjectStream<>(cooldownMap.entrySet()).setRemover(r -> removeCooldown(FluidRegistry.getFluid(r.getKey())));
    }

    public SimpleObjectStream<Map.Entry<String, ILiquidHeatExchangerManager.HeatExchangeProperty>> streamHeatupRecipes() {
        Map<String, ILiquidHeatExchangerManager.HeatExchangeProperty> heatupMap = Recipes.liquidHeatupManager.getHeatExchangeProperties();
        return new SimpleObjectStream<>(heatupMap.entrySet()).setRemover(r -> removeHeatup(FluidRegistry.getFluid(r.getKey())));
    }

    public void removeAll() {
        for (String fluid : Recipes.liquidHeatupManager.getHeatExchangeProperties().keySet()) {
            removeHeatup(FluidRegistry.getFluid(fluid));
        }
        for (String fluid : Recipes.liquidCooldownManager.getHeatExchangeProperties().keySet()) {
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
