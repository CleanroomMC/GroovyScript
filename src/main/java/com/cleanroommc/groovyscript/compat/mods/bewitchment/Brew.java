package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Predicate;

@RegistryDescription(admonition = {
        @Admonition(value = "groovyscript.wiki.bewitchment.brew.note0", type = Admonition.Type.INFO),
        @Admonition(value = "groovyscript.wiki.bewitchment.brew.note1", type = Admonition.Type.INFO),
        @Admonition(value = "groovyscript.wiki.bewitchment.cauldron.magic_power_note", type = Admonition.Type.INFO),
})
public class Brew extends ForgeRegistryWrapper<com.bewitchment.api.registry.Brew> {

    public Brew() {
        super(GameRegistry.findRegistry(com.bewitchment.api.registry.Brew.class));
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(ore('netherStar')).outputCheck(item('minecraft:nether_star')).effect(new PotionEffect(potion('minecraft:strength'), 1800, 3)).output(item('bewitchment:catechu_brown'))"),
            @Example(".input(item('minecraft:gold_ingot')).effect(new PotionEffect(potion('minecraft:instant_health'), 1, 3)).output(item('minecraft:clay'))"),
            @Example(".input(item('minecraft:deadbush')).effect(new PotionEffect(potion('minecraft:resistance'), 1800, 3))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('bewitchment:dragons_blood_resin')"))
    public void removeByInput(IIngredient input) {
        getRegistry().forEach(recipe -> {
            if (Arrays.stream(recipe.input.getMatchingStacks()).anyMatch(input)) {
                remove(recipe);
            }
        });
    }

    @MethodDescription(example = @Example("item('minecraft:bowl')"))
    public void removeByOutput(IIngredient output) {
        getRegistry().forEach(recipe -> {
            if (output.test(recipe.output)) {
                remove(recipe);
            }
        });
    }

    @MethodDescription(example = @Example("potion('minecraft:instant_health')"))
    public void removeByPotion(Potion potion) {
        getRegistry().forEach(recipe -> {
            if (recipe.effect.getPotion().equals(potion)) {
                remove(recipe);
            }
        });
    }

    @Property(property = "name")
    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(gte = 0, lte = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<com.bewitchment.api.registry.Brew> {

        @Property(comp = @Comp(not = "null"))
        private PotionEffect effect;
        @Property
        private Predicate<ItemStack> outputCheck;

        @RecipeBuilderMethodDescription
        public RecipeBuilder effect(PotionEffect effect) {
            this.effect = effect;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder outputCheck(IIngredient outputCheck) {
            // while IIngredient does implement Predicate<ItemStack>, making this method redundant,
            // having it be explicit makes documentation clearer.
            this.outputCheck = outputCheck;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder outputCheck(Predicate<ItemStack> outputCheck) {
            this.outputCheck = outputCheck;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Bewitchment Cauldron Brew Recipe";
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_brew_recipe_";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 1);
            validateFluids(msg);
            validateName();
            msg.add(effect == null, "effect must not be null, but it was null");
            msg.add(outputCheck != null && output.isEmpty(), "outputCheck was defined, yet output did not have an entry");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable com.bewitchment.api.registry.Brew register() {
            if (!validate()) return null;
            com.bewitchment.api.registry.Brew recipe = new com.bewitchment.api.registry.Brew(super.name, input.get(0).toMcIngredient(), outputCheck, output.getOrEmpty(0), effect);
            ModSupport.BEWITCHMENT.get().brew.add(recipe);
            return recipe;
        }
    }
}
