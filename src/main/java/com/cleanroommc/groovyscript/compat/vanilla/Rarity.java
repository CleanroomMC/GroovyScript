package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.IRarity;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Rarity {

    private final List<Pair<ItemStack, IRarity>> rarities = new ArrayList<>();

    public Rarity() {
        super();
    }

    public IRarity check(ItemStack testStack) {
        for (Pair<ItemStack, IRarity> pair : this.rarities) {
            if (((Predicate<ItemStack>) (Object) pair.getKey()).test(testStack)) {
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
        if (GroovyLog.msg("Error setting Item Rarity")
                .add(rarity == null, () -> "Rarity must not be null")
                .add(IngredientHelper.isEmpty(stack), () -> "Stack must not be null")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        rarities.add(Pair.of(stack, rarity));
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
