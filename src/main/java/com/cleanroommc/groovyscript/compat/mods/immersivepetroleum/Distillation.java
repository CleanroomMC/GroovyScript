package com.cleanroommc.groovyscript.compat.mods.immersivepetroleum;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import flaxbeard.immersivepetroleum.api.crafting.DistillationRecipe;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Distillation extends VirtualizedRegistry<DistillationRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('water') * 100).fluidOutput(fluid('water') * 50, fluid('lava') * 30).output(item('minecraft:diamond'), item('minecraft:clay'), item('minecraft:diamond'), item('minecraft:clay'), item('minecraft:diamond') * 5).chance(0.5, 0.2, 0.1, 0.5, 0.01).time(5).energy(1000)"),
            @Example(".fluidInput(fluid('lava') * 5).output(item('minecraft:diamond')).chance(1).time(1)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> DistillationRecipe.recipeList.removeIf(r -> r == recipe));
        DistillationRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(DistillationRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            DistillationRecipe.recipeList.add(recipe);
        }
    }

    public boolean remove(DistillationRecipe recipe) {
        if (DistillationRecipe.recipeList.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = {
            @Example(value = "item('immersivepetroleum:material')", commented = true),
            @Example(value = "fluid('lubricant')", commented = true)
    })
    public void removeByOutput(IIngredient output) {
        DistillationRecipe.recipeList.removeIf(r -> {
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
        DistillationRecipe.recipeList.removeIf(r -> {
            for (FluidStack fluidStack : r.getFluidInputs()) {
                if (input.test(fluidStack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<DistillationRecipe> streamRecipes() {
        return new SimpleObjectStream<>(DistillationRecipe.recipeList).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        DistillationRecipe.recipeList.forEach(this::addBackup);
        DistillationRecipe.recipeList.clear();
    }

    @Property(property = "fluidInput", valid = @Comp("1"))
    @Property(property = "output", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "Integer.MAX_VALUE")})
    @Property(property = "fluidOutput", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "Integer.MAX_VALUE")})
    public static class RecipeBuilder extends AbstractRecipeBuilder<DistillationRecipe> {

        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private final FloatArrayList chance = new FloatArrayList();
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int time;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int energy;

        @RecipeBuilderMethodDescription
        public RecipeBuilder chance(float chance) {
            this.chance.add(chance);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder chance(float... chances) {
            for (float chance : chances) {
                chance(chance);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder chance(Collection<Float> chances) {
            for (float chance : chances) {
                chance(chance);
            }
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
