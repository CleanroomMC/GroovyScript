package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.command.SimpleCommand;
import com.cleanroommc.groovyscript.core.mixin.CommandHandlerAccessor;
import com.cleanroommc.groovyscript.registry.AbstractReloadableStorage;
import com.cleanroommc.groovyscript.registry.NamedRegistry;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class Command extends NamedRegistry implements IScriptReloadable {

    private final List<ICommand> serverCommands = new ArrayList<>();
    private final AbstractReloadableStorage<ICommand> serverReloadableCommands = new AbstractReloadableStorage<>();
    private final AbstractReloadableStorage<ICommand> clientReloadableCommands = new AbstractReloadableStorage<>();
    private boolean serverStarted = false;

    public void registerCommand(ICommand command) {
        if (GroovyScript.getSandbox().isRunning() && GroovyScript.getSandbox().getCurrentLoader().isReloadable()) {
            this.serverReloadableCommands.addScripted(command);
        } else {
            this.serverCommands.add(command);
        }
        if (this.serverStarted) {
            forServer(commandHandler -> commandHandler.registerCommand(command));
        }
    }

    public void registerClientCommand(ICommand command) {
        if (FMLCommonHandler.instance().getSide().isServer()) return;
        ClientCommandHandler.instance.registerCommand(command);
        if (GroovyScript.getSandbox().isRunning() && GroovyScript.getSandbox().getCurrentLoader().isReloadable()) {
            this.clientReloadableCommands.addScripted(command);
        }
    }

    public void registerCommand(String name, String usage, SimpleCommand.ICommand command) {
        registerCommand(new SimpleCommand(name, usage, command));
    }

    public void registerCommand(String name, SimpleCommand.ICommand command) {
        registerCommand(new SimpleCommand(name, "/" + name, command));
    }

    public void registerClientCommand(String name, String usage, SimpleCommand.ICommand command) {
        registerClientCommand(new SimpleCommand(name, usage, command));
    }

    public void registerClientCommand(String name, SimpleCommand.ICommand command) {
        registerClientCommand(new SimpleCommand(name, "/" + name, command));
    }

    @GroovyBlacklist
    public void removeCommand(CommandHandler commandHandler, ICommand command) {
        Set<ICommand> commands = ((CommandHandlerAccessor) commandHandler).getCommandSet();
        if (commands.remove(command)) {
            commandHandler.getCommands().entrySet().removeIf(entry -> Objects.equals(command, entry.getValue()));
        }
    }

    @GroovyBlacklist
    public void onStartServer(MinecraftServer server) {
        this.serverStarted = true;
        CommandHandler commandHandler = (CommandHandler) server.getCommandManager();
        for (ICommand command : this.serverCommands) {
            commandHandler.registerCommand(command);
        }
        for (ICommand command : this.serverReloadableCommands.getScriptedRecipes()) {
            commandHandler.registerCommand(command);
        }
    }

    @GroovyBlacklist
    public void onReload() {
        this.clientReloadableCommands.removeScripted().forEach(c -> removeCommand(ClientCommandHandler.instance, c));
        forServer(commandHandler -> this.serverReloadableCommands.removeScripted().forEach(c -> removeCommand(commandHandler, c)));
    }

    @Override
    public void afterScriptLoad() {}

    private void forServer(Consumer<CommandHandler> consumer) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null) {
            consumer.accept((CommandHandler) server.getCommandManager());
        }
    }
}
