package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.botania.recipe.PageChange;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.KnowledgeType;
import vazkii.botania.api.lexicon.LexiconCategory;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconPage;
import vazkii.botania.api.recipe.*;
import vazkii.botania.common.lexicon.page.*;

import java.util.*;
import java.util.stream.Collectors;

public class Lexicon {

    public final Category category = new Category();
    public final Entry entry = new Entry();
    public final Page page = new Page();

    @RegistryDescription(
            category = RegistryDescription.Category.ENTRIES,
            priority = 2100
    )
    public static class Category extends VirtualizedRegistry<LexiconCategory> {

        @Override
        @GroovyBlacklist
        public void onReload() {
            removeScripted().forEach(BotaniaAPI.getAllCategories()::remove);
            BotaniaAPI.getAllCategories().addAll(restoreFromBackup());
        }

        @MethodDescription(description = "groovyscript.wiki.botania.category.add0", type = MethodDescription.Type.ADDITION, example = @Example("'first', resource('minecraft:textures/items/clay_ball.png'), 100"))
        public LexiconCategory add(String name, ResourceLocation icon, int priority) {
            LexiconCategory category = new LexiconCategory(name);
            category.setIcon(icon);
            category.setPriority(priority);
            add(category);
            return category;
        }

        @MethodDescription(description = "groovyscript.wiki.botania.category.add1", type = MethodDescription.Type.ADDITION, example = @Example("'test', resource('minecraft:textures/items/apple.png')"))
        public LexiconCategory add(String name, ResourceLocation icon) {
            return add(name, icon, 5);
        }

        public void add(LexiconCategory category) {
            if (category == null) return;
            addScripted(category);
            BotaniaAPI.addCategory(category);
        }

        public boolean remove(LexiconCategory category) {
            if (category == null) return false;
            addBackup(category);
            BotaniaAPI.getAllCategories().remove(category);
            return true;
        }

        @MethodDescription(description = "groovyscript.wiki.botania.category.removeCategory", example = @Example("'botania.category.alfhomancy'"))
        public boolean remove(String name) {
            LexiconCategory category = Botania.getCategory(name);
            if (category != null) return remove(category);

            GroovyLog.msg("Error removing Botania Lexica Botania Category")
                    .add("could not find category with name {}", name)
                    .error()
                    .post();
            return false;
        }

        @MethodDescription(example = @Example("'botania.category.misc'"))
        public boolean removeCategory(String name) {
            return remove(name);
        }

        @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
        public SimpleObjectStream<LexiconCategory> streamCategories() {
            return new SimpleObjectStream<>(BotaniaAPI.getAllCategories()).setRemover(this::remove);
        }

        @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
        public void removeAll() {
            BotaniaAPI.getAllCategories().forEach(this::addBackup);
            BotaniaAPI.getAllCategories().clear();
        }

    }

    @RegistryDescription(
            category = RegistryDescription.Category.ENTRIES,
            priority = 2200
    )
    public static class Page extends VirtualizedRegistry<PageChange> {

        @Override
        @GroovyBlacklist
        public void onReload() {
            removeScripted().forEach(change -> change.parent.pages.remove(change.index));
            restoreFromBackup().forEach(change -> change.parent.pages.add(change.index, change.page));
        }

        @MethodDescription(type = MethodDescription.Type.ADDITION)
        public void add(LexiconEntry entry, LexiconPage page, int index) {
            if (page == null || entry == null) return;
            if (entry.pages.contains(page)) return;
            PageChange change = new PageChange(page, entry, index);
            addScripted(change);
            entry.pages.add(index, page);
        }

        public boolean remove(LexiconEntry entry, LexiconPage page) {
            if (page == null || entry == null) return false;
            if (!entry.pages.contains(page)) return false;
            int index = entry.pages.indexOf(page);
            PageChange change = new PageChange(page, entry, index);
            addBackup(change);
            entry.pages.remove(index);
            return true;
        }

