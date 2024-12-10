package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.infusion.recipes.BasicInfusionRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class InfusionAltar extends VirtualizedRegistry<BasicInfusionRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay')).consumption(1f).chalice(false).consumeMultiple(true).time(10)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay'))")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(r -> InfusionRecipeRegistry.recipes.removeIf(rec -> rec.getUniqueRecipeId() == r.getUniqueRecipeId()));
        restoreFromBackup().forEach(InfusionRecipeRegistry::registerInfusionRecipe);
    }

    @Override
    public void afterScriptLoad() {
        InfusionRecipeRegistry.compileRecipes();
    }

    private void add(BasicInfusionRecipe recipe) {
        addScripted(recipe);
        InfusionRecipeRegistry.registerInfusionRecipe(recipe);
    }

    public BasicInfusionRecipe add(ItemStack output, IIngredient input, float consumption) {
        return add(output, AstralSorcery.toItemHandle(input), consumption);
    }

    public BasicInfusionRecipe add(ItemStack output, ItemHandle input, float consumption) {
        BasicInfusionRecipe recipe = new BasicInfusionRecipe(output, input);
        recipe.setLiquidStarlightConsumptionChance(consumption);
        InfusionRecipeRegistry.registerInfusionRecipe(recipe);

        addScripted(recipe);

        return recipe;
    }

    private boolean remove(AbstractInfusionRecipe recipe) {
        if (recipe instanceof BasicInfusionRecipe basicInfusionRecipe) addBackup(basicInfusionRecipe);
        return InfusionRecipeRegistry.recipes.removeIf(rec -> rec.getUniqueRecipeId() == recipe.getUniqueRecipeId());
    }

    @MethodDescription(example = @Example("item('minecraft:diamond_ore')"))
    public void removeByInput(ItemStack input) {
        InfusionRecipeRegistry.recipes.removeIf(r -> {
            if (r instanceof BasicInfusionRecipe basicInfusionRecipe && r.getInput().matchCrafting(input)) {
                addBackup(basicInfusionRecipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:iron_ingot')"))
    public void removeByOutput(ItemStack output) {
        addBackup((BasicInfusionRecipe) InfusionRecipeRegistry.removeFindRecipeByOutput(output));
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<AbstractInfusionRecipe> streamRecipes() {
        return new SimpleObjectStream<>(InfusionRecipeRegistry.recipes)
                .setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        InfusionRecipeRegistry.recipes.removeIf(r -> {
            if (r instanceof BasicInfusionRecipe basicInfusionRecipe) {
                addBackup(basicInfusionRecipe);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<BasicInfusionRecipe> {

        @Property(comp = @Comp(gte = 0, lte = 1))
        private float consumption = 0.05F;
        @Property
        private boolean chalice = true;
        @Property
        private boolean consumeMultiple;
        @Property(comp = @Comp(gt = 0))
        private int time = 200;


        @RecipeBuilderMethodDescription
        public RecipeBuilder consumption(float chance) {
            this.consumption = chance;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder chalice(boolean chalice) {
            this.chalice = chalice;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder chalice() {
            this.chalice = !chalice;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder consumeMultiple(boolean consumeMultiple) {
            this.consumeMultiple = consumeMultiple;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder consumeMultiple() {
            this.consumeMultiple = !consumeMultiple;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        protected int getMaxItemInput() {
            // More than 1 item cannot be placed
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Astral Infusion recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(consumption < 0 || consumption > 1, "consumption must be a float between 0 and 1, yet it was {}", consumption);
            msg.add(time <= 0, "time must be an integer greater than 0, yet it was {}", time);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable BasicInfusionRecipe register() {
            if (!validate()) return null;
            BasicInfusionRecipe recipe = new BasicInfusionRecipe(output.get(0), AstralSorcery.toItemHandle(input.get(0))) {

                @Override
                public int craftingTickTime() {
                    return time;
                }
            };
            recipe.setLiquidStarlightConsumptionChance(consumption);
            recipe.setCanBeSupportedByChalices(chalice);
            if (consumeMultiple) recipe.setConsumeMultiple();
            recipe.craftingTickTime();
            ModSupport.ASTRAL_SORCERY.get().infusionAltar.add(recipe);
            return recipe;
        }
    }
}
