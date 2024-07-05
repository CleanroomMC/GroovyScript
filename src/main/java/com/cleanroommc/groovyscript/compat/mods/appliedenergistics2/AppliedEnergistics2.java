package com.cleanroommc.groovyscript.compat.mods.appliedenergistics2;

import appeng.api.config.TunnelType;
import com.cleanroommc.groovyscript.api.IObjectParser;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

import java.util.Arrays;
import java.util.Locale;

public class AppliedEnergistics2 extends GroovyPropertyContainer {

    public final Inscriber inscriber = new Inscriber();
    public final Grinder grinder = new Grinder();
    public final CannonAmmo cannonAmmo = new CannonAmmo();
    public final Spatial spatial = new Spatial();
    public final Attunement attunement = new Attunement();

    @Override
    public void initialize(GroovyContainer<?> container) {
        container.objectMapperBuilder("tunnel", TunnelType.class)
                .parser(IObjectParser.wrapEnum(TunnelType.class, false))
                .completerOfNamed(() -> Arrays.asList(TunnelType.values()), v -> v.name().toUpperCase(Locale.ROOT))
                .docOfType("P2P tunnel type")
                .register();
    }
}
