package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.machine;

import cofh.core.util.ItemWrapper;
import cofh.thermalexpansion.util.managers.machine.ExtruderManager;
import cofh.thermalexpansion.util.managers.machine.ExtruderManager.ExtruderRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.ExtruderManagerAccessor;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.ExtruderRecipeAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.google.common.primitives.Booleans;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@RegistryDescription
public class Extruder extends VirtualizedRegistry<Pair<Boolean, ExtruderRecipe>> {

    @RecipeBuilderDescription(example = {
            @Example(".fluidHot(100).fluidCold(1000).output(item('minecraft:clay'))"),
            @Example(".fluidHot(100).fluidCold(1000).output(item('minecraft:gold_ingot')).sedimentary().energy(1000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> map(recipe.getKey()).values().removeIf(r -> r == recipe.getValue()));
        restoreFromBackup().forEach(r -> map(r.getKey()).put(new ItemWrapper(r.getValue().getOutput()), r.getValue()));
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void afterScriptLoad() {
        ExtruderManagerAccessor.getOutputListIgneous().clear();
        ExtruderManagerAccessor.getOutputListIgneous().addAll(
                ExtruderManagerAccessor.getRecipeMapIgneous().values().stream().map(ExtruderRecipe::getOutput).collect(Collectors.toList())
        );
        ExtruderManagerAccessor.getOutputListSedimentary().clear();
        ExtruderManagerAccessor.getOutputListSedimentary().addAll(
                ExtruderManagerAccessor.getRecipeMapSedimentary().values().stream().map(ExtruderRecipe::getOutput).collect(Collectors.toList())
        );
    }

    private Map<ItemWrapper, ExtruderManager.ExtruderRecipe> map(boolean isSedimentary) {
        return isSedimentary ? ExtruderManagerAccessor.getRecipeMapSedimentary() : ExtruderManagerAccessor.getRecipeMapIgneous();
    }

    public void add(boolean sedimentary, ExtruderRecipe recipe) {
        map(sedimentary).put(new ItemWrapper(recipe.getOutput()), recipe);
        addScripted(Pair.of(sedimentary, recipe));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("1000, item('minecraft:gold_block'), 100, 1000, false"))
    public ExtruderRecipe add(int energy, ItemStack output, int fluidHot, int fluidCold, boolean sedimentary) {
        return recipeBuilder()
                .energy(energy)
                .fluidCold(fluidCold)
                .fluidHot(fluidHot)
                .sedimentary(sedimentary)
                .output(output)
                .register();
    }

    public boolean remove(boolean isSedimentary, ExtruderRecipe recipe) {
        return map(isSedimentary).values().removeIf(r -> {
            if (r == recipe) {
                addBackup(Pair.of(isSedimentary, recipe));
                return true;
            }
            return false;
        });
    }

    public boolean remove(ExtruderRecipe recipe) {
        // done this way so both remove operations are called
        boolean hasRemoved = remove(false, recipe);
        hasRemoved = remove(true, recipe) || hasRemoved;
        return hasRemoved;
    }

    @MethodDescription(example = @Example(value = "false, fluid('lava')", commented = true))
    public boolean removeByInput(boolean isSedimentary, IIngredient input) {
        return map(isSedimentary).values().removeIf(r -> {
            if (input.test(r.getInputHot()) || input.test(r.getInputCold())) {
                addBackup(Pair.of(isSedimentary, r));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "fluid('water')", commented = true))
    public boolean removeByInput(IIngredient input) {
        // done this way so both remove operations are called
        boolean hasRemoved = removeByInput(false, input);
        hasRemoved = removeByInput(true, input) || hasRemoved;
        return hasRemoved;
    }

    @MethodDescription(example = @Example("true, item('minecraft:gravel')"))
    public boolean removeByOutput(boolean isSedimentary, IIngredient output) {
        return map(isSedimentary).values().removeIf(r -> {
            if (output.test(r.getOutput())) {
                addBackup(Pair.of(isSedimentary, r));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:obsidian')"))
    public boolean removeByOutput(IIngredient output) {
        // done this way so both remove operations are called
        boolean hasRemoved = removeByOutput(false, output);
        hasRemoved = removeByOutput(true, output) || hasRemoved;
        return hasRemoved;
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ExtruderRecipe> streamRecipes() {
        return new SimpleObjectStream<>(Booleans.asList(true, false).stream().map(this::map).map(Map::values).flatMap(Collection::stream).collect(Collectors.toList()))
                .setRemover(this::remove);
    }

    @MethodDescription(example = @Example(value = "true", commented = true))
    public void removeByType(boolean sedimentary) {
        map(sedimentary).values().forEach(x -> addBackup(Pair.of(sedimentary, x)));
        map(sedimentary).clear();
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        removeByType(true);
        removeByType(false);
    }

    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ExtruderRecipe> {

        @Property(defaultValue = "ExtruderManager.DEFAULT_ENERGY", valid = @Comp(value = "0", type = Comp.Type.GT), value = "groovyscript.wiki.thermalexpansion.energy.value")
        private int energy = ExtruderManager.DEFAULT_ENERGY;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int fluidHot;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int fluidCold;
        @Property
        private boolean sedimentary;


        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder fluidHot(int fluidHot) {
            this.fluidHot = fluidHot;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder fluidCold(int fluidCold) {
            this.fluidCold = fluidCold;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder sedimentary() {
            this.sedimentary = !sedimentary;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder sedimentary(boolean sedimentary) {
            this.sedimentary = sedimentary;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Expansion Extruder recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg);
            msg.add(energy <= 0, "energy must be greater than 0, yet it was {}", energy);
            msg.add(fluidHot < 0, "fluidHot must be greater than or equal to 0, yet it was {}", fluidHot);
            msg.add(fluidCold < 0, "fluidCold must be greater than or equal to 0, yet it was {}", fluidCold);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ExtruderRecipe register() {
            if (!validate()) return null;
            ExtruderRecipe recipe = ExtruderRecipeAccessor.createExtruderRecipe(output.get(0), new FluidStack(FluidRegistry.LAVA, fluidHot), new FluidStack(FluidRegistry.WATER, fluidCold), energy);
            ModSupport.THERMAL_EXPANSION.get().extruder.add(sedimentary, recipe);
            return recipe;
        }
    }
}
