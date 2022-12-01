import net.minecraft.item.ItemStack
import net.minecraft.init.Blocks
import net.minecraft.util.ResourceLocation
import thaumcraft.api.aspects.Aspect
import thaumcraft.api.aspects.AspectList
import thaumcraft.api.ThaumcraftApiHelper

mods.thaumcraft.Crucible.removeByOutput(item('minecraft:gunpowder'))

mods.thaumcraft.Crucible.recipeBuilder()
        .catalyst(item('minecraft:rotten_flesh'))
        .output(item('minecraft:gold_ingot'))
        .researchKey("UNLOCKALCHEMY@3")
        .aspects((new AspectList()).add(Aspect.METAL, 20))
        .register()

mods.thaumcraft.InfusionCrafting.removeByOutput(item('thaumcraft:crystal_terra'))

mods.thaumcraft.InfusionCrafting.recipeBuilder()
        .input(item('minecraft:gunpowder'))
        .output(item('minecraft:gold_ingot'))
        .researchKey("UNLOCKALCHEMY@3")
        .aspects((new AspectList()).add(Aspect.METAL, 20))
        .instability(10)
        .components(new Object[]{ThaumcraftApiHelper.makeCrystal(Aspect.AIR), ThaumcraftApiHelper.makeCrystal(Aspect.FIRE), ThaumcraftApiHelper.makeCrystal(Aspect.WATER), ThaumcraftApiHelper.makeCrystal(Aspect.EARTH), ThaumcraftApiHelper.makeCrystal(Aspect.ORDER)})
        .register()

mods.thaumcraft.ArcaneWorkbench.removeByOutput(item('thaumcraft:mechanism_simple'))

mods.thaumcraft.ArcaneWorkbench.recipeBuilder()
        .input(item('minecraft:melon'), 9)
        .output(item('minecraft:melon_block'))
        .researchKey("UNLOCKALCHEMY@3")
        .aspects((new AspectList()).add(Aspect.EARTH, 1))
        .vis(5)
        .shapeless()
        .register()

mods.thaumcraft.ArcaneWorkbench.recipeBuilder()
        .input(item('minecraft:melon'), 1)
        .output(item('thaumcraft:void_hoe'))
        .researchKey("UNLOCKALCHEMY@3")
        .aspects(new AspectList())
        .vis(0)
        .shapeless()
        .register()

mods.thaumcraft.ArcaneWorkbench.recipeBuilder()
        .recipe(new Object[]{"SS ", "   ", "   ", (char)'S', item('minecraft:pumpkin_seeds')})
        .output(item('minecraft:pumpkin'))
        .researchKey("UNLOCKALCHEMY@3")
        .aspects((new AspectList()).add(Aspect.EARTH, 1))
        .vis(5)
        .register()

//mods.thaumcraft.Aspect.aspectBuilder()
//        .tag("humor")
//        .chatColor(14013676)
//        .components(new Aspect[]{Aspect.ENTROPY, Aspect.MIND})
//        .image(new ResourceLocation("thaumcraft", "textures/aspects/humor.png"))
//        .register()

mods.thaumcraft.AspectHelper()
        .object(item('minecraft:pumpkin'))
        .stripAspects()
        .aspects((new AspectList()).add(Aspect.METAL, 20))
        .register()

mods.thaumcraft.Warp.addWarp(item('minecraft:pumpkin'), 3)
mods.thaumcraft.Warp.removeWarp(item('thaumcraft:void_hoe'))

mods.thaumcraft.DustTrigger.removeByOutput(item('thaumcraft:arcane_workbench'))

mods.thaumcraft.DustTrigger.triggerBuilder()
        .target(Blocks.OBSIDIAN)
        .output(item('minecraft:enchanting_table'))
        .researchKey("UNLOCKALCHEMY@3")
        .register()

mods.thaumcraft.LootBag.removeAll(new int[]{0,1,2})
mods.thaumcraft.LootBag.addItem(item('minecraft:diamond_block'), 100, new int[]{0,1,2})

mods.thaumcraft.SmeltingBonus.recipeBuilder()
        .input(item('minecraft:cobblestone'))
        .output(item('minecraft:stone_button'))
        .chance(0.2F)
        .register()

mods.thaumcraft.SmeltingBonus.removeByOutput(item('minecraft:gold_nugget'))

//mods.thaumcraft.Research.researchCategoryBuilder()
//    .key("BASICS2")
//    .researchKey("UNLOCKAUROMANCY")
//    .formula((new AspectList()).add(Aspect.PLANT, 5).add(Aspect.ORDER, 5).add(Aspect.ENTROPY, 5).add(Aspect.AIR, 5).add(Aspect.FIRE, 5).add(Aspect.EARTH, 3).add(Aspect.WATER, 5))
//    .icon(new ResourceLocation("thaumcraft", "textures/aspects/humor.png"))
//    .background(new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_1.jpg"))
//    .background2(new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_over.png"))
//    .register()
//
//mods.thaumcraft.Research.addResearchLocation(new ResourceLocation("thaumcraft", "research/new.json"))

mods.thaumcraft.Research.addScannable("KNOWLEDGETYPEHUMOR", item('minecraft:pumpkin'))

mods.thaumcraft.Research.removeCategory("BASICS");