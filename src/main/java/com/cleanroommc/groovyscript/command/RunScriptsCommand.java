package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.GroovyScript;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.io.IOException;

public class RunScriptsCommand extends CommandBase {

    @Override
    public String getName() {
        return "run";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/gs run";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        try {
            GroovyScript.runScript();
            sender.sendMessage(new TextComponentString("Successfully ran scripts"));
        } catch (IOException | ScriptException | ResourceException e) {
            e.printStackTrace();
            sender.sendMessage(new TextComponentString("Error executing scripts!"));
        }
    }
}
