package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
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

@RegistryDescription
public class SmeltingBonus extends VirtualizedRegistry<ThaumcraftApi.SmeltBonus> {

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

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.smelting_bonus.add0", type = MethodDescription.Type.ADDITION)
    public void add(IIngredient in, ItemStack out) {
        this.add(in, out, 0.33F);
    }

    @MethodDescription(description = "groovyscript.wiki.thaumcraft.smelting_bonus.add1", type = MethodDescription.Type.ADDITION)
    public void add(IIngredient in, ItemStack out, float chance) {
        if (in instanceof OreDictIngredient oreDictIngredient) {
            ThaumcraftApi.addSmeltingBonus(oreDictIngredient.getOreDict(), out, chance);
            addScripted(new ThaumcraftApi.SmeltBonus(oreDictIngredient.getOreDict(), out, chance));
        } else if (in.getMatchingStacks().length == 1) {
            ThaumcraftApi.addSmeltingBonus(in.getMatchingStacks()[0], out, chance);
            addScripted(new ThaumcraftApi.SmeltBonus(in.getMatchingStacks()[0], out, chance));
        }
    }

    public boolean remove(ThaumcraftApi.SmeltBonus bonus) {
        boolean removed = false;
        Iterator<ThaumcraftApi.SmeltBonus> it = CommonInternals.smeltingBonus.iterator();
        while (it.hasNext()) {
            ThaumcraftApi.SmeltBonus registeredBonus = it.next();
            if (registeredBonus.equals(bonus)) {
                it.remove();
                addBackup(registeredBonus);
                removed = true;
            }
        }
        return removed;
    }

    @MethodDescription(example = @Example("item('minecraft:gold_nugget')"))
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

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ThaumcraftApi.SmeltBonus> streamRecipes() {
        return new SimpleObjectStream<>(CommonInternals.smeltingBonus).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        CommonInternals.smeltingBonus.forEach(this::addBackup);
        CommonInternals.smeltingBonus.clear();
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:cobblestone')).output(item('minecraft:stone_button')).chance(0.2F)"),
            @Example(".input(ore('stone')).output(item('minecraft:obsidian'))")
    })
    public SmeltingBonusBuilder recipeBuilder() {
        return new SmeltingBonusBuilder();
    }

    public static class SmeltingBonusBuilder {

        @Property(comp = @Comp(not = "null"))
        private IIngredient in;
        @Property(comp = @Comp(not = "null"))
        private ItemStack out;
        @Property(defaultValue = "0.33F")
        private float chance = 0.33F;

        @RecipeBuilderMethodDescription(field = "in")
        public SmeltingBonusBuilder input(IIngredient input) {
            this.in = input;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "out")
        public SmeltingBonusBuilder output(ItemStack output) {
            this.out = output;
            return this;
        }

        @RecipeBuilderMethodDescription
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

        @RecipeBuilderRegistrationMethod
        public void register() {
            if (validate()) {
                ModSupport.THAUMCRAFT.get().smeltingBonus.add(in, out, chance);
            }
        }
    }
}
