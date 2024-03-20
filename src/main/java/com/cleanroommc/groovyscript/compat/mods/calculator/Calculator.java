package com.cleanroommc.groovyscript.compat.mods.calculator;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientList;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import net.minecraft.item.ItemStack;
import sonar.core.recipes.ISonarRecipeObject;
import sonar.core.recipes.RecipeInterchangable;
import sonar.core.recipes.RecipeItemStack;
import sonar.core.recipes.RecipeOreStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Calculator extends ModPropertyContainer {

    public final AlgorithmSeparator algorithmSeparator = new AlgorithmSeparator();
    public final AnalysingChamber analysingChamber = new AnalysingChamber();
    public final AtomicCalculator atomicCalculator = new AtomicCalculator();
    public final BasicCalculator basicCalculator = new BasicCalculator();
    public final ConductorMast conductorMast = new ConductorMast();
    public final ExtractionChamber extractionChamber = new ExtractionChamber();
    public final FabricationChamber fabricationChamber = new FabricationChamber();
    public final FlawlessCalculator flawlessCalculator = new FlawlessCalculator();
    public final GlowstoneExtractor glowstoneExtractor = new GlowstoneExtractor();
    public final HealthProcessor healthProcessor = new HealthProcessor();
    public final PrecisionChamber precisionChamber = new PrecisionChamber();
    public final ProcessingChamber processingChamber = new ProcessingChamber();
    public final ReassemblyChamber reassemblyChamber = new ReassemblyChamber();
    public final RedstoneExtractor redstoneExtractor = new RedstoneExtractor();
    public final RestorationChamber restorationChamber = new RestorationChamber();
    public final ScientificCalculator scientificCalculator = new ScientificCalculator();
    public final StarchExtractor starchExtractor = new StarchExtractor();
    public final StoneSeparator stoneSeparator = new StoneSeparator();

    public Calculator() {
        addRegistry(algorithmSeparator);
        addRegistry(analysingChamber);
        addRegistry(atomicCalculator);
        addRegistry(basicCalculator);
        addRegistry(conductorMast);
        addRegistry(extractionChamber);
        addRegistry(fabricationChamber);
        addRegistry(flawlessCalculator);
        addRegistry(glowstoneExtractor);
        addRegistry(healthProcessor);
        addRegistry(precisionChamber);
        addRegistry(processingChamber);
        addRegistry(reassemblyChamber);
        addRegistry(redstoneExtractor);
        addRegistry(restorationChamber);
        addRegistry(scientificCalculator);
        addRegistry(starchExtractor);
        addRegistry(stoneSeparator);
    }


    public static List<ISonarRecipeObject> toSonarRecipeObjectList(IngredientList<IIngredient> list) {
        List<ISonarRecipeObject> output = new ArrayList<>();
        for (IIngredient ingredient : list) {
            if (ingredient instanceof OreDictIngredient) {
                output.add(new RecipeOreStack(((OreDictIngredient) ingredient).getOreDict(), ingredient.getAmount()));
            } else if (IngredientHelper.isItem(ingredient)) {
                output.add(new RecipeItemStack(IngredientHelper.toItemStack(ingredient), true));
            } else {
                List<ItemStack> stacks = Arrays.asList(ingredient.getMatchingStacks());
                if (stacks.isEmpty()) return null;

                List<ISonarRecipeObject> buildList = new ArrayList<>();
                for (ItemStack listObj : stacks) {
                    if (listObj != null) {
                        ISonarRecipeObject recipeObject = new RecipeItemStack(listObj, true);
                        buildList.add(recipeObject);
                    }
                }
                output.add(new RecipeInterchangable(buildList));
            }
        }
        return output;
    }

}
