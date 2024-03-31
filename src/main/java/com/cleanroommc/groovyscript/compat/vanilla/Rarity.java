package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
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

    public void set(TextFormatting color, ItemStack stack) {
        set(color, getAppropriateRarityName(color), stack);
    }

    public void set(TextFormatting color, String rarityName, ItemStack stack) {
        set(new RarityImpl(color, rarityName), stack);
    }

    public void set(IRarity rarity, ItemStack stack) {
        set(rarity, new LambdaClosure<>(this, arr -> ((Predicate<ItemStack>) (Object) stack).test((ItemStack) arr[0])));
    }

    public void set(TextFormatting color, Closure<Boolean> predicate) {
        set(color, getAppropriateRarityName(color), predicate);
    }

    public void set(TextFormatting color, String rarityName, Closure<Boolean> predicate) {
        set(new RarityImpl(color, rarityName), predicate);
    }

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
        switch (formatting) {
            case WHITE:
                return EnumRarity.COMMON.rarityName;
            case YELLOW:
                return EnumRarity.UNCOMMON.rarityName;
            case AQUA:
                return EnumRarity.RARE.rarityName;
            case LIGHT_PURPLE:
                return EnumRarity.EPIC.rarityName;
        }
        return StringUtils.capitalize(formatting.getFriendlyName());
    }

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
