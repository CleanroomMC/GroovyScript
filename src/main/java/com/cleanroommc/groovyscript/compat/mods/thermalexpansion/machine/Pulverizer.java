package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.machine;

import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager.PulverizerRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.PulverizerManagerAccessor;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.PulverizerRecipeAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Pulverizer extends VirtualizedRegistry<PulverizerRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay'), item('minecraft:diamond')).chance(1)"),
            @Example(".input(item('minecraft:clay')).output(item('minecraft:gold_ingot'), item('minecraft:gold_ingot')).energy(1000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> PulverizerManagerAccessor.getRecipeMap().values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> PulverizerManagerAccessor.getRecipeMap().put(PulverizerManager.convertInput(r.getInput()), r));
    }

    public void add(PulverizerRecipe recipe) {
        PulverizerManagerAccessor.getRecipeMap().put(PulverizerManager.convertInput(recipe.getInput()), recipe);
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("1000, item('minecraft:obsidian'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), 100"))
    public PulverizerRecipe add(int energy, IIngredient input, ItemStack primaryOutput, ItemStack secondaryOutput, int chance) {
        return recipeBuilder()
                .energy(energy)
                .chance(chance)
                .output(primaryOutput, secondaryOutput)
                .input(input)
                .register();
    }

    public boolean remove(PulverizerRecipe recipe) {
        return PulverizerManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (r == recipe) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:emerald_ore')"))
    public boolean removeByInput(IIngredient input) {
        return PulverizerManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (input.test(r.getInput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = {
            @Example("item('thermalfoundation:material:772')"), @Example("item('minecraft:diamond')")
    })
    public boolean removeByOutput(IIngredient output) {
        return PulverizerManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (output.test(r.getPrimaryOutput()) || output.test(r.getSecondaryOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<PulverizerRecipe> streamRecipes() {
        return new SimpleObjectStream<>(PulverizerManagerAccessor.getRecipeMap().values()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        PulverizerManagerAccessor.getRecipeMap().values().forEach(this::addBackup);
        PulverizerManagerAccessor.getRecipeMap().clear();
    }

    @Property(property = "input", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "2")})
    @Property(property = "output", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "2")})
    public static class RecipeBuilder extends AbstractRecipeBuilder<PulverizerRecipe> {

        @Property(defaultValue = "PulverizerManager.DEFAULT_ENERGY", valid = @Comp(value = "0", type = Comp.Type.GT), value = "groovyscript.wiki.thermalexpansion.energy.value")
        private int energy = PulverizerManager.DEFAULT_ENERGY;
        @Property(valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "100")})
        private int chance;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder chance(int chance) {
            this.chance = chance;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Expansion Pulverizer recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 2, 1, 2);
            validateFluids(msg);
            msg.add(chance < 0 || chance > 100, "chance must be a non negative integer less than 100, yet it was {}", chance);
            msg.add(energy <= 0, "energy must be greater than 0, yet it was {}", energy);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable PulverizerRecipe register() {
            if (!validate()) return null;
            PulverizerRecipe recipe = null;

            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                PulverizerRecipe recipe1 = PulverizerRecipeAccessor.createPulverizerRecipe(itemStack, output.get(0), output.getOrEmpty(1), chance, energy);
                ModSupport.THERMAL_EXPANSION.get().pulverizer.add(recipe1);
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
