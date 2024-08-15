package com.cleanroommc.groovyscript.compat.mods.industrialforegoing;

import com.buuz135.industrial.api.recipe.BioReactorEntry;
import com.buuz135.industrial.api.recipe.IReactorEntry;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.google.common.base.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class BioReactor extends VirtualizedRegistry<IReactorEntry> {

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
        if (IngredientHelper.overMaxSize(input, 1)) {
            GroovyLog.msg("Error adding Bioreactor recipe").error()
                     .add("Input stack size must be 1")
                     .post();
            return null;
        }
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

}
