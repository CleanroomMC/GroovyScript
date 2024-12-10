package com.cleanroommc.groovyscript.compat.mods.prodigytech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.ItemsIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import lykrast.prodigytech.common.recipe.ExplosionFurnaceManager;
import net.minecraft.item.ItemStack;

import java.util.Objects;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class ExplosionFurnaceAdditives extends VirtualizedRegistry<ExplosionFurnaceAdditives.EFAdditiveRecipe> {

    @Override
    public void onReload() {
        removeScripted().forEach(EFAdditiveRecipe::unregister);
        restoreFromBackup().forEach(EFAdditiveRecipe::register);
    }

    private void add(EFAdditiveRecipe recipe) {
        recipe.register();
        addScripted(recipe);
    }

    private void remove(EFAdditiveRecipe recipe) {
        recipe.unregister();
        addBackup(recipe);
    }

    @MethodDescription(example = @Example("item('minecraft:cobblestone'), 50"), type = MethodDescription.Type.ADDITION)
    public void addExplosive(IIngredient explosive, int power) {
        if (IngredientHelper.overMaxSize(explosive, 1)) {
            GroovyLog.msg("Error adding Explosion Furnace Explosive")
                    .error()
                    .add("Expected input stack size of 1")
                    .post();
            return;
        }

        EFAdditiveRecipe recipe = new EFAdditiveExplosive(explosive, power);
        add(recipe);
    }

    @MethodDescription(example = @Example("ore('gunpowder')"))
    public boolean removeExplosive(IIngredient explosive) {
        for (ItemStack it : explosive.getMatchingStacks()) {
            ExplosionFurnaceManager.Explosive externalExplosive = ExplosionFurnaceManager.findExplosive(it);
            if (externalExplosive == null) continue;
            EFAdditiveRecipe recipe = new EFAdditiveExplosive(explosive, externalExplosive.getPower());
            remove(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAllExplosives() {
        ExplosionFurnaceManager.EXPLOSIVES.getAllContent().forEach(r -> addBackup(new EFAdditiveExplosive(new ItemsIngredient(r.getMatchingStacks()), r.getPower())));
        ExplosionFurnaceManager.removeAllExplosives();
    }

    @MethodDescription(example = @Example("item('minecraft:stone'), 50"), type = MethodDescription.Type.ADDITION)
    public void addDampener(IIngredient dampener, int power) {
        if (IngredientHelper.overMaxSize(dampener, 1)) {
            GroovyLog.msg("Error adding Explosion Furnace Dampener")
                    .error()
                    .add("Expected input stack size of 1")
                    .post();
            return;
        }

        EFAdditiveRecipe recipe = new EFAdditiveDampener(dampener, power);
        add(recipe);
    }

    @MethodDescription(example = @Example("ore('dustAsh')"))
    public boolean removeDampener(IIngredient dampener) {
        for (ItemStack it : dampener.getMatchingStacks()) {
            ExplosionFurnaceManager.Dampener externalDampener = ExplosionFurnaceManager.findDampener(it);
            if (externalDampener == null) continue;
            EFAdditiveRecipe recipe = new EFAdditiveDampener(dampener, externalDampener.getDampening());
            remove(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAllDampeners() {
        ExplosionFurnaceManager.DAMPENERS.getAllContent().forEach(r -> addBackup(new EFAdditiveDampener(new ItemsIngredient(r.getMatchingStacks()), r.getDampening())));
        ExplosionFurnaceManager.removeAllDampeners();
    }

    public interface EFAdditiveRecipe {

        void register();

        void unregister();
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static final class EFAdditiveExplosive implements EFAdditiveRecipe {

        private final IIngredient input;
        private final int value;

        public EFAdditiveExplosive(IIngredient input, int value) {
            this.input = input;
            this.value = value;
        }

        @Override
        public void register() {
            if (this.input instanceof OreDictIngredient oreDictIngredient) {
                ExplosionFurnaceManager.addExplosive(oreDictIngredient.getOreDict(), this.value);
            } else {
                for (ItemStack it : this.input.getMatchingStacks()) {
                    ExplosionFurnaceManager.addExplosive(it, this.value);
                }
            }
        }

        @Override
        public void unregister() {
            if (this.input instanceof OreDictIngredient oreDictIngredient) {
                ExplosionFurnaceManager.removeExplosive(oreDictIngredient.getOreDict());
            } else {
                for (ItemStack it : this.input.getMatchingStacks()) {
                    ExplosionFurnaceManager.removeExplosive(it);
                }
            }
        }

        public IIngredient input() {
            return input;
        }

        public int value() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (EFAdditiveExplosive) obj;
            return Objects.equals(this.input, that.input) && this.value == that.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(input, value);
        }

        @Override
        public String toString() {
            return "EFAdditiveExplosive[" + "input=" + input + ", " + "value=" + value + ']';
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static final class EFAdditiveDampener implements EFAdditiveRecipe {

        private final IIngredient input;
        private final int value;

        public EFAdditiveDampener(IIngredient input, int value) {
            this.input = input;
            this.value = value;
        }

        @Override
        public void register() {
            if (this.input instanceof OreDictIngredient oreDictIngredient) {
                ExplosionFurnaceManager.addDampener(oreDictIngredient.getOreDict(), this.value);
            } else {
                for (ItemStack it : this.input.getMatchingStacks()) {
                    ExplosionFurnaceManager.addDampener(it, this.value);
                }
            }
        }

        @Override
        public void unregister() {
            if (this.input instanceof OreDictIngredient oreDictIngredient) {
                ExplosionFurnaceManager.removeDampener(oreDictIngredient.getOreDict());
            } else {
                for (ItemStack it : this.input.getMatchingStacks()) {
                    ExplosionFurnaceManager.removeDampener(it);
                }
            }
        }

        public IIngredient input() {
            return input;
        }

        public int value() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (EFAdditiveDampener) obj;
            return Objects.equals(this.input, that.input) && this.value == that.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(input, value);
        }

        @Override
        public String toString() {
            return "EFAdditiveDampener[" + "input=" + input + ", " + "value=" + value + ']';
        }
    }
}
