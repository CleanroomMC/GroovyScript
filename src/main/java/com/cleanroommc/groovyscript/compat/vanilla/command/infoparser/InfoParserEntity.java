package com.cleanroommc.groovyscript.compat.vanilla.command.infoparser;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class InfoParserEntity extends GenericInfoParser<Entity> {

    public static final InfoParserEntity instance = new InfoParserEntity();

    @Override
    public String id() {
        return "entity";
    }

    @Override
    public String name() {
        return "Entity";
    }

    @Override
    public String plural() {
        return "Entities";
    }

    @Override
    public String text(@NotNull Entity entry, boolean colored, boolean prettyNbt) {
        return GroovyScriptCodeConverter.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getEntity() == null || !(info.getEntity() instanceof EntityLiving)) return;
        instance.add(info.getMessages(), info.getEntity(), info.isPrettyNbt());

        ResourceLocation rl = EntityList.getKey(info.getEntity());
        if (rl != null) {
            InfoParserTranslationKey.instance.add(info.getMessages(), rl.toString(), info.isPrettyNbt());
        }
    }

}
