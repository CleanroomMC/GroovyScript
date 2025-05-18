package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
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

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class Command extends NamedRegistry implements IScriptReloadable {

    private final List<ICommand> serverCommands = new ArrayList<>();
    private final AbstractReloadableStorage<ICommand> serverReloadableCommands = new AbstractReloadableStorage<>();
    private final AbstractReloadableStorage<ICommand> clientReloadableCommands = new AbstractReloadableStorage<>();
    private boolean serverStarted = false;

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.command.registerCommand0")
    public void registerCommand(ICommand command) {
        if (GroovyScript.getSandbox().isRunning() && GroovyScript.getSandbox().getCurrentLoader().isReloadable()) {
            this.serverReloadableCommands.addScripted(command);
        } else {
            this.serverCommands.add(command);
        }
        if (this.serverStarted) {
            forServer(commandHandler -> registerCommand(commandHandler, command));
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.command.registerClientCommand0")
    public void registerClientCommand(ICommand command) {
        if (FMLCommonHandler.instance().getSide().isServer()) return;

        if (registerCommand(ClientCommandHandler.instance, command) && GroovyScript.getSandbox().isRunning() && GroovyScript.getSandbox().getCurrentLoader().isReloadable()) {
            this.clientReloadableCommands.addScripted(command);
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.command.registerCommand1")
    public void registerCommand(String name, String usage, SimpleCommand.ICommand command) {
        registerCommand(new SimpleCommand(name, usage, command));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.command.registerCommand2", example = @Example("'groovy_test', { server, sender, args -> sender.sendMessage('Hello from GroovyScript')}"))
    public void registerCommand(String name, SimpleCommand.ICommand command) {
        registerCommand(new SimpleCommand(name, "/" + name, command));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.command.registerClientCommand1")
    public void registerClientCommand(String name, String usage, SimpleCommand.ICommand command) {
        registerClientCommand(new SimpleCommand(name, usage, command));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.command.registerClientCommand2")
    public void registerClientCommand(String name, SimpleCommand.ICommand command) {
        registerClientCommand(new SimpleCommand(name, "/" + name, command));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.command.registerCommand")
    public boolean registerCommand(CommandHandler handler, ICommand command) {
        if (handler.getCommands().containsKey(command.getName())) {
            GroovyLog.get().error("Error registering command '/{}', because a command with that name already exists", command.getName());
            return false;
        }
        for (String alias : command.getAliases()) {
            if (handler.getCommands().containsKey(alias)) {
                GroovyLog.get().error("Error registering command '/{}', because a command for the alias '/{}' already exists", command.getName(), alias);
                return false;
            }
        }
        handler.registerCommand(command);
        return true;
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
            registerCommand(commandHandler, command);
        }
        for (ICommand command : this.serverReloadableCommands.getScriptedRecipes()) {
            registerCommand(commandHandler, command);
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
