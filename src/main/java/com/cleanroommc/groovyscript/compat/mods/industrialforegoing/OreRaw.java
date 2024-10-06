package com.cleanroommc.groovyscript.compat.mods.industrialforegoing;

import com.buuz135.industrial.api.recipe.ore.OreFluidEntryRaw;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;

@RegistryDescription
public class OreRaw extends StandardListRegistry<OreFluidEntryRaw> {

    public OreRaw() {
        super(Alias.generateOfClass(OreRaw.class).andGenerate("Washing"));
    }

    @Override
    public Collection<OreFluidEntryRaw> getRecipes() {
        return OreFluidEntryRaw.ORE_RAW_ENTRIES;
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = {
            @Example("ore('oreGold'), fluid('meat') * 200, fluid('if.ore_fluid_raw').withNbt(['Ore': 'oreGold']) * 300"),
            @Example("ore('stone'), fluid('water') * 1000, fluid('lava') * 50")
    })
    public OreFluidEntryRaw add(OreDictIngredient ore, FluidStack input, FluidStack output) {
        if (IngredientHelper.overMaxSize(ore, 1)) {
            GroovyLog.msg("Error adding Ore Washer recipe")
                    .error()
                    .add("Stack size of input ore must be 1")
                    .post();
            return null;
        }
        return add(ore.getOreDict(), input, output);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public OreFluidEntryRaw add(String ore, FluidStack input, FluidStack output) {
        OreFluidEntryRaw recipe = new OreFluidEntryRaw(ore, input, output);
        add(recipe);
        return recipe;
    }

    @MethodDescription(example = @Example(value = "'oreRedstone'", commented = true))
    public boolean removeByOre(String ore) {
        return getRecipes().removeIf(recipe -> {
            if (ore.equals(recipe.getOre())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("ore('oreRedstone')"))
    public boolean removeByOre(OreDictIngredient ore) {
        return removeByOre(ore.getOreDict());
    }

    @MethodDescription(example = @Example(value = "fluid('meat')", commented = true))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(recipe -> {
            if (input.test(recipe.getInput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "fluid('if.ore_fluid_raw').withNbt(['Ore': 'oreRedstone']),", commented = true))
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
