
// side: client

import net.minecraft.client.gui.GuiMainMenu
import net.minecraftforge.client.event.GuiOpenEvent

// not a typo
GuiMainMenu.metaClass.makePublic('minceraftRoll')
GuiMainMenu.metaClass.makeMutable('minceraftRoll')

eventManager.listen(GuiOpenEvent) {
    if (gui instanceof GuiMainMenu) {
        // value is randomly set in constructor and checked if its smaller than 1e-4 during rendering
        // this forces the minceraft title to always activate
        gui.minceraftRoll = 0.00001
    }
}
