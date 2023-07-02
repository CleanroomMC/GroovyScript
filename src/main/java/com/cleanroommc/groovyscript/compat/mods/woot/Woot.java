package com.cleanroommc.groovyscript.compat.mods.woot;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class Woot extends ModPropertyContainer {

    StygianIronAnvil stygianIronAnvil = new StygianIronAnvil();
    Drops drops = new Drops();
    Spawning spawning = new Spawning();
    Policy policy = new Policy();
    MobConfig mobConfig = new MobConfig();

    public Woot() {
        addRegistry(stygianIronAnvil);
        addRegistry(drops);
        addRegistry(spawning);
        addRegistry(policy);
        addRegistry(mobConfig);
    }
}
