package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import net.minecraft.command.ICommandSender;
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
