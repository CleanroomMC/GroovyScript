package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.network.NetworkHandler;
import com.cleanroommc.groovyscript.network.SReloadScripts;
import com.cleanroommc.groovyscript.packmode.Packmode;
import com.cleanroommc.groovyscript.packmode.PackmodeSaveData;
import io.sommers.packmode.PackModeCommand;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import org.jetbrains.annotations.NotNull;

public class PackmodeCommand extends CommandBase {

    @Override
    public @NotNull String getName() {
        return "packmode";
    }

    @Override
    public @NotNull String getUsage(@NotNull ICommandSender sender) {
        return "packmode [mode]";
    }

    @Override
    public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, String @NotNull [] args) throws CommandException {
        if (GroovyScript.getRunConfig().isIntegratePackmodeMod()) {
            new PackModeCommand().execute(server, sender, args);
            return;
        }
        if (server.isDedicatedServer()) throw new CommandException("Can't change packmodes on the fly on dedicated servers!");
        if (!Packmode.needsPackmode()) throw new CommandException("Packmodes are not configured!");
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString("Current packmode is " + Packmode.getPackmode()));
        }
        String packmode = args[0];
        if (!Packmode.isValidPackmode(packmode)) throw new CommandException("Invalid packmode: " + packmode);
        SReloadScripts.updatePackmode(sender, packmode);
        PackmodeSaveData saveData = PackmodeSaveData.get(server);
        saveData.setPackmode(Packmode.getPackmode());
        NetworkHandler.sendToPlayer(new SReloadScripts(null, true, true), (EntityPlayerMP) sender);
    }
}
