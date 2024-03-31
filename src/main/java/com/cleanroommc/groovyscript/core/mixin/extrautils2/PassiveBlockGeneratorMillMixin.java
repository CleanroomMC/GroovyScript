package com.cleanroommc.groovyscript.core.mixin.extrautils2;

import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import com.rwtema.extrautils2.tile.TilePassiveGenerator;
import groovy.lang.Closure;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {
        "com/rwtema/extrautils2/blocks/BlockPassiveGenerator$GeneratorType$1",
        "com/rwtema/extrautils2/blocks/BlockPassiveGenerator$GeneratorType$2",
        "com/rwtema/extrautils2/blocks/BlockPassiveGenerator$GeneratorType$3",
        "com/rwtema/extrautils2/blocks/BlockPassiveGenerator$GeneratorType$4",
        "com/rwtema/extrautils2/blocks/BlockPassiveGenerator$GeneratorType$5",
        "com/rwtema/extrautils2/blocks/BlockPassiveGenerator$GeneratorType$6",
        "com/rwtema/extrautils2/blocks/BlockPassiveGenerator$GeneratorType$7",
        "com/rwtema/extrautils2/blocks/BlockPassiveGenerator$GeneratorType$8",
        "com/rwtema/extrautils2/blocks/BlockPassiveGenerator$GeneratorType$9",
}, remap = false)
public class PassiveBlockGeneratorMillMixin {

    @Inject(method = "basePowerGen", at = @At("HEAD"), cancellable = true)
    public void basePowerGen(CallbackInfoReturnable<Float> cir) {
        Float value = ModSupport.EXTRA_UTILITIES_2.get().gridPowerPassiveGenerator.basePowerMap.get(((GeneratorTypeAccessor) this).getKey());
        if (value != null) {
            cir.setReturnValue(value);
        }
    }

    @Inject(method = "getPowerLevel", at = @At("HEAD"), cancellable = true)
    public void getPowerLevel(TilePassiveGenerator generator, World world, CallbackInfoReturnable<Float> cir) {
        Closure<Float> value = ModSupport.EXTRA_UTILITIES_2.get().gridPowerPassiveGenerator.powerLevelMap.get(((GeneratorTypeAccessor) this).getKey());
        if (value != null) {
            cir.setReturnValue(ClosureHelper.call(0f, value, generator, world));
        }
    }

}
