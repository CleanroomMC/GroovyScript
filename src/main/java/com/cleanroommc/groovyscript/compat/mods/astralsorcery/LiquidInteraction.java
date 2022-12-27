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

import java.util.LinkedList;

public class LiquidInteraction extends VirtualizedRegistry<hellfirepvp.astralsorcery.common.base.LiquidInteraction> {

    public LiquidInteraction() {
        super("LiquidInteraction", "liquid_interaction");
    }

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

        private float c1;
        private float c2;
        private ItemStack result;
        private FluidStack component1 = null;
        private FluidStack component2 = null;
        private int probability = 1;
        private boolean chanceCalled = false;
        private boolean componentCalled = false;

        public RecipeBuilder result(ItemStack item) {
            this.result = item;
            return this;
        }

        public RecipeBuilder component(FluidStack fluid) {
            if (!this.componentCalled) {
                this.component1 = fluid;
                componentCalled = true;
            } else {
                this.component2 = fluid;
            }
            return this;
        }

        public RecipeBuilder component(FluidStack fluid1, FluidStack fluid2) {
            this.component1 = fluid1;
            this.component2 = fluid2;
            return this;
        }

        public RecipeBuilder weight(int weight) {
            this.probability = weight;
            return this;
        }

        public RecipeBuilder chance(float consumptionChance) {
            if (!this.chanceCalled) {
                this.c1 = consumptionChance;
                chanceCalled = true;
            } else {
                this.c2 = consumptionChance;
            }
            return this;
        }

        public RecipeBuilder chance(float consumptionChance1, float consumptionChance2) {
            this.c1 = consumptionChance1;
            this.c2 = consumptionChance2;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Astral Sorcery Liquid Interaction";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
//            validateItems(msg, 0, 0, 1, 1);
//            msg.add(this.input == null, () -> "Input cannot be null");
//            msg.add(this.chance < 0, () -> "Chance cannot be negative");
//            msg.add(this.doubleChance < 0 || this.doubleChance > 1, () -> "Chance to double output must be between [0,1]");
        }

        public hellfirepvp.astralsorcery.common.base.LiquidInteraction register() {
//            if (!validate()) return null;
            return ModSupport.ASTRAL_SORCERY.get().liquidInteraction.add(this.probability, this.component1, this.component2, hellfirepvp.astralsorcery.common.base.LiquidInteraction.createItemDropAction(this.c1, this.c2, this.result));
        }
    }
}
