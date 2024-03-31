
// Auto generated groovyscript example file
// MODS_LOADED: calculator

println 'mod \'calculator\' detected, running script'

// Algorithm Separator:
// Converts an input itemstack into two output itemstacks.

mods.calculator.algorithm_separator.removeByInput(item('calculator:tanzaniteleaves'))
mods.calculator.algorithm_separator.removeByOutput(item('calculator:weakeneddiamond'))
// mods.calculator.algorithm_separator.removeAll()

mods.calculator.algorithm_separator.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'), item('minecraft:diamond'))
    .register()


// Analysing Chamber:
// Takes a non-analysed Circuit and analyses it, converting it into usable Stable or Analysed Circuit. Will produce power
// and item outputs based on randomly generated NBT data.

mods.calculator.analysing_chamber.removeByInput(item('sonarcore:reinforceddirtblock'))
// mods.calculator.analysing_chamber.removeAll()

mods.calculator.analysing_chamber.recipeBuilder()
    .slot(6)
    .location(1)
    .output(item('minecraft:diamond'))
    .register()

mods.calculator.analysing_chamber.recipeBuilder()
    .slot(1)
    .location(18)
    .output(item('minecraft:clay'))
    .register()


// Atomic Calculator:
// Converts three input itemstacks into one output itemstack.

mods.calculator.atomic_calculator.removeByInput(item('minecraft:end_stone'))
mods.calculator.atomic_calculator.removeByOutput(item('calculator:firediamond'))
// mods.calculator.atomic_calculator.removeAll()

mods.calculator.atomic_calculator.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'))
    .output(item('minecraft:gold_ingot') * 4)
    .register()


// Calculator:
// Converts two input itemstacks into one output itemstack.

mods.calculator.basic_calculator.removeByInput(item('minecraft:cobblestone'))
mods.calculator.basic_calculator.removeByOutput(item('calculator:reinforcedironingot'))
// mods.calculator.basic_calculator.removeAll()

mods.calculator.basic_calculator.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:clay'))
    .output(item('minecraft:gold_ingot'))
    .register()


// Conductor Mast:
// Converts an input itemstack into an output itemstack, costing a configurable amount of power. This power can only be
// gained via the Conductor Mast's semi-regular generation of lightning strikes.

mods.calculator.conductor_mast.removeByInput(item('calculator:firediamond'))
mods.calculator.conductor_mast.removeByOutput(item('calculator:material:7'))
// mods.calculator.conductor_mast.removeAll()

mods.calculator.conductor_mast.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .value(100)
    .register()


// Extraction Chamber:
// Converts an input itemstack into an output itemstack, and gives either a Dirty or Damaged Circuit.

mods.calculator.extraction_chamber.removeByInput(item('minecraft:dirt'))
mods.calculator.extraction_chamber.removeByOutput(item('calculator:smallstone'))
// mods.calculator.extraction_chamber.removeAll()

mods.calculator.extraction_chamber.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.calculator.extraction_chamber.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:clay'))
    .isDamaged()
    .register()


// Fabrication Chamber:
// Converts Stable and Analysed Circuits into output itemstacks.

mods.calculator.fabrication_chamber.removeByInput(item('calculator:circuitboard:8').withNbt([Stable: 0, Analysed: 1]))
mods.calculator.fabrication_chamber.removeByOutput(item('calculator:calculatorassembly'))
// mods.calculator.fabrication_chamber.removeAll()

mods.calculator.fabrication_chamber.recipeBuilder()
    .input(item('calculator:circuitboard:8').withNbt([Stable: 0, Analysed: 1]))
    .output(item('minecraft:diamond'))
    .register()

mods.calculator.fabrication_chamber.recipeBuilder()
    .input(item('calculator:circuitboard:0').withNbt([Stable: 0, Analysed: true]), item('calculator:circuitboard:1').withNbt([Stable: 0, Analysed: true]), item('calculator:circuitboard:2').withNbt([Stable: 0, Analysed: true]), item('calculator:circuitboard:3').withNbt([Stable: 0, Analysed: true]), item('calculator:circuitboard:4').withNbt([Stable: 0, Analysed: true]))
    .input(item('calculator:circuitboard:0').withNbt([Stable: 1, Analysed: true]), item('calculator:circuitboard:1').withNbt([Stable: 1, Analysed: true]), item('calculator:circuitboard:2').withNbt([Stable: 1, Analysed: true]), item('calculator:circuitboard:3').withNbt([Stable: 1, Analysed: true]), item('calculator:circuitboard:4').withNbt([Stable: 1, Analysed: true]))
    .output(item('minecraft:clay'))
    .register()


