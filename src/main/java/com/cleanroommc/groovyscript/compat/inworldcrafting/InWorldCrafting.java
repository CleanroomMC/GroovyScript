package com.cleanroommc.groovyscript.compat.inworldcrafting;

import com.cleanroommc.groovyscript.api.IScriptReloadable;

public class InWorldCrafting implements IScriptReloadable {

    public final FluidToFluid fluidToFluid = new FluidToFluid();
    public final FluidToItem fluidToItem = new FluidToItem();

    @Override
    public void onReload() {
        this.fluidToFluid.onReload();
        this.fluidToItem.onReload();
    }

    @Override
    public void afterScriptLoad() {
        this.fluidToFluid.afterScriptLoad();
        this.fluidToItem.afterScriptLoad();
    }
}
