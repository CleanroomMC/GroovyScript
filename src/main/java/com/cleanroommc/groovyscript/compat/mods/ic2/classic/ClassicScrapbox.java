package com.cleanroommc.groovyscript.compat.mods.ic2.classic;

import com.cleanroommc.groovyscript.compat.mods.ic2.exp.Scrapbox;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import ic2.api.classic.recipe.ClassicRecipes;
import ic2.api.classic.recipe.custom.IClassicScrapBoxManager;
import ic2.core.item.recipe.ScrapBoxManager;
import net.minecraft.item.ItemStack;

public class ClassicScrapbox extends Scrapbox {

    public ClassicScrapbox() {
        super(false);
    }

    @Override
    public void onReload() {
        removeScripted().forEach(drop -> ClassicRecipes.scrapboxDrops.removeDrop(getDrop(((Drop) drop).stack)));
        restoreFromBackup().forEach(drop -> ClassicRecipes.scrapboxDrops.addDrop(((IClassicScrapBoxManager.IDrop) drop).getDrop(), ((IClassicScrapBoxManager.IDrop) drop).getChance()));
    }

    public void add(IClassicScrapBoxManager.IDrop drop) {
        ClassicRecipes.scrapboxDrops.addDrop(drop.getDrop(), drop.getChance());
        addScripted(new Drop(drop.getDrop(), drop.getChance()));
    }

    public void add(ItemStack stack, float chance) {
        ClassicRecipes.scrapboxDrops.addDrop(stack, chance);
        addScripted(new Drop(stack, chance));
    }

    public void remove(IClassicScrapBoxManager.IDrop drop) {
        ClassicRecipes.scrapboxDrops.removeDrop(drop);
        addBackup(drop);
    }

    public void remove(ItemStack stack) {
        if (IngredientHelper.isEmpty(stack)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Scrapbox recipe")
                    .add("stack must not be empty")
                    .error()
                    .post();
            return;
        }
        for (IClassicScrapBoxManager.IDrop drop : ClassicRecipes.scrapboxDrops.getEntries()) {
            if (ItemStack.areItemStacksEqual(drop.getDrop(), stack)) {
                remove(drop);
            }
        }
    }

    public void removeAll() {
        for (IClassicScrapBoxManager.IDrop drop : ClassicRecipes.scrapboxDrops.getEntries()) {
            remove(drop);
        }
    }

    private IClassicScrapBoxManager.IDrop getDrop(ItemStack stack) {
        for (IClassicScrapBoxManager.IDrop drop : ClassicRecipes.scrapboxDrops.getEntries()) {
            if (ItemStack.areItemStacksEqual(drop.getDrop(), stack)) return drop;
        }
        return null;
    }
}
