package com.cleanroommc.groovyscript.compat.mods.enderio;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.enderio.recipe.EnderIORecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.enderio.recipe.RecipeInput;
import com.cleanroommc.groovyscript.core.mixin.enderio.AlloyRecipeManagerAccessor;
import com.cleanroommc.groovyscript.core.mixin.enderio.ItemRecipeLeafNodeAccessor;
import com.cleanroommc.groovyscript.core.mixin.enderio.ItemRecipeNodeAccessor;
import com.cleanroommc.groovyscript.core.mixin.enderio.TriItemLookupAccessor;
import com.cleanroommc.groovyscript.helper.ArrayUtils;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.recipe.IManyToOneRecipe;
import crazypants.enderio.base.recipe.alloysmelter.AlloyRecipeManager;
import crazypants.enderio.base.recipe.lookup.ItemRecipeLeafNode;
import crazypants.enderio.base.recipe.lookup.ItemRecipeNode;
import crazypants.enderio.base.recipe.lookup.TriItemLookup;
import crazypants.enderio.util.NNPair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AlloySmelter extends VirtualizedRegistry<IManyToOneRecipe> {

    @GroovyBlacklist
    private Set<IManyToOneRecipe> removalQueue;

    public AlloySmelter() {
        super(VirtualizedRegistry.generateAliases("Alloying"));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void remove(ItemStack output) {
        List<IManyToOneRecipe> recipes = find(output);
        if (!recipes.isEmpty()) {
            if (this.removalQueue == null) {
                this.removalQueue = new ObjectOpenHashSet<>(recipes.size());
            }
            for (IManyToOneRecipe r : recipes) {
                addBackup(r);
                this.removalQueue.add(r);
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
        Collection<IManyToOneRecipe> markedList = removeScripted();
        if (!markedList.isEmpty()) {
            removeInternal(markedList);
        }
        Collection<IManyToOneRecipe> recoveredList = restoreFromBackup();
        if (!recoveredList.isEmpty()) {
            for (IManyToOneRecipe recipe : recoveredList) {
                AlloyRecipeManagerAccessor.invokeAddRecipeToLookup(lookup, recipe);
                if (ModSupport.TINKERS_CONSTRUCT.isLoaded()) {
                    accessor.invokeAddJEIIntegration(recipe);
                }
            }
        }
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public void afterScriptLoad() {
        if (this.removalQueue != null) {
            removeInternal(this.removalQueue);
            this.removalQueue = null;
        }
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    private void removeInternal(Collection<IManyToOneRecipe> recipes) {
        AlloyRecipeManagerAccessor accessor = (AlloyRecipeManagerAccessor) AlloyRecipeManager.getInstance();
        @SuppressWarnings("unchecked")
        Int2ObjectOpenHashMap<NNPair<NNList<IManyToOneRecipe>, ItemRecipeNode<IManyToOneRecipe, ItemRecipeLeafNode<IManyToOneRecipe>>>> map =
                ((ItemRecipeNodeAccessor<IManyToOneRecipe, ItemRecipeNode<IManyToOneRecipe, ItemRecipeLeafNode<IManyToOneRecipe>>>)
                        ((TriItemLookupAccessor<IManyToOneRecipe>) accessor.getLookup()).getRoot()).getMap();
        for (NNPair<NNList<IManyToOneRecipe>, ItemRecipeNode<IManyToOneRecipe, ItemRecipeLeafNode<IManyToOneRecipe>>> pair : map.values()) {
            Iterator<IManyToOneRecipe> listIter = pair.left.iterator();
            while (listIter.hasNext()) {
                if (recipes.contains(listIter.next())) {
                    listIter.remove();
                    @SuppressWarnings("unchecked")
                    Int2ObjectOpenHashMap<NNPair<NNList<IManyToOneRecipe>, ItemRecipeLeafNode<IManyToOneRecipe>>> nestedMap =
                            ((ItemRecipeNodeAccessor<IManyToOneRecipe, ItemRecipeLeafNode<IManyToOneRecipe>>) pair.right).getMap();
                    for (NNPair<NNList<IManyToOneRecipe>, ItemRecipeLeafNode<IManyToOneRecipe>> nestedPair : nestedMap.values()) {
                        Iterator<IManyToOneRecipe> nestedListIter = nestedPair.left.iterator();
                        while (nestedListIter.hasNext()) {
                            if (recipes.contains(nestedListIter.next())) {
                                nestedListIter.remove();
                                @SuppressWarnings("unchecked")
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

    public static class RecipeBuilder extends EnderIORecipeBuilder<Void> {

        private float xp;

        public RecipeBuilder xpChance(float xp) {
            this.xp = xp;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding EnderIO Alloy Smelter recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 3, 1, 1);
            validateFluids(msg);
            if (energy <= 0) energy = 5000;
            if (xp < 0) xp = 0;
        }

        @Override
        public @Nullable Void register() {
            if (!validate()) return null;
            AlloyRecipeManager.getInstance().addRecipe(true, ArrayUtils.mapToList(input, RecipeInput::new, new NNList<>()), output.get(0), energy, xp, level);
            return null;
        }
    }

}
