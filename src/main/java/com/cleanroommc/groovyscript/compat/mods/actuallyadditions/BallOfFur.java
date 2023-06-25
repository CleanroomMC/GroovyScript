package com.cleanroommc.groovyscript.compat.mods.actuallyadditions;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.BallOfFurReturn;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BallOfFur extends VirtualizedRegistry<BallOfFurReturn> {

    public BallOfFur() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(ActuallyAdditionsAPI.BALL_OF_FUR_RETURN_ITEMS::remove);
        ActuallyAdditionsAPI.BALL_OF_FUR_RETURN_ITEMS.addAll(restoreFromBackup());
    }

    public BallOfFurReturn add(ItemStack drop, int chance) {
        BallOfFurReturn recipe = new BallOfFurReturn(drop, chance);
        add(recipe);
        return recipe;
    }

    public void add(BallOfFurReturn recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        ActuallyAdditionsAPI.BALL_OF_FUR_RETURN_ITEMS.add(recipe);
    }

    public boolean remove(BallOfFurReturn recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ActuallyAdditionsAPI.BALL_OF_FUR_RETURN_ITEMS.remove(recipe);
        return true;
    }

    public boolean removeByOutput(ItemStack output) {
        return ActuallyAdditionsAPI.BALL_OF_FUR_RETURN_ITEMS.removeIf(recipe -> {
            boolean found = ItemStack.areItemStacksEqual(recipe.returnItem, output);
            if (found) {
                addBackup(recipe);
            }
            return found;
        });
    }

    public void removeAll() {
        ActuallyAdditionsAPI.BALL_OF_FUR_RETURN_ITEMS.forEach(this::addBackup);
        ActuallyAdditionsAPI.BALL_OF_FUR_RETURN_ITEMS.clear();
    }

    public SimpleObjectStream<BallOfFurReturn> streamRecipes() {
        return new SimpleObjectStream<>(ActuallyAdditionsAPI.BALL_OF_FUR_RETURN_ITEMS)
                .setRemover(this::remove);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<BallOfFurReturn> {

        private int weight;

        public RecipeBuilder weight(int weight) {
            this.weight = weight;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Actually Additions Ball Of Fur recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg);
            msg.add(weight < 0, "weight must be a non negative integer, yet it was {}", weight);
        }

        @Override
        public @Nullable BallOfFurReturn register() {
            if (!validate()) return null;
            BallOfFurReturn recipe = new BallOfFurReturn(output.get(0), weight);
            ModSupport.ACTUALLY_ADDITIONS.get().ballOfFur.add(recipe);
            return recipe;
        }
    }
}
