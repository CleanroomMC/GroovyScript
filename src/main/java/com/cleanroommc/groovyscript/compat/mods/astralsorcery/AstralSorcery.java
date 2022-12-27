package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.compat.mods.astralsorcery.perktree.GroovyPerkTree;
import com.cleanroommc.groovyscript.compat.mods.astralsorcery.starlightaltar.StarlightAltar;

public class AstralSorcery extends ModPropertyContainer {

    public final StarlightAltar altar = new StarlightAltar();
    public final Lightwell lightwell = new Lightwell();
    public final InfusionAltar infusionAltar = new InfusionAltar();
    public final Grindstone grindstone = new Grindstone();
    public final LightTransmutation lightTransmutation = new LightTransmutation();
    public final LiquidInteraction liquidInteraction = new LiquidInteraction();
    public final GroovyPerkTree perkTree = new GroovyPerkTree();
    public final Constellation constellation = new Constellation();
    public final Research research = new Research();

    public AstralSorcery() {

        addRegistry(altar);
        addRegistry(lightwell);
        addRegistry(infusionAltar);
        addRegistry(grindstone);
        addRegistry(lightTransmutation);
        addRegistry(liquidInteraction);
        addRegistry(perkTree);
        addRegistry(constellation);
        addRegistry(research);

    }
}
