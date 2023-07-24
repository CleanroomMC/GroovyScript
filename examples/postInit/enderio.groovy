
if (!isLoaded('enderio')) return
println 'mod \'enderio\' detected, running script'

// Alloy Smelter (Alloying):
// Convert up to 3 itemstack inputs into an itemstack output, using energy and giving XP. Can be restricted to require a given tier of machine.
// Can be set to require at least SIMPLE, NORMAL, or ENHANCED tiers, or to IGNORE the tier. SIMPLE and IGNORE are effectively the same.
mods.enderio.alloysmelter.recipeBuilder()
    .input(item('minecraft:diamond') * 4, item('minecraft:clay') * 32)
    .output(item('minecraft:nether_star'))
    .energy(100000) // Optional int, how much energy is required for the recipe. Default of 5000.
    .xp(500) // Optional  int, how much xp is output when the recipe is finished. Default of 0.
    .tierEnhanced() // Optional, sets the minimum tier of machine recipe for recipe to ENHANCED. Default IGNORE
    .register()

mods.enderio.alloysmelter.recipeBuilder()
    .input(item('minecraft:clay') * 4, item('minecraft:diamond'))
    .output(item('minecraft:obsidian'))
    .tierNormal() // Optional, sets the minimum tier of machine recipe for recipe to NORMAL. Default IGNORE
    .register()

mods.enderio.alloysmelter.recipeBuilder()
    .input(item('minecraft:diamond') * 4, item('minecraft:gold_ingot') * 2)
    .output(item('minecraft:clay') * 4)
    .tierSimple() // Optional, sets the minimum tier of machine recipe for recipe to SIMPLE. Default IGNORE
    .register()

mods.enderio.alloysmelter.recipeBuilder()
    .input(item('minecraft:diamond') * 2, item('minecraft:gold_nugget') * 2)
    .output(item('minecraft:clay') * 4)
    .tierAny() // Optional, sets the minimum tier of machine recipe for recipe to IGNORE. Default IGNORE
    .register()

mods.enderio.alloysmelter.remove(item('enderio:item_material:70')) // Remove by ItemStack output
//mods.enderio.alloysmelter.removeAll()


// Enchanter:
// Convert an input itemstack, player xp, and either a written book and lapis or a custom alternative into an enchanted book.
mods.enderio.enchanter.recipeBuilder()
    .enchantment(enchantment('sharpness'))
    .input(item('minecraft:clay'))
    .amountPerLevel(3) // Optional int, multiplier per enchantment level. Defaults to amount of the input ItemStack
    .xpCostMultiplier(2) // Optional double, base XP multiplier. Default 1.
    .customBook(item('minecraft:book')) // Optional ItemStack, item that would otherwise be a written book. Default item('minecraft:writable_book')
    .customLapis(item('minecraft:diamond')) // Optional OreDict, item that would otherwise be lapis, consumes 3 per output enchantment level. Default ore('gemLapis')
    .register()

mods.enderio.enchanter.recipeBuilder()
    .enchantment(enchantment('unbreaking'))
    .input(item('minecraft:diamond'))
    .register()

mods.enderio.enchanter.remove(enchantment('mending'))
mods.enderio.enchanter.removeAll()


// Fluid Coolant (Combustion Coolant):
// Create a Coolant with a given coolant rate that produces power with a Fuel while in a Combustion Generator.
mods.enderio.fluidcoolant.addCoolant(fluid('xpjuice'), 1000)

mods.enderio.fluidcoolant.remove(fluid('water'))
//mods.enderio.fluidcoolant.removeAll()


// Fluid Fuel (Combustion Fuel):
// Create a Fuel with a given power per tick and total burn time that produces power with a Coolant while in a Combustion Generator.
mods.enderio.fluidfuel.addFuel(fluid('lava'), 500, 1000)

mods.enderio.fluidfuel.remove(fluid('fire_water'))
//mods.enderio.fluidfuel.removeAll()


