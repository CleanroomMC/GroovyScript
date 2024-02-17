package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
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

@RegistryDescription
public class ChaliceInteraction extends VirtualizedRegistry<LiquidInteraction> {

    private static List<LiquidInteraction> getRegistry() {
        if (LiquidInteractionAccessor.getRegisteredInteractions() == null) {
            throw new IllegalStateException("Astral Sorcery Chalice Interaction getRegisteredInteractions() is not yet initialized!");
        }
        return LiquidInteractionAccessor.getRegisteredInteractions();
    }

    @RecipeBuilderDescription(example = @Example(".output(item('astralsorcery:blockmarble')).fluidInput(fluid('water') * 10).fluidInput(fluid('astralsorcery.liquidstarlight') * 30)"))
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

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public LiquidInteraction add(int probability, FluidStack component1, FluidStack component2, LiquidInteraction.FluidInteractionAction action) {
        LiquidInteraction recipe = new LiquidInteraction(probability, component1, component2, action);
        addScripted(recipe);
        getRegistry().add(recipe);
        return recipe;
    }

    private boolean remove(LiquidInteraction recipe) {
        return getRegistry().removeIf(rec -> rec.equals(recipe));
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput")
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

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("fluid('water'), fluid('lava')"))
    public void removeByInput(FluidStack fluid1, FluidStack fluid2) {
        this.removeByInput(fluid1.getFluid(), fluid2.getFluid());
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput")
    public void removeByInput(Fluid fluid) {
        getRegistry().removeIf(rec -> {
            if ((rec.getComponent1().getFluid().equals(fluid) || rec.getComponent2().getFluid().equals(fluid))) {
                addBackup(rec);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example(value = "fluid('astralsorcery.liquidstarlight')", commented = true))
    public void removeByInput(FluidStack fluid) {
        this.removeByInput(fluid.getFluid());
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('minecraft:ice')"))
    public void removeByOutput(ItemStack output) {
        getRegistry().removeIf(rec -> {
            if (((LiquidInteractionAccessor) rec).getFluidInteractionAction().getOutputForMatching().isItemEqual(output)) {
                addBackup(rec);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<LiquidInteraction> streamRecipes() {
        return new SimpleObjectStream<>(getRegistry())
                .setRemover(this::remove);
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        getRegistry().forEach(this::addBackup);
        getRegistry().clear();
    }

    @Property(property = "fluidInput", valid = @Comp("2"))
    @Property(property = "output", valid = @Comp(value = "0", type = Comp.Type.GTE))
    public static class RecipeBuilder extends AbstractRecipeBuilder<LiquidInteraction> {

        @Property(valid = @Comp("2"))
        private final FloatArrayList chances = new FloatArrayList();
        @Property(valid = @Comp(value = "0", type = Comp.Type.GT))
        private final IntArrayList probabilities = new IntArrayList();

        @RecipeBuilderMethodDescription(field = {"output", "probabilities"})
        public RecipeBuilder result(ItemStack item, int weight) {
            this.output.add(item);
            this.probabilities.add(weight);
            return this;
        }

        @RecipeBuilderMethodDescription(field = {"output", "probabilities"})
        public RecipeBuilder result(ItemStack item) {
            return this.result(item, 1);
        }

        @RecipeBuilderMethodDescription(field = {"output", "probabilities"})
        public RecipeBuilder output(ItemStack item, int weight) {
            return this.result(item, weight);
        }

        @RecipeBuilderMethodDescription(field = {"output", "probabilities"})
        public RecipeBuilder output(ItemStack item) {
            return this.result(item, 1);
        }

        @RecipeBuilderMethodDescription(field = {"fluidInput", "chances"})
        public RecipeBuilder component(FluidStack fluid, float chance) {
            this.fluidInput.add(fluid);
            this.chances.add(chance);
            return this;
        }

        @RecipeBuilderMethodDescription(field = {"fluidInput", "chances"})
        public RecipeBuilder component(FluidStack fluid) {
            return this.component(fluid, 1.0F);
        }

        @RecipeBuilderMethodDescription(field = {"fluidInput", "chances"})
        public RecipeBuilder fluidInput(FluidStack fluid, float chance) {
            return this.component(fluid, chance);
        }

        @RecipeBuilderMethodDescription(field = {"fluidInput", "chances"})
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
        @RecipeBuilderRegistrationMethod
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
