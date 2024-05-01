package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.common.lib.crafting.DustTriggerOre;
import thaumcraft.common.lib.crafting.DustTriggerSimple;

import java.lang.reflect.Field;
import java.util.Iterator;

@RegistryDescription
public class DustTrigger extends VirtualizedRegistry<IDustTrigger> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(this::remove);
        restoreFromBackup().forEach(this::add);
    }

    private Field simpleTriggerResult;
    private Field oreTriggerResult;
    private boolean didReflection = false;

    private void doDirtyReflection() {
        if (!didReflection) {
            try {
                simpleTriggerResult = DustTriggerSimple.class.getDeclaredField("result");
                simpleTriggerResult.setAccessible(true);

                oreTriggerResult = DustTriggerOre.class.getDeclaredField("result");
                oreTriggerResult.setAccessible(true);

                didReflection = true;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    public void add(IDustTrigger trigger) {
        IDustTrigger.registerDustTrigger(trigger);
        addScripted(trigger);
    }

    public boolean remove(IDustTrigger trigger) {
        doDirtyReflection();
        Iterator<IDustTrigger> it = IDustTrigger.triggers.iterator();
        boolean found = false;
        while (it.hasNext()) {
            final IDustTrigger registeredTrigger = it.next();
            if (trigger instanceof DustTriggerSimple && registeredTrigger instanceof DustTriggerSimple
                && trigger.equals(registeredTrigger)) {
                it.remove();
                addBackup(trigger);
                found = true;
            } else if (trigger instanceof DustTriggerOre && registeredTrigger instanceof DustTriggerOre
                       && trigger.equals(registeredTrigger)) {
                it.remove();
                addBackup(trigger);
                found = true;
            }
        }
        return found;
    }

    @MethodDescription(example = @Example("item('thaumcraft:arcane_workbench')"))
    public void removeByOutput(ItemStack output) {
        doDirtyReflection();
        Iterator<IDustTrigger> it = IDustTrigger.triggers.iterator();
        while (it.hasNext()) {
            final IDustTrigger trigger = it.next();
            try {
                if (trigger instanceof DustTriggerSimple && simpleTriggerResult != null
                    && output.isItemEqual((ItemStack) simpleTriggerResult.get(trigger))) {
                    it.remove();
                    addBackup(trigger);
                } else if (trigger instanceof DustTriggerOre && oreTriggerResult != null
                           && output.isItemEqual((ItemStack) oreTriggerResult.get(trigger))) {
                    it.remove();
                    addBackup(trigger);
                }
            } catch (IllegalAccessException e) {
                GroovyLog.msg("Error while applying Salis Mundus effect: " + e).error().post();
            }
        }
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IDustTrigger> streamRecipes() {
        return new SimpleObjectStream<>(IDustTrigger.triggers)
                .setRemover(this::remove);
    }

    @RecipeBuilderDescription(example = {
            @Example(".researchKey('UNLOCKALCHEMY@3').target(block('minecraft:obsidian')).output(item('minecraft:enchanting_table'))"),
            @Example(".researchKey('UNLOCKALCHEMY@3').target(ore('cropPumpkin')).output(item('minecraft:lit_pumpkin'))")
    })
    public TriggerBuilder triggerBuilder() {
        return new TriggerBuilder();
    }

    public static class TriggerBuilder {

        @Property
        private String research;
        @Property(requirement = "groovyscript.wiki.thaumcraft.dust_trigger.target.required")
        private String ore;
        @Property(requirement = "groovyscript.wiki.thaumcraft.dust_trigger.target.required")
        private Block target;
        @Property
        private ItemStack output;

        @RecipeBuilderMethodDescription(field = "research")
        public TriggerBuilder researchKey(String research) {
            this.research = research;
            return this;
        }

        @RecipeBuilderMethodDescription
        public TriggerBuilder output(ItemStack output) {
            this.output = output;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ore")
        public TriggerBuilder target(String oreDic) {
            this.ore = oreDic;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ore")
        public TriggerBuilder target(OreDictIngredient oreDic) {
            this.ore = oreDic.getOreDict();
            return this;
        }

        @RecipeBuilderMethodDescription
        public TriggerBuilder target(Block target) {
            this.target = target;
            return this;
        }

        @RecipeBuilderRegistrationMethod
        public void register() {
            if (target == null) {
                ModSupport.THAUMCRAFT.get().dustTrigger.add(new DustTriggerOre(research, ore, output));
            } else {
                ModSupport.THAUMCRAFT.get().dustTrigger.add(new DustTriggerSimple(research, target, output));
            }
        }

    }
}
