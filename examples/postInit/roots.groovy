
if (!isLoaded('roots')) return
println 'mod \'roots\' detected, running script'

// Bracket Handlers

// Cost Bracket Handler
cost('no_cost')
cost('additional_cost')
cost('all_cost_multiplier')
cost('specific_cost_adjustment')
cost('specific_cost_multiplier')

// Herb Bracket Handler
herb('spirit_herb')
herb('baffle_cap')
herb('moonglow_leaf')
herb('pereskia')
herb('terra_moss')
herb('wildroot')
herb('wildewheet')
herb('infernal_bulb')
herb('dewgonia')
herb('stalicripe')
herb('cloud_berry')

// Spell Bracket Handler
spell('roots:spell_geas')
spell('spell_geas') // Automatically adds `roots:`
spell('geas') // Automatically adds `roots:spell_`

// Modifier Bracket Handler
modifier('roots:extended_geas')

// Ritual Bracket Handler
ritual('ritual_summon_creatures')
ritual('summon_creatures') // Automatically prefixes `ritual_`


// Animal Harvest:
// Animal Harvest is a ritual that drops items from nearby mobs based on that mob's loottable without harming the mob. Only applies to allowed mobs.
mods.roots.animalharvest.recipeBuilder()
    .name('wither_skeleton_harvest') // Optional, either a ResourceLocation or a String
    .entity(entity('minecraft:wither_skeleton')) // Target Entity must extend EntityLivingBase
    .register()

mods.roots.animalharvest.recipeBuilder()
    .entity(entity('minecraft:enderman'))
    .register()

mods.roots.animalharvest.removeByName(resource('roots:chicken'))
mods.roots.animalharvest.removeByEntity(entity('minecraft:pig'))
//mods.roots.animalharvest.removeAll()


// Animal Harvest Fish:
// Animal Harvest Fish is another effect of the Animal Harvest ritual that applies if there are water source blocks within the ritual range.
mods.roots.animalharvestfish.recipeBuilder()
    .name('clay_fish') // Optional, either a ResourceLocation or a String
    .weight(50)
    .output(item('minecraft:clay'))
    .register()

mods.roots.animalharvestfish.recipeBuilder()
    .weight(13)
    .fish(item('minecraft:gold_ingot')) // fish is an alternative to output
    .register()

mods.roots.animalharvestfish.removeByName(resource('roots:cod'))
mods.roots.animalharvestfish.removeByOutput(item('minecraft:fish:1'))
mods.roots.animalharvestfish.removeByFish(item('minecraft:fish:2'))
//mods.roots.animalharvestfish.removeAll()


// Bark Carving:
// Bark Carving is a special set of alternate drops for blocks when broken with an item containing the tool type 'knife'. Amount dropped is up to 2 + fortune/looting level higher than the set amount.
// bark or barkcarving
mods.roots.bark.recipeBuilder() // also accessible via BarkCarving
    .name('gold_bark') // Optional, either a ResourceLocation or a String
    .input(item('minecraft:clay')) // An item that would be dropped by a block when broken
    .output(item('minecraft:gold_ingot'))
    .register()

mods.roots.bark.recipeBuilder()
    .blockstate(blockstate('minecraft:gold_block'))
    .output(item('minecraft:diamond'))
    .register()

mods.roots.bark.recipeBuilder()
    .input(blockstate('minecraft:diamond_block'))
    .output(item('minecraft:clay') * 10)
    .register()

mods.roots.bark.removeByName(resource('roots:wildwood'))
mods.roots.bark.removeByInput(item('minecraft:log'))
mods.roots.bark.removeByBlock(item('minecraft:log:1'))
mods.roots.bark.removeByOutput(item('roots:bark_dark_oak'))
//mods.roots.bark.removeAll()


// Chrysopoeia:
// Chrysopoeia is a spell that transmutes items held in the main hand.
mods.roots.chrysopoeia.recipeBuilder()
    .name('clay_transmute') // Optional, either a ResourceLocation or a String
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .register()

mods.roots.chrysopoeia.recipeBuilder()
    .input(item('minecraft:diamond') * 3)
    .output(item('minecraft:gold_ingot') * 3)
    .register()

mods.roots.chrysopoeia.removeByName(resource('roots:gold_from_silver'))
mods.roots.chrysopoeia.removeByInput(item('minecraft:rotten_flesh'))
mods.roots.chrysopoeia.removeByOutput(item('minecraft:iron_nugget'))
//mods.roots.chrysopoeia.removeAll()


