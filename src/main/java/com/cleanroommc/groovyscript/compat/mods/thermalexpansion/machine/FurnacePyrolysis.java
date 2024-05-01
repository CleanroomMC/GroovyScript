package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.machine;

import cofh.thermalexpansion.util.managers.machine.FurnaceManager;
import cofh.thermalexpansion.util.managers.machine.FurnaceManager.FurnaceRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.FurnaceManagerAccessor;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.FurnaceRecipeAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class FurnacePyrolysis extends VirtualizedRegistry<FurnaceRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond') * 2).creosote(100)"),
            @Example(".input(item('minecraft:gold_ingot') * 2).output(item('minecraft:clay')).creosote(1000).energy(1000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> FurnaceManagerAccessor.getRecipeMapPyrolysis().values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> FurnaceManagerAccessor.getRecipeMapPyrolysis().put(FurnaceManager.convertInput(r.getInput()), r));
    }

    public void add(FurnaceRecipe recipe) {
        FurnaceManagerAccessor.getRecipeMapPyrolysis().put(FurnaceManager.convertInput(recipe.getInput()), recipe);
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("1000, item('minecraft:obsidian') * 2, item('minecraft:clay'), 1000"))
    public FurnaceRecipe add(int energy, IIngredient input, ItemStack output, int creosote) {
        return recipeBuilder()
                .energy(energy)
                .creosote(creosote)
                .input(input)
                .output(output)
                .register();
    }

    public boolean remove(FurnaceRecipe recipe) {
        return FurnaceManagerAccessor.getRecipeMapPyrolysis().values().removeIf(r -> {
            if (r == recipe) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:cactus:*')"))
    public boolean removeByInput(IIngredient input) {
        return FurnaceManagerAccessor.getRecipeMapPyrolysis().values().removeIf(r -> {
            if (input.test(r.getInput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('thermalfoundation:storage_resource:1')"))
    public boolean removeByOutput(IIngredient output) {
        return FurnaceManagerAccessor.getRecipeMapPyrolysis().values().removeIf(r -> {
            if (output.test(r.getOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<FurnaceRecipe> streamRecipes() {
        return new SimpleObjectStream<>(FurnaceManagerAccessor.getRecipeMapPyrolysis().values()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        FurnaceManagerAccessor.getRecipeMapPyrolysis().values().forEach(this::addBackup);
        FurnaceManagerAccessor.getRecipeMapPyrolysis().clear();
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<FurnaceRecipe> {

        @Property(defaultValue = "FurnaceManager.DEFAULT_ENERGY", valid = @Comp(value = "0", type = Comp.Type.GT), value = "groovyscript.wiki.thermalexpansion.energy.value")
        private int energy = FurnaceManager.DEFAULT_ENERGY;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int creosote;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder creosote(int creosote) {
            this.creosote = creosote;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Expansion Furnace Pyrolysis recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(energy <= 0, "energy must be greater than 0, yet it was {}", energy);
            msg.add(creosote < 0, "creosote must be greater than or equal to 0, yet it was {}", creosote);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable FurnaceRecipe register() {
            if (!validate()) return null;
            FurnaceRecipe recipe = null;

            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                FurnaceRecipe recipe1 = FurnaceRecipeAccessor.createFurnaceRecipe(itemStack, output.get(0), energy, creosote);
                ModSupport.THERMAL_EXPANSION.get().furnacePyrolysis.add(recipe1);
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
