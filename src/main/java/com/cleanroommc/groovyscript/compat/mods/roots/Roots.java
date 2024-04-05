package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.IGameObjectParser;
import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.gameobjects.GameObjectHandlers;
import epicsquid.roots.api.Herb;
import epicsquid.roots.init.HerbRegistry;
import epicsquid.roots.modifiers.CostType;
import epicsquid.roots.modifiers.Modifier;
import epicsquid.roots.modifiers.ModifierRegistry;
import epicsquid.roots.ritual.RitualBase;
import epicsquid.roots.ritual.RitualRegistry;
import epicsquid.roots.spell.FakeSpell;
import epicsquid.roots.spell.SpellBase;
import epicsquid.roots.spell.SpellRegistry;
import net.minecraft.util.ResourceLocation;

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

    @Override
    public void initialize(GroovyContainer<?> container) {
        container.gameObjectHandlerBuilder("ritual", RitualBase.class)
                .parser(IGameObjectParser.wrapStringGetter(RitualRegistry::getRitual))
                .completerOfNames(() -> RitualRegistry.ritualRegistry.keySet())
                .docOfType("ritual")
                .register();
        container.gameObjectHandlerBuilder("herb", Herb.class)
                .parser(IGameObjectParser.wrapStringGetter(HerbRegistry::getHerbByName))
                .completerOfNames(HerbRegistry.registry::keySet)
                .docOfType("herb")
                .register();
        container.gameObjectHandlerBuilder("cost", CostType.class)
                .parser(IGameObjectParser.wrapEnum(CostType.class, false))
                .completerOfEnum(CostType.class, false)
                .docOfType("cost")
                .register();
        container.gameObjectHandlerBuilder("spell", SpellBase.class)
                .parser(Roots::getSpell)
                .completer(SpellRegistry.spellRegistry::keySet)
                .defaultValueSup(() -> Result.some(FakeSpell.INSTANCE))  // crashes otherwise
                .docOfType("spell")
                .register();
        container.gameObjectHandlerBuilder("modifier", Modifier.class)
                .parser(Roots::getModifier)
                .completerOfNamed(ModifierRegistry::getModifiers, v -> v.getRegistryName().toString())
                .docOfType("modifier")
                .register();
    }

    private static Result<SpellBase> getSpell(String s, Object... args) {
        if (s.contains(":")) {
            Result<ResourceLocation> rl = GameObjectHandlers.parseResourceLocation(s, args);
            if (rl.hasError()) return Result.error(rl.getError());
            SpellBase spell = SpellRegistry.getSpell(rl.getValue());
            return spell == null ? Result.error() : Result.some(spell);
        }
        if (!s.startsWith("spell_")) {
            s = "spell_" + s;
        }
        SpellBase spell = SpellRegistry.getSpell(s);
        return spell == null ? Result.error() : Result.some(spell);
    }

    private static Result<Modifier> getModifier(String s, Object... args) {
        Result<ResourceLocation> rl = GameObjectHandlers.parseResourceLocation(s, args);
        if (rl.hasError()) return Result.error(rl.getError());
        Modifier modifier = ModifierRegistry.get(rl.getValue());
        return modifier == null ? Result.error() : Result.some(modifier);
    }
}
