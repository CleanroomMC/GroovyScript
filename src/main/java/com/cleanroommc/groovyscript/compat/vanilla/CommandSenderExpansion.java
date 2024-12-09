package com.cleanroommc.groovyscript.compat.vanilla;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

public class CommandSenderExpansion {

    public static boolean isPlayer(ICommandSender commandSender) {
        return commandSender instanceof EntityPlayer;
    }

    public static void sendMessage(ICommandSender commandSender, String msg) {
        commandSender.sendMessage(new TextComponentString(msg));
    }
}
