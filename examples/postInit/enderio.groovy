
// Auto generated groovyscript example file
// MODS_LOADED: enderio

println 'mod \'enderio\' detected, running script'

// Alloy Smelter:
// Convert up to 3 itemstack inputs into an itemstack output, using energy and giving XP. Can be restricted to require a
// given tier of machine. Can be set to require at least SIMPLE, NORMAL, or ENHANCED tiers, or to IGNORE the tier. SIMPLE
// and IGNORE are effectively the same.

mods.enderio.alloy_smelter.remove(item('enderio:item_material:70'))
// mods.enderio.alloy_smelter.removeAll()

mods.enderio.alloy_smelter.recipeBuilder()
    .input(item('minecraft:diamond') * 4, item('minecraft:clay') * 32)
    .output(item('minecraft:nether_star'))
    .energy(100000)
    .xp(500)
    .tierEnhanced()
    .register()

mods.enderio.alloy_smelter.recipeBuilder()
    .input(item('minecraft:clay') * 4, item('minecraft:diamond'))
    .output(item('minecraft:obsidian'))
    .tierNormal()
    .register()

mods.enderio.alloy_smelter.recipeBuilder()
    .input(item('minecraft:diamond') * 4, item('minecraft:gold_ingot') * 2)
    .output(item('minecraft:clay') * 4)
    .tierSimple()
    .register()

mods.enderio.alloy_smelter.recipeBuilder()
    .input(item('minecraft:diamond') * 2, item('minecraft:gold_nugget') * 2)
    .output(item('minecraft:clay') * 4)
    .tierAny()
    .register()


// Enchanter:
// Convert an input itemstack, player xp, and either a written book and lapis or a custom alternative into an enchanted
// book.

mods.enderio.enchanter.remove(enchantment('minecraft:mending'))
// mods.enderio.enchanter.removeAll()

mods.enderio.enchanter.recipeBuilder()
    .enchantment(enchantment('minecraft:unbreaking'))
    .input(item('minecraft:diamond'))
    .register()

mods.enderio.enchanter.recipeBuilder()
    .enchantment(enchantment('minecraft:sharpness'))
    .input(item('minecraft:clay'))
    .amountPerLevel(3)
    .xpCostMultiplier(2)
    .customBook(item('minecraft:book'))
    .customLapis(item('minecraft:diamond'))
    .register()



// Fluid Coolant:
// Create a Coolant with a given coolant rate that produces power with a Fuel while in a Combustion Generator.

mods.enderio.fluid_coolant.remove(fluid('water'))
// mods.enderio.fluid_coolant.removeAll()

mods.enderio.fluid_coolant.addCoolant(fluid('xpjuice'), 1000)

// Fluid Fuel:
// Create a Fuel with a given power per tick and total burn time that produces power with a Coolant while in a Combustion
// Generator.

mods.enderio.fluid_fuel.remove(fluid('fire_water'))
// mods.enderio.fluid_fuel.removeAll()

mods.enderio.fluid_fuel.addFuel(fluid('lava'), 500, 1000)

// Sag Mill:
// Convert an input itemstack into up to 4 output itemstacks with chances, using energy. Output can be boosted by Grinding
// Balls based on set bonusType. Can be set to require at least SIMPLE, NORMAL, or ENHANCED tiers, or to IGNORE the tier.
// SIMPLE and IGNORE are effectively the same.

mods.enderio.sag_mill.removeByInput(item('minecraft:wheat'))
// mods.enderio.sag_mill.removeAll()

mods.enderio.sag_mill.recipeBuilder()
    .input(item('minecraft:diamond_block'))
    .output(item('minecraft:diamond') * 4)
    .output(item('minecraft:clay_ball') * 2, 0.7)
    .output(item('minecraft:gold_ingot'), 0.1)
    .output(item('minecraft:gold_ingot'), 0.1)
    .bonusTypeMultiply()
    .energy(1000)
    .tierEnhanced()
    .register()

mods.enderio.sag_mill.recipeBuilder()
    .input(item('minecraft:clay_ball'))
    .output(item('minecraft:diamond') * 4)
    .output(item('minecraft:gold_ingot'), 0.1)
    .bonusTypeChance()
    .tierNormal()
    .register()

mods.enderio.sag_mill.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:gold_ingot'), 0.1)
    .bonusTypeNone()
    .tierSimple()
    .register()

mods.enderio.sag_mill.recipeBuilder()
    .input(item('minecraft:nether_star'))
    .output(item('minecraft:clay_ball') * 2, 0.7)
    .output(item('minecraft:gold_ingot'), 0.1)
    .tierAny()
    .register()


// Sag Mill Grinding:
// Add a new Griding Ball for use in a Sag Mill with the given output multiplier, power multiplier, chance multiplier, and
// duration (in base power used).

