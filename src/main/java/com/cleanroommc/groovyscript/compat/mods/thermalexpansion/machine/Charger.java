package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.machine;

import cofh.core.inventory.ComparableItemStackValidated;
import cofh.thermalexpansion.util.managers.machine.ChargerManager;
import cofh.thermalexpansion.util.managers.machine.ChargerManager.ChargerRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.ChargerManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Charger extends VirtualizedRegistry<ChargerRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond') * 5).output(item('minecraft:clay'))"),
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond') * 2).energy(1000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> ChargerManagerAccessor.getRecipeMap().values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> ChargerManagerAccessor.getRecipeMap().put(new ComparableItemStackValidated(r.getInput()), r));
    }

    public void add(ChargerRecipe recipe) {
        ChargerManagerAccessor.getRecipeMap().put(new ComparableItemStackValidated(recipe.getInput()), recipe);
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "1000, item('minecraft:obsidian'), item('minecraft:diamond') * 2", commented = true))
    public ChargerRecipe add(int energy, IIngredient input, ItemStack output) {
        return recipeBuilder()
                .energy(energy)
                .input(input)
                .output(output)
                .register();
    }

    public boolean remove(ChargerRecipe recipe) {
        return ChargerManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (r == recipe) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('thermalfoundation:bait:1')"))
    public boolean removeByInput(IIngredient input) {
        return ChargerManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (input.test(r.getInput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('thermalfoundation:fertilizer:2')"))
    public boolean removeByOutput(IIngredient output) {
        return ChargerManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (output.test(r.getOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ChargerRecipe> streamRecipes() {
        return new SimpleObjectStream<>(ChargerManagerAccessor.getRecipeMap().values()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ChargerManagerAccessor.getRecipeMap().values().forEach(this::addBackup);
        ChargerManagerAccessor.getRecipeMap().clear();
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ChargerRecipe> {

        @Property(defaultValue = "ChargerManager.DEFAULT_ENERGY", valid = @Comp(value = "0", type = Comp.Type.GT), value = "groovyscript.wiki.thermalexpansion.energy.value")
        private int energy = ChargerManager.DEFAULT_ENERGY;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Expansion Charger recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(energy <= 0, "energy must be greater than 0, yet it was {}", energy);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ChargerRecipe register() {
            if (!validate()) return null;
            ChargerRecipe recipe = null;

            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                // For some reason this specific Recipe is public. The rest aren't.
                ChargerRecipe recipe1 = new ChargerRecipe(itemStack, output.get(0), energy);
                ModSupport.THERMAL_EXPANSION.get().charger.add(recipe1);
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
