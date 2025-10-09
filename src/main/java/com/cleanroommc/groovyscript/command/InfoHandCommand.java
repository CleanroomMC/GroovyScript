package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import org.jetbrains.annotations.NotNull;

public class InfoHandCommand extends BaseInfoCommand {

    @Override
    public @NotNull String getName() {
        return "hand";
    }

    @Override
    protected String targetDescription() {
        return "anything in your hands";
    }

    @Override
    void gatherInfo(InfoParserPackage info, EntityPlayer player) {
        info.setStack(player.getHeldItem(EnumHand.MAIN_HAND));
        if (info.getStack().isEmpty()) info.setStack(player.getHeldItem(EnumHand.OFF_HAND));
    }
}
