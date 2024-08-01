package com.cleanroommc.groovyscript.compat.mods.thaumcraft.arcane;

import com.buuz135.thaumicjei.category.ArcaneWorkbenchCategory;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.IJEIRemoval;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.JeiRemovalHelper;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.OperationHandler;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import com.cleanroommc.groovyscript.registry.NamedRegistry;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.crafting.IArcaneRecipe;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
@Optional.Interface(modid = "thaumicjei", iface = "com.cleanroommc.groovyscript.compat.mods.jei.removal.IJEIRemoval$Default")
public class ArcaneWorkbench extends NamedRegistry implements IJEIRemoval.Default {

    @Optional.Method(modid = "thaumicjei")
    private static OperationHandler.IOperation registryNameOperation() {
        return new OperationHandler.WrapperOperation<>(com.buuz135.thaumicjei.category.ArcaneWorkbenchCategory.ArcaneWorkbenchWrapper.class, wrapper ->
                wrapper.getRecipe().getRegistryName() == null
                ? Collections.emptyList()
                : Collections.singletonList(JeiRemovalHelper.format("remove", GroovyScriptCodeConverter.asGroovyCode(wrapper.getRecipe().getRegistryName(), true))));
    }

    public static final ResourceLocation DEFAULT = new ResourceLocation("");

    public void add(String name, IRecipe recipe) {
        ReloadableRegistryManager.addRegistryEntry(ForgeRegistries.RECIPES, name, recipe);
    }

    @MethodDescription
    public void remove(String name) {
        ReloadableRegistryManager.removeRegistryEntry(ForgeRegistries.RECIPES, name);
    }

    @MethodDescription(example = @Example("item('thaumcraft:mechanism_simple')"))
    public void removeByOutput(IIngredient output) {
        VanillaModule.crafting.removeByOutput(output, true);
    }

    @RecipeBuilderDescription(example = {
            @Example(".researchKey('UNLOCKALCHEMY@3').output(item('minecraft:pumpkin')).row('SS ').row('   ').row('   ').key('S', item('minecraft:pumpkin_seeds')).aspect('terra').vis(5)"),
            @Example(".researchKey('UNLOCKALCHEMY@3').output(item('minecraft:clay')).matrix('SS ','   ','   ').key('S', item('minecraft:pumpkin')).aspect(aspect('terra')).vis(5)")
    })
    public ArcaneRecipeBuilder.Shaped shapedBuilder() {
        return new ArcaneRecipeBuilder.Shaped();
    }

    @RecipeBuilderDescription(example = @Example(".researchKey('UNLOCKALCHEMY@3').input(item('minecraft:pumpkin')).input(item('minecraft:stick')).input(item('minecraft:stick')).output(item('thaumcraft:void_hoe')).vis(0)"))
    public ArcaneRecipeBuilder.Shapeless shapelessBuilder() {
        return new ArcaneRecipeBuilder.Shapeless();
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        List<IArcaneRecipe> recipes = ForgeRegistries.RECIPES.getValuesCollection().stream()
                .filter(recipe -> recipe instanceof IArcaneRecipe)
                .map(recipe -> (IArcaneRecipe) recipe)
                .collect(Collectors.toList());

        for (IRecipe recipe : recipes) {
            ReloadableRegistryManager.removeRegistryEntry(ForgeRegistries.RECIPES, recipe.getRegistryName());
        }
    }

    /**
     * Note that this is added by the third-party compat mod thaumicjei, not base Thaumcraft.
     */
    @Override
    @Optional.Method(modid = "thaumicjei")
    public @NotNull Collection<String> getCategories() {
        return Collections.singletonList(ArcaneWorkbenchCategory.UUID);
    }

    @Override
    @Optional.Method(modid = "thaumicjei")
    public @NotNull List<OperationHandler.IOperation> getJEIOperations() {
        return ImmutableList.of(registryNameOperation(), OperationHandler.ItemOperation.outputItemOperation(), OperationHandler.FluidOperation.defaultFluidOperation());
    }

}

