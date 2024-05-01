package com.cleanroommc.groovyscript.compat.mods.naturesaura;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class NaturesAura extends ModPropertyContainer {

    public final Altar altar = new Altar();
    public final Ritual ritual = new Ritual();
    public final Offering offering = new Offering();
    public final Spawning spawning = new Spawning();

    public NaturesAura() {
        addRegistry(altar);
        addRegistry(ritual);
        addRegistry(offering);
        addRegistry(spawning);
    }

}
