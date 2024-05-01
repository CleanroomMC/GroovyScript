package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.machine;

import cofh.core.util.helpers.FluidHelper;
import cofh.thermalexpansion.util.managers.machine.RefineryManager;
import cofh.thermalexpansion.util.managers.machine.RefineryManager.RefineryRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.RefineryManagerAccessor;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.RefineryRecipeAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class RefineryPotion extends VirtualizedRegistry<RefineryRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('water') * 100).fluidOutput(fluid('steam') * 200)"),
            @Example(".fluidInput(fluid('lava') * 100).fluidOutput(fluid('steam') * 30).output(item('minecraft:clay')).chance(75).energy(1000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> RefineryManagerAccessor.getRecipeMapPotion().values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> RefineryManagerAccessor.getRecipeMapPotion().put(FluidHelper.getFluidHash(r.getInput()), r));
    }

    public void add(RefineryRecipe recipe) {
        RefineryManagerAccessor.getRecipeMapPotion().put(FluidHelper.getFluidHash(recipe.getInput()), recipe);
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("1000, fluid('ender') * 100, fluid('steam') * 30, item('minecraft:clay'), 75"))
    public RefineryRecipe add(int energy, FluidStack fluidInput, FluidStack outputFluid, ItemStack outputItem, int chance) {
        return recipeBuilder()
                .energy(energy)
                .chance(chance)
                .fluidInput(fluidInput)
                .fluidOutput(outputFluid)
                .output(outputItem)
                .register();
    }

    public boolean remove(RefineryRecipe recipe) {
        return RefineryManagerAccessor.getRecipeMapPotion().values().removeIf(r -> {
            if (r == recipe) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("fluid('potion_lingering').withNbt(['Potion': 'cofhcore:healing3'])"))
    public boolean removeByInput(IIngredient input) {
        return RefineryManagerAccessor.getRecipeMapPotion().values().removeIf(r -> {
            if (input.test(r.getInput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("fluid('potion_splash').withNbt(['Potion': 'cofhcore:leaping4'])"))
    public boolean removeByOutput(IIngredient output) {
        return RefineryManagerAccessor.getRecipeMapPotion().values().removeIf(r -> {
            if (output.test(r.getOutputFluid()) || output.test(r.getOutputItem())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<RefineryRecipe> streamRecipes() {
        return new SimpleObjectStream<>(RefineryManagerAccessor.getRecipeMapPotion().values()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        RefineryManagerAccessor.getRecipeMapPotion().values().forEach(this::addBackup);
        RefineryManagerAccessor.getRecipeMapPotion().clear();
    }

    @Property(property = "fluidInput", valid = @Comp("1"))
    @Property(property = "output", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
    @Property(property = "fluidOutput", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RefineryRecipe> {

        @Property(defaultValue = "RefineryManager.DEFAULT_ENERGY", valid = @Comp(value = "0", type = Comp.Type.GT), value = "groovyscript.wiki.thermalexpansion.energy.value")
        private int energy = RefineryManager.DEFAULT_ENERGY;
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
            return "Error adding Thermal Expansion Refinery recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 0, 1);
            validateFluids(msg, 1, 1, 1, 1);
            msg.add(energy <= 0, "energy must be greater than 0, yet it was {}", energy);
            msg.add(chance < 0 || chance > 100, "chance must be a non negative integer less than 100, yet it was {}", chance);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RefineryRecipe register() {
            if (!validate()) return null;
            RefineryRecipe recipe = RefineryRecipeAccessor.createRefineryRecipe(fluidInput.get(0), fluidOutput.get(0), output.getOrEmpty(0), energy, chance);
            ModSupport.THERMAL_EXPANSION.get().refineryPotion.add(recipe);
            return recipe;
        }
    }
}
