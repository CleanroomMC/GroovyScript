
// Auto generated groovyscript example file
// MODS_LOADED: betterwithaddons

log.info 'mod \'betterwithaddons\' detected, running script'

// Drying Unit:
// Converts an input item into an output itemstack if placed within the appropriate multiblock. The multiblock is Sandstone
// directly below the Drying Box, 8 Sand around the Drying Box, and a Dead Bush placed on the Sand. Only functions in a
// non-snowy biome with sky access during the day, and functions twice as fast when in a hot biome.

mods.betterwithaddons.drying_box.removeByInput(item('betterwithaddons:japanmat:2'))
mods.betterwithaddons.drying_box.removeByOutput(item('minecraft:sponge'))
// mods.betterwithaddons.drying_box.removeAll()

mods.betterwithaddons.drying_box.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .register()

mods.betterwithaddons.drying_box.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay') * 4)
    .register()


// Fire Net:
// Converts an input item into any number of output itemstacks if placed within the appropriate multiblock. The multiblock
// is Lava or Fire directly below the Netted Screen, 8 Stone Brick around the Lava or Fire, and 8 Slat Blocks placed around
// the Netted Screen.

mods.betterwithaddons.fire_net.removeByInput(item('betterwithaddons:iron_sand'))
mods.betterwithaddons.fire_net.removeByOutput(item('betterwithaddons:japanmat:12'))
// mods.betterwithaddons.fire_net.removeAll()

mods.betterwithaddons.fire_net.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .register()

mods.betterwithaddons.fire_net.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay') * 4, item('minecraft:diamond'), item('minecraft:diamond') * 2)
    .register()


// Ancestral Infusion Crafting:
// Converts a custom crafting recipe an output itemstack, consuming Spirits from the Infused Soul Sand placed below the
// Ancestral Infuser if placed within the appropriate multiblock. The multiblock is either Soul Sand or Infused Soul Sand
// placed below the Ancestral Infuser and exclusively air blocks adjacent to the Infuser and Soul Sand blocks.

mods.betterwithaddons.infuser.removeByInput(item('betterwithaddons:japanmat:16'))
mods.betterwithaddons.infuser.removeByOutput(item('betterwithaddons:ya'))
// mods.betterwithaddons.infuser.removeAll()

mods.betterwithaddons.infuser.shapedBuilder()
    .output(item('minecraft:stone'))
    .matrix('BXX',
            'X B')
    .key('B', item('minecraft:stone'))
    .key('X', item('minecraft:gold_ingot'))
    .spirits(1)
    .mirrored()
    .register()

