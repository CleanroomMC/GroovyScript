package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.device;

import cofh.core.inventory.ComparableItemStack;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.TapperManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.github.bsideup.jabel.Desugar;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class TapperFertilizer extends VirtualizedRegistry<TapperFertilizer.TapperRecipe> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> TapperManagerAccessor.getFertilizerMap().keySet().removeIf(r -> r.isItemEqual(recipe.bait())));
        restoreFromBackup().forEach(r -> TapperManagerAccessor.getFertilizerMap().put(r.bait(), r.multiplier()));
    }

    public void add(TapperRecipe recipe) {
        TapperManagerAccessor.getFertilizerMap().put(recipe.bait(), recipe.multiplier());
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:clay'), 1000"))
    public void add(ItemStack stack, int multiplier) {
        add(new TapperRecipe(new ComparableItemStack(stack), multiplier));
    }

    public boolean remove(ComparableItemStack stack) {
        return TapperManagerAccessor.getFertilizerMap().keySet().removeIf(r -> {
            if (stack.isItemEqual(r)) {
                addBackup(new TapperRecipe(r, TapperManagerAccessor.getFertilizerMap().get(r)));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('thermalfoundation:fertilizer:2')"))
    public boolean remove(IIngredient input) {
        return TapperManagerAccessor.getFertilizerMap().keySet().removeIf(r -> {
            if (input.test(r.toItemStack())) {
                addBackup(new TapperRecipe(r, TapperManagerAccessor.getFertilizerMap().get(r)));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ComparableItemStack> streamRecipes() {
        return new SimpleObjectStream<>(TapperManagerAccessor.getFertilizerMap().keySet())
                .setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        TapperManagerAccessor.getFertilizerMap().keySet().forEach(x -> addBackup(new TapperRecipe(x, TapperManagerAccessor.getFertilizerMap().get(x))));
        TapperManagerAccessor.getFertilizerMap().clear();
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static class TapperRecipe {

        private final ComparableItemStack bait;
        private final int multiplier;

        public TapperRecipe(ComparableItemStack bait, int multiplier) {
            this.bait = bait;
            this.multiplier = multiplier;
        }

        public ComparableItemStack bait() {
            return bait;
        }

        public int multiplier() {
            return multiplier;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (TapperRecipe) obj;
            return Objects.equals(this.bait, that.bait) &&
                   this.multiplier == that.multiplier;
        }

        @Override
        public int hashCode() {
            return Objects.hash(bait, multiplier);
        }

        @Override
        public String toString() {
            return "TapperRecipe[" +
                   "bait=" + bait + ", " +
                   "multiplier=" + multiplier + ']';
        }
    }

}
