package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IObjectParser;
import com.cleanroommc.groovyscript.api.infocommand.InfoParserRegistry;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.arcane.ArcaneWorkbench;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.Aspect;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectHelper;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectItemStackExpansion;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.warp.Warp;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.warp.WarpItemStackExpansion;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import com.cleanroommc.groovyscript.sandbox.expand.ExpansionHelper;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;

import java.util.Collection;

public class Thaumcraft extends GroovyPropertyContainer {

    public final Crucible crucible = new Crucible();
    public final InfusionCrafting infusionCrafting = new InfusionCrafting();
    public final ArcaneWorkbench arcaneWorkbench = new ArcaneWorkbench();
    public final Aspect aspect = new Aspect();
    public final LootBag lootBag = new LootBag();
    public final Warp warp = new Warp();
    public final SmeltingBonus smeltingBonus = new SmeltingBonus();
    public final Research research = new Research();
    public final DustTrigger dustTrigger = new DustTrigger();

    public final AspectHelper aspectHelper = new AspectHelper();

    public static String asGroovyCode(thaumcraft.api.aspects.Aspect aspect, boolean colored) {
        return GroovyScriptCodeConverter.formatGenericHandler("aspect", aspect.getTag(), colored);
    }

    public static String asGroovyCode(AspectStack aspectStack, boolean colored) {
        return asGroovyCode(aspectStack.getAspect(), colored) + GroovyScriptCodeConverter.formatMultiple(aspectStack.getAmount(), colored);
    }

    @Override
    public void initialize(GroovyContainer<?> container) {
        container.objectMapperBuilder("aspect", AspectStack.class)
                .parser(IObjectParser.wrapStringGetter(Thaumcraft::getAspect, AspectStack::new))
                .completerOfNames(thaumcraft.api.aspects.Aspect.aspects::keySet)
                .docOfType("aspect stack")
                .toGroovyCode(x -> asGroovyCode(x, false))
                .register();
        container.objectMapperBuilder("crystal", ItemStack.class)
                .parser(IObjectParser.wrapStringGetter(Thaumcraft::getAspect, ThaumcraftApiHelper::makeCrystal))
                .completerOfNames(thaumcraft.api.aspects.Aspect.aspects::keySet)
                .defaultValue(() -> ItemStack.EMPTY)
                .docOfType("aspect crystal as item stack")
                .register();
        ExpansionHelper.mixinClass(ItemStack.class, AspectItemStackExpansion.class);
        ExpansionHelper.mixinClass(ItemStack.class, WarpItemStackExpansion.class);

        InfoParserRegistry.addInfoParser(InfoParserAspect.instance);
    }

    public static AspectList makeAspectList(Collection<AspectStack> aspects) {
        AspectList list = new AspectList();
        for (AspectStack aspectStack : aspects) {
            list.add(aspectStack.getAspect(), aspectStack.getAmount());
        }
        return list;
    }

    public static thaumcraft.api.aspects.Aspect validateAspect(String tag) {
        thaumcraft.api.aspects.Aspect aspect = thaumcraft.api.aspects.Aspect.getAspect(tag);
        if (aspect == null) GroovyLog.msg("Can't find aspect for name {}!", tag).error().post();
        return aspect;
    }

    public static thaumcraft.api.aspects.Aspect getAspect(String tag) {
        return thaumcraft.api.aspects.Aspect.getAspect(tag);
    }
}
