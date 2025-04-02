
// Auto generated groovyscript example file
// MODS_LOADED: thebetweenlands

log.info 'mod \'thebetweenlands\' detected, running script'

// Animator:
// Converts an input item, Life amount from Life Crystals, and Fuel from Sulfur into an output itemstack, summoning an
// entity, a random item from a loottable, or summoning an entity and outputting an itemstack.

mods.thebetweenlands.animator.removeByEntity(entity('thebetweenlands:sporeling'))
mods.thebetweenlands.animator.removeByInput(item('thebetweenlands:bone_leggings'))
mods.thebetweenlands.animator.removeByLootTable(resource('thebetweenlands:animator/scroll'))
mods.thebetweenlands.animator.removeByOutput(item('thebetweenlands:items_misc:46'))
// mods.thebetweenlands.animator.removeAll()

mods.thebetweenlands.animator.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .life(1)
    .fuel(1)
    .register()

mods.thebetweenlands.animator.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .lootTable(resource('minecraft:entities/zombie'))
    .life(5)
    .fuel(1)
    .register()

mods.thebetweenlands.animator.recipeBuilder()
    .input(item('minecraft:gold_block'))
    .entity(entity('minecraft:zombie').getEntityClass())
    .life(1)
    .fuel(5)
    .register()

mods.thebetweenlands.animator.recipeBuilder()
    .input(item('minecraft:diamond'))
    .entity(entity('minecraft:enderman'))
    .output(item('minecraft:clay'))
    .life(3)
    .fuel(10)
    .register()


// Compost:
// Converts an input itemstack into an amount of compost.

mods.thebetweenlands.compost.removeByInput(item('thebetweenlands:items_misc:13'))
// mods.thebetweenlands.compost.removeAll()

mods.thebetweenlands.compost.recipeBuilder()
    .input(item('minecraft:clay'))
    .amount(20)
    .time(30)
    .register()

mods.thebetweenlands.compost.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .amount(1)
    .time(5)
    .register()


// Crab Pot Filter Bubbler:
// Converts an input item into an output itemstack when a Bubbler Crab is placed inside a Crab Pot Filter.

mods.thebetweenlands.crab_pot_filter_bubbler.removeByInput(item('thebetweenlands:silt'))
mods.thebetweenlands.crab_pot_filter_bubbler.removeByOutput(item('thebetweenlands:swamp_dirt'))
// mods.thebetweenlands.crab_pot_filter_bubbler.removeAll()

mods.thebetweenlands.crab_pot_filter_bubbler.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.thebetweenlands.crab_pot_filter_bubbler.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .register()


// Crab Pot Filter Silt:
// Converts an input item into an output itemstack when a Silt Crab is placed inside a Crab Pot Filter.

mods.thebetweenlands.crab_pot_filter_silt.removeByInput(item('thebetweenlands:mud'))
mods.thebetweenlands.crab_pot_filter_silt.removeByOutput(item('thebetweenlands:mud'))
// mods.thebetweenlands.crab_pot_filter_silt.removeAll()

mods.thebetweenlands.crab_pot_filter_silt.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.thebetweenlands.crab_pot_filter_silt.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .register()


// Druid Altar:
// Converts 4 input items into an output itemstack.

// mods.thebetweenlands.druid_altar.removeByInput(item('thebetweenlands:swamp_talisman:1'))
mods.thebetweenlands.druid_altar.removeByOutput(item('thebetweenlands:swamp_talisman'))
// mods.thebetweenlands.druid_altar.removeAll()

mods.thebetweenlands.druid_altar.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.thebetweenlands.druid_altar.recipeBuilder()
    .input(item('minecraft:diamond'), item('minecraft:gold_block'), item('minecraft:gold_ingot'), item('minecraft:clay'))
    .output(item('minecraft:clay'))
    .register()


// Pestle And Mortar:
// Converts an input item into an output itemstack in a Pestle and Mortar by using a Pestle tool in the Mortar.

mods.thebetweenlands.pestle_and_mortar.removeByInput(item('thebetweenlands:limestone'))
mods.thebetweenlands.pestle_and_mortar.removeByOutput(item('thebetweenlands:fish_bait'))
// mods.thebetweenlands.pestle_and_mortar.removeAll()

mods.thebetweenlands.pestle_and_mortar.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.thebetweenlands.pestle_and_mortar.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .register()


// Purifier:
// Converts an input item into an output itemstack, consuming Sulfur and Swamp Water as fuel.

mods.thebetweenlands.purifier.removeByInput(item('thebetweenlands:items_misc:64'))
mods.thebetweenlands.purifier.removeByOutput(item('thebetweenlands:cragrock'))
// mods.thebetweenlands.purifier.removeAll()

mods.thebetweenlands.purifier.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.thebetweenlands.purifier.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .register()


// Smoking Rack:
// Converts an input item into an output itemstack over a configurable period of time, consuming Fallen Leaves to do so.

mods.thebetweenlands.smoking_rack.removeByInput(item('thebetweenlands:anadia'))
mods.thebetweenlands.smoking_rack.removeByOutput(item('thebetweenlands:barnacle_smoked'))
// mods.thebetweenlands.smoking_rack.removeAll()

mods.thebetweenlands.smoking_rack.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.thebetweenlands.smoking_rack.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .time(50)
    .register()


// Steeping Pot:
// Converts a 1,000mb of fluid into either 1,000mb of a fluid, an output itemstack, or both, consuming up to 4 items from a
// Silk Bundle placed inside the Steeping Pot to do so. The Silk Bundle is converted into a Dirty Silk Bundle in the
// process. The Silk Bundle can only hold specific items, which are also configurable.

mods.thebetweenlands.steeping_pot.removeAcceptedItem(item('thebetweenlands:items_crushed:5'))
mods.thebetweenlands.steeping_pot.removeByInput(fluid('clean_water'))
mods.thebetweenlands.steeping_pot.removeByInput(item('thebetweenlands:items_crushed:13'))
mods.thebetweenlands.steeping_pot.removeByOutput(fluid('dye_fluid').withNbt(['type': 14]))
// mods.thebetweenlands.steeping_pot.removeByOutput(item('thebetweenlands:limestone'))
// mods.thebetweenlands.steeping_pot.removeAll()
// mods.thebetweenlands.steeping_pot.removeAllAcceptedItem()

mods.thebetweenlands.steeping_pot.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'))
    .fluidInput(fluid('lava'))
    .fluidOutput(fluid('water'))
    .register()

mods.thebetweenlands.steeping_pot.recipeBuilder()
    .input(item('minecraft:diamond'))
    .fluidInput(fluid('lava'))
    .fluidOutput(fluid('dye_fluid'))
    .meta(5)
    .register()

mods.thebetweenlands.steeping_pot.recipeBuilder()
    .input(item('minecraft:emerald'))
    .fluidInput(fluid('lava'))
    .fluidOutput(fluid('water'))
    .register()


mods.thebetweenlands.steeping_pot.addAcceptedItem(item('minecraft:gold_block'))
