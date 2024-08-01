package com.cleanroommc.groovyscript.compat.mods.industrialforegoing;

import com.buuz135.industrial.api.recipe.FluidDictionaryEntry;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.IJEIRemoval;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

@RegistryDescription
public class FluidDictionary extends VirtualizedRegistry<FluidDictionaryEntry> implements IJEIRemoval.Default {

    @Override
    @GroovyBlacklist
    public void onReload() {
        FluidDictionaryEntry.FLUID_DICTIONARY_RECIPES.removeAll(removeScripted());
        FluidDictionaryEntry.FLUID_DICTIONARY_RECIPES.addAll(restoreFromBackup());
    }

    @MethodDescription(description = "groovyscript.wiki.industrialforegoing.fluid_dictionary.add0", type = MethodDescription.Type.ADDITION, example = {
            @Example("fluid('biofuel'), fluid('latex'),"),
            @Example("fluid('latex'), fluid('biofuel'),")
    })
    public FluidDictionaryEntry add(FluidStack input, FluidStack output) {
        return add(input, output, 1);
    }

    @MethodDescription(description = "groovyscript.wiki.industrialforegoing.fluid_dictionary.add1", type = MethodDescription.Type.ADDITION, example = {
            @Example("fluid('essence'), fluid('latex'), 2"),
            @Example("fluid('latex'), fluid('essence'), 0.5")
    })
    public FluidDictionaryEntry add(FluidStack input, FluidStack output, double ratio) {
        return add(input.getFluid().getName(), output.getFluid().getName(), ratio);
    }

    @MethodDescription(description = "groovyscript.wiki.industrialforegoing.fluid_dictionary.add1", type = MethodDescription.Type.ADDITION)
    public FluidDictionaryEntry add(String input, String output, double ratio) {
        FluidDictionaryEntry recipe = new FluidDictionaryEntry(input, output, ratio);
        add(recipe);
        return recipe;
    }

    public void add(FluidDictionaryEntry recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        FluidDictionaryEntry.FLUID_DICTIONARY_RECIPES.add(recipe);
    }

    public boolean remove(FluidDictionaryEntry recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        FluidDictionaryEntry.FLUID_DICTIONARY_RECIPES.remove(recipe);
        return true;
    }

    @MethodDescription
    public boolean removeByInput(String fluid) {
        return FluidDictionaryEntry.FLUID_DICTIONARY_RECIPES.removeIf(recipe -> {
            if (fluid.equals(recipe.getFluidOrigin())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "fluid('essence')", commented = true))
    public boolean removeByInput(FluidStack fluid) {
        return removeByInput(fluid.getFluid().getName());
    }

    @MethodDescription
    public boolean removeByOutput(String fluid) {
        return FluidDictionaryEntry.FLUID_DICTIONARY_RECIPES.removeIf(recipe -> {
            if (fluid.equals(recipe.getFluidResult())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "fluid(essence')", commented = true))
    public boolean removeByOutput(FluidStack fluid) {
        return removeByOutput(fluid.getFluid().getName());
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        FluidDictionaryEntry.FLUID_DICTIONARY_RECIPES.forEach(this::addBackup);
        FluidDictionaryEntry.FLUID_DICTIONARY_RECIPES.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<FluidDictionaryEntry> streamRecipes() {
        return new SimpleObjectStream<>(FluidDictionaryEntry.FLUID_DICTIONARY_RECIPES)
                .setRemover(this::remove);
    }

    /**
     * @see com.buuz135.industrial.jei.fluiddictionary.FluidDictionaryCategory#getUid()
     */
    @Override
    public @NotNull Collection<String> getCategories() {
        return Collections.singletonList("fluid_dictionary");
    }
}
