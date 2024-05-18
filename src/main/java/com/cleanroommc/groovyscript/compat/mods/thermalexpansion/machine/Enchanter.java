package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.machine;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager.EnchanterRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.EnchanterManagerAccessor;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.EnchanterRecipeAccessor;
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
public class Enchanter extends VirtualizedRegistry<EnchanterRecipe> {

    private final AbstractReloadableStorage<ItemStack> arcanaStorage = new AbstractReloadableStorage<>();


    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay'), item('minecraft:gold_ingot') * 4).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:clay'), item('minecraft:gold_ingot')).output(item('minecraft:diamond')).experience(1000).energy(1000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> EnchanterManagerAccessor.getRecipeMap().values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> EnchanterManagerAccessor.getRecipeMap().put(hash(r), r));
        arcanaStorage.removeScripted().forEach(recipe -> EnchanterManagerAccessor.getLockSet().removeIf(r -> r.equals(EnchanterManager.convertInput(recipe))));
        arcanaStorage.restoreFromBackup().forEach(r -> EnchanterManagerAccessor.getLockSet().add(EnchanterManager.convertInput(r)));
    }

    private List<ComparableItemStackValidatedNBT> hash(EnchanterRecipe recipe) {
        return hash(recipe.getPrimaryInput(), recipe.getSecondaryInput());
    }

    private List<ComparableItemStackValidatedNBT> hash(ItemStack primaryInput, ItemStack secondaryInput) {
        return Arrays.asList(EnchanterManager.convertInput(primaryInput), EnchanterManager.convertInput(secondaryInput));
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void afterScriptLoad() {
        EnchanterManagerAccessor.getValidationSet().clear();
        EnchanterManagerAccessor.getValidationSet().addAll(
                EnchanterManagerAccessor.getRecipeMap().values().stream().map(EnchanterRecipe::getPrimaryInput).map(EnchanterManager::convertInput).collect(Collectors.toList())
        );
        EnchanterManagerAccessor.getValidationSet().addAll(
                EnchanterManagerAccessor.getRecipeMap().values().stream().map(EnchanterRecipe::getSecondaryInput).map(EnchanterManager::convertInput).collect(Collectors.toList())
        );
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:clay')"))
    public boolean addArcana(ItemStack itemStack) {
        return EnchanterManagerAccessor.getLockSet().add(EnchanterManager.convertInput(itemStack)) && arcanaStorage.addScripted(itemStack);
    }

    @MethodDescription
    public boolean removeArcana(ItemStack itemStack) {
        return EnchanterManagerAccessor.getLockSet().removeIf(r -> r.equals(EnchanterManager.convertInput(itemStack))) && arcanaStorage.addBackup(itemStack);
    }

    public void add(EnchanterRecipe recipe) {
        EnchanterManagerAccessor.getRecipeMap().put(hash(recipe), recipe);
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("1000, item('minecraft:obsidian'), item('minecraft:gold_ingot'), item('minecraft:diamond'), 1000"))
    public EnchanterRecipe add(int energy, IIngredient primaryInput, IIngredient secondaryInput, ItemStack output, int experience) {
        return recipeBuilder()
                .energy(energy)
                .experience(experience)
                .input(primaryInput, secondaryInput)
                .output(output)
                .register();
    }

    public boolean remove(EnchanterRecipe recipe) {
        return EnchanterManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (r == recipe) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = {
            @Example("item('minecraft:blaze_rod')"),
            @Example(value = "item('minecraft:book')", commented = true)
    })
    public boolean removeByInput(IIngredient input) {
        return EnchanterManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (input.test(r.getPrimaryInput()) || input.test(r.getSecondaryInput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:enchanted_book').withNbt(['StoredEnchantments': [['lvl': 1, 'id': 34]]])"))
    public boolean removeByOutput(IIngredient output) {
        return EnchanterManagerAccessor.getRecipeMap().values().removeIf(r -> {
            if (output.test(r.getOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<EnchanterRecipe> streamRecipes() {
        return new SimpleObjectStream<>(EnchanterManagerAccessor.getRecipeMap().values()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        EnchanterManagerAccessor.getRecipeMap().values().forEach(this::addBackup);
        EnchanterManagerAccessor.getRecipeMap().clear();
    }

    @Property(property = "input", valid = @Comp("2"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<EnchanterRecipe> {

        @Property(defaultValue = "EnchanterManager.DEFAULT_ENERGY[0]", valid = @Comp(value = "0", type = Comp.Type.GT), value = "groovyscript.wiki.thermalexpansion.energy.value")
        private int energy = EnchanterManager.DEFAULT_ENERGY[0];
        @Property(defaultValue = "EnchanterManager.DEFAULT_EXPERIENCE[0]", valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int experience = EnchanterManager.DEFAULT_EXPERIENCE[0];
        @Property(defaultValue = "EnchanterManager.Type.STANDARD")
        private EnchanterManager.Type type = EnchanterManager.Type.STANDARD;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder experience(int experience) {
            this.experience = experience;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder type(EnchanterManager.Type type) {
            this.type = type;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Expansion Enchanter recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 2, 2, 1, 1);
            validateFluids(msg);
            msg.add(energy <= 0, "energy must be greater than 0, yet it was {}", energy);
            msg.add(experience < 0, "experience must be greater than or equal to 0, yet it was {}", experience);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable EnchanterRecipe register() {
            if (!validate()) return null;
            EnchanterRecipe recipe = null;

            for (ItemStack input0 : input.get(0).getMatchingStacks()) {
                for (ItemStack input1 : input.get(1).getMatchingStacks()) {
                    EnchanterRecipe recipe1 = EnchanterRecipeAccessor.createEnchanterRecipe(input0, input1, output.get(0), experience, energy, type);
                    ModSupport.THERMAL_EXPANSION.get().enchanter.add(recipe1);
                    if (recipe == null) recipe = recipe1;
                }
            }
            return recipe;
        }
    }
}
