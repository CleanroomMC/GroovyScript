
// Auto generated groovyscript example file
// MODS_LOADED: roots

log.info 'mod \'roots\' detected, running script'

// Animal Harvest:
// Animal Harvest is a ritual that drops items from nearby mob's based on that mobs loottable without harming the mob. Only
// applies to allowed mobs.

mods.roots.animal_harvest.removeByEntity(entity('minecraft:pig'))
mods.roots.animal_harvest.removeByName(resource('roots:chicken'))
// mods.roots.animal_harvest.removeAll()

mods.roots.animal_harvest.recipeBuilder()
    .name('wither_skeleton_harvest')
    .entity(entity('minecraft:wither_skeleton'))
    .register()

mods.roots.animal_harvest.recipeBuilder()
    .entity(entity('minecraft:enderman'))
    .register()


// Animal Harvest Fish:
// Animal Harvest Fish is another effect of the Animal Harvest ritual that applies if there are water source blocks within
// the ritual range.

mods.roots.animal_harvest_fish.removeByFish(item('minecraft:fish:2'))
mods.roots.animal_harvest_fish.removeByName(resource('roots:cod'))
mods.roots.animal_harvest_fish.removeByOutput(item('minecraft:fish:1'))
// mods.roots.animal_harvest_fish.removeAll()

mods.roots.animal_harvest_fish.recipeBuilder()
    .name('clay_fish')
    .weight(50)
    .output(item('minecraft:clay'))
    .register()

mods.roots.animal_harvest_fish.recipeBuilder()
    .weight(13)
    .fish(item('minecraft:gold_ingot'))
    .register()


// Bark Carving:
// Bark Carving is a special set of alternate drops for blocks when broken with an item containing the tool type 'knife'.
// Amount dropped is up to 2 + fortune/looting level higher than the set amount.

mods.roots.bark_carving.removeByBlock(item('minecraft:log:1'))
mods.roots.bark_carving.removeByInput(item('minecraft:log'))
mods.roots.bark_carving.removeByName(resource('roots:wildwood'))
mods.roots.bark_carving.removeByOutput(item('roots:bark_dark_oak'))
// mods.roots.bark_carving.removeAll()

