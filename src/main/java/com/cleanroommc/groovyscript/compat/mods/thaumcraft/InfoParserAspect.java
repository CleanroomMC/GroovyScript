package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.GenericInfoParser;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;

import java.util.List;
import java.util.stream.Collectors;

public class InfoParserAspect extends GenericInfoParser<AspectStack> {

    public static final InfoParserAspect instance = new InfoParserAspect();

    @Override
    public String id() {
        return "aspect";
    }

    @Override
    public String name() {
        return "Aspect";
    }

    @Override
    public String text(@NotNull AspectStack entry, boolean colored, boolean prettyNbt) {
        return Thaumcraft.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getEntity() == null && info.getStack().isEmpty()) return;
        AspectList list = info.getStack().isEmpty()
                          ? AspectHelper.getEntityAspects(info.getEntity())
                          : AspectHelper.getObjectAspects(info.getStack());

        if (list == null) return;

        // convert it into groovyscript AspectStack, so we can easily represent quantity
        List<AspectStack> target = list.aspects.entrySet().stream()
                .map(x -> new AspectStack(x.getKey(), x.getValue()))
                .collect(Collectors.toList());

        instance.add(info.getMessages(), target, info.isPrettyNbt());
    }

}
