package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RegistryDescription(admonition = @Admonition("groovyscript.wiki.bewitchment.ritual.note0"))
public class Ritual extends ForgeRegistryWrapper<com.bewitchment.api.registry.Ritual> {

    public Ritual() {
        super(GameRegistry.findRegistry(com.bewitchment.api.registry.Ritual.class));
    }

    @MethodDescription(example = @Example("item('minecraft:poisonous_potato')"))
    public void removeByInput(IIngredient input) {
        getRegistry().forEach(recipe -> {
            if (recipe.input.stream().map(Ingredient::getMatchingStacks).flatMap(Arrays::stream).anyMatch(input)) {
                remove(recipe);
            }
        });
    }

    @MethodDescription(example = @Example("item('bewitchment:purifying_earth')"))
    public void removeByOutput(IIngredient output) {
        getRegistry().forEach(recipe -> {
            if (recipe.output != null && recipe.output.stream().anyMatch(output)) {
                remove(recipe);
            }
        });
    }

    @RecipeBuilderDescription(example = {
//            @Example(".input(ore('logWood')).input(item('minecraft:deadbush')).input(item('minecraft:dye', 3)).output(item('bewitchment:catechu_brown'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }
    //WitchesRitual.NONE;
    //WitchesRitual.GOLDEN;
    //WitchesRitual.RITUAL;
    //WitchesRitual.FIERY;
    //WitchesRitual.PHASING;
    //WitchesRitual.ANY;

    public enum ChalkType {
        NONE, // No chalk
        GOLDEN, // Golden chalk
        RITUAL, // Ritual Chalk (The standard white chalk)
        FIERY, // Fiery/Infernal chalk
        PHASING, // Phasing/Otherwhere chalk
        ANY, // No matter what chalk
    }

    @Property(property = "name")
    @Property(property = "input", comp = @Comp(gte = 1, lte = 10))
    @Property(property = "output", comp = @Comp(gte = 0, lte = 5))
    public static class RecipeBuilder extends AbstractRecipeBuilder<com.bewitchment.api.registry.Ritual> {

        @Property
        private Predicate<EntityLivingBase> sacrifice;
        @Property(defaultValue = "true")
        private boolean canBePerformedRemotely = true;
        @Property(comp = @Comp(gt = 0))
        private int time;
        @Property(comp = @Comp(gt = 0))
        private int startingPower;
        @Property(comp = @Comp(gt = 0))
        private int runningPower;
        @Property(comp = @Comp(gt = 0))
        private int small;
        @Property
        private int medium;
        @Property
        private int big;

        @RecipeBuilderMethodDescription
        public RecipeBuilder sacrifice(EntityEntry sacrifice) {
            this.sacrifice = x -> sacrifice.getEntityClass().equals(x.getClass());
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder sacrifice(Predicate<EntityLivingBase> sacrifice) {
            this.sacrifice = sacrifice;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder canBePerformedRemotely(boolean canBePerformedRemotely) {
            this.canBePerformedRemotely = canBePerformedRemotely;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder startingPower(int startingPower) {
            this.startingPower = startingPower;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder runningPower(int runningPower) {
            this.runningPower = runningPower;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder small(int small) {
            this.small = small;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder medium(int medium) {
            this.medium = medium;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder big(int big) {
            this.big = big;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Bewitchment Ritual Recipe";
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_ritual_";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 10, 0, 5);
            validateFluids(msg);
            validateName();
            msg.add(time <= 0, "time must be greater than 0, yet it was {}", time);
            msg.add(startingPower <= 0, "startingPower must be greater than 0, yet it was {}", startingPower);
            msg.add(runningPower <= 0, "runningPower must be greater than 0, yet it was {}", runningPower);
            msg.add(small == 0, "small must not be equal to 0 (representing the golden circle), yet it was");
            msg.add(medium == 0, "medium must not be equal to 0 (representing the golden circle), yet it was");
            msg.add(big == 0, "big must not be equal to 0 (representing the golden circle), yet it was");
            msg.add(small <= 0, "small must be greater than 0, yet it was {}", small);
            msg.add(medium < 0 && big > 0, "medium cannot be less than 0 while big is greater than 0, yet medium was {} and big was {}", medium, big);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable com.bewitchment.api.registry.Ritual register() {
            if (!validate()) return null;
            var inputs = input.stream().map(IIngredient::toMcIngredient).collect(Collectors.toList());
            var outputs = output.isEmpty() ? null : output;
            var recipe = new com.bewitchment.api.registry.Ritual(super.name, inputs, sacrifice, outputs, canBePerformedRemotely, time, startingPower, runningPower, small, medium, big);
            ModSupport.BEWITCHMENT.get().ritual.add(recipe);
            return recipe;
        }
    }
}
