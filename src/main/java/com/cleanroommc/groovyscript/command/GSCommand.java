package com.cleanroommc.groovyscript.command;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class GSCommand extends CommandTreeBase {

    public GSCommand() {
        addSubcommand(new RunScriptsCommand());
    }

    @Override
    @Nonnull
    public String getName() {
        return "groovyscript";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return Arrays.asList("gs", "GroovyScript");
    }

    @Override
    @Nonnull
    public String getUsage(ICommandSender sender) {
        return "/gs []";
    }
}
