package com.cleanroommc.groovyscript.compat.mods.jei;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.GenericInfoParser;
import com.cleanroommc.groovyscript.core.mixin.jei.ModRegistryAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public class InfoParserTab extends GenericInfoParser<IRecipeCategory> {

    public static final InfoParserTab instance = new InfoParserTab();

    @Override
    public String id() {
        return "jeitab";
    }

    @Override
    public String name() {
        return "JEI Tab Catalyst";
    }

    @Override
    public String text(@NotNull IRecipeCategory entry, boolean colored, boolean prettyNbt) {
        return colored ? GroovyScriptCodeConverter.STRING + entry.getUid() : entry.getUid();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void parse(InfoParserPackage info) {
        if (info.getStack().isEmpty()) return;

        // this gets all categories the item appears on - and there isn't any inbuilt method to get *just* catalysts.
        List<String> allowed = ((ModRegistryAccessor) JeiPlugin.modRegistry).getRecipeCatalysts()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().stream().anyMatch(x -> x instanceof ItemStack stack && ItemStack.areItemStacksEqual(stack, info.getStack())))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        List<IRecipeCategory> list = JeiPlugin.recipeRegistry.getRecipeCategories(allowed);

        instance.add(info.getMessages(), list, info.isPrettyNbt());
    }

}
