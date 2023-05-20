package com.cleanroommc.groovyscript.compat.mods.forestry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.mixin.forestry.CharcoalManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import forestry.api.arboriculture.ICharcoalPileWall;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.ForestryAPI;
import forestry.arboriculture.charcoal.CharcoalPileWall;
import forestry.modules.ForestryModuleUids;
import net.minecraft.block.state.IBlockState;

public class CharcoalPile extends ForestryRegistry<ICharcoalPileWall> {

    @Override
    @GroovyBlacklist
    public void onReload() {
        if (!isEnabled()) return;
        removeScripted().forEach(((CharcoalManagerAccessor) TreeManager.charcoalManager).getWalls()::remove);
        restoreFromBackup().forEach(((CharcoalManagerAccessor) TreeManager.charcoalManager).getWalls()::add);
    }

    @Override
    @GroovyBlacklist
    public boolean isEnabled() {
        return ForestryAPI.moduleManager.isModuleEnabled("forestry", ForestryModuleUids.ARBORICULTURE);
    }

    public ICharcoalPileWall add(IBlockState state, int amount) {
        if (!isEnabled() || state == null) return null;
        ICharcoalPileWall wall = new CharcoalPileWall(state, amount);
        add(wall);
        return wall;
    }

    public void add(ICharcoalPileWall wall) {
        if (wall == null || !isEnabled()) return;
        addScripted(wall);
        ((CharcoalManagerAccessor) TreeManager.charcoalManager).getWalls().add(wall);
    }

    public boolean remove(ICharcoalPileWall wall) {
        if (wall == null || !isEnabled()) return false;
        addBackup(wall);
        return ((CharcoalManagerAccessor) TreeManager.charcoalManager).getWalls().remove(wall);
    }

    public boolean removeWall(IBlockState state) {
        if (!isEnabled()) return false;
        if (((CharcoalManagerAccessor) TreeManager.charcoalManager).getWalls().removeIf(wall -> {
            boolean found = wall.matches(state);
            if (found) addBackup(wall);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Charcoal Pile wall")
                .add("could not find wall for block {}", state)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        if (!isEnabled()) return;
        ((CharcoalManagerAccessor) TreeManager.charcoalManager).getWalls().forEach(this::addBackup);
        ((CharcoalManagerAccessor) TreeManager.charcoalManager).getWalls().clear();
    }

    public SimpleObjectStream<ICharcoalPileWall> streamWalls() {
        if (!isEnabled()) return null;
        return new SimpleObjectStream<>(((CharcoalManagerAccessor) TreeManager.charcoalManager).getWalls()).setRemover(this::remove);
    }
}
