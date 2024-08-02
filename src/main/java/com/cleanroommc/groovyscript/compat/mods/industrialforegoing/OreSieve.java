package com.cleanroommc.groovyscript.compat.mods.industrialforegoing;

import com.buuz135.industrial.api.recipe.ore.OreFluidEntrySieve;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.api.jeiremoval.IJEIRemoval;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

@RegistryDescription
public class OreSieve extends VirtualizedRegistry<OreFluidEntrySieve> implements IJEIRemoval.Default {

    public OreSieve() {
        super(Alias.generateOfClass(OreSieve.class).andGenerate("FluidSieving"));
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        OreFluidEntrySieve.ORE_FLUID_SIEVE.removeAll(removeScripted());
        OreFluidEntrySieve.ORE_FLUID_SIEVE.addAll(restoreFromBackup());
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = {
            @Example("fluid('if.ore_fluid_fermented').withNbt(['Ore': 'oreGold']) * 100, item('minecraft:nether_star') * 2, item('minecraft:clay')"),
            @Example("fluid('lava') * 5, item('minecraft:gold_ingot'), item('minecraft:clay')")
    })
    public OreFluidEntrySieve add(FluidStack input, ItemStack output, ItemStack sieveItem) {
        OreFluidEntrySieve recipe = new OreFluidEntrySieve(input, output, sieveItem);
        add(recipe);
        return recipe;
    }

    public void add(OreFluidEntrySieve recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        OreFluidEntrySieve.ORE_FLUID_SIEVE.add(recipe);
    }

    public boolean remove(OreFluidEntrySieve recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        OreFluidEntrySieve.ORE_FLUID_SIEVE.remove(recipe);
        return true;
    }

    @MethodDescription(example = {
            @Example("item('minecraft:sand')"),
            @Example("fluid('if.pink_slime')")
    })
    public boolean removeByInput(IIngredient input) {
        return OreFluidEntrySieve.ORE_FLUID_SIEVE.removeIf(recipe -> {
            if (input.test(recipe.getInput()) || input.test(recipe.getSieveItem())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "item('industrialforegoing:pink_slime_ingot", commented = true))
    public boolean removeByOutput(IIngredient output) {
        return OreFluidEntrySieve.ORE_FLUID_SIEVE.removeIf(recipe -> {
            if (output.test(recipe.getOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        OreFluidEntrySieve.ORE_FLUID_SIEVE.forEach(this::addBackup);
        OreFluidEntrySieve.ORE_FLUID_SIEVE.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<OreFluidEntrySieve> streamRecipes() {
        return new SimpleObjectStream<>(OreFluidEntrySieve.ORE_FLUID_SIEVE)
                .setRemover(this::remove);
    }

    @Override
    public @NotNull Collection<String> getCategories() {
        return Collections.singletonList("ORE_SIEVE");
    }
}
