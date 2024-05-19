package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.machine;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager.CrucibleRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.CrucibleManagerAccessor;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.CrucibleRecipeAccessor;
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
public class Crucible extends VirtualizedRegistry<CrucibleRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).fluidOutput(fluid('lava') * 25)"),
            @Example(".input(item('minecraft:diamond')).fluidOutput(fluid('water') * 1000).energy(1000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> CrucibleManagerAccessor.getRecipeMap().values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> CrucibleManagerAccessor.getRecipeMap().put(new ComparableItemStackValidatedNBT(r.getInput()), r));
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void afterScriptLoad() {
        CrucibleManagerAccessor.getLavaSet().clear();
        CrucibleManagerAccessor.getLavaSet().addAll(CrucibleManagerAccessor.getRecipeMap().values().stream()
                                                            .filter(x -> x.getOutput().getFluid() == FluidRegistry.LAVA)
                                                            .map(CrucibleRecipe::getInput)
                                                            .map(CrucibleManager::convertInput)
                                                            .collect(Collectors.toList()));
    }

    public void add(CrucibleRecipe recipe) {
        CrucibleManagerAccessor.getRecipeMap().put(new ComparableItemStackValidatedNBT(recipe.getInput()), recipe);
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("1000, item('minecraft:obsidian'), fluid('water') * 1000"))
    public CrucibleRecipe add(int energy, IIngredient input, FluidStack fluidOutput) {
        return recipeBuilder()
                .energy(energy)
                .input(input)
                .fluidOutput(fluidOutput)
                .register();
    }

    public boolean remove(CrucibleRecipe recipe) {
        return CrucibleManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (r == recipe) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:glowstone_dust')"))
    public boolean removeByInput(IIngredient input) {
        return CrucibleManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (input.test(r.getInput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("fluid('lava')"))
    public boolean removeByOutput(IIngredient output) {
        return CrucibleManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (output.test(r.getOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<CrucibleRecipe> streamRecipes() {
        return new SimpleObjectStream<>(CrucibleManagerAccessor.getRecipeMap().values()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        CrucibleManagerAccessor.getRecipeMap().values().forEach(this::addBackup);
        CrucibleManagerAccessor.getRecipeMap().clear();
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "fluidOutput", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CrucibleRecipe> {

        @Property(defaultValue = "CrucibleManager.DEFAULT_ENERGY", valid = @Comp(value = "0", type = Comp.Type.GT), value = "groovyscript.wiki.thermalexpansion.energy.value")
        private int energy = CrucibleManager.DEFAULT_ENERGY;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Expansion Crucible recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg, 0, 0, 1, 1);
            msg.add(energy <= 0, "energy must be greater than 0, yet it was {}", energy);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CrucibleRecipe register() {
            if (!validate()) return null;
            CrucibleRecipe recipe = null;

            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                CrucibleRecipe recipe1 = CrucibleRecipeAccessor.createCrucibleRecipe(itemStack, fluidOutput.get(0), energy);
                ModSupport.THERMAL_EXPANSION.get().crucible.add(recipe1);
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
