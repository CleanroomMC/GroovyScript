package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.compat.vanilla.FurnaceRecipeAccess;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Objects;

@Mixin(FurnaceRecipes.class)
public abstract class FurnaceRecipeMixin implements FurnaceRecipeAccess {

    @Shadow
    @Final
    private Map<ItemStack, ItemStack> smeltingList;
    @Shadow
    @Final
    private Map<ItemStack, Float> experienceList;

    @Shadow
    protected abstract boolean compareItemStacks(ItemStack stack1, ItemStack stack2);

    @Unique
    @Final
    public Map<ItemStack, ItemStack> inputMap = new Object2ObjectOpenCustomHashMap<>(new Hash.Strategy<ItemStack>() {
        @Override
        public int hashCode(ItemStack o) {
            return Objects.hash(o.getItem(), o.getMetadata());
        }

        @Override
        public boolean equals(ItemStack a, ItemStack b) {
            return a == b || (a != null && b != null && OreDictionary.itemMatches(a, b, false));
        }
    });

    @Override
    public Map<ItemStack, ItemStack> getSmeltingList() {
        return this.smeltingList;
    }

    @Override
    public Map<ItemStack, Float> getExperienceList() {
        return this.experienceList;
    }

    @Override
    public Map<ItemStack, ItemStack> getInputList() {
        return this.inputMap;
    }

    @Override
    public boolean invokeCompareItemStacks(ItemStack stack1, ItemStack stack2) {
        return compareItemStacks(stack1, stack2);
    }

    @Inject(method = "addSmeltingRecipe", at = @At("RETURN"))
    public void addRecipe(ItemStack input, ItemStack stack, float experience, CallbackInfo ci) {
        this.inputMap.put(input, input);
    }
}