mods.roots.bark_carving.recipeBuilder()
    .name('gold_bark')
    .input(item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .register()

mods.roots.bark_carving.recipeBuilder()
    .blockstate(blockstate('minecraft:gold_block'))
    .output(item('minecraft:diamond'))
    .register()

mods.roots.bark_carving.recipeBuilder()
    .input(blockstate('minecraft:diamond_block'))
    .output(item('minecraft:clay') * 10)
    .register()


// Chrysopoeia:
// Chrysopoeia is a spell that transmutes items held in the main hand.

mods.roots.chrysopoeia.removeByInput(item('minecraft:rotten_flesh'))
mods.roots.chrysopoeia.removeByName(resource('roots:gold_from_silver'))
mods.roots.chrysopoeia.removeByOutput(item('minecraft:iron_nugget'))
// mods.roots.chrysopoeia.removeAll()

mods.roots.chrysopoeia.recipeBuilder()
    .name('clay_transmute')
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .register()

mods.roots.chrysopoeia.recipeBuilder()
    .input(item('minecraft:diamond') * 3)
    .output(item('minecraft:gold_ingot') * 3)
    .register()


// Fey Crafter:
// The Fey Crafter is a crafting mechanism that requires an activated Grove Stone nearby to take 5 item inputs and return
// an item output.

mods.roots.fey_crafter.removeByName(resource('roots:unending_bowl'))
mods.roots.fey_crafter.removeByOutput(item('minecraft:gravel'))
// mods.roots.fey_crafter.removeAll()

mods.roots.fey_crafter.recipeBuilder()
    .name('clay_craft')
    .input(item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone')) // Must be exactly 5
    .output(item('minecraft:clay'))
    .xp(100)
    .register()


// Flower Generation:
// When running the Flower Growth Ritual, allowed flowers will generate in the area if they can be placed on the given soil
// block. Additionally, using the spell Growth Infusion's Floral Reproduction modifier will duplicate the flower,
// regardless of the soil block.

mods.roots.flower_generation.removeByFlower(block('minecraft:red_flower'))
mods.roots.flower_generation.removeByFlower(block('minecraft:red_flower'), 1)
mods.roots.flower_generation.removeByFlower(blockstate('minecraft:red_flower:2'))
mods.roots.flower_generation.removeByFlower(item('minecraft:red_flower:3'))
mods.roots.flower_generation.removeByName(resource('roots:dandelion'))
// mods.roots.flower_generation.removeAll()

mods.roots.flower_generation.recipeBuilder()
    .name('clay_flower')
    .flower(blockstate('minecraft:clay'))
    .register()

mods.roots.flower_generation.recipeBuilder()
    .flower(blockstate('minecraft:gold_block'))
    .allowedSoils(item('minecraft:dirt'), item('minecraft:sandstone'))
    .register()


// Life Essence:
// When shift right clicking a mob in the Life Essence Pool with Runic Shears, it will drop a Life-Essence, which allows
// that mob to be spawned via the Creature Summoning ritual.

mods.roots.life_essence.remove(entity('minecraft:sheep'))
// mods.roots.life_essence.removeAll()

mods.roots.life_essence.add(entity('minecraft:wither_skeleton'))

// Modifiers:
// Controls what spell modifiers are enabled and can be used.

mods.roots.modifiers.disable(spell('spell_geas'))
// mods.roots.modifiers.disableAll()

mods.roots.modifiers.enable(modifier('roots:weakened_response'))
mods.roots.modifiers.enable(resource('roots:animal_savior'))
mods.roots.modifiers.enable('extended_geas')

// Mortar And Pestle:
// When right clicking a mortar containing the input items with a pestle, it will display a few colored sparkles, consume
// the inputs, and drop the item output.

mods.roots.mortar.removeByName(resource('roots:wheat_flour'))
mods.roots.mortar.removeByOutput(item('minecraft:string'))
// mods.roots.mortar.removeAll()

mods.roots.mortar.recipeBuilder()
    .name('clay_mortar')
    .input(item('minecraft:stone'),item('minecraft:gold_ingot'),item('minecraft:stone'),item('minecraft:gold_ingot'),item('minecraft:stone'))
    .generate(false)
    .output(item('minecraft:clay'))
    .color(1, 0, 0.1, 1, 0, 0.1)
    .register()

mods.roots.mortar.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .color(0, 0, 0.1)
    .register()

mods.roots.mortar.recipeBuilder()
    .input(item('minecraft:diamond'), item('minecraft:diamond'))
    .output(item('minecraft:gold_ingot') * 16)
    .red(0)
    .green1(0.5)
    .green2(1)
    .register()


// Moss:
// Moss indicates a pair of items that can right click the input with a knife to turn it into the output and give a Terra
// Moss and right click the output with moss spores to turn it into the input.

mods.roots.moss.remove(item('minecraft:cobblestone'))
// mods.roots.moss.removeAll()

mods.roots.moss.recipeBuilder()
    .input(item('minecraft:gold_block'))
    .output(item('minecraft:clay'))
    .register()


mods.roots.moss.add(item('minecraft:stained_glass:3'), item('minecraft:stained_glass:4'))

// Pacifist:
// Pacifist is a list of entities which killing will give the player the advancement 'Untrue Pacifist'.

mods.roots.pacifist.removeByEntity(entity('minecraft:cow'))
mods.roots.pacifist.removeByName(resource('minecraft:chicken'))
// mods.roots.pacifist.removeAll()

mods.roots.pacifist.recipeBuilder()
    .name('wither_skeleton_pacifist')
    .entity(entity('minecraft:wither_skeleton'))
    .register()


// Predicates:
// Predicates are used in Transmutation and RunicShearBlock. They either match all blockstates of a block, or all
// blockstates that have the given properties that match the input blockstate.

mods.roots.predicates.stateBuilder()
    .blockstate(blockstate('minecraft:red_flower'))
    .register()

mods.roots.predicates.stateBuilder()
    .block(block('minecraft:red_flower'))
    .register()

mods.roots.predicates.stateBuilder()
    .blockstate(blockstate('minecraft:red_flower:type=poppy'))
    .properties('type')
    .register()

mods.roots.predicates.stateBuilder()
    .blockstate(blockstate('minecraft:log:axis=z:variant=oak'))
    .properties('axis')
    .above()
    .register()

mods.roots.predicates.stateBuilder()
    .blockstate(blockstate('minecraft:log'))
    .below()
    .register()


// Pyre:
// Converts 5 input items into the output after a period of time when the Pyre is lit on fire.

mods.roots.pyre.removeByName(resource('roots:infernal_bulb'))
mods.roots.pyre.removeByOutput(item('minecraft:gravel'))
// mods.roots.pyre.removeAll()

mods.roots.pyre.recipeBuilder()
    .name('clay_from_fire')
    .input(item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'))
    .output(item('minecraft:clay'))
    .xp(5)
    .time(1)
    .register()

mods.roots.pyre.recipeBuilder()
    .input(item('minecraft:gold_ingot'),item('minecraft:clay'),item('minecraft:clay'),item('minecraft:stone'),item('minecraft:stone'))
    .output(item('minecraft:diamond') * 32)
    .levels(5)
    .burnTime(1000)
    .register()


// Rituals:
// Set the Pyre Ritual recipe and control all stats. Dump the modifiable stats into `roots.log` by running `/roots
// rituals`.

mods.roots.rituals.recipeBuilder()
    .ritual(ritual('ritual_healing_aura'))
    .input(item('minecraft:clay'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'),item('minecraft:gold_ingot'))
    .register()


// Runic Shear Block:
// Right clicking a Runic Shear on a block to convert it into a replacement block and drop items.

mods.roots.runic_shear_block.removeByName(resource('roots:wildewheet'))
mods.roots.runic_shear_block.removeByOutput(item('roots:spirit_herb'))
mods.roots.runic_shear_block.removeByState(blockstate('minecraft:beetroots:age=3'))
// mods.roots.runic_shear_block.removeAll()

mods.roots.runic_shear_block.recipeBuilder()
    .name('clay_from_runic_diamond')
    .state(blockstate('minecraft:diamond_block'))
    .replacementState(blockstate('minecraft:air'))
    .output(item('minecraft:clay') * 64)
    .displayItem(item('minecraft:diamond') * 9)
    .register()

mods.roots.runic_shear_block.recipeBuilder()
    .state(mods.roots.predicates.stateBuilder().blockstate(blockstate('minecraft:yellow_flower:type=dandelion')).properties('type').register())
    .replacementState(blockstate('minecraft:red_flower:type=poppy'))
    .output(item('minecraft:gold_ingot'))
    .register()


// Runic Shear Entity:
// Right clicking a Runic Shear on an entity. The entity will have a cooldown, preventing spamming.

mods.roots.runic_shear_entity.removeByEntity(entity('minecraft:chicken'))
mods.roots.runic_shear_entity.removeByName(resource('roots:slime_strange_ooze'))
mods.roots.runic_shear_entity.removeByOutput(item('roots:fey_leather'))
// mods.roots.runic_shear_entity.removeAll()

mods.roots.runic_shear_entity.recipeBuilder()
    .name('clay_from_wither_skeletons')
    .entity(entity('minecraft:wither_skeleton'))
    .output(item('minecraft:clay'))
    .cooldown(1000)
    .register()

mods.roots.runic_shear_entity.recipeBuilder()
    .name('creeper_at_the_last_moment')
    .entity(entity('minecraft:creeper'))
    .output(item('minecraft:diamond'), item('minecraft:nether_star'))
    .functionMap({ entityLivingBase -> entityLivingBase.hasIgnited() ? item('minecraft:nether_star') : item('minecraft:dirt') })
    .register()

mods.roots.runic_shear_entity.recipeBuilder()
    .entity(entity('minecraft:witch'))
    .output(item('minecraft:clay'))
    .register()


// Spells:
// Controls the recipe for the given spell, the sound, all properties, the base cost, and each modifier's cost.

mods.roots.spells.costBuilder()
    .register()

mods.roots.spells.costBuilder()
    .cost(cost('additional_cost'), herb('dewgonia'), 0.25)
    .register()

mods.roots.spells.costBuilder()
    .cost(cost('additional_cost'), herb('spirit_herb'), 0.1)
    .cost(cost('all_cost_multiplier'), null, -0.125)
    .register()

mods.roots.spells.recipeBuilder()
    .spell(spell('spell_fey_light'))
    .input(item('minecraft:clay'), item('minecraft:diamond'), item('minecraft:gold_ingot'))
    .register()


// Summon Creature:
// When running a Summon Creature Ritual, the input items placed on Catalyst Plate will summon the target entity.

mods.roots.summon_creature.removeByEntity(entity('minecraft:chicken'))
mods.roots.summon_creature.removeByName(resource('roots:cow'))
// mods.roots.summon_creature.removeAll()

mods.roots.summon_creature.recipeBuilder()
    .name('wither_skeleton_from_clay')
    .input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'))
    .entity(entity('minecraft:wither_skeleton'))
    .register()


// Transmutation:
// When running the Transmutation, convert nearby blocks that match a set of conditions into either a block or items.

mods.roots.transmutation.removeByName(resource('roots:redstone_block_to_glowstone'))
mods.roots.transmutation.removeByOutput(blockstate('minecraft:log:variant=jungle'))
mods.roots.transmutation.removeByOutput(item('minecraft:dye:3'))
// mods.roots.transmutation.removeAll()

mods.roots.transmutation.recipeBuilder()
    .name('clay_duping')
    .start(blockstate('minecraft:clay'))
    .output(item('minecraft:clay_ball') * 30)
    .condition(mods.roots.predicates.stateBuilder().blockstate(blockstate('minecraft:gold_block')).below().register())
    .register()

mods.roots.transmutation.recipeBuilder()
    .start(mods.roots.predicates.stateBuilder().blockstate(blockstate('minecraft:yellow_flower:type=dandelion')).properties('type').register())
    .state(blockstate('minecraft:gold_block'))
    .condition(mods.roots.predicates.above(mods.roots.predicates.LEAVES))
    .register()

mods.roots.transmutation.recipeBuilder()
    .start(blockstate('minecraft:diamond_block'))
    .state(blockstate('minecraft:gold_block'))
    .register()


