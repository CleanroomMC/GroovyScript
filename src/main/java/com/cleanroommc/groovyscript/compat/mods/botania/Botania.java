package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.IGameObjectHandler;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.gameobjects.GameObjectHandlerManager;
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

    @Override
    public void initialize() {
        GameObjectHandlerManager.registerGameObjectHandler("botania", "brew", IGameObjectHandler.wrapStringGetter(BotaniaAPI.brewMap::get, false), BotaniaAPI.fallbackBrew);
    }
}
