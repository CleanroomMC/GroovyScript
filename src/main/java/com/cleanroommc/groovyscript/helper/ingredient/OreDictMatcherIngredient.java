package com.cleanroommc.groovyscript.helper.ingredient;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.IOreDicts;
import com.cleanroommc.groovyscript.core.mixin.OreDictionaryAccessor;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.regex.Pattern;

public class OreDictMatcherIngredient extends IngredientBase implements IOreDicts {

    private static final Pattern WILDCARD = Pattern.compile("\\*");

    private final Collection<String> oreDicts;
    private final ItemStackList itemStacks;
    private final Pattern pattern;
    private int amount = 1;

    public OreDictMatcherIngredient(String pattern) {
        this(Pattern.compile(WILDCARD.matcher(pattern).replaceAll(".*")));
    }

    public OreDictMatcherIngredient(Pattern pattern) {
        this.pattern = pattern;
        var set = new ObjectOpenHashSet<String>();
        var stacks = new ItemStackList();
        for (var ore : OreDictionaryAccessor.getIdToName()) {
            if (pattern.matcher(ore).matches() && set.add(ore)) {
                stacks.addAll(OreDictionary.getOres(ore));
            }
        }
        oreDicts = ImmutableSet.copyOf(set);
        itemStacks = stacks;
    }

    @Override
    public IIngredient exactCopy() {
        var ingredient = new OreDictMatcherIngredient(pattern);
        ingredient.amount = amount;
        ingredient.transformer = transformer;
        ingredient.matchCondition = matchCondition;
        return ingredient;
    }

    @Override
    public Ingredient toMcIngredient() {
        return Ingredient.fromStacks(getMatchingStacks());
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        ItemStack[] stacks = new ItemStack[itemStacks.size()];
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = getAt(i);
        }
        return stacks;
    }

    @Override
    public ItemStack getAt(int index) {
        ItemStack stack = this.itemStacks.get(index).copy();
        stack.setCount(getAmount());
        return stack;
    }

    @Override
    public int getAmount() {
        return itemStacks.isEmpty() ? 0 : amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        for (ItemStack itemStack1 : itemStacks) {
            if (OreDictionary.itemMatches(itemStack1, itemStack, false)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @UnmodifiableView Collection<String> getOreDicts() {
        return oreDicts;
    }

}
