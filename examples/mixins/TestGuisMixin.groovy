// mods_loaded: modularui
// side: client

import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.test.TestGuis;
import lib.ui

@Mixin(value = TestGuis.class, remap = false)
public abstract class TestGuisMixin {

    @Shadow
    public abstract ModularPanel buildToggleGridListUI(ModularGuiContext context);

    @Inject(method = "buildUI", at = @At("HEAD"), cancellable = true)
    public void buildUI(ModularGuiContext context, CallbackInfoReturnable<ModularPanel> cir) {
        //cir.setReturnValue(buildToggleGridListUI(context));
        cir.setReturnValue(ui.buildUI(context));
    }
}
