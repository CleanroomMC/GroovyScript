package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.Mekanism;
import com.cleanroommc.groovyscript.helper.StyleConstant;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.infuse.InfuseRegistry;
import mekanism.api.infuse.InfuseType;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.command.CommandTreeBase;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class GSMekanismCommand extends CommandTreeBase {

    public GSMekanismCommand() {
        addSubcommand(new SimpleCommand("gas", (server, sender, args) -> {
            sender.sendMessage(new TextComponentString("Mekanism gases:"));
            for (Gas gas : GasRegistry.getRegisteredGasses()) {
                String copyText = Mekanism.asGroovyCode(gas, true);
                sender.sendMessage(TextCopyable.string(copyText, " - " + gas.getName()).build());
            }
        }, "gases"));
        addSubcommand(new SimpleCommand("infusionTypes", (server, sender, args) -> {
            sender.sendMessage(new TextComponentString("Mekanism infusion types:"));
            for (InfuseType infuseType : InfuseRegistry.getInfuseMap().values()) {
                String copyText = "'" + infuseType.name + "'";
                sender.sendMessage(TextCopyable.string(copyText, " - " + infuseType.name).build());
            }
        }));
    }

    @Override
    public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, String @NotNull [] args) throws CommandException {
        if (!ModSupport.MEKANISM.isLoaded()) {
            sender.sendMessage(new TextComponentString("Mekanism is not loaded!").setStyle(StyleConstant.getErrorStyle()));
            return;
        }
        super.execute(server, sender, args);
    }

    @Override
    public @NotNull String getName() {
        return "mekanism";
    }

    @Override
    public @NotNull String getUsage(@NotNull ICommandSender sender) {
        return "/gs mekanism [gases|infusionTypes]";
    }

    @Override
    public @NotNull List<String> getAliases() {
        return Collections.singletonList("mek");
    }
}
