package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class Bewitchment extends ModPropertyContainer {
    // Forge Registries
    public static final Oven oven = new Oven();
    public static final Distillery distillery = new Distillery();
    public static final SpinningWheel spinningWheel = new SpinningWheel();
    public static final Frostfire frostfire = new Frostfire();
    public static final Ritual ritual = new Ritual();
    public static final Cauldron cauldron = new Cauldron();
    public static final Brew brew = new Brew();
    public static final Incense incense = new Incense();
    public static final Fortune fortune = new Fortune();
    public static final Tarot tarot = new Tarot();
    public static final Curse curse = new Curse();
    public static final Sigil sigil = new Sigil();
    // Virtualized Registries
    public static final AltarUpgrade altarUpgrade = new AltarUpgrade();
    public static final AthameLoot athameLoot = new AthameLoot();
    public static final Pet pet = new Pet();

    public Bewitchment() {
        addRegistry(oven);
        addRegistry(distillery);
        addRegistry(spinningWheel);
        addRegistry(frostfire);
        addRegistry(ritual);
        addRegistry(cauldron);
        addRegistry(brew);
        addRegistry(incense);
        addRegistry(fortune);
        addRegistry(tarot);
        addRegistry(curse);
        addRegistry(sigil);
        addRegistry(altarUpgrade);
        addRegistry(athameLoot);
        addRegistry(pet);
    }
}