// Sag Mill (SAGMill, Sag):
// Convert an input itemstack into up to 4 output itemstacks with chances, using energy. Output can be boosted by Grinding Balls based on set bonusType.
// Can be set to require at least SIMPLE, NORMAL, or ENHANCED tiers, or to IGNORE the tier. SIMPLE and IGNORE are effectively the same.
mods.enderio.sagmill.recipeBuilder()
    .input(item('minecraft:diamond_block'))
    .output(item('minecraft:diamond') * 4) // Has an 100% chance.
    .output(item('minecraft:clay_ball') * 2, 0.7)
    .output(item('minecraft:gold_ingot'), 0.1)
    .output(item('minecraft:gold_ingot'), 0.1) // Between 1 and 4 outputs.
    .bonusTypeMultiply() // Optional, controls the effect Grinding Balls have on outputs. Default bonusTypeNone.
    .energy(1000) // Optional int, how much energy is required for the recipe. Default of 5000.
    .tierEnhanced() // Optional, sets the minimum tier of machine recipe for recipe to ENHANCED. Default IGNORE
    .register()

mods.enderio.sagmill.recipeBuilder()
    .input(item('minecraft:clay_ball'))
    .output(item('minecraft:diamond') * 4)
    .output(item('minecraft:gold_ingot'), 0.1)
    .bonusTypeChance() // Optional, controls the effect Grinding Balls have on outputs. Default bonusTypeNone.
    .tierNormal() // Optional, sets the minimum tier of machine recipe for recipe to NORMAL. Default IGNORE
    .register()

mods.enderio.sagmill.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:gold_ingot'), 0.1)
    .bonusTypeNone() // Optional, controls the effect Grinding Balls have on outputs. Default bonusTypeNone.
    .tierSimple() // Optional, sets the minimum tier of machine recipe for recipe to SIMPLE. Default IGNORE
    .register()

mods.enderio.sagmill.recipeBuilder()
    .input(item('minecraft:nether_star'))
    .output(item('minecraft:clay_ball') * 2, 0.7)
    .output(item('minecraft:gold_ingot'), 0.1)
    .tierAny() // Optional, sets the minimum tier of machine recipe for recipe to IGNORE. Default IGNORE
    .register()

mods.enderio.sagmill.removeByInput(item('minecraft:wheat'))
//mods.enderio.sagmill.removeAll()


// Sag Mill Grinding (Grinding):
// Add a new Griding Ball for use in a Sag Mill with the given output multiplier, power multiplier, chance multiplier, and duration (in base power used).
mods.enderio.sagmillgrinding.recipeBuilder()
    .input(item('minecraft:clay_ball'))
    .chance(6.66) // Optional int, chance to double all outputs. Default 1.
    .power(0.001) // Optional int, multiplier to required power for recipes. Default 1.
    .grinding(3.33) // Optional int, increases the chances of outputs up to 100%. Default 1.
    .duration(10000) // Amount of power used in recipes before it is consumed.
    .register()

mods.enderio.sagmillgrinding.remove(item('minecraft:flint'))
//mods.enderio.sagmillgrinding.removeAll()


// Slice N Splice (SliceAndSplice):
// Convert up to 6 input itemstacks into an output itemstack, using energy and giving XP.
mods.enderio.slicensplice.recipeBuilder()
    .input(item('minecraft:clay'), null, item('minecraft:clay'))
    .input(null, item('minecraft:clay'), null)
    .output(item('minecraft:gold_ingot'))
    .energy(1000) // Optional int, how much energy is required for the recipe. Default of 5000.
    .xp(5) // Optional  int, how much xp is output when the recipe is finished. Default of 0.
    .register()

mods.enderio.slicensplice.remove(item('enderio:item_material:40')) // Remove by ItemStack output
mods.enderio.slicensplice.removeByInput(  // Remove by List<ItemStack> input
    [item('enderio:item_alloy_ingot:7'), item('enderio:block_enderman_skull'), item('enderio:item_alloy_ingot:7'),
    item('minecraft:potion').withNbt(["Potion": "minecraft:water"]), item('enderio:item_basic_capacitor'), item('minecraft:potion').withNbt(["Potion": "minecraft:water"])]
)
//mods.enderio.slicensplice.removeAll()


