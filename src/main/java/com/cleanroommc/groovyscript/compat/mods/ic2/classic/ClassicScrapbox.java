package com.cleanroommc.groovyscript.compat.mods.ic2.classic;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ic2.exp.Scrapbox;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import ic2.api.classic.recipe.ClassicRecipes;
import ic2.api.classic.recipe.custom.IClassicScrapBoxManager;
import net.minecraft.item.ItemStack;

public class ClassicScrapbox extends Scrapbox {

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
        if (GroovyLog.msg("Error adding Industrialcraft 2 Scrapbox recipe")
                .add(IngredientHelper.isEmpty(stack), () -> "stack must not be emtpy")
                .add(chance <= 0, () -> "chance must be higher than zero")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        ClassicRecipes.scrapboxDrops.addDrop(stack, chance);
        addScripted(new Drop(stack, chance));
    }

    public SimpleObjectStream<IClassicScrapBoxManager.IDrop> streamRecipes() {
        return new SimpleObjectStream<>(ClassicRecipes.scrapboxDrops.getEntries()).setRemover(r -> remove(r.getDrop()));
    }

    public void remove(IClassicScrapBoxManager.IDrop drop) {
        ClassicRecipes.scrapboxDrops.removeDrop(drop);
        addBackup(drop);
    }

    public boolean remove(ItemStack stack) {
        if (IngredientHelper.isEmpty(stack)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Scrapbox recipe")
                    .add("stack must not be empty")
                    .error()
                    .post();
            return false;
        }
        for (IClassicScrapBoxManager.IDrop drop : ClassicRecipes.scrapboxDrops.getEntries()) {
            if (ItemStack.areItemStacksEqual(drop.getDrop(), stack)) {
                remove(drop);
                return true;
            }
        }
        GroovyLog.msg("Error removing Industrialcraft 2 Scrapbox recipe")
                .add("no recipes found for {}", stack)
                .error()
                .post();
        return false;
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
