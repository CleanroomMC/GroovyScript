package com.cleanroommc.groovyscript.compat.mods.futuremc;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import thedarkcolour.futuremc.block.villagepillage.ComposterBlock;

import java.util.Arrays;
import java.util.Map;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class Composter extends VirtualizedRegistry<Map.Entry<Ingredient, Byte>> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        var instance = ComposterBlock.ItemsForComposter.INSTANCE;
        var entries = instance.getEntries();
        removeScripted().forEach(entry -> entries.removeIf(entry.getKey()::equals));
        restoreFromBackup().forEach(x -> instance.add(x.getKey(), x.getValue()));
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).chance(100)"),
            @Example(".input(item('minecraft:gold_ingot')).chance(30)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public boolean add(IIngredient input, byte chance) {
        if (input == null) return false;
        return add(input.toMcIngredient(), chance);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public boolean add(Ingredient input, byte chance) {
        if (input == null) return false;
        ComposterBlock.ItemsForComposter.INSTANCE.add(input, chance);
        return doAddScripted(Pair.of(input, chance));
    }

    @MethodDescription
    public boolean remove(IIngredient input) {
        if (input == null) return false;
        return remove(input.toMcIngredient());
    }

    @MethodDescription
    public boolean remove(Ingredient input) {
        return input != null && ComposterBlock.ItemsForComposter.INSTANCE.getEntries().removeIf(r -> r == input && doAddBackup(Pair.of(input, r.getByteValue())));
    }

    @MethodDescription(example = @Example("item('minecraft:cactus')"))
    public void removeByInput(IIngredient input) {
        ComposterBlock.ItemsForComposter.INSTANCE.getEntries().removeIf(r -> Arrays.stream(r.getKey().getMatchingStacks()).anyMatch(input) && doAddBackup(r));
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        var recipes = ComposterBlock.ItemsForComposter.INSTANCE.getEntries();
        recipes.forEach(this::addBackup);
        recipes.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Object2ByteMap.Entry<Ingredient>> streamRecipes() {
        return new SimpleObjectStream<>(ComposterBlock.ItemsForComposter.INSTANCE.getEntries()).setRemover(x -> remove(x.getKey()));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<Pair<Ingredient, Byte>> {

        private static final ItemStack BONE_MEAL = new ItemStack(Items.DYE, 1, 15);

        @Property(comp = @Comp(gte = 0, lte = 100))
        private int chance;

        @RecipeBuilderMethodDescription
        public RecipeBuilder chance(int chance) {
            this.chance = chance;
            return this;
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding FutureMC Composter recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg);
            if (!input.isEmpty()) msg.add(input.get(0).test(BONE_MEAL), "the input item cannot match bonemeal, yet it was {}", input.get(0));
            msg.add(chance < 0 || chance > 100, "chance must be greater than or equal to 0 and less than or equal to 100, yet it was {}", chance);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable Pair<Ingredient, Byte> register() {
            if (!validate()) return null;
            // can be safely cast to a byte due to always being between 0-100
            ComposterBlock.ItemsForComposter.INSTANCE.add(input.get(0).toMcIngredient(), (byte) chance);
            var entry = Pair.of(input.get(0).toMcIngredient(), (byte) chance);
            ModSupport.FUTURE_MC.get().composter.addBackup(entry);
            return entry;
        }
    }
}
