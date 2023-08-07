
if (!isLoaded('advancedmortars')) return
println 'mod \'advancedmortars\' detected, running script'

// Mortar

// add(List<String> types, ItemStack output, int duration, List<IIngredient> inputs)
mods.advancedmortars.Mortar.add(
        ['stone'],
        item('minecraft:diamond') * 4,
        4,
        [ore('ingotGold')]
)

mods.advancedmortars.Mortar.add(
        ['stone'],
        item('minecraft:tnt'),
        4,
        [ore('ingotGold')]
)

// add(List<String> types, ItemStack output, int duration, ItemStack secondaryOutput, float secondaryOutputChance, List<IIngredient> inputs)
mods.advancedmortars.Mortar.add(
        ['iron', 'wood'],
        item('minecraft:tnt') * 5,
        4,
        item('minecraft:tnt'),
        0.7,
        [ore('ingotIron'), ore('ingotIron'), ore('ingotIron'), ore('ingotIron'), // max 8
         ore('ingotIron'), ore('ingotIron'), ore('ingotIron'), ore('ingotIron')]
)

mods.advancedmortars.Mortar.recipeBuilder()
        .type('stone') // EnumMortarType ('wood', 'stone', 'iron', 'diamond', 'gold', 'obsidian', 'emerald')
        .duration(2) // int
        .output(item('minecraft:grass')) // ItemStack
        .input(item('minecraft:dirt')) // IIngredient
        .register()

mods.advancedmortars.Mortar.recipeBuilder()
        .type('emerald')
        .duration(4)
        .output(item('minecraft:wheat_seeds') * 16)
        .secondaryOutput(item('minecraft:melon_seeds')) // ItemStack
        .input(ore('cropWheat'))
        .register()

mods.advancedmortars.Mortar.recipeBuilder()
        .type('obsidian')
        .duration(8)
        .output(item('minecraft:wheat_seeds') * 16)
        .secondaryOutput(item('minecraft:melon_seeds'), 0.5) // ItemStack, float
        .input(ore('cropWheat'))
        .register()