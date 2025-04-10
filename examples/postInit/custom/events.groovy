
import net.minecraftforge.event.entity.living.EnderTeleportEvent
import net.minecraftforge.event.world.BlockEvent
import net.minecraft.util.text.TextComponentString


// Use eventManager.listen and listen to the desired event.
/*eventManager.listen(BlockEvent.BreakEvent) {
    it.setCanceled(true) // Many events can be canceled.
    it.player.sendMessage(new TextComponentString("${it.getState().getBlock().getLocalizedName()} Block was prevent from being broken"))
}*/

// The outer parentheses and inner curly braces are optional.
eventManager.listen(EnderTeleportEvent) { event ->
    event.setAttackDamage 19.5f
}
