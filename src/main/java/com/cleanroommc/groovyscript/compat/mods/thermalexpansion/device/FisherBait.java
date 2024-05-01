package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.device;

import cofh.core.inventory.ComparableItemStack;
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
public class FisherBait extends VirtualizedRegistry<FisherBait.FisherRecipe> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> FisherManagerAccessor.getBaitMap().keySet().removeIf(r -> r.isItemEqual(recipe.bait())));
        restoreFromBackup().forEach(r -> FisherManagerAccessor.getBaitMap().put(r.bait(), r.multiplier()));
    }

    public void add(FisherRecipe recipe) {
        FisherManagerAccessor.getBaitMap().put(recipe.bait(), recipe.multiplier());
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:clay'), 100"))
    public void add(ItemStack stack, int multiplier) {
        add(new FisherRecipe(new ComparableItemStack(stack), multiplier));
    }

    public boolean remove(ComparableItemStack stack) {
        return FisherManagerAccessor.getBaitMap().keySet().removeIf(r -> {
            if (stack.isItemEqual(r)) {
                addBackup(new FisherRecipe(r, FisherManagerAccessor.getBaitMap().get(r)));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('thermalfoundation:bait:2')"))
    public boolean remove(IIngredient input) {
        return FisherManagerAccessor.getBaitMap().keySet().removeIf(r -> {
            if (input.test(r.toItemStack())) {
                addBackup(new FisherRecipe(r, FisherManagerAccessor.getBaitMap().get(r)));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ComparableItemStack> streamRecipes() {
        return new SimpleObjectStream<>(FisherManagerAccessor.getBaitMap().keySet())
                .setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        FisherManagerAccessor.getBaitMap().keySet().forEach(x -> addBackup(new FisherRecipe(x, FisherManagerAccessor.getBaitMap().get(x))));
        FisherManagerAccessor.getBaitMap().clear();
    }

    @Desugar
    public record FisherRecipe(ComparableItemStack bait, int multiplier) {

    }

}
