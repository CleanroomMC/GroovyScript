package com.cleanroommc.groovyscript.compat.mods.artisanworktables;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.artisanworktables.RecipeRegistryAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.helper.recipe.RecipeName;
import com.cleanroommc.groovyscript.registry.AbstractCraftingRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.codetaylor.mc.artisanworktables.api.ArtisanAPI;
import com.codetaylor.mc.artisanworktables.api.internal.recipe.*;
import com.codetaylor.mc.artisanworktables.api.internal.reference.EnumTier;
import com.codetaylor.mc.artisanworktables.api.recipe.ArtisanRecipe;
import com.codetaylor.mc.artisanworktables.api.recipe.IArtisanRecipe;
import com.codetaylor.mc.artisanworktables.api.recipe.requirement.IRequirement;
import com.codetaylor.mc.artisanworktables.api.recipe.requirement.IRequirementBuilder;
import com.codetaylor.mc.artisanworktables.modules.worktables.recipe.RecipeTierCalculator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

import java.util.*;
import java.util.stream.Collectors;

@RegistryDescription(admonition = {
        @Admonition(type = Admonition.Type.INFO, value = "groovyscript.wiki.artisanworktables.tabletypes"),
        @Admonition(type = Admonition.Type.DANGER, value = "groovyscript.wiki.artisanworktables.ctcrash")
})
public class Tables extends VirtualizedRegistry<IArtisanRecipe> {

    private RecipeRegistry getRegistry(IArtisanRecipe r) {
        String[] split = r.getName().split(":");
        return ArtisanAPI.getWorktableRecipeRegistry(split[0]);
    }

    @Override
    public void onReload() {
        removeScripted().forEach(r -> {
            RecipeRegistry rr = getRegistry(r);
            ((RecipeRegistryAccessor) rr).getRecipeList().remove(r);
            ((RecipeRegistryAccessor) rr).getRecipeMap().remove(r.getName());
        });
    }

    public void add(IArtisanRecipe recipe) {
        addScripted(recipe);
        RecipeRegistry rr = getRegistry(recipe);
        rr.addRecipe(recipe);
    }

    @RecipeBuilderDescription(
            requirement = @Property(property = "gridInput", needsOverride = true),
            example = {
            @Example(value = ".type('mason').matrix('AAA', 'A A', 'BBB').key('A', item('minecraft:iron_ingot')).key('B', item('minecraft:stone')).fluidInput(fluid('lava') * 250).output(item('minecraft:furnace'))"),
            @Example(value = ".type('mage').tool(item('minecraft:iron_sword'), 20).matrix([[item('minecraft:iron_ingot')], [item('minecraft:diamond')]]).input(item('minecraft:coal') * 2, item('minecraft:stone') * 32).level(10).consumeExperience(false).output(item('minecraft:clay'), item('minecraft:nether_star'))")
    })
    public ArtisanRecipeBuilder shapedBuilder() {
        return new ArtisanRecipeBuilder(true);
    }

    @RecipeBuilderDescription(
            requirement = {
                    @Property(property = "matrix", needsOverride = true),
                    @Property(property = "key", needsOverride = true),
                    @Property(property = "mirrored", needsOverride = true)
            },
            example = @Example(value = ".type('basic').gridInput(item('minecraft:coal'), item('minecraft:iron_ingot')).output(item('minecraft:clay')).maximumTier(1).minimumTier(1)"))
    public ArtisanRecipeBuilder shapelessBuilder() {
        return new ArtisanRecipeBuilder(false);
    }

    private static class ArtisanRecipeData {
        List<IIngredient> ingredients;
        int width;
        int height;
    }

    private static class ShapedGridBuilder extends AbstractCraftingRecipeBuilder.AbstractShaped<ArtisanRecipeData> {
        public ShapedGridBuilder() {
            super(5, 5);
        }

        @Override
        public ArtisanRecipeData register() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding shaped Artisan Worktable recipe").error()
                .add((keyBasedMatrix == null || keyBasedMatrix.length == 0) && (ingredientMatrix == null || ingredientMatrix.isEmpty()), () -> "No matrix was defined")
                .add(keyBasedMatrix != null && ingredientMatrix != null, () -> "A key based matrix AND a ingredient based matrix was defined. This is not allowed!");

