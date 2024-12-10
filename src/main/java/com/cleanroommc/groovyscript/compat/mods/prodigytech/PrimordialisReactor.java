package com.cleanroommc.groovyscript.compat.mods.prodigytech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.ItemsIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import lykrast.prodigytech.common.recipe.PrimordialisReactorManager;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class PrimordialisReactor extends VirtualizedRegistry<IIngredient> {

    @Override
    public void onReload() {
        removeScripted().forEach(this::removeRecipeBase);
        restoreFromBackup().forEach(this::addRecipeBase);
    }

    private void addRecipeBase(IIngredient x) {
        if (x instanceof OreDictIngredient oreDictIngredient) {
            PrimordialisReactorManager.addInput(oreDictIngredient.getOreDict());
        } else {
            for (ItemStack it : x.getMatchingStacks()) {
                PrimordialisReactorManager.addInput(it);
            }
        }
    }

    private boolean removeRecipeBase(IIngredient x) {
        if (x instanceof OreDictIngredient oreDictIngredient) {
            PrimordialisReactorManager.removeInput(oreDictIngredient.getOreDict());
        } else {
            for (ItemStack it : x.getMatchingStacks()) {
                PrimordialisReactorManager.removeInput(it);
            }
        }
        // the mod's API does not expose a boolean there
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:diamond')"), type = MethodDescription.Type.ADDITION)
    public void add(IIngredient x) {
        if (IngredientHelper.overMaxSize(x, 1)) {
            // PT modifies the recipe to only consume 1 item
            GroovyLog.msg("Error adding Primordialis Reactor fuel")
                    .error()
                    .add("Expected input stack size of 1")
                    .post();
            return;
        }

        addScripted(x);
        addRecipeBase(x);
    }

    @MethodDescription(example = @Example("ore('sugarcane')"))
    public boolean remove(IIngredient x) {
        addBackup(x);
        return removeRecipeBase(x);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        PrimordialisReactorManager.getAllEntries().forEach(r -> addBackup(new ItemsIngredient(r)));
        PrimordialisReactorManager.getAllOreEntries().forEach(r -> addBackup(new OreDictIngredient(r)));
        PrimordialisReactorManager.removeAll();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IIngredient> streamRecipes() {
        Stream<IIngredient> normalRecipes = PrimordialisReactorManager.getAllEntries()
                .stream()
                .map(ItemsIngredient::new);
        Stream<IIngredient> oreDictRecipes = PrimordialisReactorManager.getAllOreEntries()
                .stream()
                .map(OreDictIngredient::new);
        List<IIngredient> items = Stream.concat(normalRecipes, oreDictRecipes).collect(Collectors.toList());
        return new SimpleObjectStream<>(items).setRemover(this::remove);
    }
}
