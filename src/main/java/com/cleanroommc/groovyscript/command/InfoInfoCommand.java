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
        info.setStack(player.getHeldItem(EnumHand.MAIN_HAND));
        if (info.getStack().isEmpty()) info.setStack(player.getHeldItem(EnumHand.OFF_HAND));

        // if there's nothing in the player's hands, get the entity being looked at and then the block position
        // because entity should be preferred
        if (info.getStack().isEmpty()) {
            info.setEntity(RayTracingHelper.getEntityLookingAt(player));
            if (info.getEntity() == null) {
                info.copyFromPos(RayTracingHelper.getBlockLookingAt(player));
                if (info.getPos() == null) {
                    info.setEntity(player);
                }
            }
        }
    }
}
