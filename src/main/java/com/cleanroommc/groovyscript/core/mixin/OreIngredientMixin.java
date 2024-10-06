package com.cleanroommc.groovyscript.core.mixin;

import net.minecraftforge.oredict.OreIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = OreIngredient.class, remap = false)
public class OreIngredientMixin {

    @Unique
    private String groovyScript$oreDict;

    @Inject(at = @At("TAIL"), method = "<init>(Ljava/lang/String;)V")
    private void init(String ore, CallbackInfo ci) {
        groovyScript$oreDict = ore;
    }

    @Unique
    public String getOreDict() {
        return groovyScript$oreDict;
    }
}
