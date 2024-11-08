package com.cleanroommc.groovyscript.compat.vanilla.command.infoparser;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.jetbrains.annotations.NotNull;

public class InfoParserVillagerCareer extends GenericInfoParser<VillagerRegistry.VillagerCareer> {

    public static final InfoParserVillagerCareer instance = new InfoParserVillagerCareer();

    @Override
    public int priority() {
        return 201;
    }

    @Override
    public String id() {
        return "villagercareer";
    }

    @Override
    public String name() {
        return "Villager Career";
    }

    @Override
    public String text(@NotNull VillagerRegistry.VillagerCareer entry, boolean colored, boolean prettyNbt) {
        return GroovyScriptCodeConverter.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getEntity() == null) return;
        if (info.getEntity() instanceof EntityVillager villager) {
            VillagerRegistry.VillagerCareer career = villager.getProfessionForge().getCareer(villager.careerId - 1);
            instance.add(info.getMessages(), career, info.isPrettyNbt());
            InfoParserTranslationKey.instance.add(info.getMessages(), "entity.Villager." + career.getName(), info.isPrettyNbt());
        }
    }
}
