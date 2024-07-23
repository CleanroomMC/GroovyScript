package com.cleanroommc.groovyscript.compat.vanilla.command.infoparser;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InfoParserEnchantment extends GenericInfoParser<Enchantment> {

    public static final InfoParserEnchantment instance = new InfoParserEnchantment();

    @Override
    public String id() {
        return "enchantment";
    }

    @Override
    public String name() {
        return "Enchantment";
    }

    @Override
    public String text(@NotNull Enchantment entry, boolean colored, boolean prettyNbt) {
        return GroovyScriptCodeConverter.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getStack().isEmpty()) return;
        List<Enchantment> list = new ArrayList<>(EnchantmentHelper.getEnchantments(info.getStack()).keySet());
        instance.add(info.getMessages(), list, info.isPrettyNbt());
    }

}
