package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.machine;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.thermalexpansion.util.managers.machine.SmelterManager;
import cofh.thermalexpansion.util.managers.machine.SmelterManager.SmelterRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.SmelterManagerAccessor;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.SmelterRecipeAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.AbstractReloadableStorage;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

@RegistryDescription
public class Smelter extends VirtualizedRegistry<SmelterRecipe> {

    private final AbstractReloadableStorage<ItemStack> fluxStorage = new AbstractReloadableStorage<>();

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay'), item('minecraft:diamond')).output(item('minecraft:diamond') * 4)"),
            @Example(".input(item('minecraft:clay'), item('minecraft:gold_ingot') * 2).output(item('minecraft:clay'), item('minecraft:diamond')).chance(5).energy(1000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> SmelterManagerAccessor.getRecipeMap().values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> SmelterManagerAccessor.getRecipeMap().put(hash(r), r));
        fluxStorage.removeScripted().forEach(recipe -> SmelterManagerAccessor.getLockSet().removeIf(r -> r.equals(SmelterManager.convertInput(recipe))));
        fluxStorage.restoreFromBackup().forEach(r -> SmelterManagerAccessor.getLockSet().add(SmelterManager.convertInput(r)));
    }

    private List<ComparableItemStackValidatedNBT> hash(SmelterRecipe recipe) {
        return hash(recipe.getPrimaryInput(), recipe.getSecondaryInput());
    }

    private List<ComparableItemStackValidatedNBT> hash(ItemStack primaryInput, ItemStack secondaryInput) {
        return Arrays.asList(SmelterManager.convertInput(primaryInput), SmelterManager.convertInput(secondaryInput));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public boolean addFlux(ItemStack itemStack) {
        return SmelterManagerAccessor.getLockSet().add(SmelterManager.convertInput(itemStack)) && fluxStorage.addScripted(itemStack);
    }

    @MethodDescription
    public boolean removeFlux(ItemStack itemStack) {
        return SmelterManagerAccessor.getLockSet().removeIf(r -> r.equals(SmelterManager.convertInput(itemStack))) && fluxStorage.addBackup(itemStack);
    }

    public void add(SmelterRecipe recipe) {
        SmelterManagerAccessor.getRecipeMap().put(hash(recipe), recipe);
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "1000, item('minecraft:obsidian'), item('minecraft:gold_ingot') * 2, item('minecraft:clay'), item('minecraft:diamond'), 5", commented = true))
    public SmelterRecipe add(int energy, IIngredient input0, IIngredient input1, ItemStack output0, ItemStack output1, int chance) {
        return recipeBuilder()
                .energy(energy)
                .chance(chance)
                .input(input0, input1)
                .output(output0, output1)
                .register();
    }

    public boolean remove(SmelterRecipe recipe) {
        return SmelterManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (r == recipe) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = {
            @Example("ore('sand')"), @Example("item('minecraft:iron_ingot')")
    })
    public boolean removeByInput(IIngredient input) {
        return SmelterManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (input.test(r.getPrimaryInput()) || input.test(r.getSecondaryInput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('thermalfoundation:material:166')"))
    public boolean removeByOutput(IIngredient output) {
        return SmelterManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (output.test(r.getPrimaryOutput()) || output.test(r.getSecondaryOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<SmelterRecipe> streamRecipes() {
        return new SimpleObjectStream<>(SmelterManagerAccessor.getRecipeMap().values()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        SmelterManagerAccessor.getRecipeMap().values().forEach(this::addBackup);
        SmelterManagerAccessor.getRecipeMap().clear();
    }

    @Property(property = "input", valid = @Comp("2"))
    @Property(property = "output", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "2")})
    public static class RecipeBuilder extends AbstractRecipeBuilder<SmelterRecipe> {

        @Property(defaultValue = "SmelterManager.DEFAULT_ENERGY", valid = @Comp(value = "0", type = Comp.Type.GT), value = "groovyscript.wiki.thermalexpansion.energy.value")
        private int energy = SmelterManager.DEFAULT_ENERGY;
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
            return "Error adding Thermal Expansion Smelter recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 2, 2, 1, 2);
            validateFluids(msg);
            msg.add(energy <= 0, "energy must be greater than 0, yet it was {}", energy);
            msg.add(chance < 0 || chance > 100, "chance must be a non negative integer less than 100, yet it was {}", chance);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable SmelterRecipe register() {
            if (!validate()) return null;
            SmelterRecipe recipe = null;
            for (ItemStack input0 : input.get(0).getMatchingStacks()) {
                for (ItemStack input1 : input.get(1).getMatchingStacks()) {
                    SmelterRecipe recipe1 = SmelterRecipeAccessor.createSmelterRecipe(input0, input1, output.get(0), output.getOrEmpty(1), chance, energy);
                    ModSupport.THERMAL_EXPANSION.get().smelter.add(recipe1);
                    if (recipe == null) recipe = recipe1;
                }
            }
            return recipe;
        }
    }
}
