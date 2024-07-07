
// Auto generated groovyscript example file
// MODS_LOADED: advancedrocketry

println 'mod \'advancedrocketry\' detected, running script'

// groovyscript.wiki.advancedrocketry.centrifuge.title:
// groovyscript.wiki.advancedrocketry.centrifuge.description

// mods.advancedrocketry.centrifuge.removeByInput(fluid('enrichedlava'))
mods.advancedrocketry.centrifuge.removeByOutput(item('minecraft:gold_nugget'))

mods.advancedrocketry.centrifuge.recipeBuilder()
    .fluidInput(fluid('lava') * 500)
    .output(item('minecraft:slime_ball'), 0.1f)
    .output(item('minecraft:stone'), 0.9f)
    .fluidOutput(fluid('enrichedlava') * 500, 0.5f)
    .power(50)
    .time(100)
    .outputSize(1)
    .register()


// groovyscript.wiki.advancedrocketry.chemical_reactor.title:
// groovyscript.wiki.advancedrocketry.chemical_reactor.description

mods.advancedrocketry.chemical_reactor.removeByInput(item('minecraft:bone'))
mods.advancedrocketry.chemical_reactor.removeByOutput(item('minecraft:leather_helmet'))

mods.advancedrocketry.chemical_reactor.recipeBuilder()
    .input(item('minecraft:chorus_fruit_popped'))
    .fluidInput(fluid('lava') * 500)
    .output(item('minecraft:end_rod') * 4)
    .fluidOutput(fluid('water') * 500)
    .power(50)
    .time(100)
    .register()


// groovyscript.wiki.advancedrocketry.crystallizer.title:
// groovyscript.wiki.advancedrocketry.crystallizer.description

mods.advancedrocketry.crystallizer.removeByInput(item('libvulpes:productingot', 3))
mods.advancedrocketry.crystallizer.removeByOutput(item('libvulpes:productgem'))

mods.advancedrocketry.crystallizer.recipeBuilder()
    .input(item('minecraft:blaze_powder') * 4)
    .output(item('minecraft:blaze_rod'))
    .power(50)
    .time(100)
    .register()


// groovyscript.wiki.advancedrocketry.cutting_machine.title:
// groovyscript.wiki.advancedrocketry.cutting_machine.description

mods.advancedrocketry.cutting_machine.removeByInput(item('advancedrocketry:alienwood'))
mods.advancedrocketry.cutting_machine.removeByOutput(item('minecraft:planks', 1))

mods.advancedrocketry.cutting_machine.recipeBuilder()
    .input(item('minecraft:blaze_rod'))
    .output(item('minecraft:blaze_powder') * 4)
    .power(50)
    .time(100)
    .register()


// groovyscript.wiki.advancedrocketry.electric_arc_furnace.title:
// groovyscript.wiki.advancedrocketry.electric_arc_furnace.description

mods.advancedrocketry.electric_arc_furnace.removeByInput(item('minecraft:iron_ingot'))
mods.advancedrocketry.electric_arc_furnace.removeByOutput(item('libvulpes:productingot', 3))

mods.advancedrocketry.electric_arc_furnace.recipeBuilder()
    .input(item('minecraft:blaze_powder') * 4)
    .output(item('minecraft:blaze_rod'))
    .power(50)
    .time(100)
    .register()


// groovyscript.wiki.advancedrocketry.electrolyser.title:
// groovyscript.wiki.advancedrocketry.electrolyser.description

mods.advancedrocketry.electrolyser.removeByInput(fluid('water'))
// mods.advancedrocketry.electrolyser.removeByOutput(fluid('oxygen'))

mods.advancedrocketry.electrolyser.recipeBuilder()
    .fluidInput(fluid('lava') * 10)
    .fluidOutput(fluid('nitrogen') * 50)
    .power(50)
    .time(100)
    .register()


// groovyscript.wiki.advancedrocketry.lathe.title:
// groovyscript.wiki.advancedrocketry.lathe.description

mods.advancedrocketry.lathe.removeByInput(item('libvulpes:productingot', 6))
mods.advancedrocketry.lathe.removeByOutput(item('libvulpes:productrod', 4))

mods.advancedrocketry.lathe.recipeBuilder()
    .input(ore('plankWood'))
    .output(item('minecraft:stick') * 2)
    .power(50)
    .time(100)
    .register()


// groovyscript.wiki.advancedrocketry.precision_assembler.title:
// groovyscript.wiki.advancedrocketry.precision_assembler.description

mods.advancedrocketry.precision_assembler.removeByInput(item('minecraft:redstone_block'))
mods.advancedrocketry.precision_assembler.removeByOutput(item('advancedrocketry:atmanalyser'))

mods.advancedrocketry.precision_assembler.recipeBuilder()
    .input(item('minecraft:fishing_rod'), item('minecraft:carrot'))
    .output(item('minecraft:carrot_on_a_stick'))
    .power(50)
    .time(100)
    .register()


// groovyscript.wiki.advancedrocketry.precision_laser_etcher.title:
// groovyscript.wiki.advancedrocketry.precision_laser_etcher.description

mods.advancedrocketry.precision_laser_etcher.removeByInput(item('minecraft:redstone_block'))
mods.advancedrocketry.precision_laser_etcher.removeByOutput(item('advancedrocketry:itemcircuitplate'))

mods.advancedrocketry.precision_laser_etcher.recipeBuilder()
    .input(item('minecraft:blaze_powder') * 4, item('advancedrocketry:wafer'))
    .output(item('advancedrocketry:itemcircuitplate'))
    .power(50)
    .time(100)
    .register()


// groovyscript.wiki.advancedrocketry.rolling_machine.title:
// groovyscript.wiki.advancedrocketry.rolling_machine.description

mods.advancedrocketry.rolling_machine.removeByInput(item('libvulpes:productplate'))
mods.advancedrocketry.rolling_machine.removeByOutput(item('libvulpes:productsheet', 1))

mods.advancedrocketry.rolling_machine.recipeBuilder()
    .input(item('minecraft:snow'), fluid('water') * 300)
    .output(item('minecraft:snow_layer') * 2)
    .power(50)
    .time(100)
    .register()


