package com.cleanroommc.groovyscript.compat.mods.forestry;

import com.cleanroommc.groovyscript.brackets.BracketHandlerManager;
import com.cleanroommc.groovyscript.brackets.SpeciesBracketHandler;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import org.jetbrains.annotations.Nullable;

public class Forestry extends ModPropertyContainer {

    public final CharcoalPile charcoalPile = new CharcoalPile();
    public final Squeezer squeezer = new Squeezer();
    public final Still still = new Still();
    public final Centrifuge centrifuge = new Centrifuge();
    public final Fermenter fermenter = new Fermenter();
    public final Moistener moistener = new Moistener();
    public final MoistenerFuel moistenerFuel = new MoistenerFuel();
    public final Carpenter carpenter = new Carpenter();
    public final ThermionicFabricator thermionicFabricator = new ThermionicFabricator();
    public final BeeProduce beeProduce = new BeeProduce();
    public final BeeMutations beeMutations = new BeeMutations();

    public Forestry() {
        addRegistry(charcoalPile);
        addRegistry(squeezer);
        addRegistry(still);
        addRegistry(centrifuge);
        addRegistry(fermenter);
        addRegistry(moistener);
        addRegistry(moistenerFuel);
        addRegistry(carpenter);
        addRegistry(thermionicFabricator);
        addRegistry(thermionicFabricator.smelting);
        addRegistry(beeProduce);
        addRegistry(beeMutations);
    }

    @Override
    public void initialize() {
        BracketHandlerManager.registerBracketHandler("species", SpeciesBracketHandler.INSTANCE);
    }

    @Override
    public @Nullable Object getProperty(String name) {
        Object registry = super.getProperty(name);
        if (registry instanceof ForestryRegistry<?> && !((ForestryRegistry<?>) registry).isEnabled()) return null;
        return registry;
    }
}
