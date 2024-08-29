package com.cleanroommc.groovyscript.compat.mods.extendedcrafting;

import com.blakebr0.extendedcrafting.crafting.table.ITieredRecipe;
import com.blakebr0.extendedcrafting.crafting.table.TableRecipeManager;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.List;

@RegistryDescription
public class TableCrafting extends StandardListRegistry<ITieredRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".output(item('minecraft:stone') * 64).matrix('DLLLLLDDD', '  DNIGIND', 'DDDNIGIND', '  DLLLLLD').key('D', item('minecraft:diamond')).key('L', item('minecraft:redstone')).key('N', item('minecraft:stone')).key('I', item('minecraft:iron_ingot')).key('G', item('minecraft:gold_ingot')).tierUltimate()"),
            @Example(".tierAdvanced().output(item('minecraft:stone') * 8).matrix('BXX').mirrored().key('B', item('minecraft:stone')).key('X', item('minecraft:gold_ingot'))"),
            @Example(".tierAny().output(item('minecraft:diamond')).matrix('BXXXBX').mirrored().key('B', item('minecraft:stone')).key('X', item('minecraft:gold_ingot'))"),
            @Example(".matrix([[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')]]).output(item('minecraft:gold_ingot') * 64).tier(4)"),
            @Example(".matrix([[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')]]).output(item('minecraft:gold_ingot') * 64)")
    })
    public TableRecipeBuilder.Shaped shapedBuilder() {
        return new TableRecipeBuilder.Shaped();
    }

    @RecipeBuilderDescription(example = @Example(".output(item('minecraft:stone') * 64).input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'))"))
    public TableRecipeBuilder.Shapeless shapelessBuilder() {
        return new TableRecipeBuilder.Shapeless();
    }

    @Override
    public Collection<ITieredRecipe> getRecipes() {
        return TableRecipeManager.getInstance().getRecipes();
    }

    @MethodDescription(description = "groovyscript.wiki.extendedcrafting.table_crafting.addShaped0", type = MethodDescription.Type.ADDITION)
    public ITieredRecipe addShaped(ItemStack output, List<List<IIngredient>> input) {
        return addShaped(0, output, input);
    }

    @MethodDescription(description = "groovyscript.wiki.extendedcrafting.table_crafting.addShaped1", type = MethodDescription.Type.ADDITION)
    public ITieredRecipe addShaped(int tier, ItemStack output, List<List<IIngredient>> input) {
        return shapedBuilder()
                .tier(tier)
                .matrix(input)
                .output(output)
                .register();
    }

    @MethodDescription(description = "groovyscript.wiki.extendedcrafting.table_crafting.addShapeless0", type = MethodDescription.Type.ADDITION)
    public ITieredRecipe addShapeless(ItemStack output, List<IIngredient> input) {
        return addShapeless(0, output, input);
    }

    @MethodDescription(description = "groovyscript.wiki.extendedcrafting.table_crafting.addShapeless1", type = MethodDescription.Type.ADDITION)
    public ITieredRecipe addShapeless(int tier, ItemStack output, List<IIngredient> input) {
        return shapelessBuilder()
                .tier(tier)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(example = @Example("item('extendedcrafting:singularity_ultimate')"))
    public boolean removeByOutput(ItemStack stack) {
        return TableRecipeManager.getInstance().getRecipes().removeIf(recipe -> {
            if (recipe != null && recipe.getRecipeOutput().isItemEqual(stack)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

}
