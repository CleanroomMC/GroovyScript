
import net.minecraft.item.ItemFood
import net.minecraft.potion.PotionEffect
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.world.IBlockAccess


// Localization is done in 'assets/placeholdername/lang/[language].lang'

// Textures for items are created at 'assets/placeholdername/textures/items/'.
// Add a file called '[itemname].png' to create a static item texture, and a file called
// '[itemname].png.mcmeta' to create an animated file.
// A file will be created in 'assets/placeholdername/models/item/' called '[itemname].json' and point to this location in textures.

def HOAU = content.createItem('heartofauniverse') // Set item name at 'item.[itemname].name=[desired name]'
    .setRarity(EnumRarity.EPIC) // Optional IRarity, sets the default text formatting (default none)
    .setMaxStackSize(1) // Optional int, sets the max stack size (default 64)
// Note: by not running '.register()' this item will not be created yet. This is done so we can set the creative tab correctly.

// Create the creative tab, using the not-yet-registered item
def tab = content.createCreativeTab('groovyscript.example_creative_tab', HOAU)

// When registering items, this will add them to the given creative tab without having to manually do so.
content.setDefaultCreativeTab(tab)

// Now, we register to HOAU item.
HOAU.register()


// Create an item at the location 'placeholdername:clay_2'
content.createItem('clay_2')
    .setMaxStackSize(5) // Optional int, sets the max stack size (default 64)
    .setRarity(EnumRarity.RARE) // Optional IRarity, sets the default text formatting (default none)
    .register()

// Create an item at the location 'placeholdername:clay_3'.
content.createItem('clay_3')
    .setCreativeTab(creativeTab('misc')) // Optional CreativeTab, sets the creative tab (default set via setDefaultCreativeTab)
    .setEnchantedEffect() // Optional boolean, controls if the enchanted effect plays on the item
    .register()

content.createFluid('amongium')
    .setMetalTexture()
    .setColor(0x00FF00)
    .register()

// You can register any item created, even items created via custom means.
content.registerItem('snack', (new ItemFood(20, 10, false) {
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        if (!worldIn.isRemote) {
            player.addPotionEffect(new PotionEffect(potion('regeneration'), 240000, 3, false, false))
            player.addPotionEffect(new PotionEffect(potion('resistance'), 240000, 3, false, false))
        }
    }
}).setAlwaysEdible())



// block

// Textures for blocks are created at 'assets/placeholdername/textures/blocks/'.
// Add a file called '[blockname].png' to create a static item texture, and a file called
// '[blockname].png.mcmeta' to create an animated file.
// A file will be created in 'assets/placeholdername/models/block/' called '[blockname].json' and point to this location in textures.

// Create a block at the location 'placeholdername:generic_block'
content.createBlock('generic_block')// Set block name at 'tile.[blockname].name=[desired name]'
    .register()

// Create a custom block at the location 'placeholdername:dragon_egg_lamp'
// Also changes the 'parent' setting in 'assets/placeholdername/models/block/[blockname].json' from 'block/cube_all' to 'block/dragon_egg'
content.registerBlock('dragon_egg_lamp', (new Block(Material.REDSTONE_LIGHT) {
    protected static final AxisAlignedBB DRAGON_EGG_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 1.0D, 0.9375D)

    AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return DRAGON_EGG_AABB
    }

    boolean isOpaqueCube(IBlockState state) {
        return false
    }

    boolean isFullCube(IBlockState state) {
        return false
    }

    boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true
    }

    BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED
    }
}).setLightLevel(1.0F))
