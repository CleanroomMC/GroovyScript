package com.cleanroommc.groovyscript.compat.mods.naturesaura;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.AltarRecipe;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RegistryDescription
public class Altar extends VirtualizedRegistry<AltarRecipe> {

    public Altar() {
        super(Alias.generateOfClass(Altar.class).andGenerate("Infusion"));
    }

    @RecipeBuilderDescription(example = {
            @Example(".name('demo').input(item('minecraft:clay')).catalyst(item('minecraft:clay')).output(item('minecraft:diamond')).aura(100).time(100)"),
            @Example(".name(resource('example:demo')).input(item('minecraft:clay')).output(item('minecraft:gold_ingot') * 8).aura(30).time(5)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond')).catalyst(item('minecraft:clay')).aura(50).time(100)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(x -> NaturesAuraAPI.ALTAR_RECIPES.remove(x.name));
        restoreFromBackup().forEach(x -> NaturesAuraAPI.ALTAR_RECIPES.put(x.name, x));
    }

    public void add(AltarRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        NaturesAuraAPI.ALTAR_RECIPES.put(recipe.name, recipe);
    }

    public boolean remove(AltarRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        return NaturesAuraAPI.ALTAR_RECIPES.remove(recipe.name) != null;
    }

    @MethodDescription(example = @Example("resource('naturesaura:infused_iron')"))
    public boolean removeByName(ResourceLocation name) {
        if (name == null) return false;
        var recipe = NaturesAuraAPI.ALTAR_RECIPES.remove(name);
        if (recipe == null) return false;
        addBackup(recipe);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('minecraft:rotten_flesh')"))
    public boolean removeByInput(IIngredient input) {
        return NaturesAuraAPI.ALTAR_RECIPES.entrySet().removeIf(r -> {
            for (var item : r.getValue().input.getMatchingStacks()) {
                if (input.test(item)) {
                    addBackup(r.getValue());
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByCatalyst", example = @Example("item('naturesaura:crushing_catalyst')"))
    public boolean removeByCatalyst(IIngredient catalyst) {
        return NaturesAuraAPI.ALTAR_RECIPES.entrySet().removeIf(r -> {
            for (var item : r.getValue().catalyst.getMatchingStacks()) {
                if (catalyst.test(item)) {
                    addBackup(r.getValue());
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('minecraft:soul_sand')"))
    public boolean removeByOutput(IIngredient output) {
        return NaturesAuraAPI.ALTAR_RECIPES.entrySet().removeIf(r -> {
            if (output.test(r.getValue().output)) {
                addBackup(r.getValue());
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        NaturesAuraAPI.ALTAR_RECIPES.values().forEach(this::addBackup);
        NaturesAuraAPI.ALTAR_RECIPES.entrySet().clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<ResourceLocation, AltarRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(NaturesAuraAPI.ALTAR_RECIPES.entrySet()).setRemover(x -> remove(x.getValue()));
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<AltarRecipe> {

        @Property(valid = @Comp(value = "null", type = Comp.Type.NOT), defaultValue = "IIngredient.EMPTY")
        private IIngredient catalyst = IIngredient.EMPTY;
        @Property(valid = @Comp(value = "1", type = Comp.Type.GTE))
        private int aura;
        @Property(valid = @Comp(value = "1", type = Comp.Type.GTE))
        private int time;

        @RecipeBuilderMethodDescription
        public RecipeBuilder catalyst(IIngredient catalyst) {
            this.catalyst = catalyst;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder aura(int aura) {
            this.aura = aura;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Nature's Aura Altar Recipe";
        }

        public String getRecipeNamePrefix() {
            return "groovyscript_altar_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(catalyst == null, "catalyst must not be null");
            msg.add(aura <= 0, "aura must be greater than or equal to 1, yet it was {}", aura);
            msg.add(time <= 0, "time must be greater than or equal to 1, yet it was {}", time);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable AltarRecipe register() {
            if (!validate()) return null;
            AltarRecipe recipe = new AltarRecipe(name, input.get(0).toMcIngredient(), output.get(0), catalyst.toMcIngredient(), aura, time);
            ModSupport.NATURES_AURA.get().altar.add(recipe);
            return recipe;
        }
    }
}
