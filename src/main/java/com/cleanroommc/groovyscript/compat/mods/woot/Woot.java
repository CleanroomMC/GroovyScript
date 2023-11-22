package com.cleanroommc.groovyscript.compat.mods.woot;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class Woot extends ModPropertyContainer {

    public final StygianIronAnvil stygianIronAnvil = new StygianIronAnvil();
    public final Drops drops = new Drops();
    public final Spawning spawning = new Spawning();
    public final Policy policy = new Policy();
    public final MobConfig mobConfig = new MobConfig();

    public Woot() {
        addRegistry(stygianIronAnvil);
        addRegistry(drops);
        addRegistry(spawning);
        addRegistry(policy);
        addRegistry(mobConfig);
    }
}
