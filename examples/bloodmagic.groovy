mods.bloodmagic.bloodaltar.removeByInput(item("minecraft:ender_pearl"))
mods.bloodmagic.bloodaltar.removeByOutput(item("bloodmagic:slate:4"))

// mods.bloodmagic.bloodaltar.removeAll()

mods.bloodmagic.bloodaltar.recipeBuilder()
        .input(item("minecraft:clay"))
        .output(item("minecraft:gold_ingot"))
        .tier(0)
        .drainRate(5)
        .syphon(10)
        .consumeRate(5)
        .register()

mods.bloodmagic.bloodaltar.recipeBuilder()
        .input(item("minecraft:gold_ingot"))
        .output(item("minecraft:diamond"))
        .minimumTier(3)
        .drainRate(100)
        .syphon(50000)
        .consumeRate(500)
        .register()


mods.bloodmagic.alchemyarray.removeByInput(item("bloodmagic:component:13"))
mods.bloodmagic.alchemyarray.removeByCatalyst(item("bloodmagic:slate:2"))
mods.bloodmagic.alchemyarray.removeByInputAndCatalyst(item("bloodmagic:component:7"), item("bloodmagic:slate:1"))
mods.bloodmagic.alchemyarray.removeByOutput(item("bloodmagic:sigil_void"))

// mods.bloodmagic.alchemyarray.removeAll()

mods.bloodmagic.alchemyarray.recipeBuilder()
        .input(item("minecraft:diamond"))
        .catalyst(item("bloodmagic:slate:1"))
        .output(item("minecraft:gold_ingot"))
        .register()

mods.bloodmagic.alchemyarray.recipeBuilder()
        .input(item("minecraft:clay"))
        .catalyst(item("minecraft:gold_ingot"))
        .output(item("minecraft:diamond"))
        .texture("bloodmagic:textures/models/AlchemyArrays/LightSigil.png")
        .register()



mods.bloodmagic.tartaricforge.removeByInput(item("minecraft:cauldron"), item("minecraft:stone"), item("minecraft:dye:4"), item("minecraft:diamond"))
mods.bloodmagic.tartaricforge.removeByInput(item("minecraft:gunpowder"), item("minecraft:redstone"))
mods.bloodmagic.tartaricforge.removeByOutput(item("bloodmagic:demon_crystal"))

// mods.bloodmagic.tartaricforge.removeAll()

mods.bloodmagic.tartaricforge.recipeBuilder()
        .input(item("minecraft:clay"), item("minecraft:clay"), item("minecraft:clay"), item("minecraft:clay"))
        .output(item("minecraft:gold_ingot"))
        .drain(5)
        .minimumSouls(10)
        .register()

mods.bloodmagic.tartaricforge.recipeBuilder()
        .input(item("minecraft:gold_ingot"), item("minecraft:clay"))
        .output(item("minecraft:diamond"))
        .soulDrain(200)
        .minimumSouls(500)
        .register()


mods.bloodmagic.alchemytable.removeByInput(item("minecraft:nether_wart"), item("minecraft:gunpowder"))
mods.bloodmagic.alchemytable.removeByOutput(item("minecraft:sand"))

// mods.bloodmagic.alchemytable.removeAll()

mods.bloodmagic.alchemytable.recipeBuilder()
        .input(item("minecraft:diamond"), item("minecraft:diamond"))
        .output(item("minecraft:clay"))
        .ticks(100)
        .minimumTier(2)
        .syphon(500)
        .register()

mods.bloodmagic.alchemytable.recipeBuilder()
        .input(item("minecraft:diamond"), item("minecraft:diamond"), item("minecraft:gold_ingot"), item("minecraft:gold_ingot"), item("bloodmagic:slate"), item("bloodmagic:slate"))
        .output(item("minecraft:clay"))
        .time(2000)
        .tier(5)
        .drain(25000)
        .register()


mods.bloodmagic.tranquility.remove(blockstate("minecraft:netherrack"), "FIRE")
mods.bloodmagic.tranquility.remove(blockstate("minecraft:dirt").getBlock(), "EARTHEN")

mods.bloodmagic.tranquility.recipeBuilder()
    .block(blockstate("minecraft:obsidian").getBlock())
    .tranquility("LAVA")
    .value(10)
    .register()

mods.bloodmagic.tranquility.recipeBuilder()
    .block(blockstate("minecraft:obsidian").getBlock())
    .tranquility("WATER")
    .value(10)
    .register()

mods.bloodmagic.tranquility.recipeBuilder()
    .blockstate(blockstate("minecraft:obsidian"))
    .tranquility("LAVA")
    .value(500)
    .register()


mods.bloodmagic.sacrificial.remove("minecraft:villager")

// mods.bloodmagic.sacrificial.removeAll()

mods.bloodmagic.sacrificial.recipeBuilder()
    .entity("minecraft:enderman")
    .value(1000)
    .register()

mods.bloodmagic.meteor.remove(item("minecraft:diamond_block"))
mods.bloodmagic.meteor.removeByInput(item("minecraft:gold_block"))
mods.bloodmagic.meteor.removeByCatalyst(item("minecraft:iron_block"))

// mods.bloodmagic.meteor.removeAll()

mods.bloodmagic.meteor.recipeBuilder()
    .catalyst(item("minecraft:gold_ingot"))
    .component(ore("oreIron"), 10)
    .component(ore("oreDiamond"), 10)
    .component(ore("stone"), 70)
    .radius(7)
    .explosionStrength(10)
    .cost(1000)
    .register()

mods.bloodmagic.meteor.recipeBuilder()
    .catalyst(item("minecraft:clay"))
    .component("blockClay", 10)
    .radius(20)
    .explosionStrength(20)
    .register()
