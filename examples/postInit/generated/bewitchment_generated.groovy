
// Auto generated groovyscript example file
// MODS_LOADED: bewitchment

log 'mod \'bewitchment\' detected, running script'

// Altar Upgrades:
// Controls the valid upgrades placed atop the Witches' Altar, a multiblock that gain Magic Power from nearby plants and
// logs based on their uniqueness. The upgrades modify the amount gained per second and the maximum Magic Power the Altar
// can hold.

mods.bewitchment.altar_upgrade.remove(item('bewitchment:garnet'))
mods.bewitchment.altar_upgrade.remove(blockstate('bewitchment:goblet'))
mods.bewitchment.altar_upgrade.removeByType(com.bewitchment.api.registry.AltarUpgrade.Type.WAND)
// mods.bewitchment.altar_upgrade.removeAll()

mods.bewitchment.altar_upgrade.recipeBuilder()
    .cup()
    .predicate(blockstate('minecraft:clay'))
    .gain(-10)
    .multiplier(500)
    .register()

mods.bewitchment.altar_upgrade.recipeBuilder()
    .pentacle()
    .predicate(item('minecraft:gold_ingot'))
    .gain(1000)
    .register()

mods.bewitchment.altar_upgrade.recipeBuilder()
    .sword()
    .predicate(blockstate('minecraft:gold_block'))
    .multiplier(50)
    .register()

mods.bewitchment.altar_upgrade.recipeBuilder()
    .wand()
    .predicate(item('minecraft:iron_ingot'))
    .multiplier(0.5)
    .register()


// Athame Loot:
// When killing a mob with the Athame in the main hand, drops a random amount between `0` and `stackSize + lootingLevel` of
// each item that the entity passes the predicate of.

mods.bewitchment.athame_loot.removeByOutput(item('bewitchment:spectral_dust'))
// mods.bewitchment.athame_loot.removeAll()

mods.bewitchment.athame_loot.add(entity('minecraft:pig'), item('minecraft:gold_ingot'))
mods.bewitchment.athame_loot.add(entity('minecraft:cow'), item('minecraft:clay') * 5, item('minecraft:iron_sword'))

// Witches' Cauldron Brew:
// After throwing a `bewitchment:mandrake_root` in the Witches' Cauldron while Magic Power is provided, all items thrown in
// will add their potion effects when extracted via a `minecraft:glass_bottle`. Each fill of the Cauldron can create 3
// bottles. An ingredient can also refund an itemstack.

mods.bewitchment.brew.removeByInput(item('bewitchment:dragons_blood_resin'))
mods.bewitchment.brew.removeByOutput(item('minecraft:bowl'))
mods.bewitchment.brew.removeByPotion(potion('minecraft:instant_health'))
// mods.bewitchment.brew.removeAll()

mods.bewitchment.brew.recipeBuilder()
    .input(ore('netherStar'))
    .outputCheck(item('minecraft:nether_star'))
    .effect(new PotionEffect(potion('minecraft:strength'), 1800, 3))
    .output(item('bewitchment:catechu_brown'))
    .register()

mods.bewitchment.brew.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .effect(new PotionEffect(potion('minecraft:instant_health'), 1, 3))
    .output(item('minecraft:clay'))
    .register()

mods.bewitchment.brew.recipeBuilder()
    .input(item('minecraft:deadbush'))
    .effect(new PotionEffect(potion('minecraft:resistance'), 1800, 3))
    .register()


// Witches' Cauldron:
// Converts up to 10 input ingredients into up to 3 output itemstacks in the Witches' Cauldron while Magic Power is
// provided.

mods.bewitchment.cauldron.remove(resource('bewitchment:catechu_brown'))
mods.bewitchment.cauldron.removeByInput(item('bewitchment:tongue_of_dog'))
mods.bewitchment.cauldron.removeByOutput(item('bewitchment:iron_gall_ink'))
// mods.bewitchment.cauldron.removeAll()

mods.bewitchment.cauldron.recipeBuilder()
    .input(ore('logWood'))
    .input(item('minecraft:deadbush'))
    .input(item('minecraft:dye', 3))
    .output(item('bewitchment:catechu_brown'))
    .register()


// Curses:
// Allows applying curses to a player to cause unique effects in the Brazier with a `bewitchment:taglock` targeting the
// desired player.

mods.bewitchment.curse.remove(resource('bewitchment:berserker'))
mods.bewitchment.curse.removeByInput(item('minecraft:blaze_rod'))
// mods.bewitchment.curse.removeAll()

// Distillery:
// Converts up to 6 input ingredients into up to 6 output itemstacks in the Distillery at the cost of 1 Magic Power per
// tick. Takes 10 seconds.

