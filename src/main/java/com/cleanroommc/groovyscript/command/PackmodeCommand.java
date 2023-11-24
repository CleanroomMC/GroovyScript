package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.Packmode;
import com.cleanroommc.groovyscript.network.IPacket;
import com.cleanroommc.groovyscript.network.NetworkHandler;
import com.cleanroommc.groovyscript.network.SReloadJei;
import com.cleanroommc.groovyscript.sandbox.LoadStage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
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
        if (args.length == 0) throw new CommandException("Missing packmode");
        String packmode = args[0];
        if (!Packmode.isValidPackmode(packmode)) throw new CommandException("Invalid packmode: " + packmode);
        Packmode.updatePackmode(packmode);
        sender.sendMessage(new TextComponentString("Changing packmode to " + packmode + ". This might take a minute."));
        long time = GroovyScript.runGroovyScriptsInLoader(LoadStage.POST_INIT);
        GroovyScript.postScriptRunResult((EntityPlayerMP) sender, false, true, true, time);
        NetworkHandler.sendToPlayer(new SReloadJei(), (EntityPlayerMP) sender);
        MinecraftForge.EVENT_BUS.post(new Packmode.ChangeEvent());
    }
}
