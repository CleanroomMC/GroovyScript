
// this file demonstrates custom reloading compat
// also demonstrating some event manager interactions

import classes.GenericRecipeReloading
import classes.SimpleConversionRecipe
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.event.world.BlockEvent
import com.cleanroommc.groovyscript.event.GroovyReloadEvent

// add the example recipe
GenericRecipeReloading.instance.add(new SimpleConversionRecipe(item('minecraft:clay'), item('minecraft:gold_ingot')))

// reload via an event
eventManager.listen(GroovyReloadEvent) {
    GenericRecipeReloading.instance.onReload()
}

// use an event to demonstrate the recipe in-game
eventManager.listen(BlockEvent.BreakEvent) {
    // get the block drop if it was silk touched
    def drop = it.getState().getBlock().getSilkTouchDrop(it.getState())
    // find if any of the recipes have an input that match the drop
    def found = SimpleConversionRecipe.recipes.find { it.input in drop }
    if (found != null) {
        // send the player a pair of messages to demonstrate the recipe
        it.player.sendMessage(new TextComponentString("You broke a ${it.getState().getBlock().getLocalizedName()} Block!"))
        it.player.sendMessage(new TextComponentString("A custom recipe marks a ${found.output.getDisplayName()} as its output."))
    }
}