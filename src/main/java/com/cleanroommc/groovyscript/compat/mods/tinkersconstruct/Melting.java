package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IDynamicGroovyProperty;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe.EntityMeltingRecipe;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe.MeltingRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe.MeltingRecipeRegistry;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.TinkerRegistryAccessor;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RegistryDescription
public class Melting extends MeltingRecipeRegistry implements IDynamicGroovyProperty {

    private final Map<String, Object> properties = new Object2ObjectOpenHashMap<>();

    public final EntityMelting entityMelting = new EntityMelting();

    public Melting() {
        for (String s : Alias.generateOf("EntityMelting")) this.properties.put(s, this.entityMelting);
    }

    @Override
    public @Nullable Object getProperty(String name) {
        return properties.get(name);
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:gravel')).fluidOutput(fluid('lava') * 25).time(80)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder(this);
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(TinkerRegistryAccessor.getMeltingRegistry()::remove);
        restoreFromBackup().forEach(TinkerRegistryAccessor.getMeltingRegistry()::add);
        entityMelting.onReload();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public MeltingRecipe add(IIngredient input, FluidStack output, int temp) {
        MeltingRecipe recipe = new MeltingRecipe(MeltingRecipeBuilder.recipeMatchFromIngredient(input, output.amount), output, temp);
        add(recipe);
        return recipe;
    }

    public void add(MeltingRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        TinkerRegistryAccessor.getMeltingRegistry().add(recipe);
    }

    public boolean remove(MeltingRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        TinkerRegistryAccessor.getMeltingRegistry().remove(recipe);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput")
    public boolean removeByInput(IIngredient input) {
        NonNullList<ItemStack> matching = NonNullList.from(ItemStack.EMPTY, input.getMatchingStacks());
        if (TinkerRegistryAccessor.getMeltingRegistry().removeIf(recipe -> {
            boolean found = recipe.input.matches(matching).isPresent();
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Melting recipe")
                .add("could not find recipe with input {}", input)
                .error()
                .post();
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput")
    public boolean removeByOutput(FluidStack output) {
        if (TinkerRegistryAccessor.getMeltingRegistry().removeIf(recipe -> {
            boolean found = recipe.getResult().isFluidEqual(output);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Melting recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInputAndOutput")
    public boolean removeByInputAndOutput(IIngredient input, FluidStack output) {
        NonNullList<ItemStack> matching = NonNullList.from(ItemStack.EMPTY, input.getMatchingStacks());
        if (TinkerRegistryAccessor.getMeltingRegistry().removeIf(recipe -> {
            boolean found = recipe.input.matches(matching).isPresent() && recipe.getResult().isFluidEqual(output);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Melting recipe")
                .add("could not find recipe with input {} and output {}", input, output)
                .error()
                .post();
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll")
    public void removeAll() {
        TinkerRegistryAccessor.getMeltingRegistry().forEach(this::addBackup);
        TinkerRegistryAccessor.getMeltingRegistry().forEach(TinkerRegistryAccessor.getMeltingRegistry()::remove);
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes")
    public SimpleObjectStream<MeltingRecipe> streamRecipes() {
        return new SimpleObjectStream<>(TinkerRegistryAccessor.getMeltingRegistry()).setRemover(this::remove);
    }

    public static class RecipeBuilder extends MeltingRecipeBuilder {

        public RecipeBuilder(Melting melting) {
            super(melting, "Tinkers Construct Melting recipe");
        }
    }

    @RegistryDescription
    public static class EntityMelting extends VirtualizedRegistry<EntityMeltingRecipe> {

        @RecipeBuilderDescription(example = @Example(".fluidOutput(fluid('iron') * 500).input('minecraft','pig')"))
        public RecipeBuilder recipeBuilder() {
            return new RecipeBuilder();
        }

        @Override
        @GroovyBlacklist
        public void onReload() {
            removeScripted().forEach(recipe -> TinkerRegistryAccessor.getEntityMeltingRegistry().remove(recipe.name, recipe.result));
            restoreFromBackup().forEach(recipe -> TinkerRegistryAccessor.getEntityMeltingRegistry().put(recipe.name, recipe.result));
        }

        @MethodDescription(type = MethodDescription.Type.QUERY)
        protected List<EntityMeltingRecipe> getAllRecipes() {
            return TinkerRegistryAccessor.getEntityMeltingRegistry().entrySet().stream().map(EntityMeltingRecipe::fromMapEntry).collect(Collectors.toList());
        }

        public EntityMeltingRecipe add(EntityEntry entity, FluidStack output) {
            EntityMeltingRecipe recipe = new EntityMeltingRecipe(entity, output);
            add(recipe);
            return recipe;
        }

        public void add(EntityMeltingRecipe recipe) {
            if (recipe == null || recipe.name == null) return;
            addScripted(recipe);
            TinkerRegistryAccessor.getEntityMeltingRegistry().put(recipe.name, recipe.result);
        }

        public boolean remove(EntityMeltingRecipe recipe) {
            if (recipe == null || recipe.name == null) return false;
            addBackup(recipe);
            TinkerRegistryAccessor.getEntityMeltingRegistry().remove(recipe.name, recipe.result);
            return true;
        }

        @MethodDescription(description = "groovyscript.wiki.removeByInput")
        public boolean removeByInput(EntityEntry entity) {
            ResourceLocation name = entity.getRegistryName();
            if (TinkerRegistryAccessor.getEntityMeltingRegistry().entrySet().removeIf(entry -> {
                boolean found = entry.getKey().equals(name);
                if (found) addBackup(new EntityMeltingRecipe(entry.getKey(), entry.getValue()));
                return found;
            })) return true;

            GroovyLog.msg("Error removing Tinkers Construct Entity Melting recipe")
                    .add("could not find recipe with input {}", name)
                    .error()
                    .post();
            return false;
        }

        @MethodDescription(description = "groovyscript.wiki.removeByOutput")
        public boolean removeByOutput(FluidStack output) {
            if (TinkerRegistryAccessor.getEntityMeltingRegistry().entrySet().removeIf(entry -> {
                boolean found = entry.getValue().isFluidEqual(output);
                if (found) addBackup(new EntityMeltingRecipe(entry.getKey(), entry.getValue()));
                return found;
            })) return true;

            GroovyLog.msg("Error removing Tinkers Construct Entity Melting recipe")
                    .add("could not find recipe with output {}", output)
                    .error()
                    .post();
            return false;
        }

        @MethodDescription(description = "groovyscript.wiki.removeAll")
        public void removeAll() {
            TinkerRegistryAccessor.getEntityMeltingRegistry().forEach((name, result) -> addBackup(new EntityMeltingRecipe(name, result)));
            TinkerRegistryAccessor.getEntityMeltingRegistry().forEach(TinkerRegistryAccessor.getEntityMeltingRegistry()::remove);
        }

        @MethodDescription(description = "groovyscript.wiki.streamRecipes")
        public SimpleObjectStream<EntityMeltingRecipe> streamRecipes() {
            return new SimpleObjectStream<>(getAllRecipes()).setRemover(this::remove);
        }

        @Override
        protected boolean compareRecipe(EntityMeltingRecipe recipe, EntityMeltingRecipe recipe2) {
            return recipe.equals(recipe2);
        }

        public class RecipeBuilder implements IRecipeBuilder<EntityMeltingRecipe> {

            @Property(valid = @Comp(value = "null", type = Comp.Type.NOT))
            private FluidStack output;
            @Property(valid = @Comp(value = "null", type = Comp.Type.NOT))
            private ResourceLocation input;

            @RecipeBuilderMethodDescription(field = "output")
            public RecipeBuilder fluidOutput(FluidStack stack) {
                this.output = stack;
                return this;
            }

            @RecipeBuilderMethodDescription(field = "input")
            public RecipeBuilder input(ResourceLocation name) {
                this.input = name;
                return this;
            }

            @RecipeBuilderMethodDescription(field = "input")
            public RecipeBuilder input(String name) {
                return input(new ResourceLocation(name));
            }

            @RecipeBuilderMethodDescription(field = "input")
            public RecipeBuilder input(String modid, String name) {
                return input(new ResourceLocation(modid, name));
            }

            @RecipeBuilderMethodDescription(field = "input")
            public RecipeBuilder input(EntityEntry entity) {
                return input(entity.getRegistryName());
            }

            private String getErrorMsg() {
                return "Error adding Tinkers Construct Entity Melting recipe";
            }

            private void validate(GroovyLog.Msg msg) {
                msg.add(input == null || EntityList.getClass(input) == null, "Expected valid entity name, got " + input);
                msg.add(output == null || output.amount < 1, "Expected 1 output fluid but found none!");
            }

            @Override
            public boolean validate() {
                GroovyLog.Msg msg = GroovyLog.msg(this.getErrorMsg()).error();
                this.validate(msg);
                return !msg.postIfNotEmpty();
            }

            @Override
            public @Nullable EntityMeltingRecipe register() {
                if (!validate()) return null;
                EntityMeltingRecipe recipe = new EntityMeltingRecipe(input, output);
                add(recipe);
                return recipe;
            }
        }
    }
}
