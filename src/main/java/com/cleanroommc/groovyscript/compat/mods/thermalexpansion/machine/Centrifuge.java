package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.machine;

import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager.CentrifugeRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.CentrifugeManagerAccessor;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.CentrifugeRecipeAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RegistryDescription
public class Centrifuge extends VirtualizedRegistry<CentrifugeRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).fluidOutput(fluid('water') * 100).output(item('minecraft:diamond') * 2, item('minecraft:gold_ingot'), item('minecraft:gold_ingot')).chance(50, 100, 1)"),
            @Example(".input(item('minecraft:diamond') * 3).output(item('minecraft:clay')).chance(100).energy(1000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> CentrifugeManagerAccessor.getRecipeMap().values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> CentrifugeManagerAccessor.getRecipeMap().put(CentrifugeManager.convertInput(r.getInput()), r));
    }

    public void add(CentrifugeRecipe recipe) {
        CentrifugeManagerAccessor.getRecipeMap().put(CentrifugeManager.convertInput(recipe.getInput()), recipe);
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "1000, item('minecraft:obsidian') * 3, [item('minecraft:clay')], [100], null", commented = true))
    public CentrifugeRecipe add(int energy, IIngredient input, List<ItemStack> output, List<Integer> chance, FluidStack fluidOutput) {
        return recipeBuilder()
                .energy(energy)
                .chance(chance)
                .input(input)
                .output(output)
                .fluidOutput(fluidOutput)
                .register();
    }

    public boolean remove(CentrifugeRecipe recipe) {
        return CentrifugeManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (r == recipe) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:reeds')"))
    public boolean removeByInput(IIngredient input) {
        return CentrifugeManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (input.test(r.getInput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = {@Example("fluid('redstone')"), @Example("item('minecraft:redstone')")})
    public boolean removeByOutput(IIngredient output) {
        return CentrifugeManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (output.test(r.getFluid()) || r.getOutput().stream().anyMatch(output)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<CentrifugeRecipe> streamRecipes() {
        return new SimpleObjectStream<>(CentrifugeManagerAccessor.getRecipeMap().values()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        CentrifugeManagerAccessor.getRecipeMap().values().forEach(this::addBackup);
        CentrifugeManagerAccessor.getRecipeMap().clear();
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "4")})
    @Property(property = "fluidOutput", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
    public static class RecipeBuilder extends AbstractRecipeBuilder<CentrifugeRecipe> {

        @Property(valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "100")})
        private final List<Integer> chance = new ArrayList<>();
        @Property(defaultValue = "CentrifugeManager.DEFAULT_ENERGY", valid = @Comp(value = "0", type = Comp.Type.GT), value = "groovyscript.wiki.thermalexpansion.energy.value")
        private int energy = CentrifugeManager.DEFAULT_ENERGY;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder chance(int chance) {
            this.chance.add(chance);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder chance(Integer... chance) {
            this.chance.addAll(Arrays.asList(chance));
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder chance(List<Integer> chance) {
            this.chance.addAll(chance);
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Expansion Centrifuge recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 4);
            validateFluids(msg, 0, 0, 0, 1);
            msg.add(energy <= 0, "energy must be greater than 0, yet it was {}", energy);
            chance.forEach(x -> msg.add(x < 0 || x > 100, "all chance values must be non negative integers less than 100, yet one was {}", x));
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CentrifugeRecipe register() {
            if (!validate()) return null;
            CentrifugeRecipe recipe = null;

            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                CentrifugeRecipe recipe1 = CentrifugeRecipeAccessor.createCentrifugeRecipe(itemStack, output, chance, fluidOutput.getOrEmpty(0), energy);
                ModSupport.THERMAL_EXPANSION.get().centrifuge.add(recipe1);
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
