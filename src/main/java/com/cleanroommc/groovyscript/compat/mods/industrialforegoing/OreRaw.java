package com.cleanroommc.groovyscript.compat.mods.industrialforegoing;

import com.buuz135.industrial.api.recipe.ore.OreFluidEntryRaw;
import com.buuz135.industrial.jei.ore.OreWasherCategory;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.IJEIRemoval;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

@RegistryDescription
public class OreRaw extends VirtualizedRegistry<OreFluidEntryRaw> implements IJEIRemoval.Default {

    public OreRaw() {
        super(Alias.generateOfClass(OreRaw.class).andGenerate("Washing"));
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        OreFluidEntryRaw.ORE_RAW_ENTRIES.removeAll(removeScripted());
        OreFluidEntryRaw.ORE_RAW_ENTRIES.addAll(restoreFromBackup());
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = {
            @Example("ore('oreGold'), fluid('meat') * 200, fluid('if.ore_fluid_raw').withNbt(['Ore': 'oreGold']) * 300"),
            @Example("ore('stone'), fluid('water') * 1000, fluid('lava') * 50")
    })
    public OreFluidEntryRaw add(OreDictIngredient ore, FluidStack input, FluidStack output) {
        return add(ore.getOreDict(), input, output);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public OreFluidEntryRaw add(String ore, FluidStack input, FluidStack output) {
        OreFluidEntryRaw recipe = new OreFluidEntryRaw(ore, input, output);
        add(recipe);
        return recipe;
    }

    public void add(OreFluidEntryRaw recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        OreFluidEntryRaw.ORE_RAW_ENTRIES.add(recipe);
    }

    public boolean remove(OreFluidEntryRaw recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        OreFluidEntryRaw.ORE_RAW_ENTRIES.remove(recipe);
        return true;
    }

    @MethodDescription(example = @Example(value = "'oreRedstone'", commented = true))
    public boolean removeByOre(String ore) {
        return OreFluidEntryRaw.ORE_RAW_ENTRIES.removeIf(recipe -> {
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
        return OreFluidEntryRaw.ORE_RAW_ENTRIES.removeIf(recipe -> {
            if (input.test(recipe.getInput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "fluid('if.ore_fluid_raw').withNbt(['Ore': 'oreRedstone']),", commented = true))
    public boolean removeByOutput(IIngredient output) {
        return OreFluidEntryRaw.ORE_RAW_ENTRIES.removeIf(recipe -> {
            if (output.test(recipe.getOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        OreFluidEntryRaw.ORE_RAW_ENTRIES.forEach(this::addBackup);
        OreFluidEntryRaw.ORE_RAW_ENTRIES.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<OreFluidEntryRaw> streamRecipes() {
        return new SimpleObjectStream<>(OreFluidEntryRaw.ORE_RAW_ENTRIES)
                .setRemover(this::remove);
    }

    @Override
    public @NotNull Collection<String> getCategories() {
        // why does this one has a public static ID string when none of the others have one?
        return Collections.singletonList(OreWasherCategory.ID);
    }
}
