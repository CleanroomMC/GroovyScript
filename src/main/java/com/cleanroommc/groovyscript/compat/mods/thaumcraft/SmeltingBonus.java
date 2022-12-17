package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.internal.CommonInternals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SmeltingBonus extends VirtualizedRegistry<ThaumcraftApi.SmeltBonus> {

    public SmeltingBonus() {
        super();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(this::remove);
        restoreFromBackup().forEach(this::add);
    }

    public void add(ThaumcraftApi.SmeltBonus bonus) {
        ThaumcraftApi.addSmeltingBonus(bonus.in, bonus.out, bonus.chance);
    }

    public void add(IIngredient in, ItemStack out) {
        this.add(in, out, 0.33F);
    }

    public void add(IIngredient in, ItemStack out, float chance) {
        if (in instanceof OreDictIngredient) {
            ThaumcraftApi.addSmeltingBonus(((OreDictIngredient) in).getOreDict(), out, chance);
            addScripted(new ThaumcraftApi.SmeltBonus(((OreDictIngredient) in).getOreDict(), out, chance));
        } else if (in.getMatchingStacks().length == 1) {
            ThaumcraftApi.addSmeltingBonus(in.getMatchingStacks()[0], out, chance);
            addScripted(new ThaumcraftApi.SmeltBonus(in.getMatchingStacks()[0], out, chance));
        }
    }

    public boolean remove(ThaumcraftApi.SmeltBonus bonus) {
        boolean removed = false;
        Iterator<ThaumcraftApi.SmeltBonus> it = CommonInternals.smeltingBonus.iterator();
        while(it.hasNext()) {
            ThaumcraftApi.SmeltBonus registeredBonus = it.next();
            if (registeredBonus.equals(bonus)) {
                it.remove();
                addBackup(registeredBonus);
                removed = true;
            }
        }
        return removed;
    }

    public void removeByOutput(ItemStack output) {
        List<ThaumcraftApi.SmeltBonus> remove = new ArrayList<>();
        for (ThaumcraftApi.SmeltBonus bonus : CommonInternals.smeltingBonus) {
            if (output.isItemEqual(bonus.out)) {
                remove.add(bonus);
                addBackup(bonus);
            }
        }
        CommonInternals.smeltingBonus.removeAll(remove);
    }

    public SimpleObjectStream<ThaumcraftApi.SmeltBonus> stream() {
        return new SimpleObjectStream<>(CommonInternals.smeltingBonus).setRemover(this::remove);
    }

    public SmeltingBonusBuilder recipeBuilder() {
        return new SmeltingBonusBuilder();
    }

    public static class SmeltingBonusBuilder {

        private IIngredient in = null;
        private ItemStack out = null;
        private float chance = 0.33F;

        public SmeltingBonusBuilder input(IIngredient input) {
            this.in = input;
            return this;
        }

        public SmeltingBonusBuilder output(ItemStack output) {
            this.out = output;
            return this;
        }

        public SmeltingBonusBuilder chance(float chance) {
            this.chance = chance;
            return this;
        }

        private boolean validate() {
            if (in == null) {
                GroovyLog.msg("Error while adding Smelting Bonus: input() must not be null.").error().post();
                return false;
            } else if (out == null) {
                GroovyLog.msg("Error while adding Smelting Bonus: output() must not be null.").error().post();
                return false;
            } else if (chance < 0) {
                chance = 0.33F;
            }
            return true;
        }

        public void register() {
            if (validate()) {
                ModSupport.THAUMCRAFT.get().smeltingBonus.add(in, out, chance);
            }
        }
    }
}
