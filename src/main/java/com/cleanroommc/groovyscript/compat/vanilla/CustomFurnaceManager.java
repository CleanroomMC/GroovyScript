package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.ItemStackHashStrategy;
import com.github.bsideup.jabel.Desugar;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@GroovyBlacklist
public class CustomFurnaceManager {

    /**
     * Time an itemstack takes to smelt.
     * <p>
     * By default, minecraft uses 200 ticks for everything, GroovyScript uses a mixin to allow variable times to smelt.
     *
     * @see com.cleanroommc.groovyscript.core.mixin.furnace.TileEntityFurnaceMixin TileEntityFurnaceMixin
     */
    public static final Object2IntMap<ItemStack> TIME_MAP = new Object2IntOpenCustomHashMap<>(ItemStackHashStrategy.STRATEGY);

    /**
     * Recipes for converting the fuel slot of a furnace into another item when a valid item is smelted.
     * <p>
     * By default, minecraft has custom logic to make smelting a wet sponge convert an empty bucket into a water bucket.
     *
     * @see com.cleanroommc.groovyscript.core.mixin.furnace.TileEntityFurnaceMixin TileEntityFurnaceMixin
     * @see FuelConversionRecipe
     */
    public static final List<FuelConversionRecipe> FUEL_TRANSFORMERS = new ArrayList<>();

    static {
        // reproduce the vanilla logic for converting empty buckets into water buckets on smelting wet sponge
        // in groovyscript this would be `furnace.addFuelConversion(item('minecraft:sponge', 1), item('minecraft:bucket').transform(item('minecraft:water_bucket')))`
        var bucket = IngredientHelper.toIIngredient(((ItemStackMixinExpansion) (Object) (new ItemStack(Items.BUCKET))).transform(new ItemStack(Items.WATER_BUCKET)));
        var wetSponge = IngredientHelper.toIIngredient(new ItemStack(Item.getItemFromBlock(Blocks.SPONGE), 1, 1));
        FUEL_TRANSFORMERS.add(new FuelConversionRecipe(wetSponge, bucket));
    }

    /**
     * When the smelted ItemStack passes the {@link #smelted} filter and the {@link #fuel} filter,
     * the {@link #fuel} IIngredient will use {@link IIngredient#applyTransform(ItemStack)} to convert the fuel stack.
     *
     * @param smelted an IIngredient that is checked against the item being smelted
     * @param fuel    an IIngredient that is checked against the fuel item, and if it passes uses {@link IIngredient#applyTransform(ItemStack)} to convert the fuel item.
     */
    @Desugar
    public record FuelConversionRecipe(IIngredient smelted, IIngredient fuel) {

    }

}
