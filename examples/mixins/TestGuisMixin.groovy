// X mods_loaded: modularui

import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.test.TestGuis;

@Mixin(value = TestGuis.class, remap = false)
public abstract class TestGuisMixin {

    @Shadow
    public abstract ModularPanel buildToggleGridListUI(ModularGuiContext context);

    @Inject(method = "buildUI", at = @At("HEAD"), cancellable = true)
    public void buildUI(ModularGuiContext context, CallbackInfoReturnable<ModularPanel> cir) {
        cir.setReturnValue(buildToggleGridListUI(context));
    }
}
