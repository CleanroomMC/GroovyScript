package com.cleanroommc.groovyscript.compat.mods.prodigytech;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import lykrast.prodigytech.common.recipe.SimpleRecipe;
import lykrast.prodigytech.common.recipe.SimpleRecipeManagerAbstract;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public abstract class SimpleRecipeHandlerAbstract<T extends SimpleRecipe> extends VirtualizedRegistry<T> {

    private final SimpleRecipeManagerAbstract<T> instance;
    protected final String name;

    SimpleRecipeHandlerAbstract(String name, SimpleRecipeManagerAbstract<T> instance) {
        this.instance = instance;
        this.name = name;
    }

    protected abstract int getDefaultTime();

    @Override
    public void onReload() {
        removeScripted().forEach(x -> instance.removeRecipe(x.getInput()));
        restoreFromBackup().forEach(instance::addRecipe);
    }

    public void addRecipe(T recipe) {
        addScripted(recipe);
        instance.addRecipe(recipe);
    }

    public boolean removeStackRecipe(ItemStack input) {
        T removed = instance.removeRecipe(input);
        if (removed != null) {
            addBackup(removed);
            return true;
        }
        // Try to remove the recipe through its OreDict
        for (int oreDictId : OreDictionary.getOreIDs(input)) {
            String oreDict = OreDictionary.getOreName(oreDictId);
            removed = instance.removeOreRecipe(oreDict);
            if (removed != null) {
                addBackup(removed);
                return true;
            }
        }
        return false;
    }

    public boolean removeOreRecipe(String input) {
        T removed = instance.removeOreRecipe(input);
        if (removed == null) return false;
        addBackup(removed);
        return true;
    }

    public boolean removeByInput(IIngredient input) {
        if (input instanceof OreDictIngredient oreDictIngredient) {
            return removeOreRecipe(oreDictIngredient.getOreDict());
        } else {
            boolean removed = false;
            for (ItemStack it : input.getMatchingStacks()) {
                removed |= removeStackRecipe(it);
            }
            return removed;
        }
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        instance.getAllRecipes().forEach(this::addBackup);
        instance.removeAll();
    }

    private boolean backupAndRemove(T recipe) {
        T removed;
        if (recipe.isOreRecipe()) {
            removed = instance.removeOreRecipe(recipe.getOreInput());
        } else {
            removed = instance.removeRecipe(recipe.getInput());
        }
        if (removed == null) {
            return false;
        }
        addBackup(removed);
        return true;
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<T> streamRecipes() {
        return new SimpleObjectStream<>(instance.getAllRecipes())
                .setRemover(this::backupAndRemove);
    }
}
