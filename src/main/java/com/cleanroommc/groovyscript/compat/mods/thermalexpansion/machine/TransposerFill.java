package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.machine;

import cofh.core.util.helpers.FluidHelper;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import cofh.thermalexpansion.util.managers.machine.TransposerManager.TransposerRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.TransposerManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription(
        admonition = @Admonition("groovyscript.wiki.thermalexpansion.transposer.note0")
)
public class TransposerFill extends VirtualizedRegistry<TransposerRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond') * 2).fluidInput(fluid('water') * 100)"),
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond') * 2).fluidInput(fluid('water') * 50).energy(1000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> TransposerManagerAccessor.getRecipeMapFill().values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> TransposerManagerAccessor.getRecipeMapFill().put(hash(r), r));
    }

    @Override
    @GroovyBlacklist
    public void afterScriptLoad() {
        TransposerManagerAccessor.getValidationSet().clear();
        TransposerManagerAccessor.getValidationSet().addAll(
                TransposerManagerAccessor.getRecipeMapExtract().values().stream().map(TransposerManager.TransposerRecipe::getInput).map(TransposerManager::convertInput).collect(Collectors.toList())
        );
        TransposerManagerAccessor.getValidationSet().clear();
        TransposerManagerAccessor.getValidationSet().addAll(
                TransposerManagerAccessor.getRecipeMapFill().values().stream().map(TransposerManager.TransposerRecipe::getInput).map(TransposerManager::convertInput).collect(Collectors.toList())
        );
    }

    private List<Integer> hash(TransposerRecipe recipe) {
        return hash(recipe.getInput(), recipe.getFluid());
    }

    private List<Integer> hash(ItemStack primaryInput, FluidStack fluid) {
        return Arrays.asList(TransposerManager.convertInput(primaryInput).hashCode(), FluidHelper.getFluidHash(fluid));
    }

    public void add(TransposerRecipe recipe) {
        TransposerManagerAccessor.getRecipeMapFill().put(hash(recipe), recipe);
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("1000, item('minecraft:obsidian'), fluid('water') * 50, item('minecraft:diamond') * 2, 100"))
    public TransposerRecipe add(int energy, IIngredient input, FluidStack fluidInput, ItemStack outputItem, int chance) {
        return recipeBuilder()
                .energy(energy)
                .chance(chance)
                .input(input)
                .fluidInput(fluidInput)
                .output(outputItem)
                .register();
    }

    public boolean remove(TransposerRecipe recipe) {
        return TransposerManagerAccessor.getRecipeMapFill().values().removeIf(r -> {
            if (r == recipe) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = {
            @Example("fluid('glowstone')"), @Example("item('minecraft:concrete_powder:3')")
    })
    public boolean removeByInput(IIngredient input) {
        return TransposerManagerAccessor.getRecipeMapFill().values().removeIf(r -> {
            if (input.test(r.getInput()) || input.test(r.getFluid())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:ice')"))
    public boolean removeByOutput(IIngredient output) {
        return TransposerManagerAccessor.getRecipeMapFill().values().removeIf(r -> {
            if (output.test(r.getOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<TransposerRecipe> streamRecipes() {
        return new SimpleObjectStream<>(TransposerManagerAccessor.getRecipeMapFill().values()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        TransposerManagerAccessor.getRecipeMapFill().values().forEach(this::addBackup);
        TransposerManagerAccessor.getRecipeMapFill().clear();
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "fluidInput", valid = @Comp("1"))
    @Property(property = "output", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
    public static class RecipeBuilder extends AbstractRecipeBuilder<TransposerRecipe> {

        @Property(defaultValue = "TransposerManager.DEFAULT_ENERGY", valid = @Comp(value = "0", type = Comp.Type.GT), value = "groovyscript.wiki.thermalexpansion.energy.value")
        private int energy = TransposerManager.DEFAULT_ENERGY;
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
            return "Error adding Thermal Expansion Transposer Fill recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 1);
            validateFluids(msg, 1, 1, 0, 0);
            msg.add(energy <= 0, "energy must be greater than 0, yet it was {}", energy);
            msg.add(chance < 0 || chance > 100, "chance must be a non negative integer less than 100, yet it was {}", chance);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable TransposerRecipe register() {
            if (!validate()) return null;
            TransposerRecipe recipe = null;
            for (ItemStack input0 : input.get(0).getMatchingStacks()) {
                TransposerRecipe recipe1 = new TransposerRecipe(input0, output.getOrEmpty(0), fluidInput.get(0), energy, chance);
                ModSupport.THERMAL_EXPANSION.get().transposerFill.add(recipe1);
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
