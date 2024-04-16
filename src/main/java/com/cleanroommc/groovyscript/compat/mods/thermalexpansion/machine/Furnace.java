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
import com.cleanroommc.groovyscript.registry.AbstractReloadableStorage;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Furnace extends VirtualizedRegistry<FurnaceRecipe> {

    private final AbstractReloadableStorage<ItemStack> foodStorage = new AbstractReloadableStorage<>();

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay') * 2)"),
            @Example(".input(item('minecraft:gold_ingot') * 2).output(item('minecraft:clay')).energy(1000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> FurnaceManagerAccessor.getRecipeMap().values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> FurnaceManagerAccessor.getRecipeMap().put(FurnaceManager.convertInput(r.getInput()), r));
        foodStorage.removeScripted().forEach(r -> FurnaceManagerAccessor.getFoodSet().add(FurnaceManager.convertInput(r)));
        foodStorage.restoreFromBackup().forEach(r -> FurnaceManagerAccessor.getFoodSet().remove(FurnaceManager.convertInput(r)));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:emerald_ore')"))
    public boolean addFood(ItemStack item) {
        return FurnaceManagerAccessor.getFoodSet().add(FurnaceManager.convertInput(item)) && foodStorage.addScripted(item);
    }

    @MethodDescription(example = @Example("item('minecraft:rabbit:*')"))
    public boolean removeFood(ItemStack item) {
        return FurnaceManagerAccessor.getFoodSet().remove(FurnaceManager.convertInput(item)) && foodStorage.addBackup(item);
    }

    public void add(FurnaceRecipe recipe) {
        FurnaceManagerAccessor.getRecipeMap().put(FurnaceManager.convertInput(recipe.getInput()), recipe);
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("1000, item('minecraft:obsidian') * 2, item('minecraft:clay')"))
    public FurnaceRecipe add(int energy, IIngredient input, ItemStack output) {
        return recipeBuilder()
                .energy(energy)
                .input(input)
                .output(output)
                .register();
    }

    public boolean remove(FurnaceRecipe recipe) {
        return FurnaceManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (r == recipe) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:cactus:*')"))
    public boolean removeByInput(IIngredient input) {
        return FurnaceManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (input.test(r.getInput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:cooked_porkchop')"))
    public boolean removeByOutput(IIngredient output) {
        return FurnaceManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (output.test(r.getOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<FurnaceRecipe> streamRecipes() {
        return new SimpleObjectStream<>(FurnaceManagerAccessor.getRecipeMap().values()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAllFood() {
        FurnaceManagerAccessor.getFoodSet().forEach(x -> foodStorage.addBackup(x.toItemStack()));
        FurnaceManagerAccessor.getFoodSet().clear();
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        FurnaceManagerAccessor.getRecipeMap().values().forEach(this::addBackup);
        FurnaceManagerAccessor.getRecipeMap().clear();
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<FurnaceRecipe> {

        @Property(defaultValue = "FurnaceManager.DEFAULT_ENERGY", valid = @Comp(value = "0", type = Comp.Type.GT), value = "groovyscript.wiki.thermalexpansion.energy.value")
        private int energy = FurnaceManager.DEFAULT_ENERGY;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Expansion Furnace recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(energy <= 0, "energy must be greater than 0, yet it was {}", energy);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable FurnaceRecipe register() {
            if (!validate()) return null;
            FurnaceRecipe recipe = null;

            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                FurnaceRecipe recipe1 = FurnaceRecipeAccessor.createFurnaceRecipe(itemStack, output.get(0), energy);
                ModSupport.THERMAL_EXPANSION.get().furnace.add(recipe1);
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
