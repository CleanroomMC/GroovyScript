package com.cleanroommc.groovyscript.compat.mods.immersivetechnology;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import mctmods.immersivetechnology.api.crafting.DistillerRecipe;
import mctmods.immersivetechnology.common.Config;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Distiller extends StandardListRegistry<DistillerRecipe> {

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
    public Collection<DistillerRecipe> getRecipes() {
        return DistillerRecipe.recipeList;
    }

    @MethodDescription(example = @Example(value = "fluid('water')", commented = true))
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

    @MethodDescription(example = {@Example("fluid('distwater')"), @Example(value = "item('immersivetech:material')", commented = true)})
    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(r -> {
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

    @Property(property = "fluidInput", valid = @Comp("1"))
    @Property(property = "fluidOutput", valid = @Comp("1"))
    @Property(property = "output", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
    public static class RecipeBuilder extends AbstractRecipeBuilder<DistillerRecipe> {

        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private float chance;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int time;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
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
