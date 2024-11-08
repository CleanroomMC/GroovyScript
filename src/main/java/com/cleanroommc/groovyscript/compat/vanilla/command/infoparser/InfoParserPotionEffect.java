package com.cleanroommc.groovyscript.compat.vanilla.command.infoparser;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InfoParserPotionEffect extends GenericInfoParser<PotionEffect> {

    public static final InfoParserPotionEffect instance = new InfoParserPotionEffect();

    @Override
    public String id() {
        return "potioneffect";
    }

    @Override
    public String name() {
        return "Potion Effect";
    }

    @Override
    public String text(@NotNull PotionEffect entry, boolean colored, boolean prettyNbt) {
        return GroovyScriptCodeConverter.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getStack().isEmpty()) {
            if (info.getEntity() instanceof EntityLivingBase entityLivingBase) {
                instance.add(info.getMessages(), new ArrayList<>(entityLivingBase.getActivePotionEffects()), info.isPrettyNbt());
            }
            return;
        }
        List<PotionEffect> list = PotionUtils.getEffectsFromStack(info.getStack());
        if (list.isEmpty()) return;
        instance.add(info.getMessages(), list, info.isPrettyNbt());
    }
}
