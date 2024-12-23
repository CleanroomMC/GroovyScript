package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.dynamo;

import cofh.core.inventory.ComparableItemStack;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.NumismaticManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

@RegistryDescription
public class Lapidary extends VirtualizedRegistry<Lapidary.LapidaryRecipe> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> NumismaticManagerAccessor.getGemFuelMap().keySet().removeIf(r -> r.equals(recipe.comparableItemStack())));
        restoreFromBackup().forEach(r -> NumismaticManagerAccessor.getGemFuelMap().put(r.comparableItemStack(), r.energy()));
    }

    public void add(LapidaryRecipe recipe) {
        NumismaticManagerAccessor.getGemFuelMap().put(recipe.comparableItemStack(), recipe.energy());
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:clay'), 1000"))
    public void add(ItemStack itemStack, int energy) {
        add(new LapidaryRecipe(new ComparableItemStack(itemStack), energy));
    }

    public boolean remove(ComparableItemStack recipe) {
        return NumismaticManagerAccessor.getGemFuelMap().keySet().removeIf(r -> {
            if (r.equals(recipe)) {
                addBackup(new LapidaryRecipe(r, NumismaticManagerAccessor.getGemFuelMap().get(r)));
                return true;
            }
            return false;
        });
    }

    public boolean remove(LapidaryRecipe recipe) {
        return NumismaticManagerAccessor.getGemFuelMap().keySet().removeIf(r -> {
            if (r.equals(recipe.comparableItemStack())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:diamond')"))
    public boolean removeByInput(IIngredient input) {
        return NumismaticManagerAccessor.getGemFuelMap().keySet().removeIf(r -> {
            if (input.test(r.toItemStack())) {
                addBackup(new LapidaryRecipe(r, NumismaticManagerAccessor.getGemFuelMap().get(r)));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ComparableItemStack> streamRecipes() {
        return new SimpleObjectStream<>(NumismaticManagerAccessor.getGemFuelMap().keySet()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        NumismaticManagerAccessor.getGemFuelMap().keySet().forEach(x -> addBackup(new LapidaryRecipe(x, NumismaticManagerAccessor.getGemFuelMap().get(x))));
        NumismaticManagerAccessor.getGemFuelMap().clear();
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static final class LapidaryRecipe {

        private final ComparableItemStack comparableItemStack;
        private final int energy;

        public LapidaryRecipe(ComparableItemStack comparableItemStack, int energy) {
            this.comparableItemStack = comparableItemStack;
            this.energy = energy;
        }

        public ComparableItemStack comparableItemStack() {
            return comparableItemStack;
        }

        public int energy() {
            return energy;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (LapidaryRecipe) obj;
            return Objects.equals(this.comparableItemStack, that.comparableItemStack) && this.energy == that.energy;
        }

        @Override
        public int hashCode() {
            return Objects.hash(comparableItemStack, energy);
        }

        @Override
        public String toString() {
            return "LapidaryRecipe[" + "comparableItemStack=" + comparableItemStack + ", " + "energy=" + energy + ']';
        }
    }
}
