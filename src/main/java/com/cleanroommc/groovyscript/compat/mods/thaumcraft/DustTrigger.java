package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.common.lib.crafting.DustTriggerOre;
import thaumcraft.common.lib.crafting.DustTriggerSimple;

import java.lang.reflect.Field;
import java.util.Iterator;

public class DustTrigger {

    public DustTrigger() {
        //do nothing
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
            } catch(NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeByOutput(ItemStack output) {
        doDirtyReflection();
        Iterator<IDustTrigger> it = IDustTrigger.triggers.iterator();
        while (it.hasNext()) {
            final IDustTrigger trigger = it.next();
            try {
                if (trigger instanceof DustTriggerSimple && simpleTriggerResult != null
                        && output.isItemEqual((ItemStack) simpleTriggerResult.get(trigger)))
                    it.remove();
                else if (trigger instanceof DustTriggerOre && oreTriggerResult != null
                        && output.isItemEqual((ItemStack) oreTriggerResult.get(trigger)))
                    it.remove();
            } catch(IllegalAccessException e) {
                GroovyLog.msg("Error while applying Salis Mundus effect: " + e).error().post();
            }
        }
    }

    public TriggerBuilder triggerBuilder() { return new TriggerBuilder(); }

    public class TriggerBuilder {

        private String research;
        private String ore;
        private Block target;
        private ItemStack output;

        public TriggerBuilder researchKey(String research){
            this.research = research;
            return this;
        }

        public TriggerBuilder output(ItemStack output) {
            this.output = output;
            return this;
        }

        public TriggerBuilder target(String oreDic) {
            this.ore = oreDic;
            return this;
        }

        public TriggerBuilder target(Block target) {
            this.target = target;
            return this;
        }

        public void register() {
            if(target == null) {
                IDustTrigger.registerDustTrigger(new DustTriggerOre(research, ore, output));
            } else {
                IDustTrigger.registerDustTrigger(new DustTriggerSimple(research, target, output));
            }
        }

    }
}
