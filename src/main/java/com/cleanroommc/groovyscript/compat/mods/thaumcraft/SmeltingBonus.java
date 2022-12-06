package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.ItemsIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.internal.CommonInternals;

import java.util.ArrayList;
import java.util.List;

public class SmeltingBonus extends VirtualizedRegistry<ArrayList<Object>> {

    public SmeltingBonus() { super("SmeltingBonus", "smelting_bonus"); }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> {
            this.removeByOutput((ItemStack) recipe.get(1));
        });
        restoreFromBackup().forEach(recipe -> {
            this.add((IIngredient) recipe.get(0), (ItemStack) recipe.get(1), (float) recipe.get(2));
        });
    }

    public void add(IIngredient in, ItemStack out) {
        this.add(in, out, 0.33F);
    }

    public void add(IIngredient in, ItemStack out, float chance) {
        if (in instanceof OreDictIngredient) {
            ThaumcraftApi.addSmeltingBonus(((OreDictIngredient) in).getOreDict(), out, chance);
            ArrayList<Object> sb = new ArrayList<Object>();
            sb.add(((OreDictIngredient) in).getOreDict());
            sb.add(out);
            sb.add(chance);
            addScripted(sb);
        } else if (in.getMatchingStacks().length == 1) {
            ThaumcraftApi.addSmeltingBonus(in.getMatchingStacks()[0], out, chance);
            ArrayList<Object> sb = new ArrayList<Object>();
            sb.add(in.getMatchingStacks()[0]);
            sb.add(out);
            sb.add(chance);
            addScripted(sb);
        }
    }

    public void removeByOutput(ItemStack output) {
        List<ThaumcraftApi.SmeltBonus> remove = new ArrayList<>();
        for (ThaumcraftApi.SmeltBonus bonus : CommonInternals.smeltingBonus) {
            if (output.isItemEqual(bonus.out)) {
                remove.add(bonus);
                ArrayList<Object> sb = new ArrayList<Object>();
                sb.add(bonus.in);
                sb.add(bonus.out);
                sb.add(bonus.chance);
                addBackup(sb);
            }
        }
        CommonInternals.smeltingBonus.removeAll(remove);
    }

    public SmeltingBonusBuilder recipeBuilder() { return new SmeltingBonusBuilder(); }

    public static class SmeltingBonusBuilder {

        private IIngredient in = null;
        private ItemStack out = null;
        private float chance = 0;

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
                chance = 0;
            }
            return true;
        }

        public void register() {
            if (validate()) {
                if (chance == 0)
                    ModSupport.THAUMCRAFT.get().smeltingBonus.add(in, out);
                else
                    ModSupport.THAUMCRAFT.get().smeltingBonus.add(in, out, chance);
            }
        }
    }
}
