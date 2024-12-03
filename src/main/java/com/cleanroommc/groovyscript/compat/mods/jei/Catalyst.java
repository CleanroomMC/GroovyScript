package com.cleanroommc.groovyscript.compat.mods.jei;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.jei.ModRegistryAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import mezz.jei.api.IModRegistry;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collection;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class Catalyst extends VirtualizedRegistry<Pair<String, ItemStack>> {

    /**
     * Called by {@link JeiPlugin#afterRegister()}
     */
    @GroovyBlacklist
    public void applyChanges(IModRegistry modRegistry) {
        for (var backupRecipe : getBackupRecipes()) {
            ((ModRegistryAccessor) modRegistry).getRecipeCatalysts()
                    .get(backupRecipe.getKey())
                    .removeIf(x -> backupRecipe.getValue() == null || x instanceof ItemStack stack && ItemStack.areItemStacksEqual(stack, backupRecipe.getValue()));
        }
        for (var scriptedRecipe : getScriptedRecipes()) {
            modRegistry.addRecipeCatalyst(scriptedRecipe.getValue(), scriptedRecipe.getKey());
        }
    }

    @Override
    public void onReload() {
        restoreFromBackup();
        removeScripted();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(String category, ItemStack item) {
        addScripted(Pair.of(category, item));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'minecraft.smelting', item('minecraft:clay') * 8, item('minecraft:cobblestone')"))
    public void add(String category, ItemStack... item) {
        Arrays.stream(item).map(i -> Pair.of(category, i)).forEach(this::addScripted);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(String category, Collection<ItemStack> item) {
        item.stream().map(i -> Pair.of(category, i)).forEach(this::addScripted);
    }

    @MethodDescription(example = @Example("'minecraft.smelting', item('minecraft:furnace')"))
    public void remove(String category, ItemStack item) {
        addBackup(Pair.of(category, item));
    }

    @MethodDescription
    public void remove(String category, ItemStack... item) {
        Arrays.stream(item).map(i -> Pair.of(category, i)).forEach(this::addBackup);
    }

    @MethodDescription
    public void remove(String category, Collection<ItemStack> item) {
        item.stream().map(i -> Pair.of(category, i)).forEach(this::addBackup);
    }

    @MethodDescription(example = @Example(value = "'minecraft.anvil'", commented = true))
    public void removeByType(String category) {
        addBackup(Pair.of(category, null));
    }
}