// Soulbinder:
// Converts an input itemstack into an output itemstack, requiring one of several entities in soul vials, using energy and giving XP. Must have a unique name.
// To function properly, the input entities must be allowed in Soul Vials.
mods.enderio.soulbinder.recipeBuilder()
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .entity(entity('minecraft:zombie'), entity('minecraft:enderman'))
    .name('groovy_example')
    .energy(1000) // Optional int, how much energy is required for the recipe. Default of 5000.
    .xp(5) // Optional  int, how much xp is output when the recipe is finished. Default of 0.
    .register()

mods.enderio.soulbinder.remove(item('enderio:item_material:17')) // Remove by ItemStack output
//mods.enderio.soulbinder.removeAll()


// Tank:
// Converts an input itemstack into an output fluidstack with an optional output itemstack in drain mode,
// or converts an input itemstack and fluidstack into an output itemstack in fill mode.
mods.enderio.tank.recipeBuilder()
    .drain() // puts fluid into tank
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond')) // Optional ItemStack.
    .fluidInput(fluid('water') * 500)
    .register()

mods.enderio.tank.recipeBuilder()
    .fill() // takes fluid out of tank
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay')) // Optional ItemStack.
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

mods.enderio.tank.removeDrain(item('minecraft:experience_bottle'), fluid('xpjuice'))
mods.enderio.tank.removeFill(item('minecraft:glass_bottle'), fluid('xpjuice'))
//mods.enderio.tank.removeAll()


// Vat:
// Converts an input fluidstack into an output itemstack at a rate based on up 2 itemstack inputs, and using power.
// Can be set to require at least NORMAL or ENHANCED tiers, or to IGNORE the tier. NORMAL and IGNORE are effectively the same.
mods.enderio.vat.recipeBuilder()
    .input(fluid('lava'))
    .output(fluid('hootch'))
    .baseMultiplier(2) // Optional int, determines the base fluid output relative to fluid input
    .itemInputLeft(item('minecraft:clay'), 2)
    .itemInputLeft(item('minecraft:clay_ball'), 0.5)
    .itemInputRight(item('minecraft:diamond'), 5)
    .itemInputRight(item('minecraft:diamond_block'), 50)
    .itemInputRight(item('minecraft:gold_block'), 10)
    .itemInputRight(item('minecraft:gold_ingot'), 1)
    .itemInputRight(item('minecraft:gold_nugget'), 0.1)
    .energy(1000)
    .tierEnhanced() // Optional, sets the minimum tier of machine recipe for recipe to ENHANCED (the higher of the two tiers). Default IGNORE
    .register()

mods.enderio.vat.recipeBuilder()
    .input(fluid('hootch') * 100)
    .output(fluid('water') * 50)
    .itemInputLeft(item('minecraft:clay_ball'), 1)
    .itemInputRight(item('minecraft:diamond'), 1)
    .energy(1000)
    .tierNormal() // Optional, sets the minimum tier of machine recipe for recipe to NORMAL (the lower of the two tiers). Default IGNORE
    .register()

mods.enderio.vat.recipeBuilder()
    .input(fluid('water'))
    .output(fluid('hootch'))
    .itemInputLeft(item('minecraft:clay'), 2)
    .itemInputLeft(item('minecraft:clay_ball'), 0.5)
    .itemInputRight(item('minecraft:diamond'), 5)
    .itemInputRight(item('minecraft:gold_ingot'), 1)
    .energy(1000)
    .tierAny() // Optional, sets the minimum tier of machine recipe for recipe to IGNORE. Default IGNORE
    .register()

mods.enderio.vat.remove(fluid('nutrient_distillation'))
//mods.enderio.vat.removeAll()
