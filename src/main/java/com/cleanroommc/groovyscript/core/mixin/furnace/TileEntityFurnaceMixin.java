package com.cleanroommc.groovyscript.core.mixin.furnace;

import com.cleanroommc.groovyscript.compat.vanilla.CustomFurnaceManager;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityFurnace.class)
public class TileEntityFurnaceMixin {

    @Shadow
    public NonNullList<ItemStack> furnaceItemStacks;

    @ModifyReturnValue(method = "getCookTime", at = @At("RETURN"))
    private int groovy$customCookTime(int original, ItemStack stack) {
        int time = CustomFurnaceManager.TIME_MAP.getInt(stack);
        return time <= 0 ? original : time;
    }

    /**
     * Skip the default bucket -> water bucket conversion when smelting a wet sponge,
     * as its logic is replaced by an entry in {@link CustomFurnaceManager#FUEL_TRANSFORMERS}.
     */
    @WrapWithCondition(method = "smeltItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;", ordinal = 1))
    private <E> boolean groovy$skipNormalBucketReplacement(NonNullList<E> instance, int p_set_1_, E p_set_2_) {
        return false;
    }

    @Inject(method = "smeltItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;shrink(I)V"))
    private void groovy$customFuelReplacement(CallbackInfo ci, @Local(ordinal = 0) ItemStack input) {
        var fuel = furnaceItemStacks.get(1);
        for (var fuelTransformer : CustomFurnaceManager.FUEL_TRANSFORMERS) {
            if (!fuelTransformer.smelted().test(input)) continue;
            if (!fuelTransformer.fuel().test(fuel)) continue;
            var stack = fuelTransformer.fuel().applyTransform(fuel);
            furnaceItemStacks.set(1, stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack);
            return; // we can only correctly do one transformation, so only the first transformer operates.
        }
    }

}