mods.betterwithaddons.infuser.shapedBuilder()
    .output(item('minecraft:diamond') * 32)
    .matrix([[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],
            [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')]])
    .spirits(6)
    .register()

mods.betterwithaddons.infuser.shapelessBuilder()
    .output(item('minecraft:clay') * 8)
    .input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'))
    .register()

mods.betterwithaddons.infuser.shapelessBuilder()
    .output(item('minecraft:clay') * 32)
    .input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'))
    .spirits(8)
    .register()


// Alicio Tree Foods:
// Converts an input item into an amount of food for the tree to gradually consume, eventually summoning a random creature
// nearby.

mods.betterwithaddons.lure_tree.removeByInput(item('minecraft:rotten_flesh'))
// mods.betterwithaddons.lure_tree.removeAll()

mods.betterwithaddons.lure_tree.recipeBuilder()
    .input(item('minecraft:diamond'))
    .food(1000)
    .register()

mods.betterwithaddons.lure_tree.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .food(4)
    .register()


mods.betterwithaddons.lure_tree.addBlacklist(entity('minecraft:chicken'))

// Packing:
// Converts an input itemstack in the form of a EntityItems into an IBlockState after a piston extends if the piston and
// location the EntityItems are in are fully surrounded by solid blocks.

mods.betterwithaddons.packing.removeByInput(item('minecraft:clay_ball'))
mods.betterwithaddons.packing.removeByOutput(blockstate('minecraft:gravel'))
// mods.betterwithaddons.packing.removeAll()

mods.betterwithaddons.packing.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .compress(blockstate('minecraft:clay'))
    .register()

mods.betterwithaddons.packing.recipeBuilder()
    .input(item('minecraft:clay') * 10)
    .compress(blockstate('minecraft:diamond_block'))
    .register()

mods.betterwithaddons.packing.recipeBuilder()
    .input(item('minecraft:diamond'))
    .compress(blockstate('minecraft:dirt'))
    .jeiOutput(item('minecraft:diamond') * 64)
    .register()


// Rotting Food:
// Converts an input item into an output itemstack after the given time has passed. Has the ability to customize the
// terminology used to indicate the age.

mods.betterwithaddons.rotting.removeByInput(item('betterwithaddons:food_cooked_rice'))
mods.betterwithaddons.rotting.removeByOutput(item('minecraft:rotten_flesh'))
// mods.betterwithaddons.rotting.removeAll()

mods.betterwithaddons.rotting.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .register()

mods.betterwithaddons.rotting.recipeBuilder()
    .input(item('placeholdername:snack'))
    .time(100)
    .key('groovy_example')
    .rotted(item('minecraft:clay') * 4)
    .register()


// Sand Net:
// Converts an input item into any number of output itemstacks if placed within the appropriate multiblock. The multiblock
// is a Slat Block directly below the Netted Screen, 8 Water Blocks around the Water, and 8 Slat Blocks placed around the
// Netted Screen.

mods.betterwithaddons.sand_net.removeByInput(item('minecraft:iron_ingot'))
mods.betterwithaddons.sand_net.removeByOutput(item('minecraft:sand'))
mods.betterwithaddons.sand_net.removeByOutput(item('betterwithaddons:iron_sand'))
// mods.betterwithaddons.sand_net.removeAll()

mods.betterwithaddons.sand_net.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .register()

mods.betterwithaddons.sand_net.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:gold_ingot'))
    .sand(2)
    .register()

mods.betterwithaddons.sand_net.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay') * 4, item('minecraft:diamond'), item('minecraft:diamond') * 2)
    .sand(5)
    .register()


// Soaking Unit:
// Converts an input item into an output itemstack if placed within the appropriate multiblock. The multiblock is Ice
// directly above the Soaking Box, 8 Water around the Soaking Box, and Water directly below the Soaking Box.

mods.betterwithaddons.soaking_box.removeByInput(item('betterwithaddons:bamboo'))
mods.betterwithaddons.soaking_box.removeByOutput(item('betterwithaddons:japanmat:8'))
// mods.betterwithaddons.soaking_box.removeAll()

mods.betterwithaddons.soaking_box.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .register()

mods.betterwithaddons.soaking_box.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay') * 4)
    .register()


// Spindle:
// Converts an input itemstack into an output itemstack, with the ability to consume the Spindle, when placed against a
// Spinning Wheel powered by Mechanical Power.

mods.betterwithaddons.spindle.removeByInput(item('minecraft:vine'))
mods.betterwithaddons.spindle.removeByOutput(item('betterwithaddons:bolt'))
// mods.betterwithaddons.spindle.removeAll()

mods.betterwithaddons.spindle.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .register()

mods.betterwithaddons.spindle.recipeBuilder()
    .input(item('minecraft:clay') * 3)
    .output(item('minecraft:diamond'))
    .popoff()
    .register()

mods.betterwithaddons.spindle.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay') * 4)
    .register()


// Tatara:
// Converts an input item into an output itemstack if placed within the appropriate multiblock while fueled by Rice Ashes.
// The multiblock is Lava or Fire directly below the Tatara, 8 Clay around the Lava or Fire, 9 Nether Brick above the
// Tatara, 4 Stone Brick diagonal to the Tatara and two Iron Blocks across from each other adjacent to the Tatara.

mods.betterwithaddons.tatara.removeByInput(item('betterwithaddons:japanmat:20'))
mods.betterwithaddons.tatara.removeByOutput(item('betterwithaddons:kera'))
// mods.betterwithaddons.tatara.removeAll()

mods.betterwithaddons.tatara.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .register()

mods.betterwithaddons.tatara.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay') * 4)
    .register()


// Ancestral Infusion Transmutation:
// Converts an input item into an output itemstack, consuming Spirits from the Infused Soul Sand placed below the Ancestral
// Infuser if placed within the appropriate multiblock. The multiblock is either Soul Sand or Infused Soul Sand placed
// below the Ancestral Infuser and exclusively air blocks adjacent to the Infuser and Soul Sand blocks.

mods.betterwithaddons.transmutation.removeByInput(item('minecraft:reeds'))
mods.betterwithaddons.transmutation.removeByOutput(item('betterwithaddons:crop_rice'))
// mods.betterwithaddons.transmutation.removeAll()

mods.betterwithaddons.transmutation.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .spirits(0)
    .register()

mods.betterwithaddons.transmutation.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay') * 4)
    .spirits(5)
    .register()


// Water Net:
// Converts an input item into any number of output itemstacks if placed within the appropriate multiblock. The multiblock
// is a Water Block directly below the Netted Screen, 8 Sakura Planks around the Water Block, and 8 Slat Blocks placed
// around the Netted Screen.

mods.betterwithaddons.water_net.removeByInput(item('betterwithaddons:iron_sand'))
mods.betterwithaddons.water_net.removeByOutput(item('betterwithaddons:food_sashimi'))
mods.betterwithaddons.water_net.removeByOutput(item('betterwithaddons:food_fugu_sac'))
// mods.betterwithaddons.water_net.removeAll()

mods.betterwithaddons.water_net.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .register()

mods.betterwithaddons.water_net.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay') * 4, item('minecraft:diamond'), item('minecraft:diamond') * 2)
    .register()


