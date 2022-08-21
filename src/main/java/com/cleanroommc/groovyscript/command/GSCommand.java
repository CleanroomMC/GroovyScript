package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import com.google.common.base.Joiner;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.server.command.CommandTreeBase;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class GSCommand extends CommandTreeBase {

    public GSCommand() {
        addSubcommand(new RunScriptsCommand());
        addSubcommand(new SimpleCommand("log", (server, sender, args) -> {
            sender.sendMessage(new TextComponentString(TextFormatting.UNDERLINE + (TextFormatting.GOLD + "Groovy Log"))
                    .setStyle(new Style()
                            .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, GroovyLog.LOG.getPath().toString()))
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to open GroovyScript log")))));
            sender.sendMessage(new TextComponentString(TextFormatting.UNDERLINE + (TextFormatting.GOLD + "Minecraft Log"))
                    .setStyle(new Style()
                            .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, GroovyLog.LOG.getPath().getParent().toString() + "/logs/latest.log"))
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to open Minecraft log")))));
        }));
        addSubcommand(new SimpleCommand("hand", (server, sender, args) -> {
            if (sender instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) sender;
                ItemStack item = player.getHeldItem(EnumHand.MAIN_HAND);
                if (item.isEmpty()) item = player.getHeldItem(EnumHand.OFF_HAND);
                if (!item.isEmpty()) {
                    String s = IngredientHelper.toIIngredient(item).asGroovyCode();
                    sender.sendMessage(new TextComponentString(s));
                    GuiScreen.setClipboardString(s);
                } else {
                    // send info about the block the player is looking at
                }
            }
        }));
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            if (args[0].equals("copy")) {
                GuiScreen.setClipboardString(Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length)));
                return;
            }
        }
        super.execute(server, sender, args);
    }

    @Override
    @Nonnull
    public String getName() {
        return "groovyscript";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return Arrays.asList("gs", "GroovyScript");
    }

    @Override
    @Nonnull
    public String getUsage(ICommandSender sender) {
        return "/gs []";
    }
}
