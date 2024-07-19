package com.cleanroommc.groovyscript.compat.mods.industrialforegoing;

import com.buuz135.industrial.api.recipe.FluidDictionaryEntry;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;

@RegistryDescription
public class FluidDictionary extends StandardListRegistry<FluidDictionaryEntry> {

    @Override
    public Collection<FluidDictionaryEntry> getRegistry() {
        return FluidDictionaryEntry.FLUID_DICTIONARY_RECIPES;
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

}
