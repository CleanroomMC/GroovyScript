/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

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
