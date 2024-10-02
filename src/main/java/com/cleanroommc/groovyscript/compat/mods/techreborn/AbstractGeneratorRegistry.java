package com.cleanroommc.groovyscript.compat.mods.techreborn;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import reborncore.api.praescriptum.fuels.Fuel;
import reborncore.api.praescriptum.fuels.FuelHandler;

import java.util.Collection;

public abstract class AbstractGeneratorRegistry extends StandardListRegistry<Fuel> {

    abstract FuelHandler handler();

    @Override
    public Collection<Fuel> getRecipes() {
        return handler().getFuels();
    }

    @MethodDescription
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(recipe -> {
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

}