        @MethodDescription
        public boolean remove(LexiconEntry entry, int index) {
            if (entry == null) return false;
            if (entry.pages.get(index) == null) return false;
            LexiconPage page = entry.pages.get(index);
            return remove(entry, page);
        }

        @MethodDescription
        public void removeByEntry(LexiconEntry entry) {
            entry.pages.forEach(x -> addBackup(new PageChange(x, entry, entry.pages.indexOf(x))));
            entry.pages.clear();
        }

        @MethodDescription(example = @Example("'botania.entry.runeAltar'"))
        public void removeByEntry(String name) {
            LexiconEntry entry = Botania.getEntry(name);
            if (entry == null) {
                GroovyLog.msg("Error removing Botania Lexica Botania Pages by Entry")
                        .add("could not find entry with name {}", name)
                        .error()
                        .post();
                return;
            }
            removeByEntry(entry);
        }


        @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
        public void removeAll() {
            for (LexiconEntry entry : BotaniaAPI.getAllEntries()) {
                entry.pages.forEach(x -> addBackup(new PageChange(x, entry, entry.pages.indexOf(x))));
                entry.pages.clear();
            }
        }

        @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
        public SimpleObjectStream<LexiconPage> streamPages(LexiconEntry entry) {
            return new SimpleObjectStream<>(entry.pages).setRemover(page -> remove(entry, page));
        }

        @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'groovy.exampleTextPage'"))
        public PageText createTextPage(String name) {
            return new PageText(name);
        }

        @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'groovy.exampleLoreTextPage'"))
        public PageLoreText createLoreTextPage(String name) {
            return new PageLoreText(name);
        }

        @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'groovy.exampleImagePage', 'minecraft:textures/items/apple.png'"))
        public PageImage createImagePage(String name, String image) {
            return new PageImage(name, image);
        }

        @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'groovy.exampleEntityPage', 100, 'minecraft:wither_skeleton'"))
        public PageEntity createEntityPage(String name, int size, String entity) {
            return new PageEntity(name, entity, size);
        }

        @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'groovy.exampleEntityPage', 5, entity('minecraft:wither_skeleton')"))
        public PageEntity createEntityPage(String name, int size, EntityEntry entity) {
            return createEntityPage(name, size, Objects.requireNonNull(entity.getRegistryName()).toString());
        }

        @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'groovy.exampleCraftingPage', 'minecraft:clay'"))
        public PageCraftingRecipe createCraftingPage(String name, String... recipes) {
            return new PageCraftingRecipe(name, Arrays.stream(recipes).map(ResourceLocation::new).collect(Collectors.toList()));
        }