// Fey Crafter:
// The Fey Crafter is a crafting mechanism that requires an activated Grove Stone nearby to take 5 item inputs and return an item output.
mods.roots.feycrafter.recipeBuilder()
    .name('clay_craft') // Optional, either a ResourceLocation or a String
    .input(item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone')) // Must be exactly 5
    .output(item('minecraft:clay'))
    .xp(100) // Optional, int
    .register()

mods.roots.feycrafter.removeByName(resource('roots:unending_bowl')) // WARNING: When reloading recipes with the Fey Crafter, you may encounter a ConcurrentModificationException!
mods.roots.feycrafter.removeByOutput(item('minecraft:gravel'))
//mods.roots.feycrafter.removeAll()


// Flower Generation:
// When running the Flower Growth Ritual, allowed flowers will generate in the area. Additionally, using the spell Growth Infusion's Floral Reproduction modifier will duplicate the flower.
mods.roots.flowergeneration.recipeBuilder()
    .name('clay_flower') // Optional, either a ResourceLocation or a String
    .flower(blockstate('minecraft:clay'))
    .register()

mods.roots.flowergeneration.removeByName(resource('roots:dandelion'))
//mods.roots.flowergeneration.removeByFlower(block('minecraft:red_flower')) // Removes by all blockstates of the block
mods.roots.flowergeneration.removeByFlower(block('minecraft:red_flower'), 1)
mods.roots.flowergeneration.removeByFlower(blockstate('minecraft:red_flower:2'))
mods.roots.flowergeneration.removeByFlower(item('minecraft:red_flower:3'))
//mods.roots.flowergeneration.removeAll()


// Life Essence:
// When shift right clicking a mob in the Life Essence Pool with Runic Shears, it will drop a Life-Essence, which allows that mob to be spawned via the Creature Summoning ritual.
mods.roots.lifeessence.add(entity('minecraft:wither_skeleton'))

mods.roots.lifeessence.remove(entity('minecraft:sheep'))
//mods.roots.lifeessence.removeAll()


// Mortar And Pestle:
// When right clicking a mortar containing the input items with a pestle, it will display a few colored sparkles, consume the inputs, and drop the item output.
mods.roots.mortar.recipeBuilder() // also accessible via MortarAndPestle
    .name('clay_mortar') // Optional, either a ResourceLocation or a String
    .input(item('minecraft:stone'),item('minecraft:gold_ingot'),item('minecraft:stone'),item('minecraft:gold_ingot'),item('minecraft:stone')) // Between 1 and 5
    .generate(false) // Optional, when inputs = 3 and generate isnt disabled, creates a recipe for each amount of items
    .output(item('minecraft:clay'))
    .color(1, 0, 0.1, 1, 0, 0.1) // Optional, sets color as red1, green1, blue1, red2, green2, blue2. All values must be a float between 0 and 1
    .register()

mods.roots.mortarandpestle.recipeBuilder()
    .input(item('minecraft:clay')) // With generate being true and only 1 input, this will generate a recipe for each amount of inputs to 5. Without this, only 1 clay could be converted at a time
    .output(item('minecraft:diamond'))
    .color(0, 0, 0.1) // Optional, sets the color as red, green, blue. All values must be a float between 0 and 1
    .register()

mods.roots.mortar.recipeBuilder()
    .input(item('minecraft:diamond'), item('minecraft:diamond'))
    .output(item('minecraft:gold_ingot') * 16)
    .red(0) // Optional, sets red1 and red2. All values must be a float between 0 and 1
    .green1(0.5) // Optional. The value must be a float between 0 and 1
    .green2(1) // Optional. The value must be a float between 0 and 1
    .register()

mods.roots.mortar.removeByName(resource('roots:wheat_flour')) // Many Mortar recipes are generated and have the resource location of [base]_x, where x is the output. They can all be removed by running removeByName(base)
mods.roots.mortar.removeByOutput(item('minecraft:string'))
//mods.roots.mortar.removeAll()


// Moss:
// Moss indicates a pair of items that can right click the input with a knife to turn it into the output and give a Terra Moss and right click the output with moss spores to turn it into the input.
mods.roots.moss.recipeBuilder()
    .input(item('minecraft:gold_block'))
    .output(item('minecraft:clay'))
    .register()

mods.roots.moss.add(item('minecraft:stained_glass:3'), item('minecraft:stained_glass:4'))

mods.roots.moss.remove(item('minecraft:cobblestone'))
//mods.roots.moss.removeAll()


// Pacifist:
// Pacifist is a list of entities which killing will give the player the advancement 'Untrue Pacifist'.
mods.roots.pacifist.recipeBuilder()
    .name('wither_skeleton_pacifist') // Optional, either a ResourceLocation or a String
    .entity(entity('minecraft:wither_skeleton'))
    .register()

mods.roots.pacifist.removeByName(resource('minecraft:chicken'))
mods.roots.pacifist.removeByEntity(entity('minecraft:cow'))
//mods.roots.pacifist.removeAll()


// Pyre:
// Converts 5 input items into the ouput after a period of time when the Pyre is lit on fire.
mods.roots.pyre.recipeBuilder()
    .name('clay_from_fire') // Optional, either a ResourceLocation or a String
    .input(item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'))
    .output(item('minecraft:clay'))
    .xp(5) // Optional, XP given when the recipe finishes in levels. Default 0
    .time(1) // Optional, time in ticks for the recipe to procress. Default 200
    .register()

mods.roots.pyre.recipeBuilder()
    .input(item('minecraft:gold_ingot'),item('minecraft:clay'),item('minecraft:clay'),item('minecraft:stone'),item('minecraft:stone'))
    .output(item('minecraft:diamond') * 32)
    .levels(5) // Optional, XP given when the recipe finishes in levels. Default 0
    .burnTime(1000) // Optional, time in ticks for the recipe to procress. Default 200
    .register()

mods.roots.pyre.removeByName(resource('roots:infernal_bulb'))
mods.roots.pyre.removeByOutput(item('minecraft:gravel'))
//mods.roots.pyre.removeAll()


// Runic Shear Block:
// Right clicking a Runic Shear on a block to convert it into a replacement block and drop items
mods.roots.runicshearblock.recipeBuilder()
    .name('clay_from_runic_diamond') // Optional, either a ResourceLocation or a String
    .state(blockstate('minecraft:diamond_block')) // Either an IBlockState or BlockStatePredicate
    .replacementState(blockstate('minecraft:air')) // NOTE: Not displayed in JEI
    .output(item('minecraft:clay') * 64)
    .displayItem(item('minecraft:diamond') * 9) // Optional, represents the state. Otherwise, is the first itemstack found from the given state
    .register()

mods.roots.runicshearblock.recipeBuilder()
    .state(mods.roots.predicates.stateBuilder().blockstate(blockstate('minecraft:yellow_flower:type=dandelion')).properties('type').register())
    .replacementState(blockstate('minecraft:red_flower:type=poppy'))
    .output(item('minecraft:gold_ingot'))
    .register()

mods.roots.runicshearblock.removeByName(resource('roots:wildewheet'))
mods.roots.runicshearblock.removeByOutput(item('roots:spirit_herb'))
mods.roots.runicshearblock.removeByState(blockstate('minecraft:beetroots:age=3'))
//mods.roots.runicshearblock.removeAll()


// Runic Shear Entity:
// Right clicking a Runic Shear on an entity. The entity will have a cooldown, preventing spamming.
// === WARNING: Not Reloadable ===
mods.roots.runicshearentity.recipeBuilder()
    .name('clay_from_wither_skeletons') // Optional, either a ResourceLocation or a String
    .entity(entity('minecraft:wither_skeleton'))
    .output(item('minecraft:clay'))
    .cooldown(1000) // Optional, time in ticks between harvesting. Default of 0
    .register()

mods.roots.runicshearentity.recipeBuilder()
    .name('creeper_at_the_last_moment') // Optional, either a ResourceLocation or a String
    .entity(entity('minecraft:creeper'))
    .output(item('minecraft:diamond'), item('minecraft:nether_star')) // WARNING: JEI will not display this properly. Add a tooltip to the first item informing about all options.
    .functionMap({ entityLivingBase ->
        // If the creeper has ignited (been right clicked with Flint and Steel), it will drop a Nether Star. Otherwise, its just dirt.
        if (entityLivingBase.hasIgnited()) return item('minecraft:nether_star')
        return item('minecraft:dirt')
    })
    .register()

mods.roots.runicshearentity.recipeBuilder()
    .entity(entity('minecraft:witch'))
    .output(item('minecraft:clay'))
    .register()

mods.roots.runicshearentity.removeByName(resource('roots:slime_strange_ooze'))
mods.roots.runicshearentity.removeByOutput(item('roots:fey_leather'))
mods.roots.runicshearentity.removeByEntity(entity('minecraft:chicken'))
//mods.roots.runicshearentity.removeAll()


// Summon Creature:
// When running a Summon Creature Ritual, the input items placed on Catalyst Plate will summon the target entity
mods.roots.summoncreature.recipeBuilder()
    .name('wither_skeleton_from_clay') // Optional, either a ResourceLocation or a String
    .input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay')) // Between 1 and 10
    .entity(entity('minecraft:wither_skeleton'))
    .register()

mods.roots.summoncreature.removeByName(resource('roots:cow'))
mods.roots.summoncreature.removeByEntity(entity('minecraft:chicken'))
//mods.roots.summoncreature.removeAll()


// Transmutation:
// When running the Transmutation, convert nearby blocks that match a set of conditions into either a block or items.
mods.roots.transmutation.recipeBuilder()
    .name('clay_duping') // Optional, either a ResourceLocation or a String
    .start(blockstate('minecraft:clay')) // Either an IBlockState or BlockStatePredicate
    .output(item('minecraft:clay_ball') * 30) // Either a state or output must be defined
    .condition(mods.roots.predicates.stateBuilder().blockstate(blockstate('minecraft:gold_block')).below().register()) // Optional, must be a WorldBlockStatePredicate (BlockStateAbove, or BlockStateBelow)
    .register()

mods.roots.transmutation.recipeBuilder()
    .start(mods.roots.predicates.stateBuilder().blockstate(blockstate('minecraft:yellow_flower:type=dandelion')).properties('type').register()) // Either an IBlockState or BlockStatePredicate
    .state(blockstate('minecraft:gold_block'))
    .condition(mods.roots.predicates.above(mods.roots.predicates.LEAVES))
    .register()

mods.roots.transmutation.recipeBuilder()
    .start(blockstate('minecraft:diamond_block'))
    .state(blockstate('minecraft:gold_block'))
    .register()

mods.roots.transmutation.removeByName(resource('roots:redstone_block_to_glowstone'))
mods.roots.transmutation.removeByOutput(item('minecraft:dye:3'))
mods.roots.transmutation.removeByOutput(blockstate('minecraft:log:variant=jungle'))
//mods.roots.transmutation.removeAll()


// Predicates:
// Predicates are used in Transmution and RunicShearBlock. They either match all blockstates of a block, or all blockstates that have the given properties that match the input blockstate.
// When used in Transmutation, they may require a direction (above or below) to be set.
mods.roots.predicates.stateBuilder()
    .blockstate(blockstate('minecraft:red_flower')) // Because this is has no 'properties' set, any blockstate of the base block will work.
    .register()

mods.roots.predicates.stateBuilder()
    .block(block('minecraft:red_flower')) // This is identical to the prior predicate.
    .register()

mods.roots.predicates.stateBuilder()
    .blockstate(blockstate('minecraft:red_flower:type=poppy'))
    .properties('type') // Optional, contains a String..., String[], or List<String>. Controls what properties are matched against with the provided blockstate
    .register()

mods.roots.predicates.stateBuilder() // Matches any log on the z-axis, as 'properties' only checks 'axis'. Matches above the target in Transmutation.
    .blockstate(blockstate('minecraft:log:axis=z:variant=oak'))
    .properties('axis')
    .above() // Optional, used for Transmution to indicate the required direction relative to the primary block.
    .register()

mods.roots.predicates.stateBuilder() // Matches any log, regardless of axis or variant, below the target in Transmutation.
    .blockstate(blockstate('minecraft:log'))
    .below() // Optional, used for Transmution to indicate the required direction relative to the primary block.
    .register()

// A few constant values are provided:
mods.roots.predicates.ANY // Matches everything
mods.roots.predicates.TRUE // Matches everything
mods.roots.predicates.LAVA // Matches a Lava source block. Required to render properly in JEI
mods.roots.predicates.WATER // Matches a Water source block. Required to render properly in JEI
mods.roots.predicates.LEAVES // Matches any leaf block

// Converts a BlockStatePredicate into a WorldBlockStatePredicate (BlockStateAbove, or BlockStateBelow)
mods.roots.predicates.above(mods.roots.predicates.LAVA)
mods.roots.predicates.below(mods.roots.predicates.LAVA)

mods.roots.predicates.create(blockstate('minecraft:red_flower'))
mods.roots.predicates.create(blockstate('minecraft:log:axis=z:variant=oak'), 'axis', 'variant')



// Rituals:
// Set the Pyre Ritual recipe and control all stats. Dump the modifiable stats into `roots.log` by running `/roots rituals`.
// WARNING: When reloading scripts, changes made are never undone.
mods.roots.rituals.recipeBuilder()
    .ritual(ritual('ritual_healing_aura'))
    .input(item('minecraft:clay'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot')) // Exactly 5 input items
    .register()

mods.roots.rituals.ritual(ritual('ritual_summon_creatures'))
    .recipe(item('minecraft:diamond'),item('minecraft:diamond'),item('minecraft:diamond'),item('minecraft:clay'),item('minecraft:clay'))
    .setDuration(10)
    .setProperty('tries', 10)
    .setProperty('radius_z', 1)
    .setProperty('radius_y', 10)
    .setProperty('glow_duration', 100)
    .setProperty('interval', 1)
    .setProperty('radius_x', 1)

mods.roots.rituals.ritual(ritual('ritual_spreading_forest'))
    .setDisabled()

//mods.roots.rituals.disableAll()

// Cost:
// A helper method to generate modifier costs for spells. Output should be used in a setModifierCost of a spell.
def exampleCost = mods.roots.spells.costBuilder()
    .cost(cost('additional_cost'), herb('spirit_herb'), 0.1)
    .cost(cost('all_cost_multiplier'), null, -0.125)
    .register()

// Spells:
// Controls the recipe for the given spell, the sound, all properties, the base cost, and each modifier's cost.
// WARNING: When reloading scripts, changes made are never undone.
mods.roots.spells.recipeBuilder()
    .spell(spell('spell_fey_light'))
    .input(item('minecraft:clay'),item('minecraft:diamond'),item('minecraft:gold_ingot')) // Up to 5 input items, but < 5 generates an error in the Patchouli book
    .register()

mods.roots.spells.spell(spell('spell_acid_cloud'))
    .recipe(item('minecraft:diamond'),item('minecraft:diamond'),item('minecraft:diamond'),item('minecraft:clay'),item('minecraft:clay'))
    .setSound(5) // Change the volume of the sound played
    .setCooldown(10)
    .setDamage(10)
    .setProperty('regeneration', 10) // An example list of the modifiable properties
    .setProperty('radius_general', 10)
    .setProperty('damage_count', 10)
    .setProperty('poison_amplification', 10)
    .setProperty('healing', 10)
    .setProperty('slow_duration', 10)
    .setProperty('weakness_duration', 10)
    .setProperty('regeneration_amplifier', 10)
    .setProperty('weakness_amplifier', 10)
    .setProperty('radius_boost', 10)
    .setProperty('healing_count', 10)
    .setProperty('night_modifier_high', 0.6)
    .setProperty('fire_duration', 10)
    .setProperty('undead_damage', 10)
    .setProperty('slow_amplifier', 10)
    .setProperty('underwater_boost', 10)
    .setProperty('night_modifier_low', 0.05)
    .clearCost() // clearCost or clearSpellCost
    .addCost(herb('baffle_cap'), 1.0) // addCost or addSpellCost
    .addSpellCost(herb('dewgonia'), 0.25)
    .setModifierCost(modifier('roots:moonfall'), exampleCost)
    .setModifierCost(modifier('roots:radius_boost'), exampleCost)
    .setModifierCost(modifier('roots:peaceful_cloud'), exampleCost)
    .setModifierCost(modifier('roots:weakening_cloud'), exampleCost)
    .setModifierCost(modifier('roots:moonfall'), exampleCost)
    .setModifierCost(modifier('roots:unholy_vanquisher'), exampleCost)
    .setModifierCost(modifier('roots:healing_cloud'), exampleCost)
    .setModifierCost(modifier('roots:increased_speed'), exampleCost)
    .setModifierCost(modifier('roots:fire_cloud'), exampleCost)
    .setModifierCost(modifier('roots:slowing'), exampleCost)
    .setModifierCost(modifier('roots:underwater_increase'), exampleCost)

mods.roots.spells.spell(spell('spell_harvest'))
    .disableSound()
    .setSpellCost(herb('baffle_cap'), 0.01) // setCost or setSpellCost

mods.roots.spells.spell(spell('spell_aqua_bubble'))
    .addSpellCost(herb('baffle_cap'), 0.01)
    .setSound(0.1)

mods.roots.spells.spell(spell('spell_geas'))
    .setDisabled()

//mods.roots.spells.disableAll()

// Modifiers
mods.roots.modifiers.disable(spell('spell_geas')) // Disable all Modifiers for the spell Geas
mods.roots.modifiers.enable(modifier('roots:extended_geas')) // Reenable these three modifiers
mods.roots.modifiers.enable(modifier('roots:animal_savior'))
mods.roots.modifiers.enable(modifier('roots:weakened_response'))
//mods.roots.modifiers.disableAll()
