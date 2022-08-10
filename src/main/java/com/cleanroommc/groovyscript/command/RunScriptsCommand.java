package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.network.NetworkHandler;
import com.cleanroommc.groovyscript.network.SReloadJei;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import com.cleanroommc.groovyscript.sandbox.SandboxRunner;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;

public class RunScriptsCommand extends CommandBase {

    @Override
    @Nonnull
    public String getName() {
        return "run";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/gs run";
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        GroovyLog.LOG.info("========== Reloading Groovy scripts ==========");
        Throwable throwable = SandboxRunner.run();
        if (throwable == null) {
            sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully ran scripts"));
            NetworkHandler.sendToPlayer(new SReloadJei(), (EntityPlayerMP) sender);
        } else {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Error executing scripts:"));
            sender.sendMessage(new TextComponentString(TextFormatting.RED + throwable.getMessage()));
            server.commandManager.executeCommand(sender, "/gs log");
        }
    }
}
