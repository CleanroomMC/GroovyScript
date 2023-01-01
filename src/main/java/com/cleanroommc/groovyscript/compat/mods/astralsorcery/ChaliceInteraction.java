package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.LiquidInteractionAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.LinkedList;

public class ChaliceInteraction extends VirtualizedRegistry<hellfirepvp.astralsorcery.common.base.LiquidInteraction> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(this::remove);
        restoreFromBackup().forEach(this::add);
    }

    private void add(hellfirepvp.astralsorcery.common.base.LiquidInteraction recipe) {
        LinkedList<hellfirepvp.astralsorcery.common.base.LiquidInteraction> rts = (LinkedList<hellfirepvp.astralsorcery.common.base.LiquidInteraction>) LiquidInteractionAccessor.getRegisteredInteractions();

        if (rts == null) rts = new LinkedList<>();
        rts.add(recipe);
    }

    public hellfirepvp.astralsorcery.common.base.LiquidInteraction add(int probability, FluidStack component1, FluidStack component2, hellfirepvp.astralsorcery.common.base.LiquidInteraction.FluidInteractionAction action) {
        hellfirepvp.astralsorcery.common.base.LiquidInteraction recipe = new hellfirepvp.astralsorcery.common.base.LiquidInteraction(probability, component1, component2, action);
        addScripted(recipe);

        LinkedList<hellfirepvp.astralsorcery.common.base.LiquidInteraction> rts = (LinkedList<hellfirepvp.astralsorcery.common.base.LiquidInteraction>) LiquidInteractionAccessor.getRegisteredInteractions();

        if (rts == null) rts = new LinkedList<>();
        rts.add(recipe);

        return recipe;
    }

    private void remove(hellfirepvp.astralsorcery.common.base.LiquidInteraction recipe) {
        LinkedList<hellfirepvp.astralsorcery.common.base.LiquidInteraction> rts = (LinkedList<hellfirepvp.astralsorcery.common.base.LiquidInteraction>) LiquidInteractionAccessor.getRegisteredInteractions();

        if (rts == null) return;
        rts.removeIf(rec -> rec.equals(recipe));
    }

    public void remove(FluidStack fluid1, FluidStack fluid2) {
        this.remove(fluid1.getFluid(), fluid2.getFluid());
    }

    public void remove(Fluid fluid1, Fluid fluid2) {
        LinkedList<hellfirepvp.astralsorcery.common.base.LiquidInteraction> rts = (LinkedList<hellfirepvp.astralsorcery.common.base.LiquidInteraction>) LiquidInteractionAccessor.getRegisteredInteractions();

        if (rts == null) return;
        rts.forEach(rec -> {
            if ( (rec.getComponent1().getFluid().equals(fluid1) && rec.getComponent2().getFluid().equals(fluid2))
                    || (rec.getComponent1().getFluid().equals(fluid2) && rec.getComponent2().getFluid().equals(fluid1)) )
                addBackup(rec);
        });
        rts.removeIf(rec -> ( (rec.getComponent1().getFluid().equals(fluid1) && rec.getComponent2().getFluid().equals(fluid2))
                || (rec.getComponent1().getFluid().equals(fluid2) && rec.getComponent2().getFluid().equals(fluid1)) ));
    }

    public void removeByOutput(ItemStack output) {
        LinkedList<hellfirepvp.astralsorcery.common.base.LiquidInteraction> rts = (LinkedList<hellfirepvp.astralsorcery.common.base.LiquidInteraction>) LiquidInteractionAccessor.getRegisteredInteractions();

        if (rts == null) return;
        rts.forEach(rec -> {
            if (((LiquidInteractionAccessor) rec).getFluidInteractionAction().getOutputForMatching().isItemEqual(output)) addBackup(rec);
        });
        rts.removeIf(rec -> ((LiquidInteractionAccessor) rec).getFluidInteractionAction().getOutputForMatching().isItemEqual(output));
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<hellfirepvp.astralsorcery.common.base.LiquidInteraction> {

        private final ArrayList<Float> chances = new ArrayList<>();
        private ItemStack result;
        private final ArrayList<FluidStack> components = new ArrayList<>();
        private int probability = 1;

        public RecipeBuilder result(ItemStack item) {
            this.result = item;
            return this;
        }

        public RecipeBuilder component(FluidStack fluid) {
            this.components.add(fluid);
            return this;
        }

        public RecipeBuilder component(FluidStack fluid1, FluidStack fluid2) {
            this.components.add(fluid1);
            this.components.add(fluid2);
            return this;
        }

        public RecipeBuilder weight(int weight) {
            this.probability = weight;
            return this;
        }

        public RecipeBuilder chance(float consumptionChance) {
            this.chances.add(consumptionChance);
            return this;
        }

        public RecipeBuilder chance(float consumptionChance1, float consumptionChance2) {
            this.chances.add(consumptionChance1);
            this.chances.add(consumptionChance2);
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Astral Sorcery Liquid Interaction";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            msg.add(this.probability > 0, () -> "Weight must be a positive integer. Instead found: " + this.probability);
            msg.add(this.chances.size() != 2, () -> "Exactly two consumption chances must be provided. Number provided: " + this.chances.size());
            msg.add(this.chances.get(0) < 0 || this.chances.get(1) < 0, () -> "Consumption chance cannot be negative");
            msg.add(this.components.size() != 2, () -> "Exactly two fluids must be provided. Number provided: " + this.components.size());
            msg.add(this.components.get(0) == null || this.components.get(1) == null, () -> "Neither fluid can be null");
            msg.add(this.result == null, () -> "No output provided.");
        }

        public hellfirepvp.astralsorcery.common.base.LiquidInteraction register() {
//            if (!validate()) return null;
            return ModSupport.ASTRAL_SORCERY.get().chaliceInteraction.add(this.probability, this.components.get(0), this.components.get(1), hellfirepvp.astralsorcery.common.base.LiquidInteraction.createItemDropAction(this.chances.get(0), this.chances.get(1), this.result));
        }
    }
}
