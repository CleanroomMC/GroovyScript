package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeRuneAltar;
import vazkii.botania.common.block.ModBlocks;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class RuneAltar extends StandardListRegistry<RecipeRuneAltar> {

    @RecipeBuilderDescription(example = @Example(".input(ore('gemEmerald'), item('minecraft:apple')).output(item('minecraft:diamond')).mana(500)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<RecipeRuneAltar> getRecipes() {
        return BotaniaAPI.runeAltarRecipes;
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public RecipeRuneAltar add(ItemStack output, int mana, IIngredient... inputs) {
        return recipeBuilder().mana(mana).output(output).input(inputs).register();
    }

    @MethodDescription(example = @Example("item('botania:rune:1')"))
    public boolean removeByOutput(IIngredient output) {
        if (getRecipes().removeIf(recipe -> {
            boolean found = output.test(recipe.getOutput());
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Rune Altar recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    @MethodDescription(example = @Example("ore('runeEarthB')"))
    public boolean removeByInput(IIngredient... inputs) {
        List<Object> converted = Arrays.stream(inputs)
                .map(i -> i instanceof OreDictIngredient oreDictIngredient ? oreDictIngredient.getOreDict() : i.getMatchingStacks()[0])
                .collect(Collectors.toList());
        if (getRecipes().removeIf(recipe -> {
            boolean found = converted.stream()
                    .allMatch(
                            o -> recipe.getInputs()
                                    .stream()
                                    .anyMatch(i -> (i instanceof String || o instanceof String) ? i.equals(o) : ItemStack.areItemStacksEqual((ItemStack) i, (ItemStack) o)));
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Rune Altar recipe")
                .add("could not find recipe with inputs {}", converted)
                .error()
                .post();
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("ore('feather'), ore('string')"))
    public boolean removeByInputs(IIngredient... inputs) {
        return removeByInput(inputs);
    }

    @Property(property = "input", comp = @Comp(eq = 1, unique = "groovyscript.wiki.botania.rune_altar.input.required"))
    @Property(property = "output", comp = @Comp(gte = 1, lte = 2))
    public class RecipeBuilder extends AbstractRecipeBuilder<RecipeRuneAltar> {

        @Property(comp = @Comp(gte = 1))
        protected int mana;

        @RecipeBuilderMethodDescription
        public RecipeBuilder mana(int amount) {
            this.mana = amount;
            return this;
        }

        @Override
        protected int getMaxItemInput() {
            // Each slot of Apothecary can only contain 1 item
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Botania Rune Altar recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateFluids(msg);
            validateItems(msg, 1, 20, 1, 1);
            msg.add(
                    input.stream().anyMatch(x -> x.test(new ItemStack(Item.getItemFromBlock(ModBlocks.livingrock), 1, 0))),
                    "input cannot contain a livingrock item");
            msg.add(mana < 1, "mana must be at least 1, got " + mana);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipeRuneAltar register() {
            if (!validate()) return null;
            RecipeRuneAltar recipe = new RecipeRuneAltar(
                    output.get(0),
                    mana,
                    input.stream()
                            .map(
                                    i -> i instanceof OreDictIngredient oreDictIngredient
                                            ? oreDictIngredient.getOreDict()
                                            : i.getMatchingStacks()[0])
                            .toArray());
            add(recipe);
            return recipe;
        }
    }
}
