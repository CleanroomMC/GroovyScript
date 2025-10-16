package com.cleanroommc.groovyscript.documentation.helper;

import net.minecraft.client.resources.I18n;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This is used to create the index page for the wiki.
 * <p>
 * It has a default {@link MarkdownSection} for the normal
 * categories, and in most cases this is the only section needed.
 * <p>
 * Typical use will be multiple calls of {@link #add(String)}.
 *
 * @see MarkdownSection
 */
public class LinkIndex {

    private static final String PRIMARY_SECTION = "primary";

    private final Map<String, MarkdownSection> sections = new HashMap<>();

    public LinkIndex() {
        this(new MarkdownSection(I18n.format("groovyscript.wiki.categories"), count -> I18n.format("groovyscript.wiki.subcategories_count", count)));
    }

    public LinkIndex(MarkdownSection primary) {
        register(PRIMARY_SECTION, primary);
    }

    /**
     * Registers a section to the index, with the given id.
     * While it is possible to use {@link #add(String, String)},
     * in many cases it is preferable to simply call {@link MarkdownSection#addEntry(String)}
     * on the section being registered here.
     *
     * @param id      the id the section has
     * @param section the section to register
     */
    public void register(String id, MarkdownSection section) {
        sections.put(id, section);
    }

    /**
     * @param entry the string to add to the primary section, typically a markdown bullet point link
     * @see MarkdownSection#addEntry(String)
     */
    public void add(String entry) {
        sections.get(PRIMARY_SECTION).addEntry(entry);
    }

    /**
     * @param id    the id of the section to add to - if invalid, the entry will be added to the primary category
     * @param entry the string to add to the section, typically a markdown bullet point link
     * @see MarkdownSection#addEntry(String)
     */
    public void add(String id, String entry) {
        sections.getOrDefault(id, sections.get(PRIMARY_SECTION)).addEntry(entry);
    }

    /**
     * @return the full text of the sections
     */
    public String get() {
        return sections.values().stream()
                .filter(MarkdownSection::hasEntries)
                .sorted()
                .map(MarkdownSection::get)
                .collect(Collectors.joining());
    }

    /**
     * @return the list of links directly, without a header/subtitle
     */
    public String getLinks() {
        return sections.values().stream()
                .filter(MarkdownSection::hasEntries)
                .sorted()
                .map(MarkdownSection::getEntries)
                .flatMap(Collection::stream)
                .collect(Collectors.joining("\n"));
    }
}
