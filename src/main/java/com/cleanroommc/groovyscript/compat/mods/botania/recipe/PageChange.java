package com.cleanroommc.groovyscript.compat.mods.botania.recipe;

import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconPage;

public class PageChange {

    public LexiconPage page;
    public LexiconEntry parent;
    public int index;

    public PageChange(LexiconPage page, LexiconEntry parent, int index) {
        this.page = page;
        this.parent = parent;
        this.index = index;
    }
}
