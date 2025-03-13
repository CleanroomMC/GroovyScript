package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.sandbox.LoadStage;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.ModContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.InvocationTargetException;

@Mixin(value = LoadController.class, remap = false)
public class LoaderControllerMixin {

    @Inject(method = "distributeStateMessage(Lnet/minecraftforge/fml/common/LoaderState;[Ljava/lang/Object;)V", at = @At("HEAD"))
    public void preInit(LoaderState state, Object[] eventData, CallbackInfo ci) {
        if (state == LoaderState.PREINITIALIZATION) {
            GroovyScript.initializeGroovyPreInit();
        }
        if (state == LoaderState.POSTINITIALIZATION) {
            GroovyScript.runGroovyScriptsInLoader(LoadStage.INIT);
        }
        if (state == LoaderState.AVAILABLE) {
            GroovyScript.runGroovyScriptsInLoader(LoadStage.POST_INIT);
        }
    }

    @Inject(method = "errorOccurred", at = @At(value = "NEW", target = "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)Lorg/apache/logging/log4j/message/FormattedMessage;", shift = At.Shift.BEFORE))
    public void errorOccured(ModContainer modContainer, Throwable exception, CallbackInfo ci) throws Throwable {
        if (exception instanceof InvocationTargetException) {
            exception = exception.getCause();
        }
        if (exception instanceof CustomModLoadingErrorDisplayException) {
            // normally every exception gets wrapped in a LoaderException making these """custom""" exceptions useless, thanks cpw
            throw exception;
        }
    }
}
