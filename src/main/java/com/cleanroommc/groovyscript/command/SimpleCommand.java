package com.cleanroommc.groovyscript.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class SimpleCommand extends CommandBase {

    private final String name;
    private final String usage;
    private final ICommand command;

    public SimpleCommand(String name, String usage, ICommand command) {
        this.name = name;
        this.usage = usage;
        this.command = command;
    }

    public SimpleCommand(String name, ICommand command) {
        this(name, "/gs " + name, command);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return usage;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        command.execute(server, sender, args);
    }

    public interface ICommand {
        void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException;
    }
}
