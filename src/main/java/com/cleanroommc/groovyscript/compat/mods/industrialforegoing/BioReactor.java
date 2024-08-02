package com.cleanroommc.groovyscript.compat.mods.industrialforegoing;

import com.buuz135.industrial.api.recipe.BioReactorEntry;
import com.buuz135.industrial.api.recipe.IReactorEntry;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.api.jeiremoval.IJEIRemoval;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.IOperation;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.ItemOperation;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.google.common.base.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RegistryDescription
public class BioReactor extends VirtualizedRegistry<IReactorEntry> implements IJEIRemoval.Default {

    @Override
    @GroovyBlacklist
    public void onReload() {
        BioReactorEntry.BIO_REACTOR_ENTRIES.removeAll(removeScripted());
        BioReactorEntry.BIO_REACTOR_ENTRIES.addAll(restoreFromBackup());
    }

    @MethodDescription(description = "groovyscript.wiki.industrialforegoing.bio_reactor.add0", type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:clay')"))
    public IReactorEntry add(ItemStack input) {
        return add(input, null);
    }

    @MethodDescription(description = "groovyscript.wiki.industrialforegoing.bio_reactor.add1", type = MethodDescription.Type.ADDITION)
    public IReactorEntry add(ItemStack input, @Nullable Predicate<NBTTagCompound> nbtCheck) {
        IReactorEntry recipe = new BioReactorEntry(input, nbtCheck);
        add(recipe);
        return recipe;
    }

    public void add(IReactorEntry recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        BioReactorEntry.BIO_REACTOR_ENTRIES.add(recipe);
    }

    public boolean remove(IReactorEntry recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        BioReactorEntry.BIO_REACTOR_ENTRIES.remove(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:wheat_seeds')"))
    public boolean removeByInput(IIngredient input) {
        return BioReactorEntry.BIO_REACTOR_ENTRIES.removeIf(recipe -> {
            if (input.test(recipe.getStack())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        BioReactorEntry.BIO_REACTOR_ENTRIES.forEach(this::addBackup);
        BioReactorEntry.BIO_REACTOR_ENTRIES.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IReactorEntry> streamRecipes() {
        return new SimpleObjectStream<>(BioReactorEntry.BIO_REACTOR_ENTRIES)
                .setRemover(this::remove);
    }

    /**
     * @see com.buuz135.industrial.jei.JEICustomPlugin
     * @see com.buuz135.industrial.jei.reactor.ReactorRecipeCategory#getUid()
     */
    @Override
    public @NotNull Collection<String> getCategories() {
        return Collections.singletonList("bioreactor_accepted_items");
    }

    @Override
    public @NotNull List<IOperation> getJEIOperations() {
        return Collections.singletonList(ItemOperation.defaultOperation());
    }
}
