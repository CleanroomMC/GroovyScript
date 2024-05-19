package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.machine;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.InsolatorRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.InsolatorManagerAccessor;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.InsolatorRecipeAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.AbstractReloadableStorage;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class Insolator extends VirtualizedRegistry<InsolatorRecipe> {

    private final AbstractReloadableStorage<ItemStack> fertilizerStorage = new AbstractReloadableStorage<>();

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay'), item('minecraft:diamond')).output(item('minecraft:diamond') * 4)"),
            @Example(".input(item('minecraft:clay'), item('minecraft:gold_ingot') * 2).output(item('minecraft:clay'), item('minecraft:diamond')).chance(5).water(100).tree().energy(1000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> InsolatorManagerAccessor.getRecipeMap().values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> InsolatorManagerAccessor.getRecipeMap().put(hash(r), r));
        fertilizerStorage.removeScripted().forEach(recipe -> InsolatorManagerAccessor.getLockSet().removeIf(r -> r.equals(InsolatorManager.convertInput(recipe))));
        fertilizerStorage.restoreFromBackup().forEach(r -> InsolatorManagerAccessor.getLockSet().add(InsolatorManager.convertInput(r)));
    }

    private List<ComparableItemStackValidatedNBT> hash(InsolatorRecipe recipe) {
        return hash(recipe.getPrimaryInput(), recipe.getSecondaryInput());
    }

    private List<ComparableItemStackValidatedNBT> hash(ItemStack primaryInput, ItemStack secondaryInput) {
        return Arrays.asList(InsolatorManager.convertInput(primaryInput), InsolatorManager.convertInput(secondaryInput));
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void afterScriptLoad() {
        InsolatorManagerAccessor.getValidationSet().clear();
        InsolatorManagerAccessor.getValidationSet().addAll(
                InsolatorManagerAccessor.getRecipeMap().values().stream().map(InsolatorRecipe::getPrimaryInput).map(InsolatorManager::convertInput).collect(Collectors.toList())
        );
        InsolatorManagerAccessor.getValidationSet().addAll(
                InsolatorManagerAccessor.getRecipeMap().values().stream().map(InsolatorRecipe::getSecondaryInput).map(InsolatorManager::convertInput).collect(Collectors.toList())
        );
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public boolean addFertilizer(ItemStack itemStack) {
        return InsolatorManagerAccessor.getLockSet().add(InsolatorManager.convertInput(itemStack)) && fertilizerStorage.addScripted(itemStack);
    }

    @MethodDescription
    public boolean removeFertilizer(ItemStack itemStack) {
        return InsolatorManagerAccessor.getLockSet().removeIf(r -> r.equals(InsolatorManager.convertInput(itemStack))) && fertilizerStorage.addBackup(itemStack);
    }

    public void add(InsolatorRecipe recipe) {
        InsolatorManagerAccessor.getRecipeMap().put(hash(recipe), recipe);
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "1000, 100, item('minecraft:obsidian'), item('minecraft:gold_ingot') * 2, item('minecraft:clay'), item('minecraft:diamond'), 5, InsolatorManager.Type.TREE", imports = "cofh.thermalexpansion.util.managers.machine.InsolatorManager"))
    public InsolatorRecipe add(int energy, int water, IIngredient primaryInput, IIngredient secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, InsolatorManager.Type type) {
        return recipeBuilder()
                .energy(energy)
                .water(water)
                .chance(secondaryChance)
                .type(type)
                .input(primaryInput, secondaryInput)
                .output(primaryOutput, secondaryOutput)
                .register();
    }

    public boolean remove(InsolatorRecipe recipe) {
        return InsolatorManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (r == recipe) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = {
            @Example("item('thermalfoundation:fertilizer')"), @Example("item('minecraft:double_plant:4')")
    })
    public boolean removeByInput(IIngredient input) {
        return InsolatorManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (input.test(r.getPrimaryInput()) || input.test(r.getSecondaryInput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = {
            @Example("item('minecraft:red_flower:6')"), @Example("item('minecraft:melon_seeds')")
    })
    public boolean removeByOutput(IIngredient output) {
        return InsolatorManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (output.test(r.getPrimaryOutput()) || output.test(r.getSecondaryOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<InsolatorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(InsolatorManagerAccessor.getRecipeMap().values()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        InsolatorManagerAccessor.getRecipeMap().values().forEach(this::addBackup);
        InsolatorManagerAccessor.getRecipeMap().clear();
    }

    @Property(property = "input", valid = @Comp("2"))
    @Property(property = "output", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "2")})
    public static class RecipeBuilder extends AbstractRecipeBuilder<InsolatorRecipe> {

        @Property(defaultValue = "InsolatorManager.DEFAULT_ENERGY", valid = @Comp(value = "0", type = Comp.Type.GT), value = "groovyscript.wiki.thermalexpansion.energy.value")
        private int energy = InsolatorManager.DEFAULT_ENERGY;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int water;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int chance;
        @Property(defaultValue = "InsolatorManager.Type.STANDARD")
        private InsolatorManager.Type type = InsolatorManager.Type.STANDARD;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder water(int water) {
            this.water = water;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder chance(int chance) {
            this.chance = chance;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder type(InsolatorManager.Type type) {
            this.type = type;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "type")
        public RecipeBuilder tree() {
            this.type = InsolatorManager.Type.TREE;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "type")
        public RecipeBuilder standard() {
            this.type = InsolatorManager.Type.STANDARD;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Expansion Insolator recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 2, 2, 1, 2);
            validateFluids(msg);
            msg.add(energy <= 0, "energy must be greater than 0, yet it was {}", energy);
            msg.add(water < 0, "water must be greater than or equal to 0, yet it was {}", water);
            msg.add(chance < 0, "chance must be greater than or equal to 0, yet it was {}", chance);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable InsolatorRecipe register() {
            if (!validate()) return null;
            InsolatorRecipe recipe = null;

            for (ItemStack input0 : input.get(0).getMatchingStacks()) {
                for (ItemStack input1 : input.get(1).getMatchingStacks()) {
                    InsolatorRecipe recipe1 = InsolatorRecipeAccessor.createInsolatorRecipe(input1, input0, output.get(0), output.getOrEmpty(1), chance, energy, water, type);
                    ModSupport.THERMAL_EXPANSION.get().insolator.add(recipe1);
                    if (recipe == null) recipe = recipe1;
                }

            }
            return recipe;
        }
    }
}
