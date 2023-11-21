package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.LiquidInteractionAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import hellfirepvp.astralsorcery.common.base.LiquidInteraction;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public class ChaliceInteraction extends VirtualizedRegistry<LiquidInteraction> {

    private static List<LiquidInteraction> getRegistry() {
        if (LiquidInteractionAccessor.getRegisteredInteractions() == null) {
            throw new IllegalStateException("Astral Sorcery Chalice Interaction getRegisteredInteractions() is not yet initialized!");
        }
        return LiquidInteractionAccessor.getRegisteredInteractions();
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(getRegistry()::remove);
        restoreFromBackup().forEach(getRegistry()::add);
    }

    private void add(LiquidInteraction recipe) {
        addScripted(recipe);
        getRegistry().add(recipe);
    }

    public LiquidInteraction add(int probability, FluidStack component1, FluidStack component2, LiquidInteraction.FluidInteractionAction action) {
        LiquidInteraction recipe = new LiquidInteraction(probability, component1, component2, action);
        addScripted(recipe);
        getRegistry().add(recipe);
        return recipe;
    }

    private boolean remove(LiquidInteraction recipe) {
        return getRegistry().removeIf(rec -> rec.equals(recipe));
    }

    public void removeByInput(Fluid fluid1, Fluid fluid2) {
        getRegistry().removeIf(rec -> {
            if ((rec.getComponent1().getFluid().equals(fluid1) && rec.getComponent2().getFluid().equals(fluid2)) ||
                (rec.getComponent1().getFluid().equals(fluid2) && rec.getComponent2().getFluid().equals(fluid1))) {
                addBackup(rec);
                return true;
            }
            return false;
        });
    }

    public void removeByInput(FluidStack fluid1, FluidStack fluid2) {
        this.removeByInput(fluid1.getFluid(), fluid2.getFluid());
    }

    public void removeByInput(Fluid fluid) {
        getRegistry().removeIf(rec -> {
            if ((rec.getComponent1().getFluid().equals(fluid) || rec.getComponent2().getFluid().equals(fluid))) {
                addBackup(rec);
                return true;
            }
            return false;
        });
    }

    public void removeByInput(FluidStack fluid) {
        this.removeByInput(fluid.getFluid());
    }

    public void removeByOutput(ItemStack output) {
        getRegistry().removeIf(rec -> {
            if (((LiquidInteractionAccessor) rec).getFluidInteractionAction().getOutputForMatching().isItemEqual(output)) {
                addBackup(rec);
                return true;
            }
            return false;
        });
    }

    public SimpleObjectStream<LiquidInteraction> streamRecipes() {
        return new SimpleObjectStream<>(getRegistry())
                .setRemover(this::remove);
    }

    public void removeAll() {
        getRegistry().forEach(this::addBackup);
        getRegistry().clear();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<LiquidInteraction> {

        private final FloatArrayList chances = new FloatArrayList();
        private final IntArrayList probabilities = new IntArrayList();

        public RecipeBuilder result(ItemStack item, int weight) {
            this.output.add(item);
            this.probabilities.add(weight);
            return this;
        }

        public RecipeBuilder result(ItemStack item) {
            return this.result(item, 1);
        }

        public RecipeBuilder output(ItemStack item, int weight) {
            return this.result(item, weight);
        }

        public RecipeBuilder output(ItemStack item) {
            return this.result(item, 1);
        }

        public RecipeBuilder component(FluidStack fluid, float chance) {
            this.fluidInput.add(fluid);
            this.chances.add(chance);
            return this;
        }

        public RecipeBuilder component(FluidStack fluid) {
            return this.component(fluid, 1.0F);
        }

        public RecipeBuilder fluidInput(FluidStack fluid, float chance) {
            return this.component(fluid, chance);
        }

        public RecipeBuilder fluidInput(FluidStack fluid) {
            return this.component(fluid, 1.0F);
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Astral Sorcery Chalice Interaction";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, Integer.MAX_VALUE);
            for (Integer probability : probabilities) {
                if (probability <= 0) msg.add("Weight must be a positive integer. Instead found: " + probability);
            }
            validateFluids(msg, 2, 2, 0, 0);
            msg.add(chances.getFloat(0) < 0 || chances.getFloat(1) < 0, () -> "Consumption chance cannot be negative");
        }

        @Override
        public LiquidInteraction register() {
            if (!validate()) return null;
            LiquidInteraction recipe = null;
            for (int i = 0; i < this.output.size(); i++) {
                recipe = new LiquidInteraction(
                        probabilities.getInt(i),
                        fluidInput.get(0),
                        fluidInput.get(1),
                        LiquidInteraction.createItemDropAction(chances.getFloat(0), chances.getFloat(1), output.get(i))
                );
                ModSupport.ASTRAL_SORCERY.get().chaliceInteraction.add(recipe);
            }
            return recipe;
        }
    }
}
