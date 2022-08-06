package com.cleanroommc.groovyscript.compat.enderio.recipe;

import com.cleanroommc.groovyscript.registry.IReloadableRegistry;
import net.minecraftforge.fluids.Fluid;

public interface IEnderIOFuelRegistry extends IReloadableRegistry<Void> {

    void removeCoolant(Fluid fluid);

    void removeFuel(Fluid fluid);
}