mods.enderio.sag_mill_grinding.remove(item('minecraft:flint'))
// mods.enderio.sag_mill_grinding.removeAll()

mods.enderio.sag_mill_grinding.recipeBuilder()
    .input(item('minecraft:clay_ball'))
    .chance(6.66)
    .power(0.001)
    .grinding(3.33)
    .duration(10000)
    .register()


// Slice N Splice:
// Convert up to 6 input itemstacks into an output itemstack, using energy and giving XP.

mods.enderio.slice_n_splice.remove(item('enderio:item_material:40'))
mods.enderio.slice_n_splice.removeByInput([item('enderio:item_alloy_ingot:7'), item('enderio:block_enderman_skull'), item('enderio:item_alloy_ingot:7'), item('minecraft:potion').withNbt(['Potion': 'minecraft:water']), item('enderio:item_basic_capacitor'), item('minecraft:potion').withNbt(['Potion': 'minecraft:water'])])
// mods.enderio.slice_n_splice.removeAll()

mods.enderio.slice_n_splice.recipeBuilder()
    .input(item('minecraft:clay'), null, item('minecraft:clay'))
    .input(null, item('minecraft:clay'), null)
    .output(item('minecraft:gold_ingot'))
    .energy(1000)
    .xp(5)
    .register()



// Soulbinder:
// Converts an input itemstack into an output itemstack, requiring one of several entities in soul vials, using energy and
// giving XP. Must have a unique name. To function properly, the input entities must be allowed in Soul Vials.

mods.enderio.soul_binder.remove(item('enderio:item_material:17'))
// mods.enderio.soul_binder.removeAll()

mods.enderio.soul_binder.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .entity(entity('minecraft:zombie'), entity('minecraft:enderman'))
    .name('groovy_example')
    .energy(1000)
    .xp(5)
    .register()


// Tank:
// Converts an input itemstack into an output fluidstack with an optional output itemstack in drain mode, or converts an
// input itemstack and fluidstack into an output itemstack in fill mode.

mods.enderio.tank.removeDrain(item('minecraft:experience_bottle'), fluid('xpjuice'))
mods.enderio.tank.removeFill(item('minecraft:glass_bottle'), fluid('xpjuice'))
// mods.enderio.tank.removeAll()

mods.enderio.tank.recipeBuilder()
    .drain()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .fluidInput(fluid('water') * 500)
    .register()

mods.enderio.tank.recipeBuilder()
    .fill()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .fluidOutput(fluid('water') * 500)
    .register()

mods.enderio.tank.recipeBuilder()
    .drain()
    .input(item('minecraft:diamond'))
    .fluidInput(fluid('fire_water') * 8000)
    .register()

mods.enderio.tank.recipeBuilder()
    .fill()
    .input(item('minecraft:diamond'))
    .fluidOutput(fluid('fire_water') * 8000)
    .register()



// Vat:
// Converts an input fluidstack into an output itemstack at a rate based on up 2 itemstack inputs, and using power. Can be
// set to require at least NORMAL or ENHANCED tiers, or to IGNORE the tier. NORMAL and IGNORE are effectively the same.

mods.enderio.vat.remove(fluid('nutrient_distillation'))
// mods.enderio.vat.removeAll()

mods.enderio.vat.recipeBuilder()
    .input(fluid('lava'))
    .output(fluid('hootch'))
    .baseMultiplier(2)
    .itemInputLeft(item('minecraft:clay'), 2)
    .itemInputLeft(item('minecraft:clay_ball'), 0.5)
    .itemInputRight(item('minecraft:diamond'), 5)
    .itemInputRight(item('minecraft:diamond_block'), 50)
    .itemInputRight(item('minecraft:gold_block'), 10)
    .itemInputRight(item('minecraft:gold_ingot'), 1)
    .itemInputRight(item('minecraft:gold_nugget'), 0.1)
    .energy(1000)
    .tierEnhanced()
    .register()

mods.enderio.vat.recipeBuilder()
    .input(fluid('hootch') * 100)
    .output(fluid('water') * 50)
    .itemInputLeft(item('minecraft:clay_ball'), 1)
    .itemInputRight(item('minecraft:diamond'), 1)
    .energy(1000)
    .tierNormal()
    .register()

mods.enderio.vat.recipeBuilder()
    .input(fluid('water'))
    .output(fluid('hootch'))
    .itemInputLeft(item('minecraft:clay'), 2)
    .itemInputLeft(item('minecraft:clay_ball'), 0.5)
    .itemInputRight(item('minecraft:diamond'), 5)
    .itemInputRight(item('minecraft:gold_ingot'), 1)
    .energy(1000)
    .tierAny()
    .register()


