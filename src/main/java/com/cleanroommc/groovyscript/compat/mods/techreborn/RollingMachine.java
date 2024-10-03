package com.cleanroommc.groovyscript.compat.mods.techreborn;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import techreborn.api.RollingMachineRecipe;

import java.util.List;
import java.util.Map;

@RegistryDescription
public class RollingMachine extends VirtualizedRegistry<Pair<ResourceLocation, IRecipe>> {

    @RecipeBuilderDescription(example = {
            @Example(".output(item('minecraft:stone')).matrix('BXX', 'X B').key('B', item('minecraft:stone')).key('X', item('minecraft:gold_ingot')).mirrored()"),
            @Example(".output(item('minecraft:diamond') * 32).matrix([[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')]])")
    })
    public RollingMachineRecipeBuilder.Shaped shapedBuilder() {
        return new RollingMachineRecipeBuilder.Shaped();
    }

    @RecipeBuilderDescription(example = {
            @Example(".output(item('minecraft:clay') * 8).input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'))"),
            @Example(".output(item('minecraft:clay') * 32).input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'))")
    })
    public RollingMachineRecipeBuilder.Shapeless shapelessBuilder() {
        return new RollingMachineRecipeBuilder.Shapeless();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(r -> RollingMachineRecipe.instance.getRecipeList().remove(r.getKey()));
        restoreFromBackup().forEach(r -> RollingMachineRecipe.instance.getRecipeList().put(r.getKey(), r.getValue()));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public IRecipe addShaped(ItemStack output, List<List<IIngredient>> input) {
        return shapedBuilder()
                .matrix(input)
                .output(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public IRecipe addShapeless(ItemStack output, List<IIngredient> input) {
        return shapelessBuilder()
                .input(input)
                .output(output)
                .register();
    }

    public IRecipe add(ResourceLocation key, IRecipe recipe) {
        if (recipe != null) {
            addScripted(Pair.of(key, recipe));
            RollingMachineRecipe.instance.getRecipeList().put(key, recipe);
        }
        return recipe;
    }

    @MethodDescription(example = @Example("item('minecraft:tripwire_hook')"))
    public boolean removeByOutput(IIngredient output) {
        return RollingMachineRecipe.instance.getRecipeList().entrySet().removeIf(r -> {
            if (output.test(r.getValue().getRecipeOutput())) {
                addBackup(Pair.of(r.getKey(), r.getValue()));
                return true;
            }
            return false;
        });
    }

    public boolean remove(ResourceLocation key) {
        return RollingMachineRecipe.instance.getRecipeList().remove(key) != null;
    }

    public boolean remove(IRecipe recipe) {
        return RollingMachineRecipe.instance.getRecipeList().entrySet().removeIf(r -> {
            if (r.getValue() == recipe) {
                addBackup(Pair.of(r.getKey(), r.getValue()));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<ResourceLocation, IRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(RollingMachineRecipe.instance.getRecipeList().entrySet()).setRemover(x -> remove(x.getKey()));
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        RollingMachineRecipe.instance.getRecipeList().forEach((a, b) -> addBackup(Pair.of(a, b)));
        RollingMachineRecipe.instance.getRecipeList().clear();
    }

}
