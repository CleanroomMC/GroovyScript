package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.device;

import cofh.core.inventory.ComparableItemStack;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.XpCollectorManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class XpCollector extends VirtualizedRegistry<XpCollector.XpCollectorRecipe> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> {
            XpCollectorManagerAccessor.getCatalystMap().keySet().removeIf(r -> r.equals(recipe.getComparableStack()));
            XpCollectorManagerAccessor.getCatalystFactorMap().keySet().removeIf(r -> r.equals(recipe.getComparableStack()));
        });
        restoreFromBackup().forEach(r -> {
            XpCollectorManagerAccessor.getCatalystMap().put(r.getComparableStack(), r.xp());
            XpCollectorManagerAccessor.getCatalystFactorMap().put(r.getComparableStack(), r.factor());
        });
    }

    public void add(XpCollectorRecipe recipe) {
        XpCollectorManagerAccessor.getCatalystMap().put(recipe.getComparableStack(), recipe.xp());
        XpCollectorManagerAccessor.getCatalystFactorMap().put(recipe.getComparableStack(), recipe.factor());
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:clay'), 100, 30"))
    public void add(ItemStack catalyst, int xp, int factor) {
        add(new XpCollectorRecipe(catalyst, xp, factor));
    }

    public boolean remove(ComparableItemStack comparableItemStack) {
        if (XpCollectorManagerAccessor.getCatalystMap().containsKey(comparableItemStack) && XpCollectorManagerAccessor.getCatalystFactorMap().containsKey(comparableItemStack)) {
            addBackup(new XpCollectorRecipe(new ItemStack(comparableItemStack.item, comparableItemStack.metadata),
                                            XpCollectorManagerAccessor.getCatalystMap().remove(comparableItemStack),
                                            XpCollectorManagerAccessor.getCatalystFactorMap().remove(comparableItemStack)));
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("item('minecraft:soul_sand')"))
    public boolean remove(ItemStack itemStack) {
        ComparableItemStack help = new ComparableItemStack(itemStack);
        if (XpCollectorManagerAccessor.getCatalystMap().containsKey(help) && XpCollectorManagerAccessor.getCatalystFactorMap().containsKey(help)) {
            addBackup(new XpCollectorRecipe(itemStack, XpCollectorManagerAccessor.getCatalystMap().remove(help), XpCollectorManagerAccessor.getCatalystFactorMap().remove(help)));
            return true;
        }
        return false;
    }

    public boolean remove(XpCollectorRecipe recipe) {
        if (XpCollectorManagerAccessor.getCatalystMap().containsKey(recipe.getComparableStack()) && XpCollectorManagerAccessor.getCatalystFactorMap().containsKey(recipe.getComparableStack())) {
            addBackup(new XpCollectorRecipe(recipe.catalyst(), XpCollectorManagerAccessor.getCatalystMap().remove(recipe.getComparableStack()), XpCollectorManagerAccessor.getCatalystFactorMap().remove(recipe.getComparableStack())));
            return true;
        }
        return false;
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ComparableItemStack> streamRecipes() {
        return new SimpleObjectStream<>(XpCollectorManagerAccessor.getCatalystMap().keySet())
                .setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        XpCollectorManagerAccessor.getCatalystMap().keySet().stream().filter(XpCollectorManagerAccessor.getCatalystFactorMap()::containsKey)
                .forEach(x -> addBackup(new XpCollectorRecipe(new ItemStack(x.item, x.metadata), XpCollectorManagerAccessor.getCatalystMap().get(x), XpCollectorManagerAccessor.getCatalystFactorMap().get(x))));
        XpCollectorManagerAccessor.getCatalystMap().clear();
        XpCollectorManagerAccessor.getCatalystFactorMap().clear();
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static final class XpCollectorRecipe {

        private final ItemStack catalyst;
        private final int xp;
        private final int factor;

        public XpCollectorRecipe(ItemStack catalyst, int xp, int factor) {
            this.catalyst = catalyst;
            this.xp = xp;
            this.factor = factor;
        }

        public ComparableItemStack getComparableStack() {
            return new ComparableItemStack(catalyst());
        }

        public ItemStack catalyst() {
            return catalyst;
        }

        public int xp() {
            return xp;
        }

        public int factor() {
            return factor;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (XpCollectorRecipe) obj;
            return Objects.equals(this.catalyst, that.catalyst) &&
                   this.xp == that.xp &&
                   this.factor == that.factor;
        }

        @Override
        public int hashCode() {
            return Objects.hash(catalyst, xp, factor);
        }

        @Override
        public String toString() {
            return "XpCollectorRecipe[" +
                   "catalyst=" + catalyst + ", " +
                   "xp=" + xp + ", " +
                   "factor=" + factor + ']';
        }
    }

}
