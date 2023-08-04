package com.cleanroommc.groovyscript.compat.mods.enderio;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class EnderIO extends ModPropertyContainer {

    public final AlloySmelter alloySmelter = new AlloySmelter();
    public final FluidFuel fluidFuel = new FluidFuel();
    public final FluidCoolant fluidCoolant = new FluidCoolant();
    public final Enchanter enchanter = new Enchanter();
    public final SagMill sagMill = new SagMill();
    public final SagMillGrinding sagMillGrinding = new SagMillGrinding();
    public final SliceNSplice sliceNSplice = new SliceNSplice();
    public final SoulBinder soulBinder = new SoulBinder();
    public final Tank tank = new Tank();
    public final Vat vat = new Vat();

    public EnderIO() {
        addRegistry(alloySmelter);
        addRegistry(fluidFuel);
        addRegistry(fluidCoolant);
        addRegistry(enchanter);
        addRegistry(sagMill);
        addRegistry(sagMillGrinding);
        addRegistry(sliceNSplice);
        addRegistry(soulBinder);
        addRegistry(tank);
        addRegistry(vat);
    }

}
