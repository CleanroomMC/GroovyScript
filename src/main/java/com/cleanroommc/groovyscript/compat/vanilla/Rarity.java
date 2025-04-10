package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.NamedRegistry;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import com.cleanroommc.groovyscript.sandbox.expand.LambdaClosure;
import groovy.lang.Closure;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.IRarity;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES, admonition = @Admonition(value = "groovyscript.wiki.minecraft.rarity.note", type = Admonition.Type.INFO))
public class Rarity extends NamedRegistry {

    private final List<Pair<Closure<Boolean>, IRarity>> rarities = new ArrayList<>();

    public IRarity check(ItemStack testStack) {
        for (Pair<Closure<Boolean>, IRarity> pair : this.rarities) {
            if (ClosureHelper.call(false, pair.getKey(), testStack)) {
                return pair.getValue();
            }
        }
        return testStack.getItem().getRarity(testStack);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.rarity.set0", example = {
            @Example("textformat('RESET'), item('minecraft:enchanted_book')"), @Example("textformat('AQUA'), item('minecraft:diamond')")
    })
    public void set(TextFormatting color, ItemStack stack) {
        set(color, getAppropriateRarityName(color), stack);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.rarity.set1")
    public void set(TextFormatting color, String rarityName, ItemStack stack) {
        set(new RarityImpl(color, rarityName), stack);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.rarity.set2")
    public void set(IRarity rarity, ItemStack stack) {
        set(rarity, new LambdaClosure<>(this, arr -> ((Predicate<ItemStack>) (Object) stack).test((ItemStack) arr[0])));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.rarity.set0")
    public void set(TextFormatting color, Closure<Boolean> predicate) {
        set(color, getAppropriateRarityName(color), predicate);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.rarity.set1")
    public void set(TextFormatting color, String rarityName, Closure<Boolean> predicate) {
        set(new RarityImpl(color, rarityName), predicate);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.rarity.set2")
    public void set(IRarity rarity, Closure<Boolean> predicate) {
        if (GroovyLog.msg("Error setting Item Rarity")
                .add(rarity == null, () -> "Rarity must not be null")
                .add(predicate == null, () -> "Predicate must not be null")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        rarities.add(Pair.of(predicate, rarity));
    }

    @GroovyBlacklist
    public void onReload() {
        this.rarities.clear();
    }

    private String getAppropriateRarityName(TextFormatting formatting) {
        return switch (formatting) {
            case WHITE -> EnumRarity.COMMON.rarityName;
            case YELLOW -> EnumRarity.UNCOMMON.rarityName;
            case AQUA -> EnumRarity.RARE.rarityName;
            case LIGHT_PURPLE -> EnumRarity.EPIC.rarityName;
            default -> StringUtils.capitalize(formatting.getFriendlyName());
        };
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static class RarityImpl implements IRarity {

        private final TextFormatting color;
        private final String name;

        private RarityImpl(TextFormatting color, String name) {
            this.color = color;
            this.name = name;
        }

        @Override
        public TextFormatting getColor() {
            return color;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
