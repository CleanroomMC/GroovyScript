package com.cleanroommc.groovyscript.compat.mods.industrialforegoing;

import com.buuz135.industrial.api.extractor.ExtractorEntry;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;

@RegistryDescription
public class Extractor extends StandardListRegistry<ExtractorEntry> {

    public Extractor() {
        super(Alias.generateOfClass(Extractor.class).andGenerate("TreeFluidExtractor"));
    }

    @Override
    public Collection<ExtractorEntry> getRecipes() {
        return ExtractorEntry.EXTRACTOR_ENTRIES;
    }

    @MethodDescription(description = "groovyscript.wiki.industrialforegoing.extractor.add0", type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:clay'), fluid('lava') * 50"))
    public ExtractorEntry add(ItemStack input, FluidStack output) {
        return add(input, output, 0.005F);
    }

    @MethodDescription(description = "groovyscript.wiki.industrialforegoing.extractor.add1", type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:stone'), fluid('water') * 100, 1"))
    public ExtractorEntry add(ItemStack input, FluidStack output, float breakChance) {
        ExtractorEntry recipe = new ExtractorEntry(input, output, breakChance);
        add(recipe);
        return recipe;
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

}
