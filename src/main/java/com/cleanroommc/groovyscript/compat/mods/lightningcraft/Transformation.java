package com.cleanroommc.groovyscript.compat.mods.lightningcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import sblectric.lightningcraft.recipes.LightningTransformRecipes;

import java.util.List;
import java.util.Map;

@RegistryDescription(admonition = @Admonition(value = "groovyscript.wiki.lightningcraft.transformation.note", type = Admonition.Type.WARNING))
public class Transformation extends VirtualizedRegistry<Map.Entry<List<ItemStack>, ItemStack>> {

    public Map<List<ItemStack>, ItemStack> getEntries() {
        return LightningTransformRecipes.instance().getRecipeList();
    }

    @Override
    public void onReload() {
        var instance = getEntries();
        var entries = instance.entrySet();
        removeScripted().forEach(entry -> entries.removeIf(entry.getKey()::equals));
        restoreFromBackup().forEach(x -> instance.put(x.getKey(), x.getValue()));
    }

    public boolean add(Map.Entry<List<ItemStack>, ItemStack> recipe) {
        if (recipe == null) return false;
        LightningTransformRecipes.instance().addRecipe(recipe.getValue(), recipe.getKey());
        doAddScripted(recipe);
        return true;
    }

    public boolean remove(Map.Entry<List<ItemStack>, ItemStack> recipe) {
        return recipe != null && getEntries().entrySet().removeIf(r -> r == recipe) && doAddBackup(recipe);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:gold_ingot') * 3, item('minecraft:diamond_block')).output(item('minecraft:clay') * 16)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('minecraft:iron_ingot')"))
    public boolean removeByInput(IIngredient input) {
        return getEntries().entrySet().removeIf(r -> r.getKey().stream().anyMatch(input) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('lightningcraft:material', 11)"))
    public boolean removeByOutput(IIngredient output) {
        return getEntries().entrySet().removeIf(r -> output.test(r.getValue()) && doAddBackup(r));
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        getEntries().entrySet().forEach(this::doAddBackup);
        getEntries().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<List<ItemStack>, ItemStack>> streamRecipes() {
        return new SimpleObjectStream<>(getEntries().entrySet()).setRemover(this::remove);
    }

    @Property(property = "input", comp = @Comp(gte = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<Map.Entry<List<ItemStack>, ItemStack>> {

        @Override
        public String getErrorMsg() {
            return "Error adding LightningCraft Transformation recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, Integer.MAX_VALUE, 1, 1);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable Map.Entry<List<ItemStack>, ItemStack> register() {
            if (!validate()) return null;
            var inputs = IngredientHelper.cartesianProductItemStacks(input);
            Map.Entry<List<ItemStack>, ItemStack> recipe = null;
            for (List<ItemStack> itemStacks : inputs) {
                recipe = Pair.of(itemStacks, output.get(0));
                ModSupport.LIGHTNING_CRAFT.get().transformation.add(recipe);
            }
            return recipe;
        }
    }
}
