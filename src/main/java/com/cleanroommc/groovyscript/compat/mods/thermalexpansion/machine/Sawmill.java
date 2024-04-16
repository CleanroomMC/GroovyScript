package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.machine;

import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import cofh.thermalexpansion.util.managers.machine.SawmillManager.SawmillRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.SawmillManagerAccessor;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.SawmillRecipeAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@RegistryDescription(
        admonition = @Admonition("groovyscript.wiki.thermalexpansion.sawmill.note0")
)
public class Sawmill extends VirtualizedRegistry<SawmillRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:gold_ingot') * 2)"),
            @Example(".input(item('minecraft:clay') * 4).output(item('minecraft:gold_ingot'), item('minecraft:diamond')).chance(25).energy(1000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> SawmillManagerAccessor.getRecipeMap().values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> SawmillManagerAccessor.getRecipeMap().put(SawmillManager.convertInput(r.getInput()), r));
    }

    public void add(SawmillRecipe recipe) {
        SawmillManagerAccessor.getRecipeMap().put(SawmillManager.convertInput(recipe.getInput()), recipe);
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("1000, item('minecraft:obsidian') * 4, item('minecraft:gold_ingot'), item('minecraft:diamond'), 25"))
    public SawmillRecipe add(int energy, IIngredient input, ItemStack outputItem, ItemStack secondayOutput, int chance) {
        return recipeBuilder()
                .energy(energy)
                .chance(chance)
                .input(input)
                .output(outputItem, secondayOutput)
                .register();
    }

    public boolean remove(SawmillRecipe recipe) {
        return SawmillManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (r == recipe) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:pumpkin')"))
    public boolean removeByInput(IIngredient input) {
        return SawmillManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (input.test(r.getInput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = {
            @Example("item('thermalfoundation:material:800')"), @Example("item('minecraft:leather')")
    })
    public boolean removeByOutput(IIngredient output) {
        return SawmillManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (output.test(r.getPrimaryOutput()) || output.test(r.getSecondaryOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<SawmillRecipe> streamRecipes() {
        return new SimpleObjectStream<>(SawmillManagerAccessor.getRecipeMap().values()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        SawmillManagerAccessor.getRecipeMap().values().forEach(this::addBackup);
        SawmillManagerAccessor.getRecipeMap().clear();
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "2")})
    public static class RecipeBuilder extends AbstractRecipeBuilder<SawmillRecipe> {

        @Property(defaultValue = "SawmillManager.DEFAULT_ENERGY", valid = @Comp(value = "0", type = Comp.Type.GT), value = "groovyscript.wiki.thermalexpansion.energy.value")
        private int energy = SawmillManager.DEFAULT_ENERGY;
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
            return "Error adding Thermal Expansion Sawmill recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 2);
            validateFluids(msg);
            msg.add(energy <= 0, "energy must be greater than 0, yet it was {}", energy);
            msg.add(chance < 0 || chance > 100, "chance must be a non negative integer less than 100, yet it was {}", chance);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable SawmillRecipe register() {
            if (!validate()) return null;
            SawmillRecipe recipe = null;

            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                SawmillRecipe recipe1 = SawmillRecipeAccessor.createSawmillRecipe(itemStack, output.get(0), output.getOrEmpty(1), chance, energy);
                ModSupport.THERMAL_EXPANSION.get().sawmill.add(recipe1);
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
