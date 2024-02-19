package com.cleanroommc.groovyscript.compat.mods.ic2.exp;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.mixin.ic2.ScrapboxRecipeManagerAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ic2.api.recipe.Recipes;
import net.minecraft.item.ItemStack;

import java.util.List;

public class Scrapbox extends VirtualizedRegistry<Object> {

    @SuppressWarnings("all")
    @GroovyBlacklist
    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> getDrops().remove(((Drop) recipe).id));
        getDrops().addAll(restoreFromBackup());
    }

    public void add(ItemStack stack, float chance) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Scrapbox recipe")
                .add(IngredientHelper.isEmpty(stack), () -> "stack must not be emtpy")
                .add(chance <= 0, () -> "chance must be higher than zero")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        Drop drop = new Drop(stack, chance).setId(getDrops().size());
        Recipes.scrapboxDrops.addDrop(stack, chance);
        addScripted(drop);
    }

    public boolean remove(Object obj) {
        this.addBackup(obj);
        return getDrops().remove(obj);
    }

    public void removeAll() {
        getDrops().forEach(this::addBackup);
        getDrops().clear();
    }

    @GroovyBlacklist
    public List<Object> getDrops() {
        return ((ScrapboxRecipeManagerAccessor) Recipes.scrapboxDrops).getDrops();
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
