
// Auto generated groovyscript example file

import net.minecraft.item.ItemFood
import net.minecraft.world.IBlockAccess
import net.minecraft.potion.PotionEffect
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.entity.player.EntityPlayer

log 'running Vanilla Minecraft example'

// Vanilla Content Creation:
// Creates custom items, blocks, and fluids for later use.

content.setDefaultCreativeTab(content.createCreativeTab('groovyscript.example_creative_tab', _ -> item('groovyscriptdev:heartofauniverse')))

content.registerItem('snack', (new ItemFood(20, 10, false) {
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        if (!worldIn.isRemote) {
            player.addPotionEffect(new PotionEffect(potion('minecraft:regeneration'), 240000, 3, false, false))
            player.addPotionEffect(new PotionEffect(potion('minecraft:resistance'), 240000, 3, false, false))
        }
    }
}).setAlwaysEdible())
content.registerBlock('dragon_egg_lamp', (new Block(blockMaterial('redstone_light')) {
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
content.createCreativeTab('groovyscript.other_tab_clay', _ -> item('minecraft:clay'))
