package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe.EntityMeltingRecipe;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.TinkerRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class EntityMelting extends VirtualizedRegistry<EntityMeltingRecipe> {

    @RecipeBuilderDescription(example = @Example(".fluidOutput(fluid('iron') * 500).input('minecraft','pig').register()"))
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

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        TinkerRegistryAccessor.getEntityMeltingRegistry().forEach((name, result) -> addBackup(new EntityMeltingRecipe(name, result)));
        TinkerRegistryAccessor.getEntityMeltingRegistry().forEach(TinkerRegistryAccessor.getEntityMeltingRegistry()::remove);
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
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

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(ResourceLocation name) {
            this.input = name;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(String name) {
            return input(new ResourceLocation(name));
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(String modid, String name) {
            return input(new ResourceLocation(modid, name));
        }

        @RecipeBuilderMethodDescription
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
