
// Auto generated groovyscript example file
// MODS_LOADED: jei

import mezz.jei.api.ingredients.VanillaTypes

println 'mod \'jei\' detected, running script'

// Category Catalysts:
// Modify the items shown on the left of JEI Categories which indicate where the recipe takes place

mods.jei.catalyst.remove('minecraft.smelting', item('minecraft:furnace'))
// mods.jei.catalyst.removeByType('minecraft.anvil')

mods.jei.catalyst.add('minecraft.smelting', item('minecraft:clay') * 8, item('minecraft:cobblestone'))

// Categories:
// Modify the Categories visible in JEI, each of which contain recipes and are associated with specific blocks, typically
// machines.

mods.jei.category.hideCategory('minecraft.fuel')
// mods.jei.category.hideAll()

// Description Category:
// Modify the description of the input items, where the description is a unique JEI tab containing text.

// mods.jei.description.remove(item('thaumcraft:triple_meat_treat'))

mods.jei.description.add(item('minecraft:clay'), ['wow', 'this', 'is', 'neat'])
mods.jei.description.add(item('minecraft:gold_ingot'), 'groovyscript.recipe.fluid_recipe')

// Ingredient Sidebar:
// Modify what ingredients show up in the search menu sidebar.

mods.jei.ingredient.hide(fluid('water'))
mods.jei.ingredient.hide(item('minecraft:stone:1'), item('minecraft:stone:3'))
mods.jei.ingredient.hide(VanillaTypes.ITEM, item('minecraft:bed:*'))
// mods.jei.ingredient.hide(mekanism.client.jei.MekanismJEI.TYPE_GAS, gas('tritium'))
// mods.jei.ingredient.hideByType(VanillaTypes.ITEM)
// mods.jei.ingredient.hideByType(VanillaTypes.FLUID)
// mods.jei.ingredient.hideByType(VanillaTypes.ENCHANT)
// mods.jei.ingredient.hideByType(mekanism.client.jei.MekanismJEI.TYPE_GAS)
// mods.jei.ingredient.hideByType(com.buuz135.thaumicjei.ThaumcraftJEIPlugin.ASPECT_LIST)
// mods.jei.ingredient.hideAll()

mods.jei.ingredient.add(item('minecraft:stone:1').withNbt([display:[Name:'Special Granite']]))
mods.jei.ingredient.add(VanillaTypes.ITEM, item('minecraft:bed').withNbt([display:[Name:'Beds come in 16 different colors!']]))

