package com.cleanroommc.groovyscript.compat.mods.betterwithaddons;

import betterwithaddons.handler.RotHandler;
import betterwithaddons.handler.RotHandler.RotInfo;
import betterwithaddons.interaction.InteractionBWA;
import betterwithaddons.item.ModItems;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.betterwithaddons.RotHandlerAccessor;
import com.cleanroommc.groovyscript.core.mixin.betterwithaddons.RotInfoAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.google.common.collect.Multimap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES, admonition = @Admonition(value = "groovyscript.wiki.betterwithaddons.rotting.note0", type = Admonition.Type.WARNING))
public class Rotting extends VirtualizedRegistry<Map.Entry<Item, RotInfo>> {

    private static Item toKey(RotInfo recipe) {
        return ((RotInfoAccessor) recipe).getItemStack().getItem();
    }

    private static Map.Entry<Item, RotInfo> toEntry(RotInfo recipe) {
        return Pair.of(toKey(recipe), recipe);
    }

    @Override
    public boolean isEnabled() {
        return InteractionBWA.ROTTEN_FOOD;
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:gold_ingot'))"),
            @Example(".input(item('placeholdername:snack')).time(100).key('groovy_example').rotted(item('minecraft:clay') * 4)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> getEntries().entries().removeIf(r -> r.getKey().equals(recipe.getKey()) && r.getValue().equals(recipe.getValue())));
        restoreFromBackup().forEach(r -> getEntries().put(r.getKey(), r.getValue()));
    }

    public Multimap<Item, RotInfo> getEntries() {
        return RotHandlerAccessor.getRottingItems();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, priority = 500)
    public boolean add(RotInfo recipe) {
        return recipe != null && getEntries().put(toKey(recipe), recipe) && doAddScripted(toEntry(recipe));
    }

    @MethodDescription(priority = 500)
    public boolean add(Map.Entry<Item, RotInfo> recipe) {
        return recipe != null && getEntries().put(recipe.getKey(), recipe.getValue()) && doAddScripted(recipe);
    }

    @MethodDescription(priority = 500)
    public boolean remove(Map.Entry<Item, RotInfo> recipe) {
        return recipe != null && getEntries().entries().removeIf(r -> r == recipe) && doAddBackup(recipe);
    }

    @MethodDescription(priority = 500)
    public boolean remove(RotInfo recipe) {
        return recipe != null && getEntries().entries().removeIf(r -> r.getValue() == recipe) && doAddBackup(toEntry(recipe));
    }

    @MethodDescription(example = @Example("item('betterwithaddons:food_cooked_rice')"))
    public boolean removeByInput(IIngredient input) {
        return getEntries().entries().removeIf(r -> input.test(((RotInfoAccessor) r.getValue()).getItemStack()) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('minecraft:rotten_flesh')"))
    public boolean removeByOutput(IIngredient output) {
        return getEntries().entries().removeIf(r -> output.test(((RotInfoAccessor) r.getValue()).getRottedStack()) && doAddBackup(r));
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        var recipes = getEntries();
        recipes.entries().forEach(this::addBackup);
        recipes.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<Item, RotInfo>> streamRecipes() {
        return new SimpleObjectStream<>(getEntries().entries()).setRemover(this::remove);
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RotHandler.RotInfo> {

        @Property(defaultValue = "InteractionBWA.MISC_ROT_TIME", comp = @Comp(gte = 1))
        private long time = InteractionBWA.MISC_ROT_TIME;
        @Property(defaultValue = "food", comp = @Comp(not = "null"))
        private String key = "food";
        @Property(defaultValue = "new ItemStack(ModItems.ROTTEN_FOOD)", comp = @Comp(not = "empty"))
        private ItemStack rotted = new ItemStack(ModItems.ROTTEN_FOOD);

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(long time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder key(String key) {
            this.key = key;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder rotted(ItemStack rotted) {
            this.rotted = rotted;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Better With Addons Rotting recipe";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg);
            msg.add(time < 0, "time must be greater than or equal to 0, yet it was {}", time);
            msg.add(rotted == null || rotted.isEmpty(), "rotted cannot be null or empty, yet it was");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RotHandler.RotInfo register() {
            if (!validate()) return null;
            RotHandler.RotInfo recipe = null;
            for (var stack : input.get(0).getMatchingStacks()) {
                recipe = new RotHandler.RotInfo(stack, time, key, rotted);
                ModSupport.BETTER_WITH_ADDONS.get().rotting.add(recipe);
            }
            return recipe;
        }
    }
}
