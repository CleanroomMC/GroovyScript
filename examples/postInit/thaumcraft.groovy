
// Auto generated groovyscript example file
// MODS_LOADED: thaumcraft

println 'mod \'thaumcraft\' detected, running script'

// Arcane Workbench:
// A special crafting table, allowing additional requirements in the form of Vis Crystals, Vis, and having a specific
// research.

mods.thaumcraft.arcane_workbench.removeByOutput(item('thaumcraft:mechanism_simple'))
// mods.thaumcraft.arcane_workbench.removeAll()

mods.thaumcraft.arcane_workbench.shapedBuilder()
    .researchKey('UNLOCKALCHEMY@3')
    .output(item('minecraft:pumpkin'))
    .row('SS ')
    .row('   ')
    .row('   ')
    .key('S', item('minecraft:pumpkin_seeds'))
    .aspect('terra')
    .vis(5)
    .register()

mods.thaumcraft.arcane_workbench.shapedBuilder()
    .researchKey('UNLOCKALCHEMY@3')
    .output(item('minecraft:clay'))
    .matrix('SS ',
            '   ',
            '   ')
    .key('S', item('minecraft:pumpkin'))
    .aspect(aspect('terra'))
    .vis(5)
    .register()

mods.thaumcraft.arcane_workbench.shapelessBuilder()
    .researchKey('UNLOCKALCHEMY@3')
    .input(item('minecraft:pumpkin'))
    .input(item('minecraft:stick'))
    .input(item('minecraft:stick'))
    .output(item('thaumcraft:void_hoe'))
    .vis(0)
    .register()


// Aspect Creator:
// Creates a custom Aspect.

// mods.thaumcraft.aspect.removeAll()

mods.thaumcraft.aspect.aspectBuilder()
    .tag('humor')
    .chatColor(14013676)
    .component(aspect('cognitio'))
    .component('perditio')
    .image(resource('thaumcraft:textures/aspects/humor.png'))
    .register()


// Entity/Block Aspects:
// Controls what Aspects are attached to entities or items.


mods.thaumcraft.aspect_helper.aspectBuilder()
    .object(item('minecraft:stone'))
    .stripAspects()
    .aspect(aspect('ignis') * 20)
    .aspect('ordo', 5)
    .register()

mods.thaumcraft.aspect_helper.aspectBuilder()
    .object(ore('cropPumpkin'))
    .stripAspects()
    .aspect(aspect('herba') * 20)
    .register()

mods.thaumcraft.aspect_helper.aspectBuilder()
    .entity(entity('minecraft:chicken'))
    .stripAspects()
    .aspect('bestia', 20)
    .register()



// Crucible:
// Combines an item with any number of Aspects to drop an output itemstack, potentially requiring a specific research to be
// completed.

mods.thaumcraft.crucible.removeByOutput(item('minecraft:gunpowder'))
// mods.thaumcraft.crucible.removeAll()

mods.thaumcraft.crucible.recipeBuilder()
    .researchKey('UNLOCKALCHEMY@3')
    .catalyst(item('minecraft:rotten_flesh'))
    .output(item('minecraft:gold_ingot'))
    .aspect(aspect('metallum') * 10)
    .register()



// Dust Trigger:
// Converts a block in-world into an item, when interacting with it with Salis Mundus, potentially requiring a specific
// research to be completed.

mods.thaumcraft.dust_trigger.removeByOutput(item('thaumcraft:arcane_workbench'))

mods.thaumcraft.dust_trigger.triggerBuilder()
    .researchKey('UNLOCKALCHEMY@3')
    .target(block('minecraft:obsidian'))
    .output(item('minecraft:enchanting_table'))
    .register()

mods.thaumcraft.dust_trigger.triggerBuilder()
    .researchKey('UNLOCKALCHEMY@3')
    .target(ore('cropPumpkin'))
    .output(item('minecraft:lit_pumpkin'))
    .register()


// Infusion Crafting:
// Combines any number of items and aspects together in the Infusion Altar, potentially requiring a specific research to be
// completed.

mods.thaumcraft.infusion_crafting.removeByOutput(item('thaumcraft:crystal_terra'))
// mods.thaumcraft.infusion_crafting.removeAll()

mods.thaumcraft.infusion_crafting.recipeBuilder()
    .researchKey('UNLOCKALCHEMY@3')
    .mainInput(item('minecraft:gunpowder'))
    .output(item('minecraft:gold_ingot'))
    .aspect(aspect('terra') * 20)
    .aspect('ignis', 30)
    .input(crystal('aer'))
    .input(crystal('ignis'))
    .input(crystal('aqua'))
    .input(crystal('terra'))
    .input(crystal('ordo'))
    .instability(10)
    .register()



// Lootbag:
// Control what the different rarities of lootbag drop when opened.

mods.thaumcraft.loot_bag.remove(item('minecraft:ender_pearl'), 0)
mods.thaumcraft.loot_bag.removeAll(2)

mods.thaumcraft.loot_bag.add(item('minecraft:dirt'), 100, 0)
mods.thaumcraft.loot_bag.add(item('minecraft:diamond_block'), 100, 2)

// Research:
// Create or modify existing research entries, which contain helpful information and unlock recipes, and can be gated
// behind specific items or events.

// mods.thaumcraft.research.removeCategory('BASICS')
// mods.thaumcraft.research.removeAllCategories()

mods.thaumcraft.research.researchCategoryBuilder()
    .key('BASICS2')
    .researchKey('UNLOCKAUROMANCY')
    .formulaAspect(aspect('herba') * 5)
    .formulaAspect(aspect('ordo') * 5)
    .formulaAspect(aspect('perditio') * 5)
    .formulaAspect('aer', 5)
    .formulaAspect('ignis', 5)
    .formulaAspect(aspect('terra') * 5)
    .formulaAspect('aqua', 5)
    .icon(resource('thaumcraft:textures/aspects/humor.png'))
    .background(resource('thaumcraft:textures/gui/gui_research_back_1.jpg'))
    .background2(resource('thaumcraft:textures/gui/gui_research_back_over.png'))
    .register()


// mods.thaumcraft.research.addResearchLocation(resource('thaumcraft:research/new.json'))
mods.thaumcraft.research.addScannable('KNOWLEDGETYPEHUMOR', item('minecraft:pumpkin'))

// Smelting Bonus:
// Additional item output when smelting a given item in the Infernal Furnace Multiblock.

mods.thaumcraft.smelting_bonus.removeByOutput(item('minecraft:gold_nugget'))
// mods.thaumcraft.smelting_bonus.removeAll()

mods.thaumcraft.smelting_bonus.recipeBuilder()
    .input(item('minecraft:cobblestone'))
    .output(item('minecraft:stone_button'))
    .chance(0.2F)
    .register()

mods.thaumcraft.smelting_bonus.recipeBuilder()
    .input(ore('stone'))
    .output(item('minecraft:obsidian'))
    .register()



// Warp:
// Determines if holding an item or equipping a piece of armor or a bauble gives warp, and how much warp it gives

mods.thaumcraft.warp.removeWarp(item('thaumcraft:void_hoe'))
// mods.thaumcraft.warp.removeAll()

mods.thaumcraft.warp.addWarp(item('minecraft:pumpkin'), 3)

