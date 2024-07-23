package com.cleanroommc.groovyscript.compat.mods.cyclic;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

public class Cyclic extends GroovyPropertyContainer {

    public final Dehydrator dehydrator = new Dehydrator();
    public final Hydrator hydrator = new Hydrator();
    public final Melter melter = new Melter();
    public final Packager packager = new Packager();
    public final Solidifier solidifier = new Solidifier();

}
