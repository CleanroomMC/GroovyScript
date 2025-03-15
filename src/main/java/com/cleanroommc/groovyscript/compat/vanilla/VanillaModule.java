package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.compat.content.Content;
import com.cleanroommc.groovyscript.compat.inworldcrafting.InWorldCrafting;
import com.cleanroommc.groovyscript.compat.loot.Loot;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.sandbox.expand.ExpansionHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;

public class VanillaModule extends GroovyPropertyContainer {

    public final Crafting crafting = new Crafting();
    public final Furnace furnace = new Furnace();
    public final Loot loot = new Loot();
    public final OreDict oreDict = new OreDict();
    public final Player player = new Player();
    public final Content content = new Content();
    public final Rarity rarity = new Rarity();
    public final InWorldCrafting inWorldCrafting = new InWorldCrafting();
    public final Command command = new Command();
    public final GameRule gameRule = new GameRule();

    @Override
    public void initialize(GroovyContainer<?> owner) {
        GroovyScript.getSandbox().registerBinding("Minecraft", this);
        // maybe remove some of these as globals?
        GroovyScript.getSandbox().registerBinding(crafting);
        GroovyScript.getSandbox().registerBinding(furnace);
        GroovyScript.getSandbox().registerBinding(loot);
        GroovyScript.getSandbox().registerBinding(oreDict);
        GroovyScript.getSandbox().registerBinding(player);
        GroovyScript.getSandbox().registerBinding(content);
        GroovyScript.getSandbox().registerBinding(rarity);
        GroovyScript.getSandbox().registerBinding(inWorldCrafting);
        GroovyScript.getSandbox().registerBinding(command);
        GroovyScript.getSandbox().registerBinding(gameRule);

        ExpansionHelper.mixinClass(ItemStack.class, ItemStackExpansion.class);
        ExpansionHelper.mixinClass(ICommandSender.class, CommandSenderExpansion.class);
    }
}
