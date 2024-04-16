package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.machine;

import cofh.core.util.ItemWrapper;
import cofh.thermalexpansion.util.managers.machine.PrecipitatorManager;
import cofh.thermalexpansion.util.managers.machine.PrecipitatorManager.PrecipitatorRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.PrecipitatorManagerAccessor;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.PrecipitatorRecipeAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

@RegistryDescription
public class Precipitator extends VirtualizedRegistry<PrecipitatorRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".output(item('minecraft:clay'))"),
            @Example(".water(100).output(item('minecraft:clay')).energy(1000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> PrecipitatorManagerAccessor.getRecipeMap().values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> PrecipitatorManagerAccessor.getRecipeMap().put(new ItemWrapper(r.getOutput()), r));
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void afterScriptLoad() {
        PrecipitatorManagerAccessor.getOutputList().clear();
        PrecipitatorManagerAccessor.getOutputList().addAll(
                PrecipitatorManagerAccessor.getRecipeMap().values().stream().map(PrecipitatorManager.PrecipitatorRecipe::getOutput).collect(Collectors.toList())
        );
    }

    public void add(PrecipitatorRecipe recipe) {
        PrecipitatorManagerAccessor.getRecipeMap().put(new ItemWrapper(recipe.getOutput()), recipe);
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("1000, item('minecraft:obsidian'), 100"))
    public PrecipitatorRecipe add(int energy, ItemStack output, int water) {
        return recipeBuilder()
                .energy(energy)
                .water(water)
                .output(output)
                .register();
    }

    public boolean remove(PrecipitatorRecipe recipe) {
        return PrecipitatorManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (r == recipe) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "fluid('water')", commented = true))
    public boolean removeByInput(IIngredient input) {
        return PrecipitatorManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (input.test(r.getInput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:snowball')"))
    public boolean removeByOutput(IIngredient output) {
        return PrecipitatorManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (output.test(r.getOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<PrecipitatorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(PrecipitatorManagerAccessor.getRecipeMap().values()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        PrecipitatorManagerAccessor.getRecipeMap().values().forEach(this::addBackup);
        PrecipitatorManagerAccessor.getRecipeMap().clear();
    }

    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<PrecipitatorRecipe> {

        @Property(defaultValue = "PrecipitatorManager.DEFAULT_ENERGY", valid = @Comp(value = "0", type = Comp.Type.GT), value = "groovyscript.wiki.thermalexpansion.energy.value")
        private int energy = PrecipitatorManager.DEFAULT_ENERGY;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int water;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder water(int water) {
            this.water = water;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Expansion Precipitator recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg);
            msg.add(energy <= 0, "energy must be greater than 0, yet it was {}", energy);
            msg.add(water < 0, "water must be greater than or equal to 0, yet it was {}", water);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable PrecipitatorRecipe register() {
            if (!validate()) return null;
            PrecipitatorRecipe recipe = PrecipitatorRecipeAccessor.createPrecipitatorRecipe(output.get(0), new FluidStack(FluidRegistry.WATER, water), energy);
            ModSupport.THERMAL_EXPANSION.get().precipitator.add(recipe);
            return recipe;
        }
    }
}