// Flawless Calculator:
// Converts four input itemstacks into one output itemstack.

mods.calculator.flawless_calculator.removeByInput(item('minecraft:obsidian'))
mods.calculator.flawless_calculator.removeByOutput(item('minecraft:ender_pearl'))
// mods.calculator.flawless_calculator.removeAll()

mods.calculator.flawless_calculator.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'))
    .output(item('minecraft:gold_ingot') * 16)
    .register()


// Glowstone Extractor:
// Converts an input itemstack into power, at the cost of a burnable fuel.

mods.calculator.glowstone_extractor.removeByInput(item('minecraft:glowstone'))
// mods.calculator.glowstone_extractor.removeAll()

mods.calculator.glowstone_extractor.recipeBuilder()
    .input(item('minecraft:clay'))
    .value(100)
    .register()


// Health Processor:
// Converts an input itemstack into \"Health Points\", which charge a Health or Nutrition module and are converted into
// rapidly regenerating health.

mods.calculator.health_processor.removeByInput(item('minecraft:blaze_rod'))
// mods.calculator.health_processor.removeAll()

mods.calculator.health_processor.recipeBuilder()
    .input(item('minecraft:clay'))
    .value(100)
    .register()


// Precision Chamber:
// Converts an input itemstack into two output itemstacks.

mods.calculator.precision_chamber.removeByInput(item('minecraft:clay'))
mods.calculator.precision_chamber.removeByOutput(item('calculator:soil'))
// mods.calculator.precision_chamber.removeAll()

mods.calculator.precision_chamber.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'), item('calculator:circuitdamaged:4'))
    .register()

mods.calculator.precision_chamber.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond'), item('minecraft:diamond'))
    .register()


// Processing Chamber:
// Converts an input itemstack into an output itemstack, typically a Dirty or Damaged Circuit. By default, functions as a
// combined Restoration and Reassembly Chamber.

mods.calculator.processing_chamber.removeByInput(item('calculator:circuitdamaged:4'))
mods.calculator.processing_chamber.removeByOutput(item('calculator:circuitboard:1'))
// mods.calculator.processing_chamber.removeAll()

mods.calculator.processing_chamber.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()


// Reassembly Chamber:
// Converts an input itemstack into an output itemstack, typically a Damaged Circuit.

mods.calculator.reassembly_chamber.removeByInput(item('calculator:circuitdamaged:12'))
mods.calculator.reassembly_chamber.removeByOutput(item('calculator:circuitboard:13'))
// mods.calculator.reassembly_chamber.removeAll()

mods.calculator.reassembly_chamber.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()


// Redstone Extractor:
// Converts an input itemstack into power, at the cost of a burnable fuel.

mods.calculator.redstone_extractor.removeByInput(item('minecraft:redstone_block'))
// mods.calculator.redstone_extractor.removeAll()

mods.calculator.redstone_extractor.recipeBuilder()
    .input(item('minecraft:clay'))
    .value(100)
    .register()


// Restoration Chamber:
// Converts an input itemstack into an output itemstack, typically a Dirty Circuit.

mods.calculator.restoration_chamber.removeByInput(item('calculator:circuitdirty:5'))
mods.calculator.restoration_chamber.removeByOutput(item('calculator:circuitboard:3'))
// mods.calculator.restoration_chamber.removeAll()

mods.calculator.restoration_chamber.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()


// Scientific Calculator:
// Converts two input itemstacks into one output itemstack.

mods.calculator.scientific_calculator.removeByInput(item('calculator:smallamethyst'))
mods.calculator.scientific_calculator.removeByOutput(item('calculator:redstoneingot'))
// mods.calculator.scientific_calculator.removeAll()

mods.calculator.scientific_calculator.recipeBuilder()
    .input(item('minecraft:clay'), item('minecraft:clay'))
    .output(item('minecraft:gold_ingot') * 2)
    .register()


// Starch Extractor:
// Converts an input itemstack into power, at the cost of a burnable fuel.

mods.calculator.starch_extractor.removeByInput(item('minecraft:apple'))
// mods.calculator.starch_extractor.removeAll()

mods.calculator.starch_extractor.recipeBuilder()
    .input(item('minecraft:clay'))
    .value(100)
    .register()


// Stone Separator:
// Converts an input itemstack into two output itemstacks.

mods.calculator.stone_separator.removeByInput(item('minecraft:gold_ore'))
mods.calculator.stone_separator.removeByOutput(item('calculator:reinforcedironingot'))
// mods.calculator.stone_separator.removeAll()

mods.calculator.stone_separator.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'), item('minecraft:diamond'))
    .register()


