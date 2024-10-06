package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.api.infocommand.InfoParserRegistry;
import com.cleanroommc.groovyscript.event.GsHandEvent;
import com.google.common.base.Predicates;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseInfoCommand extends CommandBase {

    /**
     * gets the block being looked at, stopping on fluid blocks
     */
    protected static BlockPos getBlockLookingAt(EntityPlayer player) {
        double distance = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        Vec3d eyes = player.getPositionEyes(0.0F);
        Vec3d look = player.getLook(0.0F);
        Vec3d end = eyes.add(look.x * distance, look.y * distance, look.z * distance);

        RayTraceResult result = player.getEntityWorld().rayTraceBlocks(eyes, end, true);
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            return result.getBlockPos();
        }
        return null;
    }

    /**
     * gets the closest entity being looked at
     */
    protected static Entity getEntityLookingAt(EntityPlayer player) {
        Entity entity = null;
        double d0 = 0.0D;

        double distance = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        Vec3d eyes = player.getPositionEyes(0.0F);
        Vec3d look = player.getLook(0.0F);
        Vec3d end = eyes.add(look.x * distance, look.y * distance, look.z * distance);

        List<Entity> list = player.world.getEntitiesInAABBexcluding(
                player,
                player.getEntityBoundingBox()
                        .expand(look.x * distance, look.y * distance, look.z * distance)
                        .grow(1.0D, 1.0D, 1.0D),
                Predicates.and(EntitySelectors.NOT_SPECTATING, e -> e != null && e.canBeCollidedWith()));

        for (Entity entity1 : list) {
            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.3);
            RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(eyes, end);

            if (raytraceresult != null) {
                double d1 = eyes.squareDistanceTo(raytraceresult.hitVec);
                if (d1 < d0 || d0 == 0.0D) {
                    entity = entity1;
                    d0 = d1;
                }
            }
        }
        return entity;
    }

    @Override
    public @NotNull String getUsage(@NotNull ICommandSender sender) {
        return String.format("/gs %s [all, pretty, %s, %s]",
                             getName(),
                             String.join(", ", InfoParserRegistry.getIds()),
                             InfoParserRegistry.getIds().stream().map(x -> "-" + x).collect(Collectors.joining(", ")));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public @NotNull List<String> getTabCompletions(@NotNull MinecraftServer server, @NotNull ICommandSender sender, String @NotNull [] args,
                                                   @Nullable BlockPos targetPos) {
        List<String> enabledModes = new ArrayList<>();
        enabledModes.add("all");
        enabledModes.add("pretty");
        InfoParserRegistry.getIds().forEach(x -> enabledModes.add("-" + x));
        enabledModes.addAll(InfoParserRegistry.getIds());
        Arrays.stream(args).map(String::toLowerCase).forEach(enabledModes::remove);
        return getListOfStringsMatchingLastWord(args, enabledModes);
    }

    abstract String targetDescription();

    protected void print(EntityPlayer player, List<ITextComponent> messages, List<String> argList) {
        if (messages.isEmpty()) {
            if (argList.isEmpty()) {
                player.sendMessage(new TextComponentString(String.format("Couldn't find %s!", targetDescription())).setStyle(new Style().setColor(TextFormatting.RED)));
            } else {
                player.sendMessage(new TextComponentString(String.format("Couldn't find %s matching the given arguments!", targetDescription())).setStyle(new Style().setColor(TextFormatting.RED)));
                player.sendMessage(new TextComponentString("The following arguments were provided: " + String.join(", ", argList)));
            }
        } else {
            // have a horizontal bar to improve readability when running multiple consecutive info commands
            player.sendMessage(new TextComponentString("================================").setStyle(new Style().setColor(TextFormatting.GOLD)));
            messages.forEach(player::sendMessage);
        }
    }

    abstract void gatherInfo(InfoParserPackage info, EntityPlayer player);

    @Override
    public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, String @NotNull [] args) {
        if (sender instanceof EntityPlayer player) {
            List<ITextComponent> messages = new ArrayList<>();

            // get all distinct arguments
            List<String> argList = Arrays.stream(args).distinct().collect(Collectors.toList());

            // if there are 0 args, the args contain "all", or all the args are negative and disabling a specific feature, we want to print every option
            boolean enabled = args.length == 0 || argList.contains("all") || argList.stream().allMatch(x -> x.startsWith("-") || "pretty".equals(x));

            InfoParserPackage info = new InfoParserPackage(server, player, argList, messages, argList.contains("pretty"));

            // add different data to the info parser depending on the command being used
            gatherInfo(info, player);

            GsHandEvent event = new GsHandEvent(info);
            MinecraftForge.EVENT_BUS.post(event);

            info.parse(enabled);
            print(player, messages, argList);
        }
    }

}
