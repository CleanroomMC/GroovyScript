package com.cleanroommc.groovyscript.compat.mods.exnihilo;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class ExNihilo extends ModPropertyContainer {

    public final Sieve sieve = new Sieve();

    public ExNihilo() {
        addRegistry(sieve);
    }
}
