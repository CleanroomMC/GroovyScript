package com.cleanroommc.groovyscript.core.mixin;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(CommandHandler.class)
public interface CommandHandlerAccessor {

    @Accessor
    Set<ICommand> getCommandSet();
}
