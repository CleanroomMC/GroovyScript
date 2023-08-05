package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.compat.content.Content;
import com.cleanroommc.groovyscript.compat.loot.Loot;
import com.cleanroommc.groovyscript.sandbox.expand.ExpansionHelper;
import net.minecraft.item.ItemStack;

public class VanillaModule implements IScriptReloadable {

    public static final Crafting crafting = new Crafting();
    public static final Furnace furnace = new Furnace();
    public static final Loot loot = new Loot();
    public static final OreDict oreDict = new OreDict();
    public static final Player player = new Player();
    public static final Content content = new Content();
    public static final Rarity rarity = new Rarity();

    public static void initializeBinding() {
        GroovyScript.getSandbox().registerBinding("Crafting", crafting);
        GroovyScript.getSandbox().registerBinding("Furnace", furnace);
        GroovyScript.getSandbox().registerBinding("Loot", loot);
        GroovyScript.getSandbox().registerBinding("OreDict", oreDict);
        GroovyScript.getSandbox().registerBinding("OreDictionary", oreDict);
        GroovyScript.getSandbox().registerBinding("Player", player);
        GroovyScript.getSandbox().registerBinding("Content", content);
        GroovyScript.getSandbox().registerBinding("Rarity", rarity);

        ExpansionHelper.mixinClass(ItemStack.class, RarityItemStackExpansion.class);
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        furnace.onReload();
        oreDict.onReload();
        rarity.onReload();
        player.onReload();
    }

    @Override
    @GroovyBlacklist
    public void afterScriptLoad() {
        furnace.afterScriptLoad();
    }

}
