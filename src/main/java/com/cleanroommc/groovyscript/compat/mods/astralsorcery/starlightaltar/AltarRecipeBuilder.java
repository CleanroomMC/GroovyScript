package com.cleanroommc.groovyscript.compat.mods.astralsorcery.starlightaltar;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
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

public class AltarRecipeBuilder extends CraftingRecipeBuilder.Shaped {

    private final TileAltar.AltarLevel altarLevel;
    private final ArrayList<IIngredient> outerIngredients = new ArrayList<>();
    protected String name;
    protected ItemHandle[] inputs = null;
    protected ItemHandle[] outerInputs = null;
    protected int starlightRequired = 0;
    protected int craftingTickTime = 1;
    private IConstellation requiredConstellation = null;

    public AltarRecipeBuilder(int width, int height, TileAltar.AltarLevel level) {
        super(width, height);
        this.altarLevel = level;
        this.keyMap.put(' ', IIngredient.EMPTY);
    }

    public AltarRecipeBuilder name(String name) {
        this.name = name;
        return this;
    }

    public AltarRecipeBuilder input(ItemHandle[] inputs) {
        this.inputs = inputs;
        return this;
    }

    public AltarRecipeBuilder row(String row) {
        if (this.keyBasedMatrix == null)
            this.keyBasedMatrix = new String[]{row};
        else
            this.keyBasedMatrix = ArrayUtils.add(this.keyBasedMatrix, row);
        return this;
    }

    public AltarRecipeBuilder matrix(List<List<IIngredient>> matrix) {
        this.ingredientMatrix = matrix;
        return this;
    }

    public AltarRecipeBuilder shape(List<List<IIngredient>> matrix) {
        this.ingredientMatrix = matrix;
        return this;
    }

    public AltarRecipeBuilder matrix(String... matrix) {
        this.keyBasedMatrix = matrix;
        return this;
    }

    public AltarRecipeBuilder shape(String... matrix) {
        this.keyBasedMatrix = matrix;
        return this;
    }

    public AltarRecipeBuilder starlight(int starlight) {
        this.starlightRequired = starlight;
        return this;
    }

    public AltarRecipeBuilder craftTime(int ticks) {
        this.craftingTickTime = ticks;
        return this;
    }

    public AltarRecipeBuilder constellation(IConstellation constellation) {
        this.requiredConstellation = constellation;
        return this;
    }

    public AltarRecipeBuilder outerInput(IIngredient ing) {
        this.outerIngredients.add(ing);
        return this;
    }

    public AltarRecipeBuilder outerInput(IIngredient... ings) {
        this.outerIngredients.addAll(Arrays.asList(ings));
        return this;
    }

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
    public IRecipe register() {
        if (!validate()) return null;

        ModSupport.ASTRAL_SORCERY.get().altar.add(this.name, this.output, this.inputs, this.starlightRequired, this.craftingTickTime, this.altarLevel, this.requiredConstellation, this.outerInputs);
        return null;
    }
}