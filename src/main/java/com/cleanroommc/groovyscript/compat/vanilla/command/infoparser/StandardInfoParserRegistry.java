package com.cleanroommc.groovyscript.compat.vanilla.command.infoparser;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserRegistry;

public class StandardInfoParserRegistry {

    public static void init() {
        InfoParserRegistry.addInfoParser(InfoParserItem.instance);
        InfoParserRegistry.addInfoParser(InfoParserFluid.instance);
        InfoParserRegistry.addInfoParser(InfoParserBlock.instance);
        InfoParserRegistry.addInfoParser(InfoParserBlockState.instance);
        InfoParserRegistry.addInfoParser(InfoParserOreDict.instance);
        InfoParserRegistry.addInfoParser(InfoParserNBT.instance);
        InfoParserRegistry.addInfoParser(InfoParserEntity.instance);
        InfoParserRegistry.addInfoParser(InfoParserVillagerProfession.instance);
        InfoParserRegistry.addInfoParser(InfoParserVillagerCareer.instance);
        InfoParserRegistry.addInfoParser(InfoParserBiome.instance);
        InfoParserRegistry.addInfoParser(InfoParserDimension.instance);
        InfoParserRegistry.addInfoParser(InfoParserCreativeTab.instance);
        InfoParserRegistry.addInfoParser(InfoParserEnchantment.instance);
        InfoParserRegistry.addInfoParser(InfoParserPotionEffect.instance);
    }

}
