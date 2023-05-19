package com.cleanroommc.groovyscript.compat.mods.forestry;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import org.jetbrains.annotations.Nullable;

public class Forestry extends ModPropertyContainer {

    public final CharcoalPile charcoalPile = new CharcoalPile();
    public final Squeezer squeezer = new Squeezer();
    public final Still still = new Still();
    public final Centrifuge centrifuge = new Centrifuge();
    public final Fermenter fermenter = new Fermenter();
    public final Moistener moistener = new Moistener();
    public final MoistenerFuel moistenerFuel = new MoistenerFuel();

    public Forestry() {
        addRegistry(charcoalPile);
        addRegistry(squeezer);
        addRegistry(still);
        addRegistry(centrifuge);
        addRegistry(fermenter);
        addRegistry(moistener);
        addRegistry(moistenerFuel);
    }

    @Override
    public @Nullable Object getProperty(String name) {
        VirtualizedRegistry<?> registry = (VirtualizedRegistry<?>) super.getProperty(name);
        if (registry instanceof ForestryRegistry<?> && !((ForestryRegistry<?>) registry).isEnabled()) return null;
        return registry;
    }
}