mods.bewitchment.distillery.remove(resource('bewitchment:bottled_frostfire'))
mods.bewitchment.distillery.removeByInput(item('bewitchment:perpetual_ice'))
mods.bewitchment.distillery.removeByOutput(item('bewitchment:demonic_elixir'))
// mods.bewitchment.distillery.removeAll()

mods.bewitchment.distillery.recipeBuilder()
    .input(item('minecraft:glass_bottle'))
    .input(item('minecraft:snow'))
    .input(item('bewitchment:cleansing_balm'))
    .input(item('bewitchment:fiery_unguent'))
    .output(item('bewitchment:bottled_frostfire'))
    .output(item('bewitchment:empty_jar') * 2)
    .register()


// Fortune:
// Modifies potential Fortunes, with a random one being selected from the list when interacting with a Crystal Ball. Some
// amount of time will pass before the Fortune occurs, whereupon a customizable effect will happen.

mods.bewitchment.fortune.remove(resource('bewitchment:cornucopia'))
// mods.bewitchment.fortune.removeAll()

// Frostfire:
// Converts an input ingredient into an output itemstack once a second while inside a Frostfire block.

mods.bewitchment.frostfire.removeByInput(item('minecraft:iron_ore'))
// mods.bewitchment.frostfire.removeByOutput(item('bewitchment:cold_iron_ingot'))
// mods.bewitchment.frostfire.removeAll()

mods.bewitchment.frostfire.recipeBuilder()
    .input(item('minecraft:water_bucket'))
    .output(item('minecraft:ice'))
    .register()


// Incense:
// Converts up to 8 input ingredients in the Brazier when activated by a Flint and Steel for any number of potion effects
// that apply whenever a player wakes up nearby.

mods.bewitchment.incense.removeByInput(item('bewitchment:essence_of_vitality'))
mods.bewitchment.incense.removeByPotion(potion('minecraft:haste'))
// mods.bewitchment.incense.removeAll()

mods.bewitchment.incense.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:gold_ingot') * 5, item('minecraft:iron_ingot'))
    .potion(potion('minecraft:strength'), potion('minecraft:resistance'))
    .time(10000)
    .register()


// Oven:
// Converts an input itemstack into an output itemstack, with the ability to have a chance to produce an optional
// itemstack, and if producing the optional itemstack will consume a `bewitchment:empty_jar`. Requires furnace fuel to run
// and takes 10 seconds per recipe.

mods.bewitchment.oven.removeByInput(item('minecraft:sapling'))
mods.bewitchment.oven.removeByOutput(item('bewitchment:tallow'))
mods.bewitchment.oven.removeByOutput(item('bewitchment:garlic_grilled'))
// mods.bewitchment.oven.removeAll()

mods.bewitchment.oven.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .requiresJar(false)
    .byproduct(item('minecraft:gold_nugget'))
    .byproductChance(0.2f)
    .register()


// Pets:
// Sets what animals are valid for summoning via the `FortuneMeetPet` effect.

mods.bewitchment.pet.remove(entity('minecraft:ocelot'))
// mods.bewitchment.pet.removeAll()

mods.bewitchment.pet.add(entity('minecraft:cow'))

// Rituals:
// Converts up to 10 input ingredients into a ritual. The ritual can output up to 5 items, can require specific small,
// medium, and large circle sizes, can require a specific type for each circle, can require a specific entity nearby as a
// sacrifice, can set the time the ritual takes, and can set the Magic Power consumed to start and run the ritual.

mods.bewitchment.ritual.removeByInput(item('minecraft:poisonous_potato'))
mods.bewitchment.ritual.removeByOutput(item('bewitchment:purifying_earth'))
// mods.bewitchment.ritual.removeAll()



// Sigils:
// Converts up to 25 itemstacks into a single output itemstack.

mods.bewitchment.sigil.remove(resource('bewitchment:mending'))
mods.bewitchment.sigil.removeByInput(item('bewitchment:bottle_of_blood'))
mods.bewitchment.sigil.removeByOutput(item('bewitchment:sigil_disorientation'))
// mods.bewitchment.sigil.removeAll()



// Spinning Wheel:
// Converts up to 4 itemstacks into up to 2 output itemstacks in the Spinning Wheel at the cost of 1 Magic Power per tick.
// Takes 10 seconds.

mods.bewitchment.spinning_wheel.remove(resource('bewitchment:cobweb'))
mods.bewitchment.spinning_wheel.removeByInput(item('minecraft:string'))
mods.bewitchment.spinning_wheel.removeByOutput(item('bewitchment:spirit_string'))
// mods.bewitchment.spinning_wheel.removeAll()

mods.bewitchment.spinning_wheel.recipeBuilder()
    .input(item('minecraft:string'), item('minecraft:string'), item('minecraft:string'), item('minecraft:string'))
    .output(item('minecraft:gold_ingot') * 4, item('minecraft:web'))
    .register()
