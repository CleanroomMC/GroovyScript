package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.IGameObjectParser;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.gameobjects.GameObjectHandler;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.LexiconCategory;
import vazkii.botania.api.lexicon.LexiconEntry;

public class Botania extends ModPropertyContainer {

    public final ElvenTrade elvenTrade = new ElvenTrade();
    public final ManaInfusion manaInfusion = new ManaInfusion();
    public final PureDaisy pureDaisy = new PureDaisy();
    public final Apothecary apothecary = new Apothecary();
    public final Orechid orechid = new Orechid();
    public final OrechidIgnem orechidIgnem = new OrechidIgnem();
    public final RuneAltar runeAltar = new RuneAltar();
    public final Brew brew = new Brew();
    public final BrewRecipe brewRecipe = new BrewRecipe();
    public final Lexicon lexicon = new Lexicon();
    public final Knowledge knowledge = new Knowledge();
    public final Magnet magnet = new Magnet();
    public final Flowers flowers = new Flowers();

    public Botania() {
        addRegistry(elvenTrade);
        addRegistry(manaInfusion);
        addRegistry(pureDaisy);
        addRegistry(apothecary);
        addRegistry(orechid);
        addRegistry(orechidIgnem);
        addRegistry(runeAltar);
        addRegistry(brew);
        addRegistry(brewRecipe);
        addRegistry(lexicon.category);
        addRegistry(lexicon.entry);
        addRegistry(lexicon.page);
        addRegistry(knowledge);
        addRegistry(magnet);
    }

    public static LexiconCategory getCategory(String name) {
        for (LexiconCategory category : BotaniaAPI.getAllCategories())
            if (category.getUnlocalizedName().equals(name)) return category;
        return null;
    }

    public static LexiconEntry getEntry(String name) {
        for (LexiconEntry entry : BotaniaAPI.getAllEntries())
            if (entry.getUnlocalizedName().equals(name)) return entry;
        return null;
    }

    // using BotaniaAPI.brewMap::get crashes
    @SuppressWarnings("Convert2MethodRef")
    @Override
    public void initialize() {
        GameObjectHandler.builder("brew", vazkii.botania.api.brew.Brew.class)
                .mod("botania")
                .parser(IGameObjectParser.wrapStringGetter(val -> BotaniaAPI.brewMap.get(val), false))
                .completerOfNames(() -> BotaniaAPI.brewMap.keySet())
                .defaultValue(() -> BotaniaAPI.fallbackBrew)
                .register();
    }
}
