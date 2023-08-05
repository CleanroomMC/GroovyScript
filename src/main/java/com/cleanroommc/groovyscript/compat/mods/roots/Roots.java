package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.brackets.BracketHandlerManager;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import epicsquid.roots.init.HerbRegistry;
import epicsquid.roots.modifiers.CostType;
import epicsquid.roots.modifiers.ModifierRegistry;
import epicsquid.roots.ritual.RitualRegistry;
import epicsquid.roots.spell.SpellRegistry;
import net.minecraft.util.ResourceLocation;

import java.util.Locale;

public class Roots extends ModPropertyContainer {

    public final AnimalHarvest animalHarvest = new AnimalHarvest();
    public final AnimalHarvestFish animalHarvestFish = new AnimalHarvestFish();
    public final BarkCarving barkCarving = new BarkCarving();
    public final Chrysopoeia chrysopoeia = new Chrysopoeia();
    public final FeyCrafter feyCrafter = new FeyCrafter();
    public final FlowerGeneration flowerGeneration = new FlowerGeneration();
    public final LifeEssence lifeEssence = new LifeEssence();
    public final Modifiers modifiers = new Modifiers();
    public final Moss moss = new Moss();
    public final Mortar mortar = new Mortar();
    public final Pacifist pacifist = new Pacifist();
    public final Predicates predicates = new Predicates();
    public final Pyre pyre = new Pyre();
    public final Rituals rituals = new Rituals();
    public final RunicShearBlock runicShearBlock = new RunicShearBlock();
    public final RunicShearEntity runicShearEntity = new RunicShearEntity();
    public final Spells spells = new Spells();
    public final SummonCreature summonCreature = new SummonCreature();
    public final Transmutation transmutation = new Transmutation();


    public Roots() {
        addRegistry(animalHarvest);
        addRegistry(animalHarvestFish);
        addRegistry(barkCarving);
        addRegistry(chrysopoeia);
        addRegistry(feyCrafter);
        addRegistry(flowerGeneration);
        addRegistry(lifeEssence);
        addRegistry(modifiers);
        addRegistry(moss);
        addRegistry(mortar);
        addRegistry(pacifist);
        addRegistry(pyre);
        addRegistry(predicates);
        addRegistry(rituals);
        addRegistry(runicShearBlock);
        addRegistry(runicShearEntity);
        addRegistry(spells);
        addRegistry(summonCreature);
        addRegistry(transmutation);
    }

    @Override
    public void initialize() {
        BracketHandlerManager.registerBracketHandler("ritual", RitualRegistry::getRitual);
        BracketHandlerManager.registerBracketHandler("herb", HerbRegistry::getHerbByName);
        BracketHandlerManager.registerBracketHandler("cost", s -> CostType.valueOf(s.toUpperCase(Locale.ROOT)));
        BracketHandlerManager.registerBracketHandler("spell", s -> {
            if (s.contains(":")) return SpellRegistry.getSpell(new ResourceLocation(s));
            if (s.startsWith("spell_")) return SpellRegistry.getSpell(s);
            return SpellRegistry.getSpell("spell_" + s);
        });
        BracketHandlerManager.registerBracketHandler("modifier", s -> ModifierRegistry.get(new ResourceLocation(s)));
    }
}
