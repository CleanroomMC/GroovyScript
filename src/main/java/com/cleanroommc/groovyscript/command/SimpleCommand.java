package com.cleanroommc.groovyscript.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

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
        this(name, "", command);
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull String getUsage(@NotNull ICommandSender sender) {
        return usage;
    }

    @Override
    public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, String @NotNull [] args) throws CommandException {
        command.execute(server, sender, args);
    }

    public interface ICommand {
        void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException;
    }
}
