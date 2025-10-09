package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.api.infocommand.InfoParserRegistry;
import com.cleanroommc.groovyscript.helper.StyleConstant;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseInfoCommand extends CommandBase {

    @Override
    public @NotNull String getUsage(@NotNull ICommandSender sender) {
        return String.format(
                "/gs %s [all, pretty, %s, %s]",
                getName(),
                String.join(", ", InfoParserRegistry.getIds()),
                InfoParserRegistry.getIds().stream().map(x -> "-" + x).collect(Collectors.joining(", ")));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public @NotNull List<String> getTabCompletions(@NotNull MinecraftServer server,
                                                   @NotNull ICommandSender sender,
                                                   String @NotNull [] args,
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
                player.sendMessage(new TextComponentString(String.format("Couldn't find %s!", targetDescription())).setStyle(StyleConstant.getErrorStyle()));
            } else {
                player.sendMessage(new TextComponentString(String.format("Couldn't find %s matching the given arguments!", targetDescription())).setStyle(StyleConstant.getErrorStyle()));
                player.sendMessage(new TextComponentString("The following arguments were provided: " + String.join(", ", argList)));
            }
        } else {
            // have a horizontal bar to improve readability when running multiple consecutive info commands
            player.sendMessage(new TextComponentString("================================").setStyle(StyleConstant.getEmphasisStyle()));
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

            info.parse(enabled);
            print(player, messages, argList);
        }
    }
}
