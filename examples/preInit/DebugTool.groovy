//
/*
 * This is an example of high end custom content.
 *
 * This code creates the Debug Stick from 1.13+ in 1.12.2,
 * which allows easily changing the blockstate in-world.
 * A functional difference from the 1.13+ version is that this
 * can also function in survival mode, and has less bugs.
 *
 * A number of comments have been added to help understand it.
 */

// import the classes that are used in this script
import com.cleanroommc.groovyscript.compat.content.GroovyItem
import net.minecraft.block.properties.IProperty
import net.minecraft.util.EnumActionResult
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.event.entity.player.PlayerInteractEvent

// a number of static methods have been created as shorthand

/**
 * @return the next blockstate in the series of the focused property, respective of direction
 */
static <T extends Comparable<T>> IBlockState cycleState(IBlockState state, IProperty<T> property, boolean invert) {
    state.withProperty(property, getRelative(property.getAllowedValues(), state.getValue(property), invert))
}

/**
 * the next or prior type relative to the target
 */
static <T> T getRelative(Collection<T> allowedValues, T currentValue, boolean invert) {
    int index = allowedValues.findIndexOf({ it == currentValue })
    if (index === -1) return allowedValues[0]
    int target = index + (invert ? 1 : -1)
    allowedValues[allowedValues.size() <= target ? 0 : target]
}

/**
 * get the name of the property for the blockstate
 */
static <T extends Comparable<T>> String getPropertyName(IBlockState state, IProperty<T> property) {
    property.getName(state.getValue(property))
}

/**
 * send a status message to the player - it appears above the hotbar
 */
static void message(EntityPlayer player, ITextComponent message) {
    player.sendStatusMessage(message, true)
}

/**
 * rotate through the states of the property or through type of property being targeted for the given blockstate interacted with
 */
static void handleInteraction(EntityPlayer player, World world, BlockPos pos, boolean shouldCycleState, ItemStack debugStick) {
    def state = world.getBlockState(pos)
    def block = state.getBlock()
    def collection = state.getPropertyKeys()
    def key = block.getRegistryName().toString()
    if (collection.isEmpty()) {
        message(player, new TextComponentTranslation(debugStick.getTranslationKey() + ".empty", key))
        return
    }
    def data = debugStick.getTagCompound()
    if (data == null) {
        data = nbt()
        debugStick.setTagCompound(data)
    }

    def targetBlock = data.getString(key)

    def property = state.getProperties().keySet().findResult({ it.getName() == targetBlock ? it : null })

    if (shouldCycleState) {
        if (property == null) {
            property = collection.iterator().next()
        }
        def blockstate = cycleState(state, property, player.isSneaking())
        world.setBlockState(pos, blockstate, 2 | 8 | 16)
        message(player, new TextComponentTranslation(debugStick.getTranslationKey() + ".update", property.getName(), getPropertyName(blockstate, property)))
    } else {
        property = getRelative(collection, property, player.isSneaking())
        def newString = property.getName()
        data.setString(key, newString)
        message(player, new TextComponentTranslation(debugStick.getTranslationKey() + ".select", newString, getPropertyName(state, property)))
    }
}

/**
 * this extends GroovyItem to make it easier add custom effects and register it.
 * any other class could be used instead
 */
class ItemDebugStick extends GroovyItem {

    /**
     * sets the name of this item - a required part of GroovyItem
     * will check the "groovyscriptdev/models/item/debug_stick.json" file for its texture
     * which has been edited to point to the texture of "minecraft:stick"
     */
    ItemDebugStick() {
        super('debug_stick')
    }

    /**
     * ensure that the debug stick cannot mine blocks in survival
     */
    float getDestroySpeed(ItemStack stack, IBlockState state) {
        0.0f
    }

    /**
     * you can override methods, this overrides interacting with the item
     */
    EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (!world.isRemote) DebugTool.handleInteraction(player, world, pos, true, player.getHeldItem(hand))
        EnumActionResult.SUCCESS
    }

    /**
     * the debug stick should not destroy blocks in creative
     */
    boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player) {
        false
    }
}

// this creates and registers the custom item
new ItemDebugStick()
    .setCreativeTab(creativeTab('tools')) // sets the tab the debug stick is added to
    // these three methods are unique to GroovyItem
    .setRarity(EnumRarity.EPIC) // sets the color of the name
    .setEnchantedEffect() // makes the item have the shimmer effect as if it was enchanted
    .register() // the same as `content.registerItem(item.getRegistryName().getPath(), item)`


// since there isnt a method to check for "left click block" (only "destroy block") in item, we use an event
// this could also occur in postInit! further, if it was in postInit it could be reloaded.
event_manager.listen { PlayerInteractEvent.LeftClickBlock event ->
    def player = event.getEntityPlayer()
    if (player.world.isRemote) return
    def stack = player.getHeldItem(event.getHand())
    if (stack.getItem() instanceof ItemDebugStick) { // could also do `stack in item('groovyscriptdev:debug_stick')`
        // only operate if in creative or if its not immediately after a prior swing
        if (player.isCreative() || player.ticksSinceLastSwing != 1) handleInteraction(player, player.world, event.getPos(), false, stack)
        event.setCanceled(true) // cancel the normal operations
    }
}
