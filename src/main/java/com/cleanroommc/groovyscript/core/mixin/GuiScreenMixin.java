package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.command.CustomClickAction;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiScreen.class)
public class GuiScreenMixin {

    @Inject(method = "handleComponentClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;sendChatMessage(Ljava/lang/String;Z)V", shift = At.Shift.BEFORE), cancellable = true)
    public void handleClickComponent(ITextComponent component, CallbackInfoReturnable<Boolean> cir) {
        ClickEvent clickEvent = component.getStyle().getClickEvent();
        if (clickEvent == null) return;
        String value = clickEvent.getValue();
        if (value.startsWith(CustomClickAction.PREFIX) && CustomClickAction.runActionHook(value.substring(CustomClickAction.PREFIX.length()))) {
            cir.setReturnValue(true);
        }
    }
}
