package com.cleanroommc.groovyscript.compat.mods.immersivepetroleum;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import flaxbeard.immersivepetroleum.api.crafting.DistillationRecipe;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Distillation extends StandardListRegistry<DistillationRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('water') * 100).fluidOutput(fluid('water') * 50, fluid('lava') * 30).output(item('minecraft:diamond'), 0.5).output(item('minecraft:clay'), 0.2).output(item('minecraft:diamond'), 0.1).output(item('minecraft:clay'), 0.5).output(item('minecraft:diamond') * 5, 0.01).time(5).energy(1000)"),
            @Example(".fluidInput(fluid('lava') * 5).output(item('minecraft:diamond')).time(1)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<DistillationRecipe> getRecipes() {
        return DistillationRecipe.recipeList;
    }

    @MethodDescription(example = {
            @Example(value = "item('immersivepetroleum:material')", commented = true),
            @Example(value = "fluid('lubricant')", commented = true)
    })
    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(r -> {
            for (ItemStack itemstack : r.getItemOutputs()) {
                if (output.test(itemstack)) {
                    addBackup(r);
                    return true;
                }
            }
            for (FluidStack fluidStack : r.getFluidOutputs()) {
                if (output.test(fluidStack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("fluid('oil')"))
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(r -> {
            for (FluidStack fluidStack : r.getFluidInputs()) {
                if (input.test(fluidStack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @Property(property = "fluidInput", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(gte = 0))
    @Property(property = "fluidOutput", comp = @Comp(gte = 0))
    public static class RecipeBuilder extends AbstractRecipeBuilder<DistillationRecipe> {

        @Property(comp = @Comp(gte = 0))
        private final FloatArrayList chance = new FloatArrayList();
        @Property(comp = @Comp(gte = 0))
        private int time;
        @Property(comp = @Comp(gte = 0))
        private int energy;

        @RecipeBuilderMethodDescription(field = {
                "output", "chance"
        })
        public RecipeBuilder output(ItemStack output, float chance) {
            this.output.add(output);
            this.chance.add(chance);
            return this;
        }

        @Override
        @RecipeBuilderMethodDescription(field = {
                "output", "chance"
        })
        public RecipeBuilder output(ItemStack output) {
            return this.output(output, 1);
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
            return "Error adding Immersive Petroleum Distillation recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 0, Integer.MAX_VALUE);
            validateFluids(msg, 1, 1, 0, Integer.MAX_VALUE);
            chance.trim();
            msg.add(output.size() != chance.size(), "output and chance must be of equal length, yet output was {} and chance was {}", output.size(), chance.size());
            msg.add(time <= 0, "time must be greater than or equal to 1, yet it was {}", time);
            msg.add(energy < 0, "energy must be a non negative integer, yet it was {}", energy);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable DistillationRecipe register() {
            if (!validate()) return null;
            DistillationRecipe recipe = new DistillationRecipe(fluidOutput.toArray(new FluidStack[0]), output.toArray(new ItemStack[0]), fluidInput.get(0), energy, time, chance.elements());
            ModSupport.IMMERSIVE_PETROLEUM.get().distillation.add(recipe);
            return recipe;
        }
    }

}
