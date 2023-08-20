package com.cleanroommc.groovyscript.compat.inworldcrafting;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IScriptReloadable;

public class InWorldCrafting implements IScriptReloadable {

    public final FluidToFluid fluidToFluid = new FluidToFluid();
    public final FluidToItem fluidToItem = new FluidToItem();
    public final FluidToBlock fluidToBlock = new FluidToBlock();
    public final Explosion explosion = new Explosion();
    public final Burning burning = new Burning();

    @GroovyBlacklist
    @Override
    public void onReload() {
        this.fluidToFluid.onReload();
        this.fluidToItem.onReload();
        this.fluidToBlock.onReload();
        this.explosion.onReload();
        this.burning.onReload();
    }

    @GroovyBlacklist
    @Override
    public void afterScriptLoad() {
        this.fluidToFluid.afterScriptLoad();
        this.fluidToItem.afterScriptLoad();
        this.fluidToBlock.afterScriptLoad();
        this.explosion.afterScriptLoad();
        this.burning.afterScriptLoad();
    }
}
