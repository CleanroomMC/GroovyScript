package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.dynamo;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.CompressionManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.github.bsideup.jabel.Desugar;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

@RegistryDescription
public class Compression extends VirtualizedRegistry<Compression.CompressionRecipe> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> CompressionManagerAccessor.getFuelMap().keySet().removeIf(r -> r.equals(recipe.fluid())));
        restoreFromBackup().forEach(r -> CompressionManagerAccessor.getFuelMap().put(r.fluid(), r.energy()));
    }

    public void add(CompressionRecipe recipe) {
        CompressionManagerAccessor.getFuelMap().put(recipe.fluid(), recipe.energy());
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("fluid('steam'), 100"))
    public void add(FluidStack fluidStack, int energy) {
        add(new CompressionRecipe(fluidStack.getFluid().getName(), energy));
    }

    public boolean remove(String fluid) {
        return CompressionManagerAccessor.getFuelMap().keySet().removeIf(r -> {
            if (r.equals(fluid)) {
                addBackup(new CompressionRecipe(r, CompressionManagerAccessor.getFuelMap().getInt(r)));
                return true;
            }
            return false;
        });
    }

    public boolean remove(CompressionRecipe recipe) {
        return CompressionManagerAccessor.getFuelMap().keySet().removeIf(r -> {
            if (r.equals(recipe.fluid())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription
    public boolean removeByInput(String input) {
        return CompressionManagerAccessor.getFuelMap().keySet().removeIf(r -> {
            if (input.equals(r)) {
                addBackup(new CompressionRecipe(r, CompressionManagerAccessor.getFuelMap().getInt(r)));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("fluid('seed_oil')"))
    public boolean removeByInput(FluidStack input) {
        return removeByInput(input.getFluid().getName());
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<String> streamRecipes() {
        return new SimpleObjectStream<>(CompressionManagerAccessor.getFuelMap().keySet()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        CompressionManagerAccessor.getFuelMap().keySet().forEach(x -> addBackup(new CompressionRecipe(x, CompressionManagerAccessor.getFuelMap().getInt(x))));
        CompressionManagerAccessor.getFuelMap().clear();
    }

    @SuppressWarnings({"unused", "ClassCanBeRecord"})
    public static class CompressionRecipe {

        private final String fluid;
        private final int energy;

        public CompressionRecipe(String fluid, int energy) {
            this.fluid = fluid;
            this.energy = energy;
        }

        public String fluid() {
            return fluid;
        }

        public int energy() {
            return energy;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (CompressionRecipe) obj;
            return Objects.equals(this.fluid, that.fluid) &&
                   this.energy == that.energy;
        }

        @Override
        public int hashCode() {
            return Objects.hash(fluid, energy);
        }

        @Override
        public String toString() {
            return "CompressionRecipe[" +
                   "fluid=" + fluid + ", " +
                   "energy=" + energy + ']';
        }
    }

}
