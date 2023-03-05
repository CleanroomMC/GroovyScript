package com.cleanroommc.groovyscript.compat.mods.tcomplement;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class TinkersComplement extends ModPropertyContainer {
    public final Melter melter = new Melter();
    public final HighOven highOven = new HighOven();
    public TinkersComplement() {
        addRegistry(melter);
        addRegistry(highOven);
        addRegistry(highOven.fuel);
        addRegistry(highOven.heating);
        addRegistry(highOven.mixing);
    }
}
