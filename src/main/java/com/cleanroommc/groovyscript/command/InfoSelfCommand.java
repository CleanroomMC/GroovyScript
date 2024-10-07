package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;

public class InfoSelfCommand extends BaseInfoCommand {

    @Override
    public @NotNull String getName() {
        return "self";
    }

    @Override
    protected String targetDescription() {
        return "anything targeting yourself";
    }

    @Override
    void gatherInfo(InfoParserPackage info, EntityPlayer player) {
        info.setEntity(player);
    }
}
