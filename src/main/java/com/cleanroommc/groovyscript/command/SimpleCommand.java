package com.cleanroommc.groovyscript.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleCommand extends CommandBase {

    private final String name;
    private final String usage;
    private final ICommand command;
    private final List<String> aliases = new ArrayList<>();

    public SimpleCommand(String name, String usage, ICommand command, String... aliases) {
        this.name = name;
        this.usage = usage;
        this.command = command;
        Collections.addAll(this.aliases, aliases);
    }

    public SimpleCommand(String name, ICommand command, String... aliases) {
        this(name, "/gs " + name, command, aliases);
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

    @Override
    public @NotNull List<String> getAliases() {
        return aliases;
    }

}
