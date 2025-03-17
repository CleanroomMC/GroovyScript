package com.cleanroommc.groovyscript.helper.ingredient;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.IOreDicts;
import com.cleanroommc.groovyscript.core.mixin.OreDictionaryAccessor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OreDictWildcardIngredient extends ItemsIngredient implements IOreDicts {

    private final String oreDict;
    private final List<String> matchingOreDictionaries = new ArrayList<>();
    public final List<String> ores = Collections.unmodifiableList(this.matchingOreDictionaries);

    private OreDictWildcardIngredient(String oreDict, List<String> matchingOreDictionaries, List<ItemStack> itemStacks) {
        super(itemStacks);
        this.oreDict = oreDict;
        this.matchingOreDictionaries.addAll(matchingOreDictionaries);
    }

    public OreDictWildcardIngredient(String oreDict) {
        this.oreDict = oreDict;
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
        this.matchingOreDictionaries.addAll(matchingOreDictionaries);
        setItemStacks(stacks);
    }

    public String getOreDict() {
        return oreDict;
    }

    @Override
    public List<String> getOreDicts() {
        return getMatchingOreDictionaries();
    }

    public List<String> getMatchingOreDictionaries() {
        return ores;
    }

    public List<OreDictIngredient> getOres() {
        return this.ores.stream().map(OreDictIngredient::new).collect(Collectors.toList());
    }

    @Override
    public IIngredient exactCopy() {
        OreDictWildcardIngredient odwi = new OreDictWildcardIngredient(this.oreDict, this.matchingOreDictionaries, getItemStacks());
        odwi.setAmount(getAmount());
        odwi.transformer = transformer;
        odwi.matchCondition = matchCondition;
        return odwi;
    }
}
