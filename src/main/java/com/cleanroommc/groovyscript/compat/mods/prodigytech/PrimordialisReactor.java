package com.cleanroommc.groovyscript.compat.mods.prodigytech;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import lykrast.prodigytech.common.recipe.PrimordialisReactorManager;
import net.minecraft.item.ItemStack;

@RegistryDescription
public class PrimordialisReactor extends VirtualizedRegistry<IIngredient> {
    @Override
    public void onReload() {
        removeScripted().forEach(this::removeRecipeBase);
        restoreFromBackup().forEach(this::addRecipeBase);
    }

    private void addRecipeBase(IIngredient x) {
        if (x instanceof OreDictIngredient) {
            PrimordialisReactorManager.addInput(((OreDictIngredient) x).getOreDict());
        } else {
            for (ItemStack it : x.getMatchingStacks()) {
                PrimordialisReactorManager.addInput(it);
            }
        }
    }

    private void removeRecipeBase(IIngredient x) {
        if (x instanceof OreDictIngredient) {
            PrimordialisReactorManager.removeInput(((OreDictIngredient) x).getOreDict());
        } else {
            for (ItemStack it : x.getMatchingStacks()) {
                PrimordialisReactorManager.removeInput(it);
            }
        }
    }

    @MethodDescription(example = @Example("item('minecraft:diamond')"))
    public void addRecipe(IIngredient x) {
        addScripted(x);
        addRecipeBase(x);
    }

    @MethodDescription(example = @Example("ore('sugarcane')"))
    public void removeRecipe(IIngredient x) {
        addBackup(x);
        removeRecipeBase(x);
    }
}
