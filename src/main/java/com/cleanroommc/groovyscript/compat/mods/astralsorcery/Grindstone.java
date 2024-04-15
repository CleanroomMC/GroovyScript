package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipe;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipeRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

@RegistryDescription
public class Grindstone extends VirtualizedRegistry<GrindstoneRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(ore('blockDiamond')).output(item('minecraft:clay')).weight(1).secondaryChance(1.0F)"),
            @Example(".input(item('minecraft:stone')).output(item('minecraft:cobblestone')).weight(5)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(r -> GrindstoneRecipeRegistry.recipes.removeIf(recipe -> r == recipe));
        restoreFromBackup().forEach(GrindstoneRecipeRegistry::registerGrindstoneRecipe);
    }

    private void add(GrindstoneRecipe recipe) {
        addScripted(recipe);
        GrindstoneRecipeRegistry.registerGrindstoneRecipe(recipe);
    }

    @MethodDescription(description = "groovyscript.wiki.astralsorcery.grindstone.add0", type = MethodDescription.Type.ADDITION)
    public GrindstoneRecipe add(ItemHandle input, ItemStack output, int weight) {
        return add(input, output, weight, 0);
    }

    @MethodDescription(description = "groovyscript.wiki.astralsorcery.grindstone.add1", type = MethodDescription.Type.ADDITION)
    public GrindstoneRecipe add(ItemHandle input, ItemStack output, int weight, float secondaryChance) {
        GrindstoneRecipe recipe = new GrindstoneRecipe(input, output, weight, secondaryChance);
        addScripted(recipe);
        return GrindstoneRecipeRegistry.registerGrindstoneRecipe(recipe);
    }

    private boolean remove(GrindstoneRecipe recipe) {
        addBackup(recipe);
        return GrindstoneRecipeRegistry.recipes.remove(recipe);
    }

    @MethodDescription(example = @Example("item('minecraft:redstone_ore')"))
    public void removeByInput(ItemStack item) {
        GrindstoneRecipeRegistry.recipes.removeIf(recipe -> {
            if (recipe.isValid() && recipe.matches(item)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("ore('dustIron')"))
    public void removeByOutput(OreDictIngredient ore) {
        for (ItemStack item : ore.getMatchingStacks())
            this.removeByOutput(item);
    }

    @MethodDescription
    public void removeByOutput(ItemStack item) {
        GrindstoneRecipeRegistry.recipes.removeIf(recipe -> {
            if (recipe.isValid() && recipe.getOutputForMatching().isItemEqual(item)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<GrindstoneRecipe> streamRecipes() {
        return new SimpleObjectStream<>(GrindstoneRecipeRegistry.recipes)
                .setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        GrindstoneRecipeRegistry.recipes.forEach(this::addBackup);
        GrindstoneRecipeRegistry.recipes.clear();
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<GrindstoneRecipe> {

        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int weight = 0;
        @Property(valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "1", type = Comp.Type.LTE)})
        private float secondaryChance = 0.0F;

        @RecipeBuilderMethodDescription
        public RecipeBuilder weight(int weight) {
            this.weight = weight;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder secondaryChance(float chance) {
            this.secondaryChance = chance;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Astral Sorcery Grindstone recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(weight < 0, () -> "Weight cannot be negative");
            msg.add(secondaryChance < 0 || secondaryChance > 1, () -> "Secondary chance must be between [0,1]. Instead found " + secondaryChance + ".");
        }

        @RecipeBuilderRegistrationMethod
        public GrindstoneRecipe register() {
            if (!validate()) return null;
            GrindstoneRecipe recipe = new GrindstoneRecipe(AstralSorcery.toItemHandle(input.get(0)), output.get(0), weight, secondaryChance);
            ModSupport.ASTRAL_SORCERY.get().grindstone.add(recipe);
            return recipe;
        }
    }
}
