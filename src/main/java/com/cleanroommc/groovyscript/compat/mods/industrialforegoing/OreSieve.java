package com.cleanroommc.groovyscript.compat.mods.industrialforegoing;

import com.buuz135.industrial.api.recipe.ore.OreFluidEntrySieve;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;

@RegistryDescription
public class OreSieve extends StandardListRegistry<OreFluidEntrySieve> {

    public OreSieve() {
        super(Alias.generateOfClass(OreSieve.class).andGenerate("FluidSieving"));
    }

    @Override
    public Collection<OreFluidEntrySieve> getRecipes() {
        return OreFluidEntrySieve.ORE_FLUID_SIEVE;
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = {
            @Example("fluid('if.ore_fluid_fermented').withNbt(['Ore': 'oreGold']) * 100, item('minecraft:nether_star') * 2, item('minecraft:clay')"),
            @Example("fluid('lava') * 5, item('minecraft:gold_ingot'), item('minecraft:clay')")
    })
    public OreFluidEntrySieve add(FluidStack input, ItemStack output, ItemStack sieveItem) {
        if (IngredientHelper.overMaxSize(sieveItem, 1)) {
            GroovyLog.msg("Error adding Fluid Sieving recipe")
                    .error()
                    .add("Sieve item stack size must be 1")
                    .post();
            return null;
        }
        OreFluidEntrySieve recipe = new OreFluidEntrySieve(input, output, sieveItem);
        add(recipe);
        return recipe;
    }

    @MethodDescription(example = {
            @Example("item('minecraft:sand')"),
            @Example("fluid('if.pink_slime')")
    })
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(recipe -> {
            if (input.test(recipe.getInput()) || input.test(recipe.getSieveItem())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "item('industrialforegoing:pink_slime_ingot", commented = true))
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
