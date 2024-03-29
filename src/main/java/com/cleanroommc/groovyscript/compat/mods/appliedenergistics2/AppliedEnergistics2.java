package com.cleanroommc.groovyscript.compat.mods.appliedenergistics2;

import appeng.api.config.TunnelType;
import com.cleanroommc.groovyscript.api.IGameObjectParser;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.gameobjects.GameObjectHandler;

import java.util.Arrays;
import java.util.Locale;

public class AppliedEnergistics2 extends ModPropertyContainer {

    public final Inscriber inscriber = new Inscriber();
    public final Grinder grinder = new Grinder();
    public final CannonAmmo cannonAmmo = new CannonAmmo();
    public final Spatial spatial = new Spatial();
    public final Attunement attunement = new Attunement();

    public AppliedEnergistics2() {
        addRegistry(inscriber);
        addRegistry(grinder);
        addRegistry(cannonAmmo);
        addRegistry(spatial);
        addRegistry(attunement);
    }

    @Override
    public void initialize() {
        GameObjectHandler.builder("tunnel", TunnelType.class)
                .mod("appliedenergistics2")
                .parser(IGameObjectParser.wrapEnum(TunnelType.class, false))
                .completerOfNamed(() -> Arrays.asList(TunnelType.values()), v -> v.name().toUpperCase(Locale.ROOT))
                .register();
    }
}
