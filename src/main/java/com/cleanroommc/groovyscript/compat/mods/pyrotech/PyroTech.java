package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.codetaylor.mc.pyrotech.ModPyrotechConfig;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.machine.ModuleTechMachine;

import java.util.function.Supplier;

public class PyroTech extends GroovyPropertyContainer {

    public final Barrel barrel;
    public final Campfire campfire;
    public final StoneOven stoneOven;
    public final BrickOven brickOven;
    public final ChoppingBlock choppingBlock;
    public final CompactingBin compactingBin;
    public final CompostBin compostBin;
    public final CrudeDryingRack crudeDryingRack;
    public final DryingRack dryingRack;
    public final PitKiln pitKiln;
    public final StoneKiln stoneKiln;
    public final BrickKiln brickKiln;
    public final Anvil anvil;
    public final SoakingPot soakingPot;
    public final TanningRack tanningRack;

    public PyroTech() {
        this.barrel = register(ModuleTechBasic.MODULE_ID, Barrel::new);
        this.campfire = register(ModuleTechBasic.MODULE_ID, Campfire::new);
        this.stoneOven = register(ModuleTechMachine.MODULE_ID, StoneOven::new);
        this.brickOven = register(ModuleTechMachine.MODULE_ID, BrickOven::new);
        this.choppingBlock = register(ModuleTechBasic.MODULE_ID, ChoppingBlock::new);
        this.compactingBin = register(ModuleTechBasic.MODULE_ID, CompactingBin::new);
        this.compostBin = register(ModuleTechBasic.MODULE_ID, CompostBin::new);
        this.crudeDryingRack = register(ModuleTechBasic.MODULE_ID, CrudeDryingRack::new);
        this.dryingRack = register(ModuleTechBasic.MODULE_ID, DryingRack::new);
        this.pitKiln = register(ModuleTechBasic.MODULE_ID, PitKiln::new);
        this.stoneKiln = register(ModuleTechMachine.MODULE_ID, StoneKiln::new);
        this.brickKiln = register(ModuleTechMachine.MODULE_ID, BrickKiln::new);
        this.anvil = register(ModuleTechBasic.MODULE_ID, Anvil::new);
        this.soakingPot = register(ModuleTechBasic.MODULE_ID, SoakingPot::new);
        this.tanningRack = register(ModuleTechBasic.MODULE_ID, TanningRack::new);
    }

    private static <T> T register(String moduleName, Supplier<T> supplier) {
        Boolean bool = ModPyrotechConfig.MODULES.get(moduleName);
        if (bool != null && bool) return supplier.get();
        return null;
    }
}
