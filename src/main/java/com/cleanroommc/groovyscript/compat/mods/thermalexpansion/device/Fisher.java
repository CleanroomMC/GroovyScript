package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.device;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.FisherManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.github.bsideup.jabel.Desugar;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class Fisher extends VirtualizedRegistry<Fisher.FisherRecipe> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        // not sure why this isn't stored as a Map<ItemStack, Integer>, having to manage two lists is rather annoying.
        removeScripted().forEach(recipe -> {
            int index = FisherManagerAccessor.getFishList().indexOf(recipe.fish());
            FisherManagerAccessor.getFishList().remove(index);
            FisherManagerAccessor.setTotalWeight(FisherManagerAccessor.getTotalWeight() - FisherManagerAccessor.getWeightList().remove(index));
        });
        restoreFromBackup().forEach(r -> {
            FisherManagerAccessor.getFishList().add(r.fish());
            FisherManagerAccessor.getWeightList().add(r.weight());
            FisherManagerAccessor.setTotalWeight(FisherManagerAccessor.getTotalWeight() + r.weight());
        });
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void afterScriptLoad() {
        // if the total weight is 0, then when `FisherManager.getFish()` calls `nextInt` we would implode
        FisherManagerAccessor.setTotalWeight(1);
    }

    public void add(FisherRecipe recipe) {
        FisherManagerAccessor.getFishList().add(recipe.fish());
        FisherManagerAccessor.getWeightList().add(recipe.weight());
        FisherManagerAccessor.setTotalWeight(FisherManagerAccessor.getTotalWeight() + recipe.weight());
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:clay'), 100"))
    public void add(ItemStack fish, int weight) {
        add(new FisherRecipe(fish, weight));
    }

    public boolean remove(ItemStack itemStack) {
        return FisherManagerAccessor.getFishList().removeIf(r -> {
            if (itemStack.isItemEqual(r)) {
                int weight = FisherManagerAccessor.getWeightList().remove(FisherManagerAccessor.getFishList().indexOf(r));
                addBackup(new FisherRecipe(r, weight));
                FisherManagerAccessor.setTotalWeight(FisherManagerAccessor.getTotalWeight() + weight);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:fish:0')"))
    public boolean remove(IIngredient input) {
        return FisherManagerAccessor.getFishList().removeIf(r -> {
            if (input.test(r)) {
                int weight = FisherManagerAccessor.getWeightList().remove(FisherManagerAccessor.getFishList().indexOf(r));
                addBackup(new FisherRecipe(r, weight));
                FisherManagerAccessor.setTotalWeight(FisherManagerAccessor.getTotalWeight() + weight);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ItemStack> streamRecipes() {
        return new SimpleObjectStream<>(FisherManagerAccessor.getFishList())
                .setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        FisherManagerAccessor.getFishList().forEach(x -> addBackup(new FisherRecipe(x, FisherManagerAccessor.getWeightList().remove(FisherManagerAccessor.getFishList().indexOf(x)))));
        FisherManagerAccessor.getFishList().clear();
        FisherManagerAccessor.setTotalWeight(0);
    }

    @Desugar
    public record FisherRecipe(ItemStack fish, int weight) {

    }

}
