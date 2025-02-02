package com.cleanroommc.groovyscript.compat.mods.multiblocked;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.multiblocked.api.capability.MultiblockCapability;
import com.cleanroommc.multiblocked.api.recipe.Content;
import com.cleanroommc.multiblocked.api.recipe.Recipe;
import com.cleanroommc.multiblocked.api.recipe.RecipeCondition;
import com.cleanroommc.multiblocked.api.recipe.RecipeMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@RegistryDescription
public class RecipeMapManager extends VirtualizedRegistry<Recipe> {
    private final RecipeMap recipeMap;

    public RecipeMapManager(RecipeMap map) {
        super();
        this.recipeMap = map;
    }

    @Override
    public void onReload() {
        for (Recipe r : removeScripted()) this.recipeMap.recipes.remove(r.uid);
        for (Recipe r : restoreFromBackup()) this.recipeMap.addRecipe(r);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.add_to_list", priority = 500)
    public boolean add(Recipe recipe) {
        if (recipe == null) return false;
        this.recipeMap.addRecipe(recipe);
        return doAddScripted(recipe);
    }

    @MethodDescription(description = "groovyscript.wiki.remove_from_list", priority = 500)
    public boolean remove(Recipe recipe) {
        return recipe != null && this.recipeMap.recipes.remove(recipe.uid, recipe) && doAddBackup(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Recipe> streamRecipes() {
        return new SimpleObjectStream<>(recipeMap.recipes.values()).setRemover(this::remove);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:iron_ingot')).output(item('minecraft:clay')).time(100)"),
            @Example(".input(item('minecraft:gold_ingot')).input(mbd.fluid(fluid('lava') * 5).perTick().chance(0.5)).output(mbd.item(item('minecraft:diamond')).chance(0.5)).time(200)"),
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder(recipeMap);
    }

    public static class RecipeBuilder implements IRecipeBuilder<Recipe> {
        private final RecipeMap recipeMap;

        private String uid = "";

        @Property
        private int time = 0;

        @Property
        private final List<MbdCapabilityData<?>> inputs = new ArrayList<>();

        @Property
        private final List<MbdCapabilityData<?>> outputs = new ArrayList<>();

        @Property
        private final List<RecipeCondition> conditions = new ArrayList<>();

        private RecipeBuilder(RecipeMap map) {
            this.recipeMap = map;
        }

        public RecipeBuilder uid(String uid) {
            this.uid = uid;
            return this;
        }

        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        public RecipeBuilder input(@Nullable MbdCapabilityData<?> input) {
            if (input != null) this.inputs.add(input);
            return this;
        }

        public RecipeBuilder output(@Nullable MbdCapabilityData<?> output) {
            if (output != null) this.outputs.add(output);
            return this;
        }

        private MbdCapabilityData<?> toCapability(IIngredient ingredient) {
            if (ingredient instanceof FluidStack f)
                return ModSupport.MULTIBLOCKED.get().mappers.fluid(f);
            return ModSupport.MULTIBLOCKED.get().mappers.item(ingredient);
        }

        public RecipeBuilder input(IIngredient input) {
            return input(toCapability(input));
        }

        public RecipeBuilder output(IIngredient output) {
            return output(toCapability(output));
        }

        public RecipeBuilder condition(RecipeCondition condition) {
            this.conditions.add(condition);
            return this;
        }

        public String getErrorMsg() {
            return "Error registering a Multiblocked recipe in Recipe Map " + recipeMap.name;
        }

        public void validate(GroovyLog.Msg msg) {
            msg.add(time <= 0, "Time must be > 0");
            msg.add(inputs.isEmpty(), "Must have at least one input");
            msg.add(outputs.isEmpty(), "Must have at least one output");

            if (uid.isEmpty()) {
                uid = UUID.randomUUID().toString();
            } else {
                msg.add(recipeMap.recipes.containsKey(uid), "Recipe with ID {} already exists", uid);
            }
        }

        private ImmutableMap<MultiblockCapability<?>, ImmutableList<Content>> toMbdMap(List<MbdCapabilityData<?>> d, boolean perTick) {
            Map<MultiblockCapability<?>, List<Content>> map = new HashMap<>();
            for (MbdCapabilityData<?> data : d) {
                if (data.isPerTick() != perTick) continue;
                MultiblockCapability<?> cap = data.getCapability();
                if (!map.containsKey(cap)) map.put(cap, new ArrayList<>());
                map.get(cap).add(data.getContent());
            }

            ImmutableMap.Builder<MultiblockCapability<?>, ImmutableList<Content>> builder = ImmutableMap.builder();
            for (Map.Entry<MultiblockCapability<?>, List<Content>> entry : map.entrySet()) {
                MultiblockCapability<?> cap = entry.getKey();
                builder.put(cap, ImmutableList.copyOf(entry.getValue()));
            }
            return builder.build();
        }

        @Override
        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg(getErrorMsg()).error();
            validate(msg);
            return !msg.postIfNotEmpty();
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable Recipe register() {
            if (!validate()) return null;
            Recipe r = new Recipe(
                    uid,
                    toMbdMap(inputs, false), toMbdMap(outputs, false),
                    toMbdMap(inputs, true), toMbdMap(outputs, true),
                    ImmutableList.copyOf(conditions), time);
            ModSupport.MULTIBLOCKED.get().recipeMap(recipeMap.name).add(r);
            return r;
        }
    }

}
