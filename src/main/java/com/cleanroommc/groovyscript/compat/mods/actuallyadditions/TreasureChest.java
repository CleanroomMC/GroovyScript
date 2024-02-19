package com.cleanroommc.groovyscript.compat.mods.actuallyadditions;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.TreasureChestLoot;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class TreasureChest extends VirtualizedRegistry<TreasureChestLoot> {

    @RecipeBuilderDescription(example = @Example(".output(item('minecraft:clay')).weight(50).min(16).max(32)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(ActuallyAdditionsAPI.TREASURE_CHEST_LOOT::remove);
        ActuallyAdditionsAPI.TREASURE_CHEST_LOOT.addAll(restoreFromBackup());
    }

    public TreasureChestLoot add(ItemStack returnItem, int chance, int minAmount, int maxAmount) {
        TreasureChestLoot recipe = new TreasureChestLoot(returnItem, chance, minAmount, maxAmount);
        add(recipe);
        return recipe;
    }

    public void add(TreasureChestLoot recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        ActuallyAdditionsAPI.TREASURE_CHEST_LOOT.add(recipe);
    }

    public boolean remove(TreasureChestLoot recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ActuallyAdditionsAPI.TREASURE_CHEST_LOOT.remove(recipe);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('minecraft:iron_ingot')"))
    public boolean removeByOutput(ItemStack output) {
        return ActuallyAdditionsAPI.TREASURE_CHEST_LOOT.removeIf(recipe -> {
            boolean matches = ItemStack.areItemStacksEqual(recipe.returnItem, output);
            if (matches) {
                addBackup(recipe);
            }
            return matches;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ActuallyAdditionsAPI.TREASURE_CHEST_LOOT.forEach(this::addBackup);
        ActuallyAdditionsAPI.TREASURE_CHEST_LOOT.clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<TreasureChestLoot> streamRecipes() {
        return new SimpleObjectStream<>(ActuallyAdditionsAPI.TREASURE_CHEST_LOOT)
                .setRemover(this::remove);
    }


    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<TreasureChestLoot> {

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "0"))
        private int weight;
        @Property(valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "max")})
        private int min;
        @Property(valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.GTE, value = "min")})
        private int max;

        @RecipeBuilderMethodDescription
        public RecipeBuilder weight(int weight) {
            this.weight = weight;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder min(int min) {
            this.min = min;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder max(int max) {
            this.max = max;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Actually Additions Treasure Chest Loot recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg);
            msg.add(weight < 0, "weight must be a non negative integer, yet it was {}", weight);
            msg.add(min < 0, "min must be a non negative integer, yet it was {}", min);
            msg.add(max < 0, "max must be a non negative integer, yet it was {}", max);
            msg.add(max < min, "max must be greater than min, yet max was {} while min was {}", max, min);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable TreasureChestLoot register() {
            if (!validate()) return null;
            TreasureChestLoot recipe = new TreasureChestLoot(output.get(0), weight, min, max);
            ModSupport.ACTUALLY_ADDITIONS.get().treasureChest.add(recipe);
            return recipe;
        }
    }
}
