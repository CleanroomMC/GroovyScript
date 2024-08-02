package com.cleanroommc.groovyscript.compat.mods.prodigytech;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.api.jeiremoval.IJEIRemoval;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.IOperation;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.ItemOperation;
import com.cleanroommc.groovyscript.helper.ingredient.ItemsIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.github.bsideup.jabel.Desugar;
import com.google.common.collect.ImmutableList;
import lykrast.prodigytech.common.compat.jei.ExplosionFurnaceDampenerCategory;
import lykrast.prodigytech.common.compat.jei.ExplosionFurnaceExplosiveCategory;
import lykrast.prodigytech.common.recipe.ExplosionFurnaceManager;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class ExplosionFurnaceAdditives extends VirtualizedRegistry<ExplosionFurnaceAdditives.EFAdditiveRecipe> implements IJEIRemoval.Default {

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

    @MethodDescription(example = @Example(priority = 2000, commented = true))
    public void removeAllExplosives() {
        ExplosionFurnaceManager.EXPLOSIVES.getAllContent().forEach(r ->
                                                                           addBackup(new EFAdditiveExplosive(new ItemsIngredient(r.getMatchingStacks()), r.getPower())));
        ExplosionFurnaceManager.removeAllExplosives();
    }

    @MethodDescription(example = @Example("item('minecraft:stone'), 50"), type = MethodDescription.Type.ADDITION)
    public void addDampener(IIngredient dampener, int power) {
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

    @MethodDescription(example = @Example(priority = 2000, commented = true))
    public void removeAllDampeners() {
        ExplosionFurnaceManager.DAMPENERS.getAllContent().forEach(r ->
                                                                          addBackup(new EFAdditiveDampener(new ItemsIngredient(r.getMatchingStacks()), r.getDampening())));
        ExplosionFurnaceManager.removeAllDampeners();
    }

    @Override
    public @NotNull Collection<String> getCategories() {
        return ImmutableList.of(ExplosionFurnaceDampenerCategory.UID, ExplosionFurnaceExplosiveCategory.UID);
    }

    @Override
    public @NotNull List<IOperation> getJEIOperations() {
        return ImmutableList.of(ItemOperation.defaultOperation().input("removeDampener"));
    }

    public interface EFAdditiveRecipe {

        void register();

        void unregister();
    }

    @Desugar
    public record EFAdditiveExplosive(IIngredient input, int value) implements EFAdditiveRecipe {

        @Override
        public void register() {
            if (this.input instanceof OreDictIngredient) {
                ExplosionFurnaceManager.addExplosive(((OreDictIngredient) this.input).getOreDict(), this.value);
            } else {
                for (ItemStack it : this.input.getMatchingStacks()) {
                    ExplosionFurnaceManager.addExplosive(it, this.value);
                }
            }
        }

        @Override
        public void unregister() {
            if (this.input instanceof OreDictIngredient) {
                ExplosionFurnaceManager.removeExplosive(((OreDictIngredient) this.input).getOreDict());
            } else {
                for (ItemStack it : this.input.getMatchingStacks()) {
                    ExplosionFurnaceManager.removeExplosive(it);
                }
            }
        }
    }

    @Desugar
    public record EFAdditiveDampener(IIngredient input, int value) implements EFAdditiveRecipe {

        @Override
        public void register() {
            if (this.input instanceof OreDictIngredient) {
                ExplosionFurnaceManager.addDampener(((OreDictIngredient) this.input).getOreDict(), this.value);
            } else {
                for (ItemStack it : this.input.getMatchingStacks()) {
                    ExplosionFurnaceManager.addDampener(it, this.value);
                }
            }
        }

        @Override
        public void unregister() {
            if (this.input instanceof OreDictIngredient) {
                ExplosionFurnaceManager.removeDampener(((OreDictIngredient) this.input).getOreDict());
            } else {
                for (ItemStack it : this.input.getMatchingStacks()) {
                    ExplosionFurnaceManager.removeDampener(it);
                }
            }
        }
    }
}
