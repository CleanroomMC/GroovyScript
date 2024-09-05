
// Auto generated groovyscript example file
// MODS_LOADED: artisanworktables

log.info 'mod \'artisanworktables\' detected, running script'

// Artisan's Worktables:
// 15 themed crafting tables with 3x3 and 5x5 grids, optional fluid input, optional tool input, extra item inputs and
// outputs, weighted outputs, and experience costs.

mods.artisanworktables.tables.shapedBuilder()
    .type('mason')
    .matrix('AAA',
            'A A',
            'BBB')
    .key('A', item('minecraft:iron_ingot'))
    .key('B', item('minecraft:stone'))
    .fluidInput(fluid('lava') * 250)
    .output(item('minecraft:furnace'))
    .register()

mods.artisanworktables.tables.shapedBuilder()
    .type('mage')
    .tool(item('minecraft:iron_sword'), 20)
    .matrix([[item('minecraft:iron_ingot')],
            [item('minecraft:diamond')]])
    .input(item('minecraft:coal') * 2, item('minecraft:stone') * 32)
    .level(10)
    .consumeExperience(false)
    .output(item('minecraft:clay'), item('minecraft:nether_star'))
    .register()

mods.artisanworktables.tables.shapelessBuilder()
    .type('basic')
    .gridInput(item('minecraft:coal'), item('minecraft:iron_ingot'))
    .output(item('minecraft:clay'))
    .maximumTier(1)
    .minimumTier(1)
    .register()


