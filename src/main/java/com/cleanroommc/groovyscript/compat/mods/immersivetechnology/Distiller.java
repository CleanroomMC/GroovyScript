package com.cleanroommc.groovyscript.compat.mods.immersivetechnology;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import mctmods.immersivetechnology.api.crafting.DistillerRecipe;
import mctmods.immersivetechnology.common.Config;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Distiller extends VirtualizedRegistry<DistillerRecipe> {

    @Override
    public boolean isEnabled() {
        return Config.ITConfig.Machines.Multiblock.enable_distiller;
    }

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('lava') * 100).fluidOutput(fluid('hot_spring_water') * 500).time(100)"),
            @Example(".fluidInput(fluid('water') * 50).fluidOutput(fluid('lava') * 50).output(item('minecraft:diamond')).chance(0.5f).time(50).energy(5000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        DistillerRecipe.recipeList.removeAll(removeScripted());
        DistillerRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(DistillerRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            DistillerRecipe.recipeList.add(recipe);
        }
    }

    public boolean remove(DistillerRecipe recipe) {
        if (DistillerRecipe.recipeList.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example(value = "fluid('water')", commented = true))
    public void removeByInput(IIngredient input) {
        DistillerRecipe.recipeList.removeIf(r -> {
            for (FluidStack fluidStack : r.getFluidInputs()) {
                if (input.test(fluidStack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(example = {@Example("fluid('distwater')"), @Example(value = "item('immersivetech:material')", commented = true)})
    public void removeByOutput(IIngredient output) {
        DistillerRecipe.recipeList.removeIf(r -> {
            for (FluidStack fluidStack : r.getFluidOutputs()) {
                if (output.test(fluidStack)) {
                    addBackup(r);
                    return true;
                }
            }
            for (ItemStack itemStack : r.getItemOutputs()) {
                if (output.test(itemStack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<DistillerRecipe> streamRecipes() {
        return new SimpleObjectStream<>(DistillerRecipe.recipeList).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        DistillerRecipe.recipeList.forEach(this::addBackup);
        DistillerRecipe.recipeList.clear();
    }

    @Property(property = "fluidInput", comp = @Comp(types = Comp.Type.EQ, eq = 1))
    @Property(property = "fluidOutput", comp = @Comp(types = Comp.Type.EQ, eq = 1))
    @Property(property = "output", comp = @Comp(types = {Comp.Type.GTE, Comp.Type.LTE}, gte = 0, lte = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<DistillerRecipe> {

        @Property(comp = @Comp(types = Comp.Type.GTE, gte = 0))
        private float chance;
        @Property(comp = @Comp(types = Comp.Type.GTE, gte = 0))
        private int time;
        @Property(comp = @Comp(types = Comp.Type.GTE, gte = 0))
        private int energy;

        @RecipeBuilderMethodDescription
        public RecipeBuilder chance(float chance) {
            this.chance = chance;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Technology Distiller recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 0, 1);
            validateFluids(msg, 1, 1, 1, 1);
            msg.add(time <= 0, "time must be greater than or equal to 1, yet it was {}", time);
            msg.add(energy < 0, "energy must be a non negative integer, yet it was {}", energy);
            msg.add(chance < 0 || chance > 1, "chance must be between 0 and 1, yet it was {}", chance);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable DistillerRecipe register() {
            if (!validate()) return null;
            DistillerRecipe recipe = new DistillerRecipe(fluidOutput.get(0), fluidInput.get(0), output.getOrEmpty(0), energy, time, chance);
            ModSupport.IMMERSIVE_TECHNOLOGY.get().distiller.add(recipe);
            return recipe;
        }
    }

}
