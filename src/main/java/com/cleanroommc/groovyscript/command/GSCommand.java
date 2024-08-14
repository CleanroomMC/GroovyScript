package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.jei.JeiPlugin;
import com.cleanroommc.groovyscript.documentation.Documentation;
import com.cleanroommc.groovyscript.network.NetworkHandler;
import com.cleanroommc.groovyscript.network.SReloadScripts;
import com.cleanroommc.groovyscript.sandbox.LoadStage;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.server.command.CommandTreeBase;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class GSCommand extends CommandTreeBase {

    public static void runReload(EntityPlayerMP player, MinecraftServer server) {
        if (server.isDedicatedServer()) {
            player.sendMessage(new TextComponentString("Reloading in multiplayer is currently not allowed to avoid desync."));
            return;
        }
        GroovyLog.get().info("========== Reloading Groovy scripts ==========");
        long time = GroovyScript.runGroovyScriptsInLoader(LoadStage.POST_INIT);
        GroovyScript.postScriptRunResult(player, false, true, false, time);
        NetworkHandler.sendToPlayer(new SReloadScripts(null, false, true), player);
    }

    public GSCommand() {
        addSubcommand(new SimpleCommand("log", (server, sender, args) -> postLogFiles(sender)));

        addSubcommand(new SimpleCommand("reload", (server, sender, args) -> {
            if (sender instanceof EntityPlayerMP) {
                runReload((EntityPlayerMP) sender, server);
            }
        }));

        addSubcommand(new SimpleCommand("check", (server, sender, args) -> {
            if (sender instanceof EntityPlayerMP) {
                sender.sendMessage(new TextComponentString("Checking groovy syntax..."));
                long time = System.currentTimeMillis();
                GroovyScript.getSandbox().checkSyntax();
                time = System.currentTimeMillis() - time;
                sender.sendMessage(new TextComponentString("Checking syntax took " + time + "ms"));
                GroovyScript.postScriptRunResult((EntityPlayerMP) sender, false, false, false, time);
            }
        }));

        addSubcommand(new PackmodeCommand());

        addSubcommand(new InfoInfoCommand());
        addSubcommand(new InfoHandCommand());
        addSubcommand(new InfoLookingCommand());
        addSubcommand(new InfoSelfCommand());

        addSubcommand(new SimpleCommand("wiki", (server, sender, args) ->
                sender.sendMessage(new TextComponentString("GroovyScript wiki")
                                           .setStyle(new Style()
                                                             .setColor(TextFormatting.GOLD)
                                                             .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to open wiki in browser")))
                                                             .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://cleanroommc.com/groovy-script/"))))
                , "doc", "docs", "documentation"));

        addSubcommand(new SimpleCommand("generateWiki", (server, sender, args) -> {
            Documentation.generateWiki();
            sender.sendMessage(new TextComponentString("Generated a local version of the Groovyscript wiki has been generated to the ")
                                       .appendSibling(getTextForFile("Wiki Folder", Documentation.WIKI.toPath().toString(), new TextComponentString("Click to open the generated GroovyScript wiki folder"))));
        }, "generateDoc", "generateDocs", "generateDocumentation"));

        addSubcommand(new SimpleCommand("generateExamples", (server, sender, args) -> {
            Documentation.generateExamples();
            sender.sendMessage(new TextComponentString("Generated examples for the enabled Groovyscript compat to the ")
                                       .appendSibling(getTextForFile("Examples Folder", Documentation.EXAMPLES.toPath().toString(), new TextComponentString("Click to open the Groovyscript examples folder"))));
        }));

        addSubcommand(new SimpleCommand("creativeTabs", (server, sender, args) -> {
            GroovyLog.get().info("All creative tabs:");
            for (CreativeTabs tab : CreativeTabs.CREATIVE_TAB_ARRAY) {
                GroovyLog.get().getWriter().println(" - " + tab.getTabLabel());
            }
            sender.sendMessage(new TextComponentString("Creative tabs has been logged to the ")
                                       .appendSibling(GSCommand.getTextForFile("Groovy Log", GroovyLog.get().getLogFilerPath().toString(), new TextComponentString("Click to open GroovyScript log"))));
        }));

        addSubcommand(new SimpleCommand("deleteScriptCache", (server, sender, args) -> {
            if (GroovyScript.getSandbox().deleteScriptCache()) {
                sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Deleted groovy script cache"));
            } else {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "An error occurred while deleting groovy script cache"));
            }
        }));

        addSubcommand(new SimpleCommand("runLS", (server, sender, args) -> {
            if (GroovyScript.runLanguageServer()) {
                sender.sendMessage(new TextComponentString("Starting language server"));
            } else {
                sender.sendMessage(new TextComponentString("Language server is already running"));
            }
        }));

        if (ModSupport.MEKANISM.isLoaded()) {
            addSubcommand(new GSMekanismCommand());
        }
        if (ModSupport.JEI.isLoaded()) {
            addSubcommand(JeiPlugin.getJeiCategoriesCommand());
        }
    }

    @Override
    @Nonnull
    public String getName() {
        return "groovyscript";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return Arrays.asList("grs", "GroovyScript", "gs");
    }

    @Override
    @Nonnull
    public String getUsage(@NotNull ICommandSender sender) {
        return "/grs []";
    }

    public static void postLogFiles(ICommandSender sender) {
        sender.sendMessage(getTextForFile("Groovy Log", GroovyLog.get().getLogFilerPath().toString(), new TextComponentString("Click to open GroovyScript log")));
        sender.sendMessage(getTextForFile("Minecraft Log", GroovyLog.get().getLogFilerPath().getParent().toString() + File.separator + "latest.log", new TextComponentString("Click to open Minecraft log")));
    }

    public static ITextComponent getTextForFile(String name, String path, ITextComponent hoverText) {
        return new TextComponentString(TextFormatting.UNDERLINE + (TextFormatting.GOLD + name))
                .setStyle(new Style()
                                  .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path))
                                  .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)));
    }

}
