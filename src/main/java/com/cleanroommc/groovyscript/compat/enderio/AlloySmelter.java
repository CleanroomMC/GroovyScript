package com.cleanroommc.groovyscript.compat.enderio;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.compat.ModSupport;
import com.cleanroommc.groovyscript.compat.enderio.recipe.EnderIORecipeBuilder;
import com.cleanroommc.groovyscript.compat.enderio.recipe.RecipeInput;
import com.cleanroommc.groovyscript.helper.ArrayUtils;
import com.cleanroommc.groovyscript.mixin.enderio.AlloyRecipeManagerAccessor;
import com.cleanroommc.groovyscript.mixin.enderio.ItemRecipeLeafNodeAccessor;
import com.cleanroommc.groovyscript.mixin.enderio.ItemRecipeNodeAccessor;
import com.cleanroommc.groovyscript.mixin.enderio.TriItemLookupAccessor;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.recipe.IManyToOneRecipe;
import crazypants.enderio.base.recipe.alloysmelter.AlloyRecipeManager;
import crazypants.enderio.base.recipe.lookup.ItemRecipeLeafNode;
import crazypants.enderio.base.recipe.lookup.ItemRecipeNode;
import crazypants.enderio.base.recipe.lookup.TriItemLookup;
import crazypants.enderio.util.NNPair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AlloySmelter {

    @GroovyBlacklist
    private final ThreadLocal<Boolean> captureRecipe = ThreadLocal.withInitial(() -> false);

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void remove(ItemStack output) {
        List<IManyToOneRecipe> recipes = find(output);
        if (!recipes.isEmpty()) {
            for (IManyToOneRecipe recipe : recipes) {
                ReloadableRegistryManager.addRecipeForRecovery(AlloySmelter.class, recipe);
            }
            remove(recipes);
        }
    }

    @SuppressWarnings("unchecked")
    public void remove(List<IManyToOneRecipe> recipes) {
        AlloyRecipeManagerAccessor accessor = (AlloyRecipeManagerAccessor) AlloyRecipeManager.getInstance();
        Int2ObjectOpenHashMap<NNPair<NNList<IManyToOneRecipe>, ItemRecipeNode<IManyToOneRecipe, ItemRecipeLeafNode<IManyToOneRecipe>>>> map =
                ((ItemRecipeNodeAccessor<IManyToOneRecipe, ItemRecipeNode<IManyToOneRecipe, ItemRecipeLeafNode<IManyToOneRecipe>>>)
                        ((TriItemLookupAccessor<IManyToOneRecipe>) accessor.getLookup()).getRoot()).getMap();
        for (NNPair<NNList<IManyToOneRecipe>, ItemRecipeNode<IManyToOneRecipe, ItemRecipeLeafNode<IManyToOneRecipe>>> pair : map.values()) {
            Iterator<IManyToOneRecipe> listIter = pair.left.iterator();
            while (listIter.hasNext()) {
                if (recipes.contains(listIter.next())) {
                    listIter.remove();
                    Int2ObjectOpenHashMap<NNPair<NNList<IManyToOneRecipe>, ItemRecipeLeafNode<IManyToOneRecipe>>> nestedMap =
                            ((ItemRecipeNodeAccessor<IManyToOneRecipe, ItemRecipeLeafNode<IManyToOneRecipe>>) pair.right).getMap();
                    for (NNPair<NNList<IManyToOneRecipe>, ItemRecipeLeafNode<IManyToOneRecipe>> nestedPair : nestedMap.values()) {
                        Iterator<IManyToOneRecipe> nestedListIter = nestedPair.left.iterator();
                        while (nestedListIter.hasNext()) {
                            if (recipes.contains(nestedListIter.next())) {
                                nestedListIter.remove();
                                Int2ObjectOpenHashMap<NNList<IManyToOneRecipe>> lastNestedMap = ((ItemRecipeLeafNodeAccessor<IManyToOneRecipe>) nestedPair.right).getMap();
                                for (NNList<IManyToOneRecipe> lastNestedList : lastNestedMap.values()) {
                                    lastNestedList.removeIf(recipes::contains);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public List<IManyToOneRecipe> find(ItemStack output) {
        List<IManyToOneRecipe> recipes = new ArrayList<>();
        for (IManyToOneRecipe recipe : ((AlloyRecipeManagerAccessor) AlloyRecipeManager.getInstance()).getLookup()) {
            if (OreDictionary.itemMatches(output, recipe.getOutput(), false)) {
                recipes.add(recipe);
            }
        }
        return recipes;
    }

    @GroovyBlacklist
    public void onReload() {
        AlloyRecipeManagerAccessor accessor = (AlloyRecipeManagerAccessor) AlloyRecipeManager.getInstance();
        TriItemLookup<IManyToOneRecipe> lookup = accessor.getLookup();
        List<?> markedList = ReloadableRegistryManager.unmarkScriptRecipes(AlloySmelter.class);
        if (!markedList.isEmpty()) {
            remove((List<IManyToOneRecipe>) markedList);
        }
        List<?> recoveredList = ReloadableRegistryManager.recoverRecipes(AlloySmelter.class);
        if (!recoveredList.isEmpty()) {
            for (Object recovered : recoveredList) {
                IManyToOneRecipe recipe = (IManyToOneRecipe) recovered;
                AlloyRecipeManagerAccessor.invokeAddRecipeToLookup(lookup, recipe);
                if (ModSupport.TINKERS_CONSTRUCT.isLoaded()) {
                    accessor.invokeAddJEIIntegration(recipe);
                }
            }
        }
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
