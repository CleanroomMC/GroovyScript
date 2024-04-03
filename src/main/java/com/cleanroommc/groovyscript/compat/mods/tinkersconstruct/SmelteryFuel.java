package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe.SmelteryFuelRecipe;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.TinkerRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.AbstractReloadableStorage;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.stream.Collectors;

public class SmelteryFuel extends VirtualizedRegistry<SmelteryFuelRecipe> {

    @Override
    @GroovyBlacklist
    protected AbstractReloadableStorage<SmelteryFuelRecipe> createRecipeStorage() {
        return new AbstractReloadableStorage<>() {
            @Override
            @GroovyBlacklist
            protected boolean compareRecipe(SmelteryFuelRecipe recipe, SmelteryFuelRecipe recipe2) {
                return recipe.equals(recipe2);
            }
        };
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(recipe -> TinkerRegistryAccessor.getSmelteryFuels().remove(recipe.fluid, recipe.duration));
        restoreFromBackup().forEach(recipe -> TinkerRegistryAccessor.getSmelteryFuels().put(recipe.fluid, recipe.duration));
    }

    protected List<SmelteryFuelRecipe> getAllRecipes() {
        return TinkerRegistryAccessor.getSmelteryFuels().entrySet().stream().map(SmelteryFuelRecipe::fromMapEntry).collect(Collectors.toList());
    }

    public SmelteryFuelRecipe addFuel(FluidStack fluid, int duration) {
        SmelteryFuelRecipe recipe = new SmelteryFuelRecipe(fluid, duration);
        add(recipe);
        return recipe;
    }

    public boolean removeFuel(FluidStack fluid) {
        if (TinkerRegistryAccessor.getSmelteryFuels().entrySet().removeIf(entry -> {
            boolean found = entry.getKey().isFluidEqual(fluid);
            if (found) addBackup(SmelteryFuelRecipe.fromMapEntry(entry));
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Smeltery Fuel")
                .add("could not find smeltery fuel entry for {}", fluid)
                .error()
                .post();
        return false;
    }

    public void add(SmelteryFuelRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        TinkerRegistryAccessor.getSmelteryFuels().put(recipe.fluid, recipe.duration);
    }

    public boolean remove(SmelteryFuelRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        TinkerRegistryAccessor.getSmelteryFuels().remove(recipe.fluid, recipe.duration);
        return true;
    }

    public void removeAll() {
        TinkerRegistryAccessor.getSmelteryFuels().forEach((fluid, duration) -> addBackup(new SmelteryFuelRecipe(fluid, duration)));
        TinkerRegistryAccessor.getSmelteryFuels().forEach(TinkerRegistryAccessor.getSmelteryFuels()::remove);
    }

    public SimpleObjectStream<SmelteryFuelRecipe> streamRecipes() {
        return new SimpleObjectStream<>(getAllRecipes()).setRemover(this::remove);
    }

}
