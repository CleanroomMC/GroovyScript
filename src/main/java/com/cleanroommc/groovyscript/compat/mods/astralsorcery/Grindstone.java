package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipe;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipeRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;

public class Grindstone extends VirtualizedRegistry<GrindstoneRecipe> {

    public Grindstone() {
        super("Grindstone", "grindstone");
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(this::remove);
        restoreFromBackup().forEach(this::add);
    }

    private void add(GrindstoneRecipe recipe) {
        GrindstoneRecipeRegistry.registerGrindstoneRecipe(recipe);
    }

    public GrindstoneRecipe add(ItemHandle input, ItemStack output, int chance, float doubleChance) {
        GrindstoneRecipe recipe = new GrindstoneRecipe(input, output, chance, doubleChance);
        addScripted(recipe);
        return GrindstoneRecipeRegistry.registerGrindstoneRecipe(recipe);
    }

    private void remove(GrindstoneRecipe recipe) {
        GrindstoneRecipeRegistry.recipes.remove(recipe);
    }

    public void removeByOutput(OreDictIngredient ore) {
        for (ItemStack item : ore.getMatchingStacks())
            this.removeByOutput(item);
    }

    public void removeByOutput(ItemStack item) {
        ArrayList<GrindstoneRecipe> remove = new ArrayList<>();

        for (GrindstoneRecipe recipe : GrindstoneRecipeRegistry.recipes) {
            if (recipe.isValid() && recipe.getOutputForMatching().isItemEqual(item)) {
                addBackup(recipe);
                remove.add(recipe);
            }
        }

        remove.forEach(recipe -> GrindstoneRecipeRegistry.recipes.remove(recipe));
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<GrindstoneRecipe> {

        private ItemHandle input = null;
        private int chance = 0;
        private float doubleChance = 0.0F;

        public RecipeBuilder input(ItemStack item) {
            this.input = new ItemHandle(item);
            return this;
        }

        public RecipeBuilder input(String ore) {
            this.input = new ItemHandle(ore);
            return this;
        }

        public RecipeBuilder input(OreDictIngredient ore) {
            this.input = new ItemHandle(ore.getOreDict());
            return this;
        }

        public RecipeBuilder chance(int chance) {
            this.chance = chance;
            return this;
        }

        public RecipeBuilder doubleChance(float chance) {
            this.doubleChance = chance;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Astral Sorcery Grindstone recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            msg.add(this.input == null, () -> "Input cannot be null");
            msg.add(this.chance < 0, () -> "Chance cannot be negative");
            msg.add(this.doubleChance < 0 || this.doubleChance > 1, () -> "Chance to double output must be between [0,1]");
        }

        public GrindstoneRecipe register() {
            return ModSupport.ASTRAL_SORCERY.get().grindstone.add(input, output.get(0), chance, doubleChance);
        }
    }
}
