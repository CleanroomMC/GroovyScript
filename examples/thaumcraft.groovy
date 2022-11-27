import net.minecraft.item.ItemStack
import net.minecraft.init.Blocks
import net.minecraft.util.ResourceLocation
import thaumcraft.api.aspects.Aspect
import thaumcraft.api.aspects.AspectList
import thaumcraft.api.ThaumcraftApiHelper
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.AspectWrapper
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.Warp
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.LootBag
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.DustTrigger
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.SmeltingBonus
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.Research

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

//new AspectWrapper.AspectBuilder()
//        .tag("humor")
//        .chatColor(14013676)
//        .components(new Aspect[]{Aspect.ENTROPY, Aspect.MIND})
//        .image(new ResourceLocation("thaumcraft", "textures/aspects/humor.png"))
//        .register()

new AspectWrapper.AspectHelper()
    .object(item('minecraft:pumpkin'))
    .stripAspects()
    .aspects((new AspectList()).add(Aspect.METAL, 20))
    .register()

new Warp().addWarp(item('minecraft:pumpkin'), 3)
new Warp().removeWarp(item('thaumcraft:void_hoe'))

new DustTrigger().removeByOutput(item('thaumcraft:arcane_workbench'))

new DustTrigger.TriggerBuilder()
    .target(Blocks.OBSIDIAN)
    .output(item('minecraft:enchanting_table'))
    .researchKey("UNLOCKALCHEMY@3")
    .register()

new LootBag().removeAll(new int[]{0,1,2})
new LootBag().addItem(item('minecraft:diamond_block'), 100, new int[]{0,1,2})

new SmeltingBonus.SmeltingBonusBuilder()
    .input(item('minecraft:cobblestone'))
    .output(item('minecraft:stone_button'))
    .chance(0.2F)
    .register()

SmeltingBonus.removeByOutput(item('minecraft:gold_nugget'))

//new Research.ResearchCategoryBuilder()
//    .key("BASICS2")
//    .researchKey("UNLOCKAUROMANCY")
//    .formula((new AspectList()).add(Aspect.PLANT, 5).add(Aspect.ORDER, 5).add(Aspect.ENTROPY, 5).add(Aspect.AIR, 5).add(Aspect.FIRE, 5).add(Aspect.EARTH, 3).add(Aspect.WATER, 5))
//    .icon(new ResourceLocation("thaumcraft", "textures/aspects/humor.png"))
//    .background(new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_1.jpg"))
//    .background2(new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_over.png"))
//    .register()
//
//Research.addResearchLocation(new ResourceLocation("thaumcraft", "research/new.json"))

Research.addScannable("KNOWLEDGETYPEHUMOR", item('minecraft:pumpkin'))

Research.removeCategory("BASICS");