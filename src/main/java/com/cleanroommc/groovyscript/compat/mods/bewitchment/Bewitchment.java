package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

public class Bewitchment extends GroovyPropertyContainer {

    // Forge Registries
    public final Oven oven = new Oven();
    public final Distillery distillery = new Distillery();
    public final SpinningWheel spinningWheel = new SpinningWheel();
    public final Frostfire frostfire = new Frostfire();
    public final Ritual ritual = new Ritual();
    public final Cauldron cauldron = new Cauldron();
    public final Brew brew = new Brew();
    public final Incense incense = new Incense();
    public final Fortune fortune = new Fortune();
    public final Tarot tarot = new Tarot();
    public final Curse curse = new Curse();
    public final Sigil sigil = new Sigil();
    // Virtualized Registries
    public final AltarUpgrade altarUpgrade = new AltarUpgrade();
    public final AthameLoot athameLoot = new AthameLoot();
    public final Pet pet = new Pet();

}
