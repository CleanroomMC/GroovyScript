package com.cleanroommc.groovyscript.compat.mods.industrialforegoing;

import com.buuz135.industrial.api.recipe.ore.OreFluidEntryFermenter;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;

@RegistryDescription
public class OreFermenter extends StandardListRegistry<OreFluidEntryFermenter> {

    public OreFermenter() {
        super(Alias.generateOfClass(OreFermenter.class).andGenerate("Fermentation"));
    }

    @Override
    public Collection<OreFluidEntryFermenter> getRecipes() {
        return OreFluidEntryFermenter.ORE_FLUID_FERMENTER;
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("fluid('if.ore_fluid_raw').withNbt(['Ore': 'oreGold']), fluid('if.ore_fluid_fermented').withNbt(['Ore': 'oreGold']) * 2"))
    public OreFluidEntryFermenter add(FluidStack input, FluidStack output) {
        OreFluidEntryFermenter recipe = new OreFluidEntryFermenter(input, output);
        add(recipe);
        return recipe;
    }

    @MethodDescription(example = @Example("fluid('if.ore_fluid_raw').withNbt([Ore: 'oreRedstone'])"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(recipe -> {
            if (input.test(recipe.getInput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "fluid('if.ore_fluid_fermented').withNbt([Ore: 'oreRedstone'])", commented = true))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(recipe -> {
            if (output.test(recipe.getOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

}
