package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;

public class InfoLookingCommand extends BaseInfoCommand {

    @Override
    public @NotNull String getName() {
        return "looking";
    }

    @Override
    protected String targetDescription() {
        return "anything being looked at";
    }

    @Override
    void gatherInfo(InfoParserPackage info, EntityPlayer player) {
        // get the entity being looked at and then the block position because entity should be preferred
        info.setEntity(getEntityLookingAt(player));
        if (info.getEntity() == null) {
            info.copyFromPos(getBlockLookingAt(player));
        }
    }
}
