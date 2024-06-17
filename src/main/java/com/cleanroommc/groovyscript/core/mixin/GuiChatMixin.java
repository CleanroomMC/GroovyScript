package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.command.CustomClickAction;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiChat.class)
public class GuiChatMixin {

    @WrapOperation(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiChat;handleComponentClick(Lnet/minecraft/util/text/ITextComponent;)Z"))
    private boolean handleClickComponent(GuiChat instance, ITextComponent component, Operation<Boolean> original) {
        ClickEvent clickEvent = component.getStyle().getClickEvent();
        if (clickEvent != null) {
            String value = clickEvent.getValue();
            if (value.startsWith(CustomClickAction.PREFIX) && CustomClickAction.runActionHook(value.substring(CustomClickAction.PREFIX.length()))) {
                // we have already handled the click logic
                return false;
            }
        }
        return original.call(instance, component);
    }
}
