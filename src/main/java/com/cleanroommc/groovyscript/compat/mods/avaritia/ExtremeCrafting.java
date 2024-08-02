package com.cleanroommc.groovyscript.compat.mods.avaritia;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.IJEIRemoval;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.OperationHandler;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.google.common.collect.ImmutableList;
import morph.avaritia.compat.jei.AvaritiaJEIPlugin;
import morph.avaritia.recipe.AvaritiaRecipeManager;
import morph.avaritia.recipe.extreme.IExtremeRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RegistryDescription
public class ExtremeCrafting extends VirtualizedRegistry<IExtremeRecipe> implements IJEIRemoval.Default {

    @RecipeBuilderDescription(example = {
            @Example(".matrix([[item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')]]).output(item('minecraft:gold_block'))"),
            @Example(".output(item('minecraft:stone') * 64).matrix('DLLLLLDDD', '  DNIGIND', 'DDDNIGIND', '  DLLLLLD').key('D', item('minecraft:diamond')).key('L', item('minecraft:redstone')).key('N', item('minecraft:stone').reuse()).key('I', item('minecraft:iron_ingot')).key('G', item('minecraft:gold_ingot'))")
    })
    public ExtremeRecipeBuilder.Shaped shapedBuilder() {
        return new ExtremeRecipeBuilder.Shaped();
    }

    @RecipeBuilderDescription(example = @Example(".output(item('minecraft:stone') * 64).input(item('minecraft:stone'), item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'), item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'), item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'), item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'), item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'))"))
    public ExtremeRecipeBuilder.Shapeless shapelessBuilder() {
        return new ExtremeRecipeBuilder.Shapeless();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> AvaritiaRecipeManager.EXTREME_RECIPES.values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(recipe -> AvaritiaRecipeManager.EXTREME_RECIPES.put(recipe.getRegistryName(), recipe));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public IExtremeRecipe addShaped(ItemStack output, List<List<IIngredient>> input) {
        return shapedBuilder()
                .matrix(input)
                .output(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public IExtremeRecipe addShapeless(ItemStack output, List<IIngredient> input) {
        return shapelessBuilder()
                .input(input)
                .output(output)
                .register();
    }

    public IExtremeRecipe add(IExtremeRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            AvaritiaRecipeManager.EXTREME_RECIPES.put(recipe.getRegistryName(), recipe);
        }
        return recipe;
    }

    @MethodDescription(example = @Example("item('avaritia:resource', 6)"))
    public boolean removeByOutput(ItemStack stack) {
        return AvaritiaRecipeManager.EXTREME_RECIPES.values().removeIf(recipe -> {
            if (recipe != null && recipe.getRecipeOutput().isItemEqual(stack)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    public boolean remove(IExtremeRecipe recipe) {
        IExtremeRecipe remove = AvaritiaRecipeManager.EXTREME_RECIPES.remove(recipe.getRegistryName());
        if (remove != null) {
            addBackup(remove);
        }
        return remove != null;
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IExtremeRecipe> streamRecipes() {
        return new SimpleObjectStream<>(AvaritiaRecipeManager.EXTREME_RECIPES.values()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        AvaritiaRecipeManager.EXTREME_RECIPES.values().forEach(this::addBackup);
        AvaritiaRecipeManager.EXTREME_RECIPES.values().clear();
    }

    @Override
    public @NotNull Collection<String> getCategories() {
        return Collections.singletonList(AvaritiaJEIPlugin.EXTREME_CRAFTING);
    }

    @Override
    public @NotNull List<OperationHandler.IOperation> getJEIOperations() {
        return ImmutableList.of(OperationHandler.ItemOperation.defaultItemOperation().include(0));
    }

}
