package com.cleanroommc.groovyscript.compat.mods.essentialcraft;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.IJEIRemoval;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.OperationHandler;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import essentialcraft.api.DemonTrade;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RegistryDescription(
        category = RegistryDescription.Category.ENTRIES,
        admonition = @Admonition(value = "groovyscript.wiki.essentialcraft.demon_trade.note0", type = Admonition.Type.DANGER))
public class DemonTradeManager extends VirtualizedRegistry<DemonTrade> implements IJEIRemoval.Default {

    public DemonTradeManager() {
        super(Alias.generateOf("DemonTrade"));
    }

    @Override
    public void onReload() {
        removeScripted().forEach(DemonTrade::removeTrade);
        restoreFromBackup().forEach(r -> {
            DemonTrade.TRADES.add(r);
            if (r.desiredItem.isEmpty()) {
                DemonTrade.ALL_MOBS.add(r.entityType);
            }
        });
    }

    @MethodDescription(example = @Example("item('minecraft:diamond')"), type = MethodDescription.Type.ADDITION)
    public void add(IIngredient x) {
        for (ItemStack it : x.getMatchingStacks()) {
            DemonTrade t = new DemonTrade(it);  // this automatically registers the trade
            addScripted(t);
        }
    }

    @MethodDescription(example = @Example("entity('minecraft:chicken')"), type = MethodDescription.Type.ADDITION)
    public void add(EntityEntry x) {
        DemonTrade t = new DemonTrade(x);  // this automatically registers the trade
        addScripted(t);
    }

    @MethodDescription(example = @Example("item('minecraft:nether_star')"))
    public boolean remove(IIngredient x) {
        return DemonTrade.TRADES.removeIf(r -> {
            if (!r.desiredItem.isEmpty() && x.test(r.desiredItem)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.essentialcraft.demon_trade.removeEntity", example = @Example("entity('minecraft:enderman')"))
    public boolean remove(EntityEntry x) {
        return DemonTrade.TRADES.removeIf(r -> {
            if (r.desiredItem.isEmpty() && x.equals(r.entityType)) {
                addBackup(r);
                DemonTrade.ALL_MOBS.remove(r.entityType);
                return true;
            }
            return false;
        });
    }

    private boolean remove(DemonTrade t) {
        if (DemonTrade.TRADES.stream().anyMatch(r -> r.equals(t))) {
            addBackup(t);
            DemonTrade.removeTrade(t);
            return true;
        }
        return false;
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        DemonTrade.TRADES.forEach(this::addBackup);
        DemonTrade.TRADES.clear();
        DemonTrade.ALL_MOBS.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<DemonTrade> streamRecipes() {
        return new SimpleObjectStream<>(DemonTrade.TRADES).setRemover(this::remove);
    }

    @Override
    public @NotNull Collection<String> getCategories() {
        return Collections.singletonList(essentialcraft.integration.jei.DemonTrading.UID);
    }

    @Override
    public @NotNull List<OperationHandler.IOperation> getJEIOperations() {
        return Collections.singletonList(OperationHandler.ItemOperation.defaultItemOperation().include(0).output("remove"));
    }

}