package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
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

    public static class Category extends VirtualizedRegistry<LexiconCategory> {

        @Override
        @GroovyBlacklist
        public void onReload() {
            removeScripted().forEach(BotaniaAPI.getAllCategories()::remove);
            BotaniaAPI.getAllCategories().addAll(restoreFromBackup());
        }

        public LexiconCategory add(String name, ResourceLocation icon, int priority) {
            LexiconCategory category = new LexiconCategory(name);
            category.setIcon(icon);
            category.setPriority(priority);
            add(category);
            return category;
        }

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

        public boolean remove(String name) {
            LexiconCategory category = Botania.getCategory(name);
            if (category != null) return remove(category);

            GroovyLog.msg("Error removing Botania Lexica Botania Category")
                    .add("could not find category with name {}", name)
                    .error()
                    .post();
            return false;
        }

        public boolean removeCategory(String name) {
            return remove(name);
        }

        public SimpleObjectStream<LexiconCategory> streamCategories() {
            return new SimpleObjectStream<>(BotaniaAPI.getAllCategories()).setRemover(this::remove);
        }

        public void removeAll() {
            BotaniaAPI.getAllCategories().forEach(this::addBackup);
            BotaniaAPI.getAllCategories().clear();
        }

    }

    public static class Page extends VirtualizedRegistry<PageChange> {

        @Override
        @GroovyBlacklist
        public void onReload() {
            removeScripted().forEach(change -> change.parent.pages.remove(change.index));
            restoreFromBackup().forEach(change -> change.parent.pages.add(change.index, change.page));
        }

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

        public boolean remove(LexiconEntry entry, int index) {
            if (entry == null) return false;
            if (entry.pages.get(index) == null) return false;
            LexiconPage page = entry.pages.get(index);
            return remove(entry, page);
        }

        public void removeByEntry(LexiconEntry entry) {
            entry.pages.forEach(x -> addBackup(new PageChange(x, entry, entry.pages.indexOf(x))));
            entry.pages.clear();
        }

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

        public void removeAll() {
            for (LexiconEntry entry : BotaniaAPI.getAllEntries()) {
                entry.pages.forEach(x -> addBackup(new PageChange(x, entry, entry.pages.indexOf(x))));
                entry.pages.clear();
            }
        }

        public SimpleObjectStream<LexiconPage> streamPages(LexiconEntry entry) {
            return new SimpleObjectStream<>(entry.pages).setRemover(page -> remove(entry, page));
        }

        public PageText createTextPage(String name) {
            return new PageText(name);
        }

        public PageLoreText createLoreTextPage(String name) {
            return new PageLoreText(name);
        }

        public PageImage createImagePage(String name, String image) {
            return new PageImage(name, image);
        }

        public PageEntity createEntityPage(String name, int size, String entity) {
            return new PageEntity(name, entity, size);
        }

        public PageEntity createEntityPage(String name, int size, EntityEntry entity) {
            return createEntityPage(name, size, Objects.requireNonNull(entity.getRegistryName()).toString());
        }

        public PageCraftingRecipe createCraftingPage(String name, String... recipes) {
            return new PageCraftingRecipe(name, Arrays.stream(recipes).map(ResourceLocation::new).collect(Collectors.toList()));
        }

        public PageBrew createBrewingPage(String name, String bottomText, RecipeBrew recipe) {
            return new PageBrew(recipe, name, bottomText);
        }

        public PageManaInfusionRecipe createInfusionPage(String name, RecipeManaInfusion... recipes) {
            return new PageManaInfusionRecipe(name, Arrays.asList(recipes));
        }

        public PageRuneRecipe createRunePage(String name, RecipeRuneAltar... recipes) {
            return new PageRuneRecipe(name, Arrays.asList(recipes));
        }

        public PagePetalRecipe<RecipePetals> createPetalPage(String name, RecipePetals... recipes) {
            return new PagePetalRecipe<>(name, Arrays.asList(recipes));
        }

        public PageElvenRecipe createElvenTradePage(String name, RecipeElvenTrade... recipes) {
            return new PageElvenRecipe(name, Arrays.asList(recipes));
        }
    }

    public static class Entry extends VirtualizedRegistry<LexiconEntry> {

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

        public LexiconEntry add(String name, LexiconCategory category) {
            LexiconEntry entry = new LexiconEntry(name, category);
            add(entry);
            return entry;
        }

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

        public boolean remove(String name) {
            LexiconEntry entry = Botania.getEntry(name);
            if (entry != null) return remove(entry);

            GroovyLog.msg("Error removing Botania Lexica Botania Entry")
                    .add("could not find entry with name {}", name)
                    .error()
                    .post();
            return false;
        }

        public boolean removeEntry(String name) {
            return remove(name);
        }

        public void setKnowledgeType(String entry, KnowledgeType type) {
            Objects.requireNonNull(Botania.getEntry(entry)).setKnowledgeType(type);
        }

        public void setKnowledgeType(String entry, String type) {
            setKnowledgeType(entry, BotaniaAPI.knowledgeTypes.get(type));
        }

        public void removeByCategory(LexiconCategory category) {
            category.entries.forEach(this::addBackup);
            category.entries.clear();
        }

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

        public void removeAll() {
            BotaniaAPI.getAllEntries().forEach(this::addBackup);
            BotaniaAPI.getAllEntries().clear();
        }

        public SimpleObjectStream<LexiconEntry> streamEntries() {
            return new SimpleObjectStream<>(BotaniaAPI.getAllEntries()).setRemover(this::remove);
        }

        public class EntryBuilder extends AbstractRecipeBuilder<LexiconEntry> {

            protected String name;
            protected LexiconCategory category;
            protected KnowledgeType type = BotaniaAPI.basicKnowledge;
            protected ItemStack icon = ItemStack.EMPTY;
            protected final List<LexiconPage> pages = new ArrayList<>();
            protected final List<ItemStack> extraRecipes = new ArrayList<>();
            protected boolean priority = false;

            public EntryBuilder isPriority() {
                this.priority = true;
                return this;
            }

            public EntryBuilder icon(IIngredient icon) {
                this.icon = icon.getMatchingStacks()[0];
                return this;
            }

            public EntryBuilder name(String name) {
                this.name = name;
                return this;
            }

            public EntryBuilder category(LexiconCategory category) {
                this.category = category;
                return this;
            }

            public EntryBuilder category(String categoryName) {
                return category(Botania.getCategory(categoryName));
            }

            public EntryBuilder knowledgeType(KnowledgeType type) {
                this.type = type;
                return this;
            }

            public EntryBuilder page(LexiconPage page) {
                this.pages.add(page);
                return this;
            }

            public EntryBuilder page(LexiconPage... pages) {
                for (LexiconPage page : pages) {
                    page(page);
                }
                return this;
            }

            public EntryBuilder page(Collection<LexiconPage> pages) {
                for (LexiconPage page : pages) {
                    page(page);
                }
                return this;
            }

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
