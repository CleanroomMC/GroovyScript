package com.cleanroommc.groovyscript.compat.mods.enderio;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.enderio.recipe.EnderIORecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.enderio.recipe.RecipeInput;
import com.cleanroommc.groovyscript.compat.mods.enderio.recipe.SagRecipe;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import crazypants.enderio.base.recipe.Recipe;
import crazypants.enderio.base.recipe.RecipeBonusType;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.RecipeOutput;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription(admonition = @Admonition(value = "groovyscript.wiki.enderio.sag_mill.note", type = Admonition.Type.WARNING))
public class SagMill extends StandardListRegistry<Recipe> {

    public SagMill() {
        super(Alias.generateOfClassAnd(SagMill.class, "Sag").and("SAGMill"));
    }

    @Override
    public Collection<Recipe> getRecipes() {
        return SagMillRecipeManager.getInstance().getRecipes();
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond_block')).output(item('minecraft:diamond') * 4).output(item('minecraft:clay_ball') * 2, 0.7).output(item('minecraft:gold_ingot'), 0.1).output(item('minecraft:gold_ingot'), 0.1).bonusTypeMultiply().energy(1000).tierEnhanced()"),
            @Example(".input(item('minecraft:clay_ball')).output(item('minecraft:diamond') * 4).output(item('minecraft:gold_ingot'), 0.1).bonusTypeChance().tierNormal()"),
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:gold_ingot'), 0.1).bonusTypeNone().tierSimple()"),
            @Example(".input(item('minecraft:nether_star')).output(item('minecraft:clay_ball') * 2, 0.7).output(item('minecraft:gold_ingot'), 0.1).tierAny()")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('minecraft:wheat')"))
    public void removeByInput(ItemStack input) {
        Recipe recipe = (Recipe) SagMillRecipeManager.getInstance().getRecipeForInput(RecipeLevel.IGNORE, input);
        if (recipe == null) {
            GroovyLog.get().error("Can't find EnderIO Sag Mill recipe for input " + input);
        } else {
            getRecipes().remove(recipe);
            addBackup(recipe);
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(gte = 1, lte = 4))
    @Property(property = "energy", comp = @Comp(gt = 0))
    @Property(property = "level")
    public static class RecipeBuilder extends EnderIORecipeBuilder<Recipe> {

        @Property
        private final FloatList chances = new FloatArrayList();
        @Property(defaultValue = "RecipeBonusType.NONE")
        private RecipeBonusType bonusType = RecipeBonusType.NONE;

        @RecipeBuilderMethodDescription(field = {
                "output", "chances"
        })
        public RecipeBuilder output(ItemStack itemStack, float chance) {
            this.output.add(itemStack);
            this.chances.add(Math.max(0, chance));
            return this;
        }

        @Override
        @RecipeBuilderMethodDescription(field = {
                "output", "chances"
        })
        public AbstractRecipeBuilder<Recipe> output(ItemStack output) {
            return output(output, 1.0f);
        }

        @RecipeBuilderMethodDescription(field = "bonusType")
        public RecipeBuilder bonusTypeNone() {
            this.bonusType = RecipeBonusType.NONE;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "bonusType")
        public RecipeBuilder bonusTypeMultiply() {
            this.bonusType = RecipeBonusType.MULTIPLY_OUTPUT;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "bonusType")
        public RecipeBuilder bonusTypeChance() {
            this.bonusType = RecipeBonusType.CHANCE_ONLY;
            return this;
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding EnderIO Sag Mill recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 4);
            validateFluids(msg);
            if (energy <= 0) energy = 5000;
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable Recipe register() {
            if (!validate()) return null;
            RecipeOutput[] outputs = new RecipeOutput[output.size()];
            for (int i = 0; i < outputs.length; i++) {
                outputs[i] = new RecipeOutput(output.get(i), chances.get(i));
            }
            Recipe recipe = new SagRecipe(new RecipeInput(input.get(0)), energy, bonusType, level, outputs);
            ModSupport.ENDER_IO.get().sagMill.add(recipe);
            return recipe;
        }
    }
}
