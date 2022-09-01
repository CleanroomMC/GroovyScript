package com.cleanroommc.groovyscript.compat.mods.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.thermalexpansion.util.managers.machine.CompactorManager;
import com.cleanroommc.groovyscript.api.IGroovyPropertyGetter;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.EnergyRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.mixin.thermalexpansion.CompactorManagerAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Compactor extends VirtualizedRegistry<CompactorManager.CompactorRecipe> implements IGroovyPropertyGetter {

    private final Map<String, VirtualizedRegistry<CompactorManager.CompactorRecipe>> properties = new Object2ObjectOpenHashMap<>();
    public final Coin coin;
    public final Gear gear;
    public final Plate plate;

    public Compactor() {
        super("Compactor", "compactor");
        this.coin = new Coin();
        this.gear = new Gear();
        this.plate = new Plate();
        this.coin.getAliases().forEach(s -> properties.put(s, this.coin));
        this.gear.getAliases().forEach(s -> properties.put(s, this.gear));
        this.plate.getAliases().forEach(s -> properties.put(s, this.plate));
    }

    @Override
    public void onReload() {
        this.coin.onReload();
        this.gear.onReload();
        this.plate.onReload();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder("", CompactorManager.Mode.ALL);
    }

    public CompactorManager.CompactorRecipe add(ItemStack input, ItemStack output, int energy, CompactorManager.Mode mode) {
        CompactorManager.CompactorRecipe recipe = CompactorManager.addRecipe(energy, input, output, mode);
        if (recipe != null) {
            switch (mode) {
                case ALL:
                    addScripted(recipe);
                    break;
                case COIN:
                    this.coin.addScripted(recipe);
                    break;
                case GEAR:
                    this.gear.addScripted(recipe);
                    break;
                case PLATE:
                    this.plate.addScripted(recipe);
                    break;
            }
        }
        return recipe;
    }

    public void add(ItemStack input, ItemStack output, int energy) {
        add(input, output, energy, CompactorManager.Mode.ALL);
    }

    public boolean remove(CompactorManager.CompactorRecipe recipe) {
        if (CompactorManagerAccessor.getRecipeMapAll().values().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public void removeByInput(IIngredient input, CompactorManager.Mode mode) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Thermal Expansion Compactor " + mode.name().toLowerCase() + " recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return;
        }
        boolean found = false;
        VirtualizedRegistry<CompactorManager.CompactorRecipe> vr;
        Map<ComparableItemStackValidatedNBT, CompactorManager.CompactorRecipe> map;
        switch (mode) {
            case ALL:
                vr = this;
                map = CompactorManagerAccessor.getRecipeMapAll();
                break;
            case COIN:
                vr = this.coin;
                map = CompactorManagerAccessor.getRecipeMapCoin();
                break;
            case GEAR:
                vr = this.gear;
                map = CompactorManagerAccessor.getRecipeMapGear();
                break;
            case PLATE:
                vr = this.plate;
                map = CompactorManagerAccessor.getRecipeMapPlate();
                break;
            default:
                throw new IllegalStateException();
        }
        for (ItemStack stack : input.getMatchingStacks()) {
            CompactorManager.CompactorRecipe recipe = map.remove(CompactorManager.convertInput(stack));
            if (recipe != null) {
                found = true;
                vr.addBackup(recipe);
            }
        }
        if (!found) {
            GroovyLog.msg("Error removing Thermal Expansion Compactor " + mode.name().toLowerCase() + "recipe")
                    .add("could not find recipe for %s", input)
                    .error()
                    .post();
        }
    }

    public void removeByInput(IIngredient input) {
        removeByInput(input, CompactorManager.Mode.ALL);
    }

    public SimpleObjectStream<CompactorManager.CompactorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(CompactorManagerAccessor.getRecipeMapAll().values())
                .setRemover(this::remove);
    }

    public SimpleObjectStream<CompactorManager.CompactorRecipe> streamAllRecipes() {
        List<CompactorManager.CompactorRecipe> recipes = new ArrayList<>();
        recipes.addAll(CompactorManagerAccessor.getRecipeMapAll().values());
        recipes.addAll(CompactorManagerAccessor.getRecipeMapCoin().values());
        recipes.addAll(CompactorManagerAccessor.getRecipeMapGear().values());
        recipes.addAll(CompactorManagerAccessor.getRecipeMapPlate().values());
        return new SimpleObjectStream<>(recipes)
                .setRemover(recipe -> {
                    if (!remove(recipe)) {
                        if (!this.coin.remove(recipe)) {
                            if (!this.gear.remove(recipe)) {
                                return this.plate.remove(recipe);
                            }
                        }
                    }
                    return true;
                });
    }

    @Override
    public @Nullable Object getProperty(String name) {
        return this.properties.get(name);
    }

    public static class Coin extends VirtualizedRegistry<CompactorManager.CompactorRecipe> {

        private Coin() {
            super("Coin", "coin");
        }

        @Override
        public void onReload() {
            Map<ComparableItemStackValidatedNBT, CompactorManager.CompactorRecipe> map = CompactorManagerAccessor.getRecipeMapCoin();
            removeScripted().forEach(recipe -> map.values().removeIf(r -> r == recipe));
            restoreFromBackup().forEach(r -> map.put(CompactorManager.convertInput(r.getInput()), r));
        }

        public RecipeBuilder recipeBuilder() {
            return new RecipeBuilder("coin", CompactorManager.Mode.COIN);
        }

        public void add(ItemStack input, ItemStack output, int energy) {
            ModSupport.THERMAL_EXPANSION.get().compactor.add(input, output, energy, CompactorManager.Mode.COIN);
        }

        public boolean remove(CompactorManager.CompactorRecipe recipe) {
            if (CompactorManagerAccessor.getRecipeMapCoin().values().removeIf(r -> r == recipe)) {
                addBackup(recipe);
                return true;
            }
            return false;
        }

        public void removeByInput(IIngredient input) {
            ModSupport.THERMAL_EXPANSION.get().compactor.removeByInput(input, CompactorManager.Mode.COIN);
        }

        public SimpleObjectStream<CompactorManager.CompactorRecipe> streamRecipes() {
            return new SimpleObjectStream<>(CompactorManagerAccessor.getRecipeMapCoin().values())
                    .setRemover(this::remove);
        }
    }

    public static class Gear extends VirtualizedRegistry<CompactorManager.CompactorRecipe> {

        public Gear() {
            super("Gear", "gear");
        }

        @Override
        public void onReload() {
            Map<ComparableItemStackValidatedNBT, CompactorManager.CompactorRecipe> map = CompactorManagerAccessor.getRecipeMapGear();
            removeScripted().forEach(recipe -> map.values().removeIf(r -> r == recipe));
            restoreFromBackup().forEach(r -> map.put(CompactorManager.convertInput(r.getInput()), r));
        }

        public RecipeBuilder recipeBuilder() {
            return new RecipeBuilder("gear", CompactorManager.Mode.GEAR);
        }

        public void add(ItemStack input, ItemStack output, int energy) {
            ModSupport.THERMAL_EXPANSION.get().compactor.add(input, output, energy, CompactorManager.Mode.GEAR);
        }

        public boolean remove(CompactorManager.CompactorRecipe recipe) {
            if (CompactorManagerAccessor.getRecipeMapGear().values().removeIf(r -> r == recipe)) {
                addBackup(recipe);
                return true;
            }
            return false;
        }

        public void removeByInput(IIngredient input) {
            ModSupport.THERMAL_EXPANSION.get().compactor.removeByInput(input, CompactorManager.Mode.GEAR);
        }

        public SimpleObjectStream<CompactorManager.CompactorRecipe> streamRecipes() {
            return new SimpleObjectStream<>(CompactorManagerAccessor.getRecipeMapGear().values())
                    .setRemover(this::remove);
        }
    }

    public static class Plate extends VirtualizedRegistry<CompactorManager.CompactorRecipe> {

        public Plate() {
            super("Plate", "plate");
        }

        @Override
        public void onReload() {
            Map<ComparableItemStackValidatedNBT, CompactorManager.CompactorRecipe> map = CompactorManagerAccessor.getRecipeMapPlate();
            removeScripted().forEach(recipe -> map.values().removeIf(r -> r == recipe));
            restoreFromBackup().forEach(r -> map.put(CompactorManager.convertInput(r.getInput()), r));
        }

        public RecipeBuilder recipeBuilder() {
            return new RecipeBuilder("plate", CompactorManager.Mode.PLATE);
        }

        public void add(ItemStack input, ItemStack output, int energy) {
            ModSupport.THERMAL_EXPANSION.get().compactor.add(input, output, energy, CompactorManager.Mode.PLATE);
        }

        public boolean remove(CompactorManager.CompactorRecipe recipe) {
            if (CompactorManagerAccessor.getRecipeMapPlate().values().removeIf(r -> r == recipe)) {
                addBackup(recipe);
                return true;
            }
            return false;
        }

        public void removeByInput(IIngredient input) {
            ModSupport.THERMAL_EXPANSION.get().compactor.removeByInput(input, CompactorManager.Mode.PLATE);
        }

        public SimpleObjectStream<CompactorManager.CompactorRecipe> streamRecipes() {
            return new SimpleObjectStream<>(CompactorManagerAccessor.getRecipeMapPlate().values())
                    .setRemover(this::remove);
        }
    }

    public static class RecipeBuilder extends EnergyRecipeBuilder<CompactorManager.CompactorRecipe> {

        private final String type;
        private final CompactorManager.Mode mode;

        public RecipeBuilder(String type, CompactorManager.Mode mode) {
            this.type = type;
            this.mode = mode;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Expansion Compactor " + type + " recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            if (energy <= 0) energy = 4000;
        }

        @Override
        public @Nullable CompactorManager.CompactorRecipe register() {
            if (!validate()) return null;
            CompactorManager.CompactorRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                CompactorManager.CompactorRecipe recipe1 = ModSupport.THERMAL_EXPANSION.get().compactor.add(itemStack, output.get(0), energy, mode);
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
