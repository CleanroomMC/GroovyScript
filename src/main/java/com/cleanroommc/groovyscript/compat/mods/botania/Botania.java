package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.IObjectParser;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.LexiconCategory;
import vazkii.botania.api.lexicon.LexiconEntry;

public class Botania extends GroovyPropertyContainer {

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

    public static String asGroovyCode(vazkii.botania.api.brew.Brew entry, boolean colored) {
        return GroovyScriptCodeConverter.formatGenericHandler("brew", entry.getKey(), colored);
    }

    public Botania() {
        addProperty(lexicon.category);
        addProperty(lexicon.entry);
        addProperty(lexicon.page);
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
    public void initialize(GroovyContainer<?> container) {
        container.objectMapperBuilder("brew", vazkii.botania.api.brew.Brew.class)
                .parser(IObjectParser.wrapStringGetter(val -> BotaniaAPI.brewMap.get(val), false))
                .completerOfNames(() -> BotaniaAPI.brewMap.keySet())
                .defaultValue(() -> BotaniaAPI.fallbackBrew)
                .docOfType("brew")
                .register();
    }
}
