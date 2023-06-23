package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.botania.recipe.MagnetSubject;
import com.cleanroommc.groovyscript.core.mixin.botania.BotaniaAPIAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import vazkii.botania.api.BotaniaAPI;

public class Magnet extends VirtualizedRegistry<MagnetSubject> {

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(m -> BotaniaAPI.magnetBlacklist.remove(m.getMagnetKey()));
        restoreFromBackup().forEach(m -> BotaniaAPI.magnetBlacklist.add(m.getMagnetKey()));
    }

    public boolean isInBlacklist(IIngredient item) {
        return BotaniaAPI.isItemBlacklistedFromMagnet(item.getMatchingStacks()[0]);
    }

    public boolean isInBlacklist(Block block, int meta) {
        return BotaniaAPI.isBlockBlacklistedFromMagnet(block, meta);
    }

    public boolean isInBlacklist(IBlockState state) {
        return BotaniaAPI.isBlockBlacklistedFromMagnet(state);
    }

    public void addToBlacklist(IIngredient item) {
        if (item == null) return;
        MagnetSubject subject = new MagnetSubject(item);
        addScripted(subject);
        BotaniaAPI.blacklistItemFromMagnet(item.getMatchingStacks()[0]);
    }

    public void addToBlacklist(Block block, int meta) {
        if (block == null) return;
        MagnetSubject subject = new MagnetSubject(block, meta);
        addScripted(subject);
        BotaniaAPI.blacklistBlockFromMagnet(block, meta);
    }

    public void addToBlacklist(IBlockState state) {
        addToBlacklist(state.getBlock(), state.getBlock().getMetaFromState(state));
    }

    public boolean removeFromBlacklist(IIngredient item) {
        if (item == null) return false;
        String key = BotaniaAPIAccessor.invokeGetMagnetKey(item.getMatchingStacks()[0]);
        if (!BotaniaAPI.magnetBlacklist.contains(key)) return false;
        MagnetSubject subject = new MagnetSubject(item);
        addBackup(subject);
        BotaniaAPI.magnetBlacklist.remove(key);
        return true;
    }

    public boolean removeFromBlacklist(Block block, int meta) {
        if (block == null) return false;
        String key = BotaniaAPIAccessor.invokeGetMagnetKey(block, meta);
        if (!BotaniaAPI.magnetBlacklist.contains(key)) return false;
        MagnetSubject subject = new MagnetSubject(block, meta);
        addBackup(subject);
        BotaniaAPI.magnetBlacklist.remove(key);
        return true;
    }

    public boolean removeFromBlacklist(IBlockState state) {
        return removeFromBlacklist(state.getBlock(), state.getBlock().getMetaFromState(state));
    }
}
