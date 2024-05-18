package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.dynamo;

import cofh.core.inventory.ComparableItemStack;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.MagmaticManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.github.bsideup.jabel.Desugar;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;

@RegistryDescription
public class Magmatic extends VirtualizedRegistry<Magmatic.MagmaticRecipe> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> MagmaticManagerAccessor.getFuelMap().keySet().removeIf(r -> r.equals(recipe.fluid())));
        restoreFromBackup().forEach(r -> MagmaticManagerAccessor.getFuelMap().put(r.fluid(), r.energy()));
    }

    public void add(MagmaticRecipe recipe) {
        MagmaticManagerAccessor.getFuelMap().put(recipe.fluid(), recipe.energy());
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("fluid('steam'), 100"))
    public void add(FluidStack fluidStack, int energy) {
        add(new MagmaticRecipe(fluidStack.getFluid().getName(), energy));
    }

    public boolean remove(String fluid) {
        return MagmaticManagerAccessor.getFuelMap().keySet().removeIf(r -> {
            if (r.equals(fluid)) {
                addBackup(new MagmaticRecipe(r, MagmaticManagerAccessor.getFuelMap().getInt(r)));
                return true;
            }
            return false;
        });
    }

    public boolean remove(MagmaticRecipe recipe) {
        return MagmaticManagerAccessor.getFuelMap().keySet().removeIf(r -> {
            if (r.equals(recipe.fluid())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription
    public boolean removeByInput(String input) {
        return MagmaticManagerAccessor.getFuelMap().keySet().removeIf(r -> {
            if (input.equals(r)) {
                addBackup(new MagmaticRecipe(r, MagmaticManagerAccessor.getFuelMap().getInt(r)));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("fluid('lava')"))
    public boolean removeByInput(FluidStack input) {
        return removeByInput(input.getFluid().getName());
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<String> streamRecipes() {
        return new SimpleObjectStream<>(MagmaticManagerAccessor.getFuelMap().keySet()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        MagmaticManagerAccessor.getFuelMap().keySet().forEach(x -> addBackup(new MagmaticRecipe(x, MagmaticManagerAccessor.getFuelMap().getInt(x))));
        MagmaticManagerAccessor.getFuelMap().clear();
    }

    @Desugar
    public record MagmaticRecipe(String fluid, int energy) {

    }

}
