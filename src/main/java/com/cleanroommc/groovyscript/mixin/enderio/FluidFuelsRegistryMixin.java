package com.cleanroommc.groovyscript.mixin.enderio;

import com.cleanroommc.groovyscript.compat.enderio.recipe.IEnderIOFuelRegistry;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import crazypants.enderio.base.fluid.FluidFuelRegister;
import crazypants.enderio.base.fluid.IFluidCoolant;
import crazypants.enderio.base.fluid.IFluidFuel;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraftforge.fluids.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = FluidFuelRegister.class, remap = false)
public class FluidFuelsRegistryMixin implements IEnderIOFuelRegistry {

    @Shadow
    @Final
    private Map<String, IFluidCoolant> coolants;

    @Shadow
    @Final
    private Map<String, IFluidFuel> fuels;

    @Unique
    @Final
    private Map<String, IFluidCoolant> coolantsBackup = new Object2ObjectOpenHashMap<>();
    @Unique
    @Final
    private Map<String, IFluidFuel> fuelsBackup = new Object2ObjectOpenHashMap<>();

    @Override
    public void removeCoolant(Fluid fluid) {
        coolants.remove(fluid.getName());
    }

    @Override
    public void removeFuel(Fluid fluid) {
        fuels.remove(fluid.getName());
    }

    @Override
    public void onReload() {
        coolants.clear();
        fuels.clear();
        coolants.putAll(coolantsBackup);
        fuels.putAll(fuelsBackup);
    }

    @Override
    public void removeEntry(Void unused) {
        throw new UnsupportedOperationException("Use removeCoolant or removeFuel instead!");
    }

    @Inject(method = "addFuel(Lnet/minecraftforge/fluids/Fluid;II)V", at = @At("RETURN"))
    public void addFuel(Fluid fluid, int powerPerCycleRF, int totalBurnTime, CallbackInfo ci) {
        if (!ReloadableRegistryManager.isShouldRegisterAsReloadable()) {
            fuelsBackup.put(fluid.getName(), fuels.get(fluid.getName()));
        }
    }

    @Inject(method = "addCoolant(Lnet/minecraftforge/fluids/Fluid;F)V", at = @At("RETURN"))
    public void addCoolant(Fluid fluid, float degreesCoolingPerMB, CallbackInfo ci) {
        if (!ReloadableRegistryManager.isShouldRegisterAsReloadable()) {
            coolantsBackup.put(fluid.getName(), coolants.get(fluid.getName()));
        }
    }
}