            if (msg.postIfNotEmpty()) return null;

            ArtisanRecipeData recipe = null;
            if (keyBasedMatrix != null) {
                recipe = validateShape(msg, errors, keyBasedMatrix, keyMap, this::makeRecipeData);
            } else if (ingredientMatrix != null) {
                recipe = validateShape(msg, ingredientMatrix, this::makeRecipeData);
            }
            return recipe;
        }

        private ArtisanRecipeData makeRecipeData(int width, int height, List<IIngredient> ingredients) {
            ArtisanRecipeData ard = new ArtisanRecipeData();
            ard.width = width;
            ard.height = height;
            ard.ingredients = ingredients;
            return ard;
        }
    }

    private static class ShapelessGridBuilder extends AbstractCraftingRecipeBuilder.AbstractShapeless<ArtisanRecipeData> {

        public ShapelessGridBuilder() {
            super(5, 5);
        }

        @Override
        public ArtisanRecipeData register() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding shapeless Artisan Worktable recipe").error();
            msg.add(ingredients.isEmpty(), "Grid input must not be empty");
            msg.add(ingredients.size() > width * height, () -> "maximum inputs are " + (width * height) + " but found " + ingredients.size());
            if (msg.postIfNotEmpty()) return null;
            ArtisanRecipeData recipe = new ArtisanRecipeData();
            recipe.width = recipe.height = 0;
            recipe.ingredients = ingredients;
            return recipe;
        }
    }

    @SuppressWarnings("rawtypes")
    @Property(property = "matrix", requirement = "groovyscript.wiki.craftingrecipe.matrix.required", virtual = true, defaultValue = "empty")
    @Property(property = "key", defaultValue = "' ' = IIngredient.EMPTY", virtual = true)
    @Property(property = "gridInput", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "25", type = Comp.Type.LTE)}, virtual = true, defaultValue = "empty")
    @Property(property = "input", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "9", type = Comp.Type.LTE)}, value = "groovyscript.wiki.artisanworktables.tables.secondaryinputs.value")
    public static class ArtisanRecipeBuilder extends AbstractRecipeBuilder<IArtisanRecipe> {
        @Property
        private String type = "";
        private final boolean shaped;
        private final List<String> issues = new ArrayList<>();

        @Property
        private String name = "";

        @Property
        private boolean mirrored = false;

        @Property(defaultValue = "empty")
        private final Map<ResourceLocation, IRequirement> requirements = new HashMap<>();

        @Property
        private final List<OutputWeightPair> output = new ArrayList<>();

        @Property(defaultValue = "empty", valid = @Comp(type = Comp.Type.LTE, value = "3"))
        private final List<ToolEntry> tools = new ArrayList<>();

        @Property(defaultValue = "true")
        private boolean consumeSecondaryIngredients = true;

        @Property
        private int experience = 0;

        @Property
        private int level = 0;

        @Property(defaultValue = "true")
        private boolean consumeExperience = true;

        @Property(defaultValue = "empty", valid = @Comp(type = Comp.Type.LTE, value = "3"))
        private final List<ExtraOutputChancePair> extraOutputs = new ArrayList<>();

        @Property(defaultValue = "0")
        private int minimumTier = 0;

        @Property(defaultValue = "2")
        private int maximumTier = 2;

        @Property
        private boolean hidden = false;

        private ArtisanRecipeData data = null;

        private final AbstractCraftingRecipeBuilder<ArtisanRecipeData> gridBuilder;

        public ArtisanRecipeBuilder(boolean shaped) {
            this.shaped = shaped;
            if (shaped) {
                this.gridBuilder = new ShapedGridBuilder();
            } else {
                this.gridBuilder = new ShapelessGridBuilder();
            }
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder type(String type) {
            if (ArtisanAPI.getWorktableNames().contains(type)) {
                this.type = type;
            } else {
                issues.add("Unknown table type: " + type);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder name(String name) {
            this.name = name;
            return this;
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder name(ResourceLocation name) {
            return this.name(name.toString());
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder mirrored(boolean mirrored) {
            if (!shaped) {
                issues.add("Shapeless recipes cannot be mirrored");
            }
            this.mirrored = mirrored;
            return this;
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder mirrored() {
            return mirrored(true);
        }

        @RecipeBuilderMethodDescription(field = "requirements")
        public ArtisanRecipeBuilder requirement(IRequirementBuilder matchRequirementBuilder) {
            ResourceLocation location = matchRequirementBuilder.getResourceLocation();
            String modIdRequired = matchRequirementBuilder.getRequirementId().toLowerCase();

            if (Loader.isModLoaded(modIdRequired)) {
                this.requirements.put(location, matchRequirementBuilder.create());
            } else {
                issues.add(String.format("Mod %s required for %s was not loaded", modIdRequired, matchRequirementBuilder));
            }

            return this;
        }

        @RecipeBuilderMethodDescription(field = "tools")
        public ArtisanRecipeBuilder tool(ToolEntry te) {
            tools.add(te);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "tools")
        public ArtisanRecipeBuilder tool(IIngredient tool, int durability) {
            return tool(new ToolEntry(ArtisanIngredient.from(tool.toMcIngredient()), durability));
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder consumeSecondaryIngredients(boolean consume) {
            consumeSecondaryIngredients = consume;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "consumeSecondaryIngredients")
        public ArtisanRecipeBuilder retainSecondaryIngredients() {
            return consumeSecondaryIngredients(false);
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder experience(int experience) {
            this.experience = experience;
            return this;
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder level(int level) {
            this.level = level;
            return this;
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder consumeExperience(boolean consume) {
            consumeExperience = consume;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "consumeExperience")
        public ArtisanRecipeBuilder retainExperience() {
            return consumeExperience(false);
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder output(ItemStack stack) {
            return output(stack, 1);
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder output(OutputWeightPair owp) {
            output.add(owp);
            return this;
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder output(ItemStack stack, int weight) {
            return output(new OutputWeightPair(ArtisanItemStack.from(stack), weight));
        }

        @RecipeBuilderMethodDescription(field = "extraOutputs")
        public ArtisanRecipeBuilder extraOutput(ItemStack stack) {
            return extraOutput(stack, 1.0f);
        }

        @RecipeBuilderMethodDescription(field = "extraOutputs")
        public ArtisanRecipeBuilder extraOutput(ExtraOutputChancePair eocp) {
            extraOutputs.add(eocp);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "extraOutputs")
        public ArtisanRecipeBuilder extraOutput(ItemStack stack, float chance) {
            return extraOutput(new ExtraOutputChancePair(ArtisanItemStack.from(stack), chance));
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder minimumTier(int minimumTier) {
            this.minimumTier = minimumTier;
            return this;
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder maximumTier(int maximumTier) {
            this.maximumTier = maximumTier;
            return this;
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder hidden() {
            return hidden(true);
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder hidden(boolean hidden) {
            this.hidden = hidden;
            return this;
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder gridInput(IIngredient input) {
            if (this.shaped) {
                issues.add("Grid Input can only be used for shapeless recipes");
                return this;
            }

            ((ShapelessGridBuilder) this.gridBuilder).input(input);
            return this;
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder gridInput(IIngredient... inputs) {
            for (IIngredient ing : inputs) gridInput(ing);
            return this;
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder gridInput(Collection<IIngredient> inputs) {
            for (IIngredient ing : inputs) gridInput(ing);
            return this;
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder key(char c, IIngredient input) {
            if (!this.shaped) {
                issues.add("Input key can only be used for shaped recipes");
                return this;
            }

            ((ShapedGridBuilder) this.gridBuilder).key(c, input);
            return this;
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder key(String c, IIngredient input) {
            if (!this.shaped) {
                issues.add("Input key can only be used for shaped recipes");
                return this;
            }

            ((ShapedGridBuilder) this.gridBuilder).key(c, input);
            return this;
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder matrix(String... strings) {
            if (!this.shaped) {
                issues.add("Input matrix can only be used for shaped recipes");
                return this;
            }

            ((ShapedGridBuilder) this.gridBuilder).matrix(strings);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "matrix")
        public ArtisanRecipeBuilder shape(String... strings) {
            return matrix(strings);
        }

        @RecipeBuilderMethodDescription
        public ArtisanRecipeBuilder matrix(List<List<IIngredient>> matrix) {
            if (!this.shaped) {
                issues.add("Input matrix can only be used for shaped recipes");
                return this;
            }

            ((ShapedGridBuilder) this.gridBuilder).matrix(matrix);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "matrix")
        public ArtisanRecipeBuilder shape(List<List<IIngredient>> matrix) {
            return matrix(matrix);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public IArtisanRecipe register() {
            if (!validate()) return null;

            // The mod expects three Nonnull ExtraOutputChancePairs... yuck
            ExtraOutputChancePair[] extraOutputs = new ExtraOutputChancePair[3];
            Arrays.fill(extraOutputs, new ExtraOutputChancePair(ArtisanItemStack.EMPTY, 0));
            for (int i = 0; i < this.extraOutputs.size(); i++) extraOutputs[i] = this.extraOutputs.get(i);

            IArtisanRecipe recipe = new ArtisanRecipe(
                    type + ':' + name,
                    requirements,
                    output,
                    tools.toArray(new ToolEntry[0]),
                    data.ingredients.stream().map(r -> ArtisanIngredient.from(r.toMcIngredient())).collect(Collectors.toList()),
                    input.stream().map(r -> ArtisanIngredient.from(r.toMcIngredient())).collect(Collectors.toList()),
                    consumeSecondaryIngredients,
                    fluidInput.isEmpty() ? null : fluidInput.get(0),
                    experience,
                    level,
                    consumeExperience,
                    extraOutputs,
                    shaped ? IRecipeMatrixMatcher.SHAPED : IRecipeMatrixMatcher.SHAPELESS,
                    mirrored,
                    data.width,
                    data.height,
                    minimumTier,
                    maximumTier,
                    hidden);
            ModSupport.ARTISAN_WORKTABLES.get().tables.add(recipe);
            return recipe;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Artisan Worktable recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            if (name == null || name.isEmpty()) {
                name = RecipeName.generate("groovyscript_");
            }

            validateItems(msg, 0, 9, 0, 0);  // this validation is for secondary inputs, not grid inputs
            validateFluids(msg, 0, 1, 0, 0);
            msg.add(type.isEmpty(), "Table type must be set");
            msg.add(experience < 0, "Expected experience >= 0, but got {}", experience);
            msg.add(level < 0, "Expected level >= 0, but got {}", level);
            msg.add(extraOutputs.size() > 3, "Expected at most 3 secondary outputs, but got {}", extraOutputs.size());
            msg.add(tools.size() > 3, "Expected at most 3 tools, but got {}", tools.size());
            msg.add(minimumTier < 0 || minimumTier > 2, "Minimum tier must be between 0 and 2, but got {}", minimumTier);
            msg.add(maximumTier < 0 || maximumTier > 2, "Maximum tier must be between 0 and 2, but got {}", maximumTier);
            msg.add(minimumTier > maximumTier, "Maximum tier must be greater than or equal to Minimum tier");
            msg.add(output.isEmpty(), "Weighted outputs can't be empty");

            data = this.gridBuilder.register();
            if (data == null) {
                msg.add("Error generating the recipe grid");
            } else {
                EnumTier tier = RecipeTierCalculator.calculateTier(
                        this.type,
                        data.width,
                        data.height,
                        this.tools.size(),
                        this.input.size(),
                        this.fluidInput.isEmpty() ? null : this.fluidInput.get(0)
                );
                // weird cornercase that the mod doesn't handle
                if (!shaped && data.ingredients.size() > 9 && tier != null) {
                    if (data.ingredients.size() <= 25) tier = EnumTier.WORKSHOP;
                    else tier = null;
                }
                msg.add(tier == null, "Unable to calculate the table tier");
                if (tier != null) {
                    int tierNeeded = tier.getId();
                    msg.add(tierNeeded > maximumTier, "The recipe requires at least tier {}, but maximumTier was set to {}", tierNeeded, maximumTier);
                    minimumTier = Math.max(minimumTier, tierNeeded);
                }
            }

            for (String s : issues) msg.add(s);
        }
    }

}
