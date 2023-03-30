package com.cleanroommc.groovyscript.helper.ingredient;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.core.mixin.OreDictionaryAccessor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class OreDictWildcardIngredient extends ItemsIngredient {

    private final String oreDict;
    private final List<String> matchingOreDictionaries = new ArrayList<>();

    public static OreDictWildcardIngredient of(String oreDict) {
        List<String> matchingOreDictionaries = new ArrayList<>();
        List<ItemStack> stacks = new ArrayList<>();
        Pattern pattern = Pattern.compile(oreDict.replace("*", ".*"));

        for (String ore : OreDictionaryAccessor.getIdToName()) {
            if (pattern.matcher(ore).matches()) {
                matchingOreDictionaries.add(ore);
                for (ItemStack stack : OreDictionary.getOres(ore)) {
                    stacks.add(stack.copy());
                }
            }
        }
        return new OreDictWildcardIngredient(oreDict, matchingOreDictionaries, stacks);
    }

    public OreDictWildcardIngredient(String oreDict, List<String> matchingOreDictionaries, List<ItemStack> itemStacks) {
        super(itemStacks);
        this.oreDict = oreDict;
        this.matchingOreDictionaries.addAll(matchingOreDictionaries);
    }

    public String getOreDict() {
        return oreDict;
    }

    public List<String> getMatchingOreDictionaries() {
        return matchingOreDictionaries;
    }

    @Override
    public IIngredient exactCopy() {
        return new OreDictWildcardIngredient(this.oreDict, this.matchingOreDictionaries, getItemStacks());
    }
}
