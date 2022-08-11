package com.cleanroommc.groovyscript.compat.enderio;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.compat.ModSupport;
import com.cleanroommc.groovyscript.compat.enderio.recipe.EnderIORecipeBuilder;
import com.cleanroommc.groovyscript.compat.enderio.recipe.RecipeInput;
import com.cleanroommc.groovyscript.helper.ArrayUtils;
import com.cleanroommc.groovyscript.mixin.enderio.AlloyRecipeManagerAccessor;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import crazypants.enderio.base.recipe.IManyToOneRecipe;
import crazypants.enderio.base.recipe.alloysmelter.AlloyRecipeManager;
import crazypants.enderio.base.recipe.lookup.TriItemLookup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class AlloySmelter {

    @GroovyBlacklist
    private final ThreadLocal<Boolean> captureRecipe = ThreadLocal.withInitial(() -> false);

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void remove(ItemStack output) {
        IManyToOneRecipe recipe = find(output);
        if (recipe != null) {
            remove(recipe);
        }
    }

    public void remove(IManyToOneRecipe recipe) {
        ReloadableRegistryManager.addRecipeForRecovery(AlloySmelter.class, recipe);
        Iterator<IManyToOneRecipe> iter = ((AlloyRecipeManagerAccessor) AlloyRecipeManager.getInstance()).getLookup().iterator();
        while (iter.hasNext()) {
            if (iter.next() == recipe) {
                iter.remove();
            }
        }
    }

    @Nullable
    public IManyToOneRecipe find(ItemStack output) {
        for (IManyToOneRecipe recipe : ((AlloyRecipeManagerAccessor) AlloyRecipeManager.getInstance()).getLookup()) {
            if (OreDictionary.itemMatches(output, recipe.getOutput(), false)) {
                return recipe;
            }
        }
        return null;
    }

    @GroovyBlacklist
    public void onReload() {
        AlloyRecipeManagerAccessor accessor = (AlloyRecipeManagerAccessor) AlloyRecipeManager.getInstance();
        TriItemLookup<IManyToOneRecipe> newLookup = accessor.getLookup();
        List<Object> markedList = ReloadableRegistryManager.unmarkScriptRecipes(AlloySmelter.class);
        if (!markedList.isEmpty()) {
            newLookup = new TriItemLookup<>();
            outer: for (IManyToOneRecipe recipe : accessor.getLookup()) {
                for (Object marked : markedList) {
                    if (marked == recipe) {
                        continue outer;
                    }
                }
                AlloyRecipeManagerAccessor.invokeAddRecipeToLookup(newLookup, recipe);
            }
        }
        List<Object> recoveredList = ReloadableRegistryManager.recoverRecipes(AlloySmelter.class);
        if (!recoveredList.isEmpty()) {
            for (Object recovered : recoveredList) {
                IManyToOneRecipe recipe = (IManyToOneRecipe) recovered;
                AlloyRecipeManagerAccessor.invokeAddRecipeToLookup(newLookup, recipe);
                accessor.invokeAddJEIIntegration(recipe);
            }
        }
        accessor.setLookup(newLookup);
    }

    @GroovyBlacklist
    public boolean isCapturingRecipe() {
        return captureRecipe.get();
    }

    public static class RecipeBuilder extends EnderIORecipeBuilder<Void> {

        private float xp;

        public RecipeBuilder xpChance(float xp) {
            this.xp = xp;
            return this;
        }

        @Override
        public boolean validate() {
            GroovyLog.Msg msg = new GroovyLog.Msg("Error adding EnderIO Alloy Smelter recipe").error();
            input.trim();
            output.trim();
            msg.add(input.size() < 1 || input.size() > 3, () -> "Must have 1 - 3 inputs, but found " + input.size());
            msg.add(output.size() != 1, () -> "Must have exactly 1 output, but found " + output.size());

            if (energy <= 0) energy = 5000;
            if (xp < 0) xp = 0;

            if (msg.hasSubMessages()) {
                GroovyLog.LOG.log(msg);
                return false;
            }
            return true;
        }

        @Override
        public @Nullable Void register() {
            if (!validate()) return null;
            ThreadLocal<Boolean> captureRecipe = ModSupport.ENDER_IO.get().AlloySmelter.captureRecipe;
            captureRecipe.set(true);
            AlloyRecipeManager.getInstance().addRecipe(true, ArrayUtils.mapToList(input, RecipeInput::new, new NNList<>()), output.get(0), energy, xp, level);
            captureRecipe.set(false);
            return null;
        }
    }
}
