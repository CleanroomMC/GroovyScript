package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.codetaylor.mc.athenaeum.module.ModuleBase;
import com.codetaylor.mc.pyrotech.ModPyrotech;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.machine.ModuleTechMachine;

import java.util.function.Supplier;

public class PyroTech extends GroovyPropertyContainer {

    public final Barrel barrel;
    public final Campfire campfire;
    public final StoneOven stoneOven;
    public final BrickOven brickOven;
    public final ChoppingBlock choppingBlock;
    public final StoneSawmill stoneSawmill;
    public final BrickSawmill brickSawmill;
    public final CompactingBin compactingBin;
    public final MechanicalCompactingBin mechanicalCompactingBin;
    public final CompostBin compostBin;
    public final CrudeDryingRack crudeDryingRack;
    public final DryingRack dryingRack;
    public final PitKiln pitKiln;
    public final StoneKiln stoneKiln;
    public final BrickKiln brickKiln;
    public final Anvil anvil;
    public final SoakingPot soakingPot;
    public final TanningRack tanningRack;
    public final StoneCrucible stoneCrucible;
    public final BrickCrucible brickCrucible;

    public PyroTech() {
        this.barrel = register(ModuleTechBasic.class, Barrel::new);
        this.campfire = register(ModuleTechBasic.class, Campfire::new);
        this.stoneOven = register(ModuleTechMachine.class, StoneOven::new);
        this.brickOven = register(ModuleTechMachine.class, BrickOven::new);
        this.choppingBlock = register(ModuleTechBasic.class, ChoppingBlock::new);
        this.stoneSawmill = register(ModuleTechMachine.class, StoneSawmill::new);
        this.brickSawmill = register(ModuleTechMachine.class, BrickSawmill::new);
        this.compactingBin = register(ModuleTechBasic.class, CompactingBin::new);
        this.mechanicalCompactingBin = register(ModuleTechMachine.class, MechanicalCompactingBin::new);
        this.compostBin = register(ModuleTechBasic.class, CompostBin::new);
        this.crudeDryingRack = register(ModuleTechBasic.class, CrudeDryingRack::new);
        this.dryingRack = register(ModuleTechBasic.class, DryingRack::new);
        this.pitKiln = register(ModuleTechBasic.class, PitKiln::new);
        this.stoneKiln = register(ModuleTechMachine.class, StoneKiln::new);
        this.brickKiln = register(ModuleTechMachine.class, BrickKiln::new);
        this.anvil = register(ModuleTechBasic.class, Anvil::new);
        this.soakingPot = register(ModuleTechBasic.class, SoakingPot::new);
        this.tanningRack = register(ModuleTechBasic.class, TanningRack::new);
        this.stoneCrucible = register(ModuleTechMachine.class, StoneCrucible::new);
        this.brickCrucible = register(ModuleTechMachine.class, BrickCrucible::new);
    }

    private static <T> T register(Class<? extends ModuleBase> moduleClass, Supplier<T> supplier) {
        if (ModPyrotech.INSTANCE.isModuleEnabled(moduleClass)) {
            return supplier.get();
        }
        return null;
    }
}