        @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "'groovy.exampleBrewingPage', 'bottomText', 'bottomText', mods.botania.brewrecipe.recipeBuilder().input(item('minecraft:clay'), ore('ingotGold'), ore('gemDiamond')).brew(brew('absorption')).register()", commented = true))
        public PageBrew createBrewingPage(String name, String bottomText, RecipeBrew recipe) {
            return new PageBrew(recipe, name, bottomText);
        }

        @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "'groovy.exampleInfusionPage', mods.botania.manainfusion.recipeBuilder().input(ore('ingotGold')).output(item('botania:manaresource', 1)).mana(500).catalyst(blockstate('minecraft:stone')).register()", commented = true))
        public PageManaInfusionRecipe createInfusionPage(String name, RecipeManaInfusion... recipes) {
            return new PageManaInfusionRecipe(name, Arrays.asList(recipes));
        }

        @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "'groovy.exampleRunePage', mods.botania.runealtar.recipeBuilder().input(ore('gemEmerald'), item('minecraft:apple')).output(item('minecraft:diamond')).mana(500).register()", commented = true))
        public PageRuneRecipe createRunePage(String name, RecipeRuneAltar... recipes) {
            return new PageRuneRecipe(name, Arrays.asList(recipes));
        }

        @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "'groovy.examplePetalPage', mods.botania.apothecary.recipeBuilder().input(ore('blockGold'), ore('ingotIron'), item('minecraft:apple')).output(item('minecraft:golden_apple')).register()", commented = true))
        public PagePetalRecipe<RecipePetals> createPetalPage(String name, RecipePetals... recipes) {
            return new PagePetalRecipe<>(name, Arrays.asList(recipes));
        }

        @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "'groovy.exampleElvenTradePage', mods.botania.elventrade.recipeBuilder().input(ore('ingotGold'), ore('ingotIron')).output(item('botania:manaresource:7')).register()", commented = true))
        public PageElvenRecipe createElvenTradePage(String name, RecipeElvenTrade... recipes) {
            return new PageElvenRecipe(name, Arrays.asList(recipes));
        }
    }

    @RegistryDescription(
            category = RegistryDescription.Category.ENTRIES,
            priority = 2300
    )
    public static class Entry extends VirtualizedRegistry<LexiconEntry> {

        @RecipeBuilderDescription(example = @Example(".name('test_entry').icon(ore('blockIron')).category('test').knowledgeType(newType).page(mods.botania.lexicon.page.createTextPage('groovy.exampleTextPage'))"))
        public EntryBuilder entryBuilder() {
            return new EntryBuilder();
        }

        @Override
        @GroovyBlacklist
        public void onReload() {
            removeScripted().forEach(BotaniaAPI.getAllEntries()::remove);
            restoreFromBackup().forEach(entry -> {
                BotaniaAPI.getAllEntries().add(entry);
                entry.category.entries.add(entry);
            });
        }

        @MethodDescription(type = MethodDescription.Type.ADDITION)
        public LexiconEntry add(String name, LexiconCategory category) {
            LexiconEntry entry = new LexiconEntry(name, category);
            add(entry);
            return entry;
        }

        @MethodDescription(type = MethodDescription.Type.ADDITION)
        public LexiconEntry add(String name, String category) {
            return add(name, Botania.getCategory(category));
        }

        public void add(LexiconEntry entry) {
            if (entry == null) return;
            addScripted(entry);
            BotaniaAPI.addEntry(entry, entry.category);
        }

        public boolean remove(LexiconEntry entry) {
            if (entry == null) return false;
            addBackup(entry);
            BotaniaAPI.getAllEntries().remove(entry);
            entry.category.entries.remove(entry);
            return true;
        }

        @MethodDescription(description = "groovyscript.wiki.botania.entry.removeEntry", example = @Example("'botania.entry.flowers'"))
        public boolean remove(String name) {
            LexiconEntry entry = Botania.getEntry(name);
            if (entry != null) return remove(entry);

            GroovyLog.msg("Error removing Botania Lexica Botania Entry")
                    .add("could not find entry with name {}", name)
                    .error()
                    .post();
            return false;
        }

        @MethodDescription(example = @Example("'botania.entry.apothecary'"))
        public boolean removeEntry(String name) {
            return remove(name);
        }

        @MethodDescription(type = MethodDescription.Type.VALUE)
        public void setKnowledgeType(String entry, KnowledgeType type) {
            Objects.requireNonNull(Botania.getEntry(entry)).setKnowledgeType(type);
        }

        @MethodDescription(type = MethodDescription.Type.VALUE)
        public void setKnowledgeType(String entry, String type) {
            setKnowledgeType(entry, BotaniaAPI.knowledgeTypes.get(type));
        }

        @MethodDescription
        public void removeByCategory(LexiconCategory category) {
            category.entries.forEach(this::addBackup);
            category.entries.clear();
        }

        @MethodDescription
        public void removeByCategory(String name) {
            LexiconCategory category = Botania.getCategory(name);
            if (category == null) {
                GroovyLog.msg("Error removing Botania Lexica Botania Entries by Category")
                        .add("could not find category with name {}", name)
                        .error()
                        .post();
                return;
            }
            removeByCategory(category);
        }

        @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
        public void removeAll() {
            BotaniaAPI.getAllEntries().forEach(this::addBackup);
            BotaniaAPI.getAllEntries().clear();
        }

        @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
        public SimpleObjectStream<LexiconEntry> streamEntries() {
            return new SimpleObjectStream<>(BotaniaAPI.getAllEntries()).setRemover(this::remove);
        }

        public class EntryBuilder extends AbstractRecipeBuilder<LexiconEntry> {

            @Property(valid = @Comp(value = "1", type = Comp.Type.GTE))
            protected final List<LexiconPage> pages = new ArrayList<>();
            @Property
            protected final List<ItemStack> extraRecipes = new ArrayList<>();
            @Property(ignoresInheritedMethods = true, valid = @Comp(value = "null", type = Comp.Type.NOT))
            protected String name;
            @Property(valid = @Comp(value = "null", type = Comp.Type.NOT))
            protected LexiconCategory category;
            @Property(defaultValue = "BotaniaAPI.basicKnowledge")
            protected KnowledgeType type = BotaniaAPI.basicKnowledge;
            @Property(defaultValue = "ItemStack.EMPTY")
            protected ItemStack icon = ItemStack.EMPTY;
            @Property
            protected boolean priority = false;

            @RecipeBuilderMethodDescription(field = "priority")
            public EntryBuilder isPriority() {
                this.priority = true;
                return this;
            }

            @RecipeBuilderMethodDescription
            public EntryBuilder icon(IIngredient icon) {
                this.icon = icon.getMatchingStacks()[0];
                return this;
            }

            @RecipeBuilderMethodDescription
            public EntryBuilder name(String name) {
                this.name = name;
                return this;
            }

            @RecipeBuilderMethodDescription
            public EntryBuilder category(LexiconCategory category) {
                this.category = category;
                return this;
            }

            @RecipeBuilderMethodDescription
            public EntryBuilder category(String categoryName) {
                return category(Botania.getCategory(categoryName));
            }

            @RecipeBuilderMethodDescription(field = "type")
            public EntryBuilder knowledgeType(KnowledgeType type) {
                this.type = type;
                return this;
            }

            @RecipeBuilderMethodDescription(field = "pages")
            public EntryBuilder page(LexiconPage page) {
                this.pages.add(page);
                return this;
            }

            @RecipeBuilderMethodDescription(field = "pages")
            public EntryBuilder page(LexiconPage... pages) {
                for (LexiconPage page : pages) {
                    page(page);
                }
                return this;
            }

            @RecipeBuilderMethodDescription(field = "pages")
            public EntryBuilder page(Collection<LexiconPage> pages) {
                for (LexiconPage page : pages) {
                    page(page);
                }
                return this;
            }

            @RecipeBuilderMethodDescription(field = "extraRecipes")
            public EntryBuilder extraRecipe(IIngredient stack) {
                this.extraRecipes.add(stack.getMatchingStacks()[0]);
                return this;
            }

            @Override
            public String getErrorMsg() {
                return "Error adding Botania Lexicon Entry";
            }

            @Override
            public void validate(GroovyLog.Msg msg) {
                validateFluids(msg, 0, 0, 0, 0);
                validateItems(msg, 0, 0, 0, 0);
                msg.add(name == null, "expected a valid name, got " + name);
                msg.add(pages.size() < 1, "entry must have at least 1 page, got " + pages.size());
                msg.add(category == null, "expected a valid category, got " + category);
            }

            @Override
            @RecipeBuilderRegistrationMethod
            public @Nullable LexiconEntry register() {
                if (!validate()) return null;
                LexiconEntry entry = new LexiconEntry(name, category);
                if (priority) entry.setPriority();
                entry.setKnowledgeType(type);
                entry.setIcon(icon);
                pages.forEach(entry::addPage);
                extraRecipes.forEach(entry::addExtraDisplayedRecipe);
                add(entry);
                return entry;
            }
        }
    }
}
