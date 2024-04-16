package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.dynamo;

import cofh.core.inventory.ComparableItemStack;
import cofh.thermalexpansion.util.managers.dynamo.ReactantManager;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.ReactantManagerAccessor;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.ReactionAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.AbstractReloadableStorage;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class Reactant extends VirtualizedRegistry<ReactantManager.Reaction> {

    private final AbstractReloadableStorage<ItemStack> elementalReactantStorage = new AbstractReloadableStorage<>();
    private final AbstractReloadableStorage<String> elementalFluidStorage = new AbstractReloadableStorage<>();

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).fluidInput(fluid('steam'))"),
            @Example(".input(item('minecraft:clay')).fluidInput(fluid('glowstone')).energy(100)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> ReactantManagerAccessor.getReactionMap().values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> ReactantManagerAccessor.getReactionMap().put(hash(r), r));
        elementalReactantStorage.removeScripted().forEach(recipe -> ReactantManagerAccessor.getValidReactantsElemental().removeIf(r -> r.equals(new ComparableItemStack(recipe))));
        elementalReactantStorage.restoreFromBackup().forEach(r -> ReactantManagerAccessor.getValidReactantsElemental().add(new ComparableItemStack(r)));
        elementalFluidStorage.removeScripted().forEach(recipe -> ReactantManagerAccessor.getValidFluidsElemental().removeIf(r -> r.equals(recipe)));
        elementalFluidStorage.restoreFromBackup().forEach(r -> ReactantManagerAccessor.getValidFluidsElemental().add(r));
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void afterScriptLoad() {
        ReactantManagerAccessor.getValidReactants().clear();
        ReactantManagerAccessor.getValidReactants().addAll(ReactantManagerAccessor.getReactionMap().values().stream().map(ReactantManager.Reaction::getReactant).map(ComparableItemStack::new).collect(Collectors.toList()));
        ReactantManagerAccessor.getValidFluids().clear();
        ReactantManagerAccessor.getValidFluids().addAll(ReactantManagerAccessor.getReactionMap().values().stream().map(ReactantManager.Reaction::getFluidName).collect(Collectors.toList()));
    }

    private List<Integer> hash(ReactantManager.Reaction recipe) {
        return hash(recipe.getReactant(), recipe.getFluid());
    }

    private List<Integer> hash(ItemStack reactant, Fluid fluid) {
        return Arrays.asList((new ComparableItemStack(reactant)).hashCode(), fluid.getName().hashCode());
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = {@Example("item('minecraft:gunpowder')"), @Example("item('minecraft:clay')")})
    public boolean addElementalReactant(ItemStack itemStack) {
        return ReactantManagerAccessor.getValidReactantsElemental().add(new ComparableItemStack(itemStack)) && elementalReactantStorage.addScripted(itemStack);
    }

    @MethodDescription(example = @Example("item('thermalfoundation:material:1024')"))
    public boolean removeElementalReactant(ItemStack itemStack) {
        return ReactantManagerAccessor.getValidReactantsElemental().removeIf(r -> r.equals(new ComparableItemStack(itemStack))) && elementalReactantStorage.addBackup(itemStack);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public boolean addElementalFluid(String fluid) {
        return ReactantManagerAccessor.getValidFluidsElemental().add(fluid) && elementalFluidStorage.addScripted(fluid);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("fluid('glowstone')"))
    public boolean addElementalFluid(FluidStack fluid) {
        return addElementalFluid(fluid.getFluid().getName());
    }

    @MethodDescription
    public boolean removeElementalFluid(String fluid) {
        return ReactantManagerAccessor.getValidFluidsElemental().removeIf(r -> r.equals(fluid)) && elementalFluidStorage.addBackup(fluid);
    }

    @MethodDescription(example = @Example("fluid('cryotheum')"))
    public boolean removeElementalFluid(FluidStack fluid) {
        return removeElementalFluid(fluid.getFluid().getName());
    }

    public void add(ReactantManager.Reaction recipe) {
        ReactantManagerAccessor.getReactionMap().put(hash(recipe), recipe);
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:clay'), fluid('steam'), 100"))
    public void add(IIngredient ingredient, FluidStack fluidStack, int energy) {
        recipeBuilder()
                .energy(energy)
                .input(ingredient)
                .fluidInput(fluidStack)
                .register();
    }

    public boolean remove(ReactantManager.Reaction recipe) {
        return ReactantManagerAccessor.getReactionMap().values().removeIf(r -> {
            if (r == recipe) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = {
            @Example("item('minecraft:blaze_powder')"), @Example("fluid('redstone')")
    })
    public boolean removeByInput(IIngredient input) {
        return ReactantManagerAccessor.getReactionMap().values().removeIf(r -> {
            if (input.test(r.getReactant()) || (input instanceof FluidStack && IngredientHelper.toFluidStack(input).getFluid().getName().equals(r.getFluidName()))) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ReactantManager.Reaction> streamRecipes() {
        return new SimpleObjectStream<>(ReactantManagerAccessor.getReactionMap().values()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ReactantManagerAccessor.getReactionMap().values().forEach(this::addBackup);
        ReactantManagerAccessor.getReactionMap().clear();
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "fluidInput", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ReactantManager.Reaction> {

        @Property(defaultValue = "ReactantManager.DEFAULT_ENERGY", valid = @Comp(value = "0", type = Comp.Type.GT))
        private int energy = ReactantManager.DEFAULT_ENERGY;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Expansion Reactant Dynamo recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg, 1, 1, 0, 0);
            msg.add(energy <= 0, "energy must be greater than 0, yet it was {}", energy);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ReactantManager.Reaction register() {
            if (!validate()) return null;
            ReactantManager.Reaction recipe = null;

            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                ReactantManager.Reaction recipe1 = ReactionAccessor.createReaction(itemStack, fluidInput.get(0).getFluid(), energy);
                ModSupport.THERMAL_EXPANSION.get().reactant.add(recipe1);
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }

}
