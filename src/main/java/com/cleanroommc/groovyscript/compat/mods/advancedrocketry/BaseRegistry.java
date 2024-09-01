package com.cleanroommc.groovyscript.compat.mods.advancedrocketry;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class BaseRegistry extends VirtualizedRegistry<IRecipe> {

    protected abstract Class<?> getMachineClass();

    @Nonnull
    @SuppressWarnings("unchecked")
    protected List<IRecipe> getRecipeList() {
        Class<?> clazz = getMachineClass();
        RecipesMachine registry = RecipesMachine.getInstance();
        List<IRecipe> recipes = registry.getRecipes(clazz);
        if (recipes == null) {
            recipes = new LinkedList<>();
            // NOTE: this cast is completely invalid but Advanced Rocketry's code is abusing type erasure on Class<?> generic
            // to perform the same cast when adding recipes to Small Plate Press (which is not a TileMultiblockMachine)
            registry.recipeList.put((Class<? extends TileMultiblockMachine>) clazz, recipes);
        }
        return recipes;
    }

    @Override
    public void onReload() {
        List<IRecipe> recipes = getRecipeList();
        recipes.addAll(restoreFromBackup());
        recipes.removeAll(removeScripted());
    }

    public void addRecipe(IRecipe r) {
        getRecipeList().add(r);
        addScripted(r);
    }

    public boolean removeRecipe(IRecipe r) {
        if (getRecipeList().remove(r)) {
            addBackup(r);
            return true;
        }
        return false;
    }

    public boolean removeByFluidInput(FluidStack fluidStack) {
        return getRecipeList().removeIf(r -> {
            List<FluidStack> inputFluid = r.getFluidIngredients();
            if (inputFluid.stream().anyMatch(fluidIn -> fluidIn.isFluidEqual(fluidStack))) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    public boolean removeByInput(IIngredient inputItem) {
        if (inputItem instanceof FluidStack) {
            return removeByFluidInput((FluidStack) inputItem);
        }
        return getRecipeList().removeIf(r -> {
            List<List<ItemStack>> input = r.getIngredients();
            if (input.stream().anyMatch(itemStacks -> itemStacks.stream().anyMatch(inputItem))) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    public boolean removeByFluidOutput(FluidStack fluidStack) {
        return getRecipeList().removeIf(r -> {
            List<FluidStack> outputFluid = r.getFluidOutputs();
            if (outputFluid.stream().anyMatch(fluidOut -> fluidOut.isFluidEqual(fluidStack))) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    public boolean removeByOutput(IIngredient outputItem) {
        if (outputItem instanceof FluidStack) {
            return removeByFluidInput((FluidStack) outputItem);
        }
        return getRecipeList().removeIf(r -> {
            List<ItemStack> output = r.getOutput();
            if (output.stream().anyMatch(outputItem)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        List<IRecipe> recipes = getRecipeList();
        recipes.forEach(this::addBackup);
        recipes.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IRecipe> streamRecipes() {
        List<IRecipe> recipes = getRecipeList();
        return new SimpleObjectStream<>(recipes)
                .setRemover(this::removeRecipe);
    }

    public abstract static class RecipeBuilder extends AbstractRecipeBuilder<IRecipe> {
        // still have to override the Validate method + add getRegistry method

        @Property(value = "groovyscript.wiki.advancedrocketry.power.value", needsOverride = true)
        protected int power = 0;
        @Property(value = "groovyscript.wiki.advancedrocketry.time.value", needsOverride = true)
        protected int time = 0;
        @Property(value = "groovyscript.wiki.advancedrocketry.outputSize.value", needsOverride = true)
        protected int outputSize = 0;

        protected final List<Float> outputChances = new ArrayList<>();

        // 0.0f is used because that's what AR uses for "100% chance". Copium.
        @Override
        @RecipeBuilderMethodDescription
        public RecipeBuilder output(ItemStack output) {
            return output(output, 0.0f);
        }

        @RecipeBuilderMethodDescription(field = {"output", "outputChances"})
        protected RecipeBuilder output(ItemStack output, float chance) {
            this.output.add(output);
            this.outputChances.add(chance);
            return this;
        }

        @Override
        public String getErrorMsg() {
            return String.format("Error adding Advanced Rocketry %s recipe", getRegistry().getName());
        }

        protected int getHatchesNeeded() {
            return (int) (Math.ceil(((double) input.size()) / 4) + Math.ceil(((double) output.size()) / 4) + fluidInput.size() + fluidOutput.size());
        }

        protected abstract BaseRegistry getRegistry();

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IRecipe register() {
            if (!validate()) return null;
            List<List<ItemStack>> inputs = new LinkedList<>();
            Map<Integer, String> oredicts = new HashMap<>();
            for (int i = 0; i < input.size(); i++) {
                IIngredient in = input.get(i);
                inputs.add(Arrays.asList(in.getMatchingStacks()));
                if (in instanceof OreDictIngredient) {
                    oredicts.put(i, ((OreDictIngredient) in).getOreDict());
                }
            }

            List<RecipesMachine.ChanceItemStack> outputs = new LinkedList<>();
            for (int i = 0; i < output.size(); i++) {
                ItemStack out = output.get(i);
                float chance = outputChances.get(i);
                outputs.add(new RecipesMachine.ChanceItemStack(out, chance));
            }

            List<RecipesMachine.ChanceFluidStack> fluidOutputs = new LinkedList<>();
            for (FluidStack out : fluidOutput) {
                // Fluid chances don't work properly, at least in the centrifuge.
                fluidOutputs.add(new RecipesMachine.ChanceFluidStack(out, 1.0f));
            }

            RecipesMachine.Recipe r = new RecipesMachine.Recipe(outputs, inputs, fluidOutputs, fluidInput, time, power, oredicts);
            if (outputSize > 0) r.setMaxOutputSize(outputSize);
            getRegistry().addRecipe(r);
            return r;
        }
    }

    @Property(property = "power", comp = @Comp(gte = 1), value = "groovyscript.wiki.advancedrocketry.power.value", hierarchy = 5)
    @Property(property = "time", comp = @Comp(gte = 1), value = "groovyscript.wiki.advancedrocketry.time.value", hierarchy = 5)
    @Property(property = "outputSize", comp = @Comp(gte = 1), value = "groovyscript.wiki.advancedrocketry.outputSize.value", hierarchy = 5)
    public abstract static class MultiblockRecipeBuilder extends RecipeBuilder {

        @RecipeBuilderMethodDescription
        public MultiblockRecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public MultiblockRecipeBuilder power(int power) {
            this.power = power;
            return this;
        }

        @RecipeBuilderMethodDescription
        public MultiblockRecipeBuilder outputSize(int outputSize) {
            this.outputSize = outputSize;
            return this;
        }

        @Override
        @RecipeBuilderMethodDescription
        public RecipeBuilder output(ItemStack output) {
            return output(output, 0.0f);
        }

        @Override
        @RecipeBuilderMethodDescription
        public RecipeBuilder output(ItemStack output, float chance) {
            return super.output(output, chance);
        }
    }

}
