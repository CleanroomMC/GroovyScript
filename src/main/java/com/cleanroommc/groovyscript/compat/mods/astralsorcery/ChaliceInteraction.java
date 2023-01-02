package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.LiquidInteractionAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import hellfirepvp.astralsorcery.common.base.LiquidInteraction;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
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

        private final FloatArrayList chances = new FloatArrayList();
        private final ArrayList<ItemStack> results = new ArrayList<>();
        private final ArrayList<FluidStack> components = new ArrayList<>();
        private final IntArrayList probabilities = new IntArrayList();

        public RecipeBuilder result(ItemStack item) {
            return this.result(item, 1);
        }

        public RecipeBuilder result(ItemStack item, int weight) {
            this.results.add(item);
            this.probabilities.add(weight);
            return this;
        }

        public RecipeBuilder component(FluidStack fluid) {
            return this.component(fluid, 1.0F);
        }

        public RecipeBuilder component(FluidStack fluid, float chance) {
            this.components.add(fluid);
            this.chances.add(chance);
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Astral Sorcery Liquid Interaction";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            msg.add(this.results.isEmpty(), () -> "No output(s) provided.");
            for (Integer probability : this.probabilities) {
                if (probability <= 0) msg.add("Weight must be a positive integer. Instead found: " + probability);
            }
            msg.add(this.components.size() != 2, () -> "Exactly two fluids must be provided. Number provided: " + this.components.size());
            if (this.components.size() < 2) return;
            msg.add(this.components.get(0) == null || this.components.get(1) == null, () -> "Neither fluid can be null");
            msg.add(this.chances.getFloat(0) < 0 || this.chances.getFloat(1) < 0, () -> "Consumption chance cannot be negative");
        }

        @Override
        public LiquidInteraction register() {
            if (!validate()) return null;
            for (int i = 0; i < this.results.size(); i++) {
                ModSupport.ASTRAL_SORCERY.get().chaliceInteraction.add(this.probabilities.getInt(i), this.components.get(0), this.components.get(1), hellfirepvp.astralsorcery.common.base.LiquidInteraction.createItemDropAction(this.chances.getFloat(0), this.chances.getFloat(1), this.results.get(i)));
            }
            return null;
        }
    }
}
