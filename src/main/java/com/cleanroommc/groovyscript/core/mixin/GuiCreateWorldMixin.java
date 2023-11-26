package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.packmode.Packmode;
import com.cleanroommc.groovyscript.packmode.PackmodeButton;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.LoadStage;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiCreateWorld.class)
public class GuiCreateWorldMixin extends GuiScreen {

    @Shadow
    private GuiButton btnGameMode;
    @Shadow
    private String gameModeDesc1;
    @Shadow
    private String gameModeDesc2;
    @Unique
    @Mutable
    private PackmodeButton packmodeButton;

    @Inject(method = "initGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiCreateWorld;showMoreWorldOptions(Z)V", shift = At.Shift.BEFORE))
    public void init(CallbackInfo ci) {
        GuiCreateWorld this$0 = (GuiCreateWorld) (Object) this;
        this.packmodeButton = addButton(new PackmodeButton(65, this$0.width / 2 + 25, 115, 150, 20));
        this.btnGameMode.x = this$0.width / 2 - 175;
    }

    @Inject(method = "showMoreWorldOptions", at = @At("HEAD"))
    public void showMoreWorldOptions(boolean toggle, CallbackInfo ci) {
        this.packmodeButton.visible = !toggle;
    }

    @Inject(method = "actionPerformed", at = @At("TAIL"))
    public void actionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.id == 65) {
            this.packmodeButton.updatePackmode();
        }
    }

    @Inject(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;launchIntegratedServer(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/world/WorldSettings;)V", shift = At.Shift.BEFORE))
    public void actionPerformed2(GuiButton button, CallbackInfo ci) {
        if (Packmode.needsPackmode()) {
            Packmode.updatePackmode(this.packmodeButton.getPackmode());
            GroovyScript.runGroovyScriptsInLoader(LoadStage.POST_INIT);
            ReloadableRegistryManager.reloadJei(false);
        }
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiCreateWorld;drawString(Lnet/minecraft/client/gui/FontRenderer;Ljava/lang/String;III)V", ordinal = 6, shift = At.Shift.BEFORE), cancellable = true)
    public void draw(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        GuiCreateWorld this$0 = (GuiCreateWorld) (Object) this;
        String s = this.gameModeDesc1 + " " + this.gameModeDesc2;
        this.fontRenderer.drawSplitString(s, this$0.width / 2 - 175, 137, 150, -6250336);
        if (Packmode.needsPackmode() && this.packmodeButton.getDesc() != null) {
            s = this.packmodeButton.getDesc();
            this.fontRenderer.drawSplitString(s, this$0.width / 2 + 25, 137, 150, -6250336);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        ci.cancel();
    }
}
