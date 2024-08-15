package com.cleanroommc.groovyscript.compat.mods.industrialforegoing;

import com.buuz135.industrial.api.extractor.ExtractorEntry;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

@RegistryDescription
public class Extractor extends VirtualizedRegistry<ExtractorEntry> {

    public Extractor() {
        super(Alias.generateOfClass(Extractor.class).andGenerate("TreeFluidExtractor"));
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        ExtractorEntry.EXTRACTOR_ENTRIES.removeAll(removeScripted());
        ExtractorEntry.EXTRACTOR_ENTRIES.addAll(restoreFromBackup());
    }

    @MethodDescription(description = "groovyscript.wiki.industrialforegoing.extractor.add0", type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:clay'), fluid('lava') * 50"))
    public ExtractorEntry add(ItemStack input, FluidStack output) {
        return add(input, output, 0.005F);
    }

    @MethodDescription(description = "groovyscript.wiki.industrialforegoing.extractor.add1", type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:stone'), fluid('water') * 100, 1"))
    public ExtractorEntry add(ItemStack input, FluidStack output, float breakChance) {
        if (IngredientHelper.overMaxSize(input, 1)) {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding Fluid Extractor recipe").error();
            msg.add("Stack size of input must be 1");
            msg.post();
            return null;
        }
        ExtractorEntry recipe = new ExtractorEntry(input, output, breakChance);
        add(recipe);
        return recipe;
    }

    public void add(ExtractorEntry recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        ExtractorEntry.EXTRACTOR_ENTRIES.add(recipe);
    }

    public boolean remove(ExtractorEntry recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ExtractorEntry.EXTRACTOR_ENTRIES.remove(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:log2:1')"))
    public boolean removeByInput(IIngredient input) {
        return ExtractorEntry.EXTRACTOR_ENTRIES.removeIf(recipe -> {
            if (input.test(recipe.getItemStack())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "fluid('latex')", commented = true))
    public boolean removeByOutput(IIngredient output) {
        return ExtractorEntry.EXTRACTOR_ENTRIES.removeIf(recipe -> {
            if (output.test(recipe.getFluidStack())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ExtractorEntry.EXTRACTOR_ENTRIES.forEach(this::addBackup);
        ExtractorEntry.EXTRACTOR_ENTRIES.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ExtractorEntry> streamRecipes() {
        return new SimpleObjectStream<>(ExtractorEntry.EXTRACTOR_ENTRIES)
                .setRemover(this::remove);
    }

}
