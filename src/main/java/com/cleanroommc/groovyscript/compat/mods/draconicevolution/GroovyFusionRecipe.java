package com.cleanroommc.groovyscript.compat.mods.draconicevolution;

import com.brandon3055.draconicevolution.api.fusioncrafting.ICraftingInjector;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionCraftingInventory;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class GroovyFusionRecipe implements IFusionRecipe {

    protected final ItemStack result;
    protected final ItemStack catalyst;
    protected final List<IIngredient> ingredients;
    protected final List<ItemStack> displayItems;
    protected final long energyCost;
    protected final int craftingTier;

    public GroovyFusionRecipe(ItemStack result, ItemStack catalyst, Collection<IIngredient> input, long energyCost, int craftingTier) {
        this.result = result;
        this.catalyst = catalyst;
        this.ingredients = new ArrayList<>(input);
        this.energyCost = energyCost;
        this.craftingTier = craftingTier;
        this.displayItems = new ArrayList<>();
        for (IIngredient ingredient : this.ingredients) {
            ItemStack[] stacks = ingredient.getMatchingStacks();
            this.displayItems.add(stacks.length > 0 ? stacks[0] : ItemStack.EMPTY);
        }
    }

    @Override
    public ItemStack getRecipeOutput(@Nullable ItemStack itemStack) {
        return result;
    }

    @Override
    public boolean isRecipeCatalyst(ItemStack itemStack) {
        return itemStack != null && IngredientHelper.toIIngredient(catalyst).test(itemStack);
    }

    @Override
    public List<ItemStack> getRecipeIngredients() {
        return this.displayItems;
    }

    @Override
    public int getRecipeTier() {
        return craftingTier;
    }

    @Override
    public ItemStack getRecipeCatalyst() {
        return catalyst;
    }

    @Override
    public long getIngredientEnergyCost() {
        return energyCost;
    }

    @Override
    public boolean matches(IFusionCraftingInventory inventory, World world, BlockPos blockPos) {
        ItemStack coreStack = inventory.getStackInCore(0);
        if (coreStack.isEmpty() || !isRecipeCatalyst(coreStack) || coreStack.getCount() < catalyst.getCount()) {
            return false;
        }

        List<ICraftingInjector> injectors = new ArrayList<>(inventory.getInjectors());
        int found = 0;
        for (IIngredient ingredient : ingredients) {
            if (injectors.isEmpty()) break;
            Iterator<ICraftingInjector> it = injectors.iterator();
            while (it.hasNext()) {
                ICraftingInjector craftingInjector = it.next();
                if (ingredient.test(craftingInjector.getStackInPedestal()) && ingredient.getAmount() <= craftingInjector.getStackInPedestal().getCount()) {
                    found++;
                    it.remove();
                    break;
                }
            }
        }


        return found == ingredients.size();
    }

    @Override
    public void craft(IFusionCraftingInventory inventory, World world, BlockPos blockPos) {
        List<ICraftingInjector> injectors = new ArrayList<>(inventory.getInjectors());
        int found = 0;
        for (IIngredient ingredient : ingredients) {
            if (injectors.isEmpty()) break;
            Iterator<ICraftingInjector> it = injectors.iterator();
            while (it.hasNext()) {
                ICraftingInjector craftingInjector = it.next();
                ItemStack pedestalStack = craftingInjector.getStackInPedestal();
                if (ingredient.test(pedestalStack) && ingredient.getAmount() <= pedestalStack.getCount()) {
                    pedestalStack.shrink(ingredient.getAmount());
                    if (pedestalStack.isEmpty()) {
                        craftingInjector.setStackInPedestal(ItemStack.EMPTY);
                    }

                    found++;
                    it.remove();
                    break;
                }
            }
        }
        ItemStack catalyst = inventory.getStackInCore(0);
        ItemStack result = this.getRecipeOutput(catalyst);
        catalyst.shrink(this.catalyst.getCount());
        if (catalyst.getCount() <= 0) {
            catalyst = ItemStack.EMPTY;
        }

        inventory.setStackInCore(0, catalyst);
        inventory.setStackInCore(1, result.copy());
    }

    @Override
    public void onCraftingTick(IFusionCraftingInventory iFusionCraftingInventory, World world, BlockPos blockPos) {

    }

    @Override
    public String canCraft(IFusionCraftingInventory inventory, World world, BlockPos blockPos) {
        if (!inventory.getStackInCore(1).isEmpty()) {
            return "outputObstructed";
        }

        Iterator<ICraftingInjector> it = inventory.getInjectors().iterator();

        ICraftingInjector pedestal;
        do {
            if (!it.hasNext()) {
                return "true";
            }

            pedestal = it.next();
        } while (pedestal.getStackInPedestal().isEmpty() || pedestal.getPedestalTier() >= this.craftingTier);

        return "tierLow";
    }

}
