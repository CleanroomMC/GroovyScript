package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.dynamo;

import cofh.core.inventory.ComparableItemStack;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.SteamManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.github.bsideup.jabel.Desugar;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

@RegistryDescription
public class Steam extends VirtualizedRegistry<Steam.SteamRecipe> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> SteamManagerAccessor.getFuelMap().keySet().removeIf(r -> r.equals(recipe.comparableItemStack())));
        restoreFromBackup().forEach(r -> SteamManagerAccessor.getFuelMap().put(r.comparableItemStack(), r.energy()));
    }

    public void add(SteamRecipe recipe) {
        SteamManagerAccessor.getFuelMap().put(recipe.comparableItemStack(), recipe.energy());
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:clay'), 100"))
    public void add(ItemStack itemStack, int energy) {
        add(new SteamRecipe(new ComparableItemStack(itemStack), energy));
    }

    public boolean remove(ComparableItemStack recipe) {
        return SteamManagerAccessor.getFuelMap().keySet().removeIf(r -> {
            if (r.equals(recipe)) {
                addBackup(new SteamRecipe(r, SteamManagerAccessor.getFuelMap().get(r)));
                return true;
            }
            return false;
        });
    }

    public boolean remove(SteamRecipe recipe) {
        return SteamManagerAccessor.getFuelMap().keySet().removeIf(r -> {
            if (r.equals(recipe.comparableItemStack())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:coal:1')"))
    public boolean removeByInput(IIngredient input) {
        return SteamManagerAccessor.getFuelMap().keySet().removeIf(r -> {
            if (input.test(r.toItemStack())) {
                addBackup(new SteamRecipe(r, SteamManagerAccessor.getFuelMap().get(r)));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ComparableItemStack> streamRecipes() {
        return new SimpleObjectStream<>(SteamManagerAccessor.getFuelMap().keySet()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        SteamManagerAccessor.getFuelMap().keySet().forEach(x -> addBackup(new SteamRecipe(x, SteamManagerAccessor.getFuelMap().get(x))));
        SteamManagerAccessor.getFuelMap().clear();
    }

    @Desugar
    public record SteamRecipe(ComparableItemStack comparableItemStack, int energy) {

    }

}
