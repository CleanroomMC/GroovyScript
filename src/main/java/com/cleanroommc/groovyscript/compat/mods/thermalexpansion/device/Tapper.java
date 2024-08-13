package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.device;

import cofh.core.util.BlockWrapper;
import cofh.core.util.ItemWrapper;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.TapperManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.AbstractReloadableStorage;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.github.bsideup.jabel.Desugar;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.stream.Collectors;

@RegistryDescription(
        admonition = @Admonition("groovyscript.wiki.thermalexpansion.tapper.note0")
)
public class Tapper extends VirtualizedRegistry<Tapper.TapperItemRecipe> {

    private final AbstractReloadableStorage<TapperBlockRecipe> blockStorage = new AbstractReloadableStorage<>();

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> TapperManagerAccessor.getItemMap().entrySet().removeIf(r -> r.getKey().equals(recipe.itemWrapper()) && r.getValue().equals(recipe.fluidStack())));
        restoreFromBackup().forEach(r -> TapperManagerAccessor.getItemMap().put(r.itemWrapper(), r.fluidStack()));
        blockStorage.removeScripted().forEach(r -> TapperManagerAccessor.getBlockMap().put(r.blockWrapper(), r.fluidStack()));
        blockStorage.restoreFromBackup().forEach(r -> TapperManagerAccessor.getBlockMap().remove(r.blockWrapper()));
    }

    public void addItem(TapperItemRecipe recipe) {
        TapperManagerAccessor.getItemMap().put(recipe.itemWrapper(), recipe.fluidStack());
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:clay'), fluid('lava') * 300"))
    public void addItem(ItemStack itemStack, FluidStack fluidStack) {
        addItem(new TapperItemRecipe(new ItemWrapper(itemStack), fluidStack));
    }

    public void addBlock(TapperBlockRecipe recipe) {
        TapperManagerAccessor.getBlockMap().put(recipe.blockWrapper(), recipe.fluidStack());
        blockStorage.addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:clay'), fluid('lava') * 150"))
    public void addBlock(ItemStack itemStack, FluidStack fluidStack) {
        if (itemStack.getItem() instanceof ItemBlock itemBlock) {
            addBlock(new TapperBlockRecipe(new BlockWrapper(itemBlock.getBlock(), itemStack.getMetadata()), fluidStack));
        } else {
            GroovyLog.get().debug("couldnt add a block recipe for {} because it was not an ItemBlock", itemStack);
        }
    }

    public boolean remove(ItemWrapper recipe) {
        return TapperManagerAccessor.getItemMap().keySet().removeIf(r -> {
            if (r.equals(recipe)) {
                addBackup(new TapperItemRecipe(r, TapperManagerAccessor.getItemMap().get(r)));
                return true;
            }
            return false;
        });
    }

    public boolean removeItem(TapperItemRecipe recipe) {
        return TapperManagerAccessor.getItemMap().keySet().removeIf(r -> {
            if (r.equals(recipe.itemWrapper())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:log:1')"))
    public boolean removeItemByInput(IIngredient input) {
        return TapperManagerAccessor.getItemMap().entrySet().removeIf(r -> {
            if (input.test(r.getValue()) || input.test(new ItemStack(r.getKey().item, r.getKey().metadata))) {
                addBackup(new TapperItemRecipe(r.getKey(), r.getValue()));
                return true;
            }
            return false;
        });
    }

    public boolean removeBlock(BlockWrapper wrapper) {
        return TapperManagerAccessor.getBlockMap().entrySet().removeIf(r -> {
            if (r.getKey().equals(wrapper)) {
                blockStorage.addBackup(new TapperBlockRecipe(r.getKey(), r.getValue()));
                return true;
            }
            return false;
        });
    }

    public boolean removeBlock(IBlockState state) {
        return removeBlock(new BlockWrapper(state));
    }

    public boolean removeBlock(TapperBlockRecipe recipe) {
        return removeBlock(recipe.blockWrapper());
    }

    @MethodDescription(example = @Example("item('minecraft:log')"))
    public boolean removeBlockByInput(IIngredient input) {
        return TapperManagerAccessor.getBlockMap().entrySet().removeIf(r -> {
            if (input.test(r.getValue())) {
                blockStorage.addBackup(new TapperBlockRecipe(r.getKey(), r.getValue()));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<TapperItemRecipe> streamRecipes() {
        return new SimpleObjectStream<>(TapperManagerAccessor.getItemMap().entrySet().stream().map(x -> new TapperItemRecipe(x.getKey(), x.getValue())).collect(Collectors.toList()))
                .setRemover(this::removeItem);
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<TapperBlockRecipe> streamBlockRecipes() {
        return new SimpleObjectStream<>(TapperManagerAccessor.getBlockMap().entrySet().stream().map(x -> new TapperBlockRecipe(x.getKey(), x.getValue())).collect(Collectors.toList()))
                .setRemover(this::removeBlock);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAllItems() {
        TapperManagerAccessor.getItemMap().forEach((key, value) -> addBackup(new TapperItemRecipe(key, value)));
        TapperManagerAccessor.getItemMap().clear();
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAllBlocks() {
        TapperManagerAccessor.getBlockMap().forEach((key, value) -> blockStorage.addBackup(new TapperBlockRecipe(key, value)));
        TapperManagerAccessor.getBlockMap().clear();
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        TapperManagerAccessor.getItemMap().forEach((key, value) -> addBackup(new TapperItemRecipe(key, value)));
        TapperManagerAccessor.getItemMap().clear();
        TapperManagerAccessor.getBlockMap().forEach((key, value) -> blockStorage.addBackup(new TapperBlockRecipe(key, value)));
        TapperManagerAccessor.getBlockMap().clear();
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static class TapperItemRecipe {

        private final ItemWrapper itemWrapper;
        private final FluidStack fluidStack;

        public TapperItemRecipe(ItemWrapper itemWrapper, FluidStack fluidStack) {
            this.itemWrapper = itemWrapper;
            this.fluidStack = fluidStack;
        }

        public ItemWrapper itemWrapper() {
            return itemWrapper;
        }

        public FluidStack fluidStack() {
            return fluidStack;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (TapperItemRecipe) obj;
            return Objects.equals(this.itemWrapper, that.itemWrapper) &&
                   Objects.equals(this.fluidStack, that.fluidStack);
        }

        @Override
        public int hashCode() {
            return Objects.hash(itemWrapper, fluidStack);
        }

        @Override
        public String toString() {
            return "TapperItemRecipe[" +
                   "itemWrapper=" + itemWrapper + ", " +
                   "fluidStack=" + fluidStack + ']';
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static class TapperBlockRecipe {

        private final BlockWrapper blockWrapper;
        private final FluidStack fluidStack;

        public TapperBlockRecipe(BlockWrapper blockWrapper, FluidStack fluidStack) {
            this.blockWrapper = blockWrapper;
            this.fluidStack = fluidStack;
        }

        public BlockWrapper blockWrapper() {
            return blockWrapper;
        }

        public FluidStack fluidStack() {
            return fluidStack;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (TapperBlockRecipe) obj;
            return Objects.equals(this.blockWrapper, that.blockWrapper) &&
                   Objects.equals(this.fluidStack, that.fluidStack);
        }

        @Override
        public int hashCode() {
            return Objects.hash(blockWrapper, fluidStack);
        }

        @Override
        public String toString() {
            return "TapperBlockRecipe[" +
                   "blockWrapper=" + blockWrapper + ", " +
                   "fluidStack=" + fluidStack + ']';
        }
    }

}
