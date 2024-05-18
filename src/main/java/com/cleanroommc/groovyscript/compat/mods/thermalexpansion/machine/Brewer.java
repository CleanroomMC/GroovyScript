package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.machine;

import cofh.core.util.helpers.FluidHelper;
import cofh.thermalexpansion.util.managers.machine.BrewerManager;
import cofh.thermalexpansion.util.managers.machine.BrewerManager.BrewerRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.BrewerManagerAccessor;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.BrewerRecipeAccessor;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class Brewer extends VirtualizedRegistry<BrewerRecipe> {

    public Brewer() {
        super(Alias.generateOfClass(Brewer.class).andGenerate("Imbuer"));
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).fluidInput(fluid('water') * 100).fluidOutput(fluid('lava') * 100)"),
            @Example(".input(item('minecraft:diamond') * 2).fluidInput(fluid('water') * 1000).fluidOutput(fluid('steam') * 100).energy(1000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> BrewerManagerAccessor.getRecipeMap().values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> BrewerManagerAccessor.getRecipeMap().put(hash(r), r));
    }

    @Override
    @GroovyBlacklist
    public void afterScriptLoad() {
        BrewerManagerAccessor.getValidationSet().clear();
        BrewerManagerAccessor.getValidationSet().addAll(
                BrewerManagerAccessor.getRecipeMap().values().stream().map(BrewerRecipe::getInput).map(BrewerManager::convertInput).collect(Collectors.toList())
        );
        BrewerManagerAccessor.getValidationFluids().clear();
        BrewerManagerAccessor.getValidationFluids().addAll(
                BrewerManagerAccessor.getRecipeMap().values().stream().map(BrewerRecipe::getInputFluid).map(FluidStack::getFluid).map(Fluid::getName).collect(Collectors.toList())
        );
    }

    private List<Integer> hash(BrewerRecipe recipe) {
        return hash(recipe.getInput(), recipe.getInputFluid());
    }

    private List<Integer> hash(ItemStack input, FluidStack fluid) {
        return Arrays.asList(BrewerManager.convertInput(input).hashCode(), FluidHelper.getFluidHash(fluid));
    }

    public void add(BrewerRecipe recipe) {
        BrewerManagerAccessor.getRecipeMap().put(hash(recipe), recipe);
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "1000, item('minecraft:obsidian') * 2, fluid('water') * 1000, fluid('steam') * 100", commented = true))
    public BrewerRecipe add(int energy, IIngredient input, FluidStack fluidInput, FluidStack fluidOutput) {
        return recipeBuilder()
                .energy(energy)
                .input(input)
                .fluidInput(fluidInput)
                .fluidOutput(fluidOutput)
                .register();
    }

    public boolean remove(BrewerRecipe recipe) {
        return BrewerManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (r == recipe) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = {
            @Example("fluid('potion').withNbt(['Potion': 'minecraft:leaping'])"),
            @Example("item('minecraft:glowstone_dust')")
    })
    public boolean removeByInput(IIngredient input) {
        return BrewerManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (input.test(r.getInput()) || input.test(r.getInputFluid())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("fluid('potion_splash').withNbt(['Potion': 'cofhcore:luck2'])"))
    public boolean removeByOutput(IIngredient output) {
        return BrewerManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (output.test(r.getOutputFluid())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<BrewerRecipe> streamRecipes() {
        return new SimpleObjectStream<>(BrewerManagerAccessor.getRecipeMap().values()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        BrewerManagerAccessor.getRecipeMap().values().forEach(this::addBackup);
        BrewerManagerAccessor.getRecipeMap().clear();
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "fluidInput", valid = @Comp("1"))
    @Property(property = "fluidOutput", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<BrewerRecipe> {

        @Property(defaultValue = "BrewerManager.DEFAULT_ENERGY", valid = @Comp(value = "0", type = Comp.Type.GT), value = "groovyscript.wiki.thermalexpansion.energy.value")
        private int energy = BrewerManager.DEFAULT_ENERGY;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Expansion Brewer recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg, 1, 1, 1, 1);
            msg.add(energy <= 0, "energy must be greater than 0, yet it was {}", energy);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable BrewerRecipe register() {
            if (!validate()) return null;
            BrewerRecipe recipe = null;

            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                BrewerRecipe recipe1 = BrewerRecipeAccessor.createBrewerRecipe(itemStack, fluidInput.get(0), fluidOutput.get(0), energy);
                ModSupport.THERMAL_EXPANSION.get().brewer.add(recipe1);
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
