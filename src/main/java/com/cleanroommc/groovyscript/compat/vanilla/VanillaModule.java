package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.compat.content.Content;
import com.cleanroommc.groovyscript.compat.inworldcrafting.InWorldCrafting;
import com.cleanroommc.groovyscript.compat.loot.Loot;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.sandbox.expand.ExpansionHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class VanillaModule extends GroovyPropertyContainer {

    public static final VanillaModule INSTANCE = new VanillaModule();

    public static final Crafting crafting = new Crafting();
    public static final Furnace furnace = new Furnace();
    public static final Loot loot = new Loot();
    public static final OreDict oreDict = new OreDict();
    public static final Player player = new Player();
    public static final Content content = new Content();
    public static final Rarity rarity = new Rarity();
    public static final InWorldCrafting inWorldCrafting = new InWorldCrafting();
    public static final Command command = new Command();
    public static final GameRule gameRule = new GameRule();

    private final List<INamed> globalBindings = new ArrayList<>();

    private VanillaModule() {
        // maybe remove some of these as globals?
        globalBindings.add(crafting);
        globalBindings.add(furnace);
        globalBindings.add(loot);
        globalBindings.add(oreDict);
        globalBindings.add(player);
        globalBindings.add(content);
        globalBindings.add(rarity);
        globalBindings.add(inWorldCrafting);
        globalBindings.add(command);
        globalBindings.add(gameRule);
    }

    public void addProperty(INamed property, boolean addGlobalBinding) {
        addProperty(property);
        globalBindings.add(property);
    }

    @Override
    public void addProperty(INamed property) {
        super.addProperty(property);
    }

    @Override
    public void initialize(GroovyContainer<?> owner) {
        GroovyScript.getSandbox().registerBinding("Minecraft", this);
        GroovyScript.getSandbox().registerBinding("Vanilla", this);

        for (INamed registry : globalBindings) {
            GroovyScript.getSandbox().registerBinding(registry);
        }

        ExpansionHelper.mixinClass(ItemStack.class, ItemStackExpansion.class);
        ExpansionHelper.mixinClass(ICommandSender.class, CommandSenderExpansion.class);
    }
}
