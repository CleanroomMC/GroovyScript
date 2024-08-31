
// Auto generated groovyscript example file
// MODS_LOADED: advancedrocketry

log.info 'mod \'advancedrocketry\' detected, running script'

// Centrifuge:
// Converts an input fluid into up to 12 output items and up to 4 output fluids, consuming RF.

mods.advancedrocketry.centrifuge.removeByInput(fluid('enrichedlava'))
// mods.advancedrocketry.centrifuge.removeAll()

mods.advancedrocketry.centrifuge.recipeBuilder()
    .fluidInput(fluid('lava') * 500)
    .output(item('minecraft:slime_ball'), 0.1f)
    .output(item('minecraft:stone'), 0.9f)
    .fluidOutput(fluid('enrichedlava') * 500)
    .power(50)
    .time(100)
    .outputSize(1)
    .register()


// Chemical Reactor:
// Converts up to 2 fluids and 8 input items into up to 1 fluid and up to 4 output items, consuming RF.

mods.advancedrocketry.chemical_reactor.removeByInput(item('minecraft:bone'))
mods.advancedrocketry.chemical_reactor.removeByOutput(item('minecraft:leather_helmet'))
// mods.advancedrocketry.chemical_reactor.removeAll()

mods.advancedrocketry.chemical_reactor.recipeBuilder()
    .input(item('minecraft:chorus_fruit_popped'))
    .fluidInput(fluid('lava') * 500)
    .output(item('minecraft:end_rod') * 4)
    .fluidOutput(fluid('water') * 500)
    .power(50)
    .time(100)
    .register()


// Crystallizer:
// Converts up to 4 input items into up to 4 output items, consuming RF.

mods.advancedrocketry.crystallizer.removeByInput(item('libvulpes:productingot', 3))
mods.advancedrocketry.crystallizer.removeByOutput(item('libvulpes:productgem'))
// mods.advancedrocketry.crystallizer.removeAll()

mods.advancedrocketry.crystallizer.recipeBuilder()
    .input(item('minecraft:blaze_powder') * 4)
    .output(item('minecraft:blaze_rod'))
    .power(50)
    .time(100)
    .register()


// Cutting Machine:
// Converts up to 4 input items into up to 4 output items, consuming RF.

mods.advancedrocketry.cutting_machine.removeByInput(item('advancedrocketry:alienwood'))
mods.advancedrocketry.cutting_machine.removeByOutput(item('minecraft:planks', 1))
// mods.advancedrocketry.cutting_machine.removeAll()

mods.advancedrocketry.cutting_machine.recipeBuilder()
    .input(item('minecraft:blaze_rod'))
    .output(item('minecraft:blaze_powder') * 4)
    .power(50)
    .time(100)
    .register()


// Electric Arc Furnace:
// Converts input items and fluids into output items and fluids, consuming RF.

mods.advancedrocketry.electric_arc_furnace.removeByInput(item('minecraft:iron_ingot'))
mods.advancedrocketry.electric_arc_furnace.removeByOutput(item('libvulpes:productingot', 3))
// mods.advancedrocketry.electric_arc_furnace.removeAll()

mods.advancedrocketry.electric_arc_furnace.recipeBuilder()
    .input(item('minecraft:blaze_powder') * 4)
    .output(item('minecraft:blaze_rod'))
    .power(50)
    .time(100)
    .register()


// Electrolyser:
// Converts an input fluid into up to 2 output fluids, consuming RF.

mods.advancedrocketry.electrolyser.removeByInput(fluid('water'))
// mods.advancedrocketry.electrolyser.removeByOutput(fluid('oxygen'))
// mods.advancedrocketry.electrolyser.removeAll()

mods.advancedrocketry.electrolyser.recipeBuilder()
    .fluidInput(fluid('lava') * 10)
    .fluidOutput(fluid('nitrogen') * 50)
    .power(50)
    .time(100)
    .register()


// Lathe:
// Converts up to 4 input items into up to 4 output items, consuming RF.

mods.advancedrocketry.lathe.removeByInput(item('libvulpes:productingot', 6))
mods.advancedrocketry.lathe.removeByOutput(item('libvulpes:productrod', 4))
// mods.advancedrocketry.lathe.removeAll()

mods.advancedrocketry.lathe.recipeBuilder()
    .input(ore('plankWood'))
    .output(item('minecraft:stick') * 2)
    .power(50)
    .time(100)
    .register()


// Precision Assembler:
// Converts input items and fluids into output items and fluids, consuming RF.

mods.advancedrocketry.precision_assembler.removeByInput(item('minecraft:redstone_block'))
mods.advancedrocketry.precision_assembler.removeByOutput(item('advancedrocketry:atmanalyser'))
// mods.advancedrocketry.precision_assembler.removeAll()

mods.advancedrocketry.precision_assembler.recipeBuilder()
    .input(item('minecraft:fishing_rod'), item('minecraft:carrot'))
    .output(item('minecraft:carrot_on_a_stick'))
    .power(50)
    .time(100)
    .register()


// Precision Laser Etcher:
// Converts up to 4 input items into up to 4 output items, consuming RF.

mods.advancedrocketry.precision_laser_etcher.removeByInput(item('minecraft:redstone_block'))
mods.advancedrocketry.precision_laser_etcher.removeByOutput(item('advancedrocketry:itemcircuitplate'))
// mods.advancedrocketry.precision_laser_etcher.removeAll()

mods.advancedrocketry.precision_laser_etcher.recipeBuilder()
    .input(item('minecraft:blaze_powder') * 4, item('advancedrocketry:wafer'))
    .output(item('advancedrocketry:itemcircuitplate'))
    .power(50)
    .time(100)
    .register()


// Rolling Machine:
// Consumes up to 1 input fluid and up to 4 input items into up to 4 output items, consuming RF.

mods.advancedrocketry.rolling_machine.removeByInput(item('libvulpes:productplate'))
mods.advancedrocketry.rolling_machine.removeByOutput(item('libvulpes:productsheet', 1))
// mods.advancedrocketry.rolling_machine.removeAll()

mods.advancedrocketry.rolling_machine.recipeBuilder()
    .input(item('minecraft:snow'), fluid('water') * 300)
    .output(item('minecraft:snow_layer') * 2)
    .power(50)
    .time(100)
    .register()


// Small Plate Presser:
// Converts a block right below it into output items when powered by redstone.

mods.advancedrocketry.small_plate_presser.removeByInput(item('minecraft:iron_block'))
mods.advancedrocketry.small_plate_presser.removeByOutput(item('libvulpes:productplate', 2))
// mods.advancedrocketry.small_plate_presser.removeAll()

mods.advancedrocketry.small_plate_presser.recipeBuilder()
    .input(item('minecraft:cobblestone'))
    .output(item('minecraft:diamond'))
    .register()


