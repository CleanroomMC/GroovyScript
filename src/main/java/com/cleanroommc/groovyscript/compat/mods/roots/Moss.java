package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.roots.MossConfigAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.config.MossConfig;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RegistryDescription
public class Moss extends VirtualizedRegistry<Pair<ItemStack, ItemStack>> {

    public Moss() {
        super();
        MossConfig.getMossyCobblestones(); // Initialize backing map first, this way we can respect its config
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:gold_block')).output(item('minecraft:clay'))"))
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> MossConfigAccessor.getMossyCobblestones().remove(pair.getKey(), pair.getValue()));
        restoreFromBackup().forEach(pair -> MossConfigAccessor.getMossyCobblestones().put(pair.getKey(), pair.getValue()));
        Moss.reload();
    }

    public static void reload() {
        MossConfigAccessor.getMossyBlocks().clear();
        MossConfigAccessor.getMossyStates().clear();

        for (Map.Entry<ItemStack, ItemStack> entry : MossConfigAccessor.getMossyCobblestones().entrySet()) {
            ItemStack in = entry.getKey();
            ItemStack out = entry.getValue();

            if (!(in.getItem() instanceof ItemBlock itemBlockIn) || !(out.getItem() instanceof ItemBlock itemBlockOut)) continue;

            Block blockIn = itemBlockIn.getBlock();
            Block blockOut = itemBlockOut.getBlock();

            if (in.getMetadata() == 0 && out.getMetadata() == 0) {
                MossConfigAccessor.getMossyBlocks().put(blockIn, blockOut);
            } else {
                MossConfigAccessor.getMossyStates().put(blockIn.getStateFromMeta(in.getMetadata()), blockOut.getStateFromMeta(out.getMetadata()));
            }
        }
    }

    @MethodDescription(example = @Example("item('minecraft:stained_glass:3'), item('minecraft:stained_glass:4')"), type = MethodDescription.Type.ADDITION)
    public void add(ItemStack in, ItemStack out) {
        MossConfigAccessor.getMossyCobblestones().put(in, out);
        addScripted(Pair.of(in, out));
        Moss.reload();
    }

    @MethodDescription(description = "groovyscript.wiki.roots.moss.remove0")
    public boolean remove(ItemStack in, ItemStack out) {
        if (MossConfigAccessor.getMossyCobblestones().remove(in, out)) {
            addBackup(Pair.of(in, out));
            Moss.reload();
            return true;
        }
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.roots.moss.remove1", example = @Example("item('minecraft:cobblestone')"))
    public boolean remove(ItemStack in) {
        ItemStack out = MossConfigAccessor.getMossyCobblestones().remove(in);
        if (out != null) {
            addBackup(Pair.of(in, out));
            Moss.reload();
            return true;
        }
        return false;
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        MossConfigAccessor.getMossyCobblestones().forEach((in, out) -> addBackup(Pair.of(in, out)));
        MossConfigAccessor.getMossyCobblestones().clear();
        Moss.reload();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<ItemStack, ItemStack>> streamRecipes() {
        return new SimpleObjectStream<>(MossConfigAccessor.getMossyCobblestones().entrySet()).setRemover(r -> this.remove(r.getKey()));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<Pair<ItemStack, ItemStack>> {

        @Override
        public String getErrorMsg() {
            return "Error adding Roots Moss conversion";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(!(input.get(0).getMatchingStacks()[0].getItem() instanceof ItemBlock), "input must be an instance of ItemBlock");
            msg.add(!(output.get(0).getItem() instanceof ItemBlock), "output must be an instance of ItemBlock");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable Pair<ItemStack, ItemStack> register() {
            if (!validate()) return null;
            ModSupport.ROOTS.get().moss.add(input.get(0).getMatchingStacks()[0], output.get(0));
            Moss.reload();
            return Pair.of(input.get(0).getMatchingStacks()[0], output.get(0));
        }
    }
}
