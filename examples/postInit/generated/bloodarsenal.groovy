
// Auto generated groovyscript example file
// MODS_LOADED: bloodarsenal

log.info 'mod \'bloodarsenal\' detected, running script'

// Sanguine Infusion:
// Converts an input infusion itemstack and up to 8 input surrounding itemstacks into an output itemstack, consuming Life
// Essence from the network to do so when the Infusion de Sanguine Ritual is activated. Alternatively, instead of consuming
// an infusion item, adds or upgrades a modifier to the given stasis tool, with the ability to increase the quantity of
// inputs consumed based on level.

// mods.bloodarsenal.sanguine_infusion.removeBlacklist(WayofTime.bloodmagic.iface.ISigil.class)
mods.bloodarsenal.sanguine_infusion.removeByInput(item('minecraft:feather'))
mods.bloodarsenal.sanguine_infusion.removeByInput(item('bloodmagic:bound_axe'))
// mods.bloodarsenal.sanguine_infusion.removeByModifierKey('beneficial_potion')
mods.bloodarsenal.sanguine_infusion.removeByOutput(item('bloodarsenal:stasis_pickaxe'))
// mods.bloodarsenal.sanguine_infusion.removeAll()
// mods.bloodarsenal.sanguine_infusion.removeAllBlacklist()

mods.bloodarsenal.sanguine_infusion.recipeBuilder()
    .infuse(item('minecraft:gold_ingot'))
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .cost(1000)
    .register()

mods.bloodarsenal.sanguine_infusion.recipeBuilder()
    .infuse(item('minecraft:emerald'))
    .input(item('minecraft:clay') * 64, item('minecraft:clay') * 64, item('minecraft:clay') * 64, item('minecraft:clay') * 64, item('minecraft:clay') * 64, item('minecraft:clay') * 64, item('minecraft:clay') * 64, item('minecraft:clay') * 64)
    .output(item('minecraft:diamond') * 64)
    .cost(5000)
    .register()

mods.bloodarsenal.sanguine_infusion.recipeBuilder()
    .infuse(item('minecraft:gold_ingot'))
    .input(item('minecraft:clay'), item('minecraft:diamond'))
    .output(item('minecraft:diamond'))
    .register()

mods.bloodarsenal.sanguine_infusion.recipeBuilder()
    .input(item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2)
    .modifier('xperienced')
    .levelMultiplier(3)
    .cost(3000)
    .register()


// mods.bloodarsenal.sanguine_infusion.addBlacklist(WayofTime.bloodmagic.iface.ISigil.class)

