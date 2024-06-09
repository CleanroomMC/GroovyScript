package com.cleanroommc.groovyscript.compat.mods.techreborn;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import reborncore.api.praescriptum.fuels.Fuel;
import reborncore.api.praescriptum.fuels.FuelHandler;

public abstract class AbstractGeneratorRegistry extends VirtualizedRegistry<Fuel> {

    abstract FuelHandler handler();

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        handler().getFuels().removeAll(removeScripted());
        handler().getFuels().addAll(restoreFromBackup());
    }

    public void add(Fuel recipe) {
        if (recipe != null) {
            addScripted(recipe);
            recipe.register();
        }
    }

    public boolean remove(Fuel recipe) {
        if (handler().getFuels().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription
    public void removeByInput(IIngredient input) {
        handler().getFuels().removeIf(recipe -> {
            if (recipe.getInputIngredients().stream().map(x -> x.ingredient).anyMatch(x -> {
                if (x instanceof ItemStack itemStack) return input.test(itemStack);
                if (x instanceof FluidStack fluidStack) return input.test(fluidStack);
                if (x instanceof String s && input instanceof OreDictIngredient ore) return ore.getOreDict().equals(s);
                return false;
            })) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Fuel> streamRecipes() {
        return new SimpleObjectStream<>(handler().getFuels()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        handler().getFuels().forEach(this::addBackup);
        handler().getFuels().clear();
    }

}
