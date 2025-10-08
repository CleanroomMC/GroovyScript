package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.bewitchment.api.registry.CauldronRecipe;
import com.bewitchment.registry.ModObjects;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription(admonition = {
        @Admonition(value = "groovyscript.wiki.bewitchment.cauldron.note0", type = Admonition.Type.INFO),
        @Admonition(value = "groovyscript.wiki.bewitchment.cauldron.magic_power_note", type = Admonition.Type.INFO)
}, override = @MethodOverride(method = @MethodDescription(method = "remove(Lnet/minecraft/util/ResourceLocation;)V", example = @Example("resource('bewitchment:catechu_brown')"))))
public class Cauldron extends ForgeRegistryWrapper<CauldronRecipe> {

    public Cauldron() {
        super(GameRegistry.findRegistry(CauldronRecipe.class));
    }

    @RecipeBuilderDescription(example = @Example(".input(ore('logWood')).input(item('minecraft:deadbush')).input(item('minecraft:dye', 3)).output(item('bewitchment:catechu_brown'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('bewitchment:tongue_of_dog')"))
    public void removeByInput(IIngredient input) {
        getRegistry().forEach(recipe -> {
            if (recipe.input.stream().map(Ingredient::getMatchingStacks).flatMap(Arrays::stream).anyMatch(input)) {
                remove(recipe);
            }
        });
    }

    @MethodDescription(example = @Example("item('bewitchment:iron_gall_ink')"))
    public void removeByOutput(IIngredient output) {
        getRegistry().forEach(recipe -> {
            if (recipe.output.stream().anyMatch(output)) {
                remove(recipe);
            }
        });
    }

    @Property(property = "name")
    @Property(property = "input", comp = @Comp(gte = 1, lte = 10))
    @Property(property = "output", comp = @Comp(gte = 1, lte = 3))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CauldronRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Bewitchment Cauldron Recipe";
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_cauldron_recipe_";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 10, 1, 3);
            validateFluids(msg);
            validateName();

            var ash = new ItemStack(ModObjects.wood_ash);
            msg.add(input.stream().anyMatch(x -> x.test(ash)), "inputs contained 'bewitchment:wood_ash', which clears the Witches' Cauldron");

            if (!input.isEmpty()) {
                var slot = input.get(0);
                var root = new ItemStack(ModObjects.mandrake_root);
                var sand = new ItemStack(ModObjects.dimensional_sand);
                msg.add(slot.test(root), "the first input stack matched 'bewitchment:mandrake_root', which causes a Brew to be made instead");
                msg.add(slot.test(sand), "the first input stack matched 'bewitchment:dimensional_sand', which causes the Cauldron to teleport the player");
            }
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CauldronRecipe register() {
            if (!validate()) return null;
            List<Ingredient> inputs = input.stream().map(IIngredient::toMcIngredient).collect(Collectors.toList());
            CauldronRecipe recipe = new CauldronRecipe(super.name, inputs, output);
            ModSupport.BEWITCHMENT.get().cauldron.add(recipe);
            return recipe;
        }
    }
}
