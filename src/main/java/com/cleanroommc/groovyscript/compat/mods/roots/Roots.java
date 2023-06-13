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

    AnimalHarvest animalHarvest = new AnimalHarvest();
    AnimalHarvestFish animalHarvestFish = new AnimalHarvestFish();
    BarkCarving barkCarving = new BarkCarving();
    Chrysopoeia chrysopoeia = new Chrysopoeia();
    FeyCrafter feyCrafter = new FeyCrafter();
    FlowerGeneration flowerGeneration = new FlowerGeneration();
    LifeEssence lifeEssence = new LifeEssence();
    Modifiers modifiers = new Modifiers();
    Moss moss = new Moss();
    Mortar mortar = new Mortar();
    Pacifist pacifist = new Pacifist();
    Predicates predicates = new Predicates();
    Pyre pyre = new Pyre();
    Rituals rituals = new Rituals();
    RunicShearBlock runicShearBlock = new RunicShearBlock();
    RunicShearEntity runicShearEntity = new RunicShearEntity();
    Spells spells = new Spells();
    SummonCreature summonCreature = new SummonCreature();
    Transmutation transmutation = new Transmutation();


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
