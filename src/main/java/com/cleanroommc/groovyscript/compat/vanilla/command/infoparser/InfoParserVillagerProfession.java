package com.cleanroommc.groovyscript.compat.vanilla.command.infoparser;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.jetbrains.annotations.NotNull;

public class InfoParserVillagerProfession extends GenericInfoParser<VillagerRegistry.VillagerProfession> {

    public static final InfoParserVillagerProfession instance = new InfoParserVillagerProfession();

    @Override
    public int priority() {
        return 200;
    }

    @Override
    public String id() {
        return "villagerprofession";
    }

    @Override
    public String name() {
        return "Villager Profession";
    }

    @Override
    public String text(@NotNull VillagerRegistry.VillagerProfession entry, boolean colored, boolean prettyNbt) {
        return GroovyScriptCodeConverter.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getEntity() == null) return;
        if (info.getEntity() instanceof EntityVillager villager) {
            instance.add(info.getMessages(), villager.getProfessionForge(), info.isPrettyNbt());
        }
    }
}
