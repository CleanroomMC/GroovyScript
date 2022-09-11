package com.cleanroommc.groovyscript.compat.mods.ic2.exp;

import com.cleanroommc.groovyscript.core.mixin.ic2.ScrapboxRecipeManagerAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ic2.api.recipe.Recipes;
import net.minecraft.item.ItemStack;

import java.util.List;

public class Scrapbox extends VirtualizedRegistry<Object> {

    private List drops;

    public Scrapbox(boolean check) {
        super("Scrapbox", "scrapbox");
        if (check) drops = ((ScrapboxRecipeManagerAccessor) Recipes.scrapboxDrops).getDrops();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> drops.remove(((Drop) recipe).id));
        drops.addAll(restoreFromBackup());
    }

    public void add(ItemStack stack, float chance) {
        Drop drop = new Drop(stack, chance).setId(drops.size());
        Recipes.scrapboxDrops.addDrop(stack, chance);
        addScripted(drop);
    }

    public void removeAll() {
        drops.forEach(this::addBackup);
        drops.clear();
    }

    public static class Drop {
        int id;
        public ItemStack stack;
        public float chance;

        public Drop(ItemStack stack, float chance) {
            this.stack = stack;
            this.chance = chance;
        }

        public Drop setId(int id) {
            this.id = id;
            return this;
        }
    }
}
