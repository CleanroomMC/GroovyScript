package com.cleanroommc.groovyscript.compat.mods.astralsorcery.starlightaltar;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Comp;
import com.cleanroommc.groovyscript.api.documentation.annotations.Property;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderMethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.astralsorcery.AstralSorcery;
import com.cleanroommc.groovyscript.compat.vanilla.CraftingRecipeBuilder;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Property(property = "replace", needsOverride = true)
@Property(property = "mirrored", needsOverride = true)
@Property(property = "recipeFunction", needsOverride = true)
@Property(property = "recipeAction", needsOverride = true)
public class AltarRecipeBuilder extends CraftingRecipeBuilder.Shaped {

    private final TileAltar.AltarLevel altarLevel;
    @Property(needsOverride = true)
    private final ArrayList<IIngredient> outerIngredients = new ArrayList<>();
    @Property(ignoresInheritedMethods = true)
    protected String name;
    protected ItemHandle[] inputs = null;
    protected ItemHandle[] outerInputs = null;
    @Property(needsOverride = true)
    protected int starlightRequired = 0;
    @Property(valid = @Comp(value = "0", type = Comp.Type.GT))
    protected int craftingTickTime = 1;
    @Property
    private IConstellation requiredConstellation = null;

    public AltarRecipeBuilder(int width, int height, TileAltar.AltarLevel level) {
        super(width, height);
        this.altarLevel = level;
        this.keyMap.put(' ', IIngredient.EMPTY);
    }

    @RecipeBuilderMethodDescription
    public AltarRecipeBuilder name(String name) {
        this.name = name;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "inputs")
    public AltarRecipeBuilder input(ItemHandle[] inputs) {
        this.inputs = inputs;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "keyBasedMatrix")
    public AltarRecipeBuilder row(String row) {
        if (this.keyBasedMatrix == null)
            this.keyBasedMatrix = new String[]{row};
        else
            this.keyBasedMatrix = ArrayUtils.add(this.keyBasedMatrix, row);
        return this;
    }

    @RecipeBuilderMethodDescription(field = "ingredientMatrix")
    public AltarRecipeBuilder matrix(List<List<IIngredient>> matrix) {
        this.ingredientMatrix = matrix;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "ingredientMatrix")
    public AltarRecipeBuilder shape(List<List<IIngredient>> matrix) {
        this.ingredientMatrix = matrix;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "keyBasedMatrix")
    public AltarRecipeBuilder matrix(String... matrix) {
        this.keyBasedMatrix = matrix;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "keyBasedMatrix")
    public AltarRecipeBuilder shape(String... matrix) {
        this.keyBasedMatrix = matrix;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "starlightRequired")
    public AltarRecipeBuilder starlight(int starlight) {
        this.starlightRequired = starlight;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "craftingTickTime")
    public AltarRecipeBuilder craftTime(int ticks) {
        this.craftingTickTime = ticks;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "requiredConstellation")
    public AltarRecipeBuilder constellation(IConstellation constellation) {
        this.requiredConstellation = constellation;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "outerIngredients")
    public AltarRecipeBuilder outerInput(IIngredient ing) {
        this.outerIngredients.add(ing);
        return this;
    }

    @RecipeBuilderMethodDescription(field = "outerIngredients")
    public AltarRecipeBuilder outerInput(IIngredient... ings) {
        this.outerIngredients.addAll(Arrays.asList(ings));
        return this;
    }

    @RecipeBuilderMethodDescription(field = "outerIngredients")
    public AltarRecipeBuilder outerInput(Collection<IIngredient> ings) {
        this.outerIngredients.addAll(ings);
        return this;
    }

    private boolean flattenMatrix() {
        ItemHandle[] in = AltarInputOrder.initInputList(this.altarLevel);
        int[][] map = AltarInputOrder.getMap(this.altarLevel);
        if (map == null || in == null) return false;

        for (int i = 0; i < this.keyBasedMatrix.length; i++) {
            String row = this.keyBasedMatrix[i];
            for (int j = 0; j < row.length(); j++) {
                if (map[i][j] >= 0 && row.charAt(j) != ' ') {
                    in[map[i][j]] = AstralSorcery.toItemHandle(keyMap.get(row.charAt(j)));
                }
            }
        }

        this.input(in);

        ItemHandle[] outerIn = new ItemHandle[this.outerIngredients.size()];
        for (int j = 0; j < this.outerIngredients.size(); j++)
            outerIn[j] = AstralSorcery.toItemHandle(this.outerIngredients.get(j));
        this.outerInputs = outerIn;

        return true;
    }

    public boolean validate() {
        GroovyLog.Msg out = GroovyLog.msg("Error adding Astral Sorcery Starlight Altar recipe").warn();

        if (this.output == null || this.output.isItemEqual(ItemStack.EMPTY)) {
            out.add("Recipe output cannot be empty.").error();
        }
        if (this.starlightRequired < 0) {
            out.add("Starlight amount cannot be negative, setting starlight amount to 0.");
            this.starlightRequired = 0;
        }
        if (this.craftingTickTime <= 0) {
            out.add("Crafting time cannot be negative or 0, setting crafting time to 1.");
            this.craftingTickTime = 1;
        }

        switch (this.altarLevel) {
            case DISCOVERY:
                if (this.starlightRequired > 1000)
                    out.add("Discovery Altar recipe cannot exceed 1000 starlight, clamping starlight to max table value.");
                this.starlightRequired = Math.min(starlightRequired, 1000);
            case ATTUNEMENT:
                if (this.starlightRequired > 2000)
                    out.add("Attunement Altar recipe cannot exceed 2000 starlight, clamping starlight to max table value.");
                this.starlightRequired = Math.min(starlightRequired, 2000);
            case CONSTELLATION_CRAFT:
                if (this.starlightRequired > 4000)
                    out.add("Constellation Altar recipe cannot exceed 4000 starlight, clamping starlight to max table value.");
                this.starlightRequired = Math.min(starlightRequired, 4000);
            case TRAIT_CRAFT:
                if (this.starlightRequired > 8000)
                    out.add("Trait Altar recipe cannot exceed 8000 starlight, clamping starlight to max table value.");
                this.starlightRequired = Math.min(starlightRequired, 8000);
        }

        if (keyBasedMatrix != null) {
            validateShape(out, errors, keyBasedMatrix, keyMap, (width, height, ingredients) -> null);
        } else if (ingredientMatrix != null) {
            validateShape(out, ingredientMatrix, (width, height, ingredients) -> null);
        }

        out.postIfNotEmpty();
        return (out.getLevel() != Level.ERROR && this.flattenMatrix());
    }

    @Override
    @RecipeBuilderRegistrationMethod
    public IRecipe register() {
        if (!validate()) return null;

        ModSupport.ASTRAL_SORCERY.get().altar.add(this.name, this.output, this.inputs, this.starlightRequired, this.craftingTickTime, this.altarLevel, this.requiredConstellation, this.outerInputs);
        return null;
    }
}