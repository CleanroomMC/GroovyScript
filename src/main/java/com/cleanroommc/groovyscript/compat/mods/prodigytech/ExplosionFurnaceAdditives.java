package com.cleanroommc.groovyscript.compat.mods.prodigytech;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.ingredient.ItemsIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import lykrast.prodigytech.common.recipe.ExplosionFurnaceManager;
import net.minecraft.item.ItemStack;

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
        EFAdditiveRecipe recipe = new EFAdditiveRecipe(true, explosive, power);
        add(recipe);
    }

    @MethodDescription(example = @Example("ore('gunpowder')"))
    public boolean removeExplosive(IIngredient explosive) {
        for (ItemStack it : explosive.getMatchingStacks()) {
            ExplosionFurnaceManager.Explosive externalExplosive = ExplosionFurnaceManager.findExplosive(it);
            EFAdditiveRecipe recipe = new EFAdditiveRecipe(true, explosive, externalExplosive.getPower());
            remove(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example(priority = 2000, commented = true))
    public void removeAllExplosives() {
        ExplosionFurnaceManager.EXPLOSIVES.getAllContent().forEach(r ->
            addBackup(new EFAdditiveRecipe(true, new ItemsIngredient(r.getMatchingStacks()), r.getPower())));
        ExplosionFurnaceManager.removeAllExplosives();
    }

    @MethodDescription(example = @Example("item('minecraft:stone'), 50"), type = MethodDescription.Type.ADDITION)
    public void addDampener(IIngredient dampener, int power) {
        EFAdditiveRecipe recipe = new EFAdditiveRecipe(false, dampener, power);
        add(recipe);
    }

    @MethodDescription(example = @Example("ore('dustAsh')"))
    public boolean removeDampener(IIngredient dampener) {
        for (ItemStack it : dampener.getMatchingStacks()) {
            ExplosionFurnaceManager.Dampener externalDampener = ExplosionFurnaceManager.findDampener(it);
            EFAdditiveRecipe recipe = new EFAdditiveRecipe(false, dampener, externalDampener.getDampening());
            remove(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example(priority = 2000, commented = true))
    public void removeAllDampeners() {
        ExplosionFurnaceManager.DAMPENERS.getAllContent().forEach(r ->
            addBackup(new EFAdditiveRecipe(false, new ItemsIngredient(r.getMatchingStacks()), r.getDampening())));
        ExplosionFurnaceManager.removeAllDampeners();
    }

    public static class EFAdditiveRecipe {
        boolean isExplosive;
        IIngredient input;
        int value;

        EFAdditiveRecipe(boolean isExplosive, IIngredient input, int value) {
            this.isExplosive = isExplosive;
            this.input = input;
            this.value = value;
        }

        void register() {
            if (this.isExplosive) {
                if (this.input instanceof OreDictIngredient) {
                    ExplosionFurnaceManager.addExplosive(((OreDictIngredient) this.input).getOreDict(), this.value);
                } else {
                    for (ItemStack it : this.input.getMatchingStacks()) {
                        ExplosionFurnaceManager.addExplosive(it, this.value);
                    }
                }
            } else {
                if (this.input instanceof OreDictIngredient) {
                    ExplosionFurnaceManager.addDampener(((OreDictIngredient) this.input).getOreDict(), this.value);
                } else {
                    for (ItemStack it : this.input.getMatchingStacks()) {
                        ExplosionFurnaceManager.addDampener(it, this.value);
                    }
                }
            }
        }

        void unregister() {
            if (this.isExplosive) {
                if (this.input instanceof OreDictIngredient) {
                    ExplosionFurnaceManager.removeExplosive(((OreDictIngredient) this.input).getOreDict());
                } else {
                    for (ItemStack it : this.input.getMatchingStacks()) {
                        ExplosionFurnaceManager.removeExplosive(it);
                    }
                }
            } else {
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
}
