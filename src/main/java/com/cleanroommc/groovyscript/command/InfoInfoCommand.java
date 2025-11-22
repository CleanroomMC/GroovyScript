package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.helper.RayTracingHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import org.jetbrains.annotations.NotNull;

public class InfoInfoCommand extends BaseInfoCommand {

    @Override
    public @NotNull String getName() {
        return "info";
    }

    @Override
    protected String targetDescription() {
        return "anything in your hands or being looked at";
    }

    @Override
    void gatherInfo(InfoParserPackage info, EntityPlayer player) {
        info.copyFromPlayer(player);
    }
}
