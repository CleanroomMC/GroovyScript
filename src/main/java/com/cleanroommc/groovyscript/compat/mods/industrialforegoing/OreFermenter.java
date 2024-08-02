package com.cleanroommc.groovyscript.compat.mods.industrialforegoing;

import com.buuz135.industrial.api.recipe.ore.OreFluidEntryFermenter;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.api.jeiremoval.IJEIRemoval;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

@RegistryDescription
public class OreFermenter extends VirtualizedRegistry<OreFluidEntryFermenter> implements IJEIRemoval.Default {

    public OreFermenter() {
        super(Alias.generateOfClass(OreFermenter.class).andGenerate("Fermentation"));
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        OreFluidEntryFermenter.ORE_FLUID_FERMENTER.removeAll(removeScripted());
        OreFluidEntryFermenter.ORE_FLUID_FERMENTER.addAll(restoreFromBackup());
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("fluid('if.ore_fluid_raw').withNbt(['Ore': 'oreGold']), fluid('if.ore_fluid_fermented').withNbt(['Ore': 'oreGold']) * 2"))
    public OreFluidEntryFermenter add(FluidStack input, FluidStack output) {
        OreFluidEntryFermenter recipe = new OreFluidEntryFermenter(input, output);
        add(recipe);
        return recipe;
    }

    public void add(OreFluidEntryFermenter recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        OreFluidEntryFermenter.ORE_FLUID_FERMENTER.add(recipe);
    }

    public boolean remove(OreFluidEntryFermenter recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        OreFluidEntryFermenter.ORE_FLUID_FERMENTER.remove(recipe);
        return true;
    }

    @MethodDescription(example = @Example("fluid('if.ore_fluid_raw').withNbt([Ore: 'oreRedstone'])"))
    public boolean removeByInput(IIngredient input) {
        return OreFluidEntryFermenter.ORE_FLUID_FERMENTER.removeIf(recipe -> {
            if (input.test(recipe.getInput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "fluid('if.ore_fluid_fermented').withNbt([Ore: 'oreRedstone'])", commented = true))
    public boolean removeByOutput(IIngredient output) {
        return OreFluidEntryFermenter.ORE_FLUID_FERMENTER.removeIf(recipe -> {
            if (output.test(recipe.getOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        OreFluidEntryFermenter.ORE_FLUID_FERMENTER.forEach(this::addBackup);
        OreFluidEntryFermenter.ORE_FLUID_FERMENTER.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<OreFluidEntryFermenter> streamRecipes() {
        return new SimpleObjectStream<>(OreFluidEntryFermenter.ORE_FLUID_FERMENTER)
                .setRemover(this::remove);
    }

    @Override
    public @NotNull Collection<String> getCategories() {
        return Collections.singletonList("ORE_FERMENTER");
    }
}
