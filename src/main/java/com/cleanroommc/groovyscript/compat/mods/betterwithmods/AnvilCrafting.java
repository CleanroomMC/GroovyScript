package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import betterwithmods.common.registry.anvil.AnvilCraftingManager;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;

@RegistryDescription
public class AnvilCrafting extends VirtualizedRegistry<IRecipe> {

    public AnvilCrafting() {
        super(Alias.generateOfClass(AnvilCrafting.class).andGenerate("SoulforgedSteelAnvil"));
    }

    @RecipeBuilderDescription(example = {
            @Example(".output(item('minecraft:diamond') * 32).matrix([[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),null],[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),null],[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),null],[null,null,null,item('minecraft:gold_ingot').transform({ _ -> item('minecraft:diamond') })]])"),
            @Example(".output(item('minecraft:diamond')).matrix('BXXX').mirrored().key('B', item('minecraft:stone')).key('X', item('minecraft:gold_ingot'))")
    })
    public AnvilRecipeBuilder.Shaped shapedBuilder() {
        return new AnvilRecipeBuilder.Shaped();
    }

    @RecipeBuilderDescription(example = @Example(".name(resource('example:anvil_clay')).output(item('minecraft:clay')).input([item('minecraft:cobblestone'), item('minecraft:gold_ingot')])"))
    public AnvilRecipeBuilder.Shapeless shapelessBuilder() {
        return new AnvilRecipeBuilder.Shapeless();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> AnvilCraftingManager.ANVIL_CRAFTING.removeIf(r -> r == recipe));
        AnvilCraftingManager.ANVIL_CRAFTING.addAll(restoreFromBackup());
    }

    public IRecipe add(IRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            AnvilCraftingManager.ANVIL_CRAFTING.add(recipe);
        }
        return recipe;
    }

    public boolean remove(IRecipe recipe) {
        if (AnvilCraftingManager.ANVIL_CRAFTING.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("item('betterwithmods:steel_block')"))
    public boolean removeByOutput(IIngredient output) {
        return AnvilCraftingManager.ANVIL_CRAFTING.removeIf(r -> {
            if (output.test(r.getRecipeOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:redstone')"))
    public boolean removeByInput(IIngredient input) {
        return AnvilCraftingManager.ANVIL_CRAFTING.removeIf(r -> {
            for (Ingredient ingredient : r.getIngredients()) {
                for (ItemStack item : ingredient.getMatchingStacks()) {
                    if (input.test(item)) {
                        addBackup(r);
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IRecipe> streamRecipes() {
        return new SimpleObjectStream<>(AnvilCraftingManager.ANVIL_CRAFTING).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        AnvilCraftingManager.ANVIL_CRAFTING.forEach(this::addBackup);
        AnvilCraftingManager.ANVIL_CRAFTING.clear();
    }

}
