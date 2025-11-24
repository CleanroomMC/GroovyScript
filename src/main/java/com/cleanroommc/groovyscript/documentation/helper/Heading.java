package com.cleanroommc.groovyscript.documentation.helper;

import com.cleanroommc.groovyscript.documentation.Documentation;
import com.google.common.collect.ComparisonChain;
import net.minecraft.client.resources.I18n;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * This is used to create headings on the wiki index page.
 * <p>
 * Contains header text (which will be formatted as h2 via {@link #get()}, but can be configured {@link #get(int)}),
 * an optional normal text comment below it, and some number of entries.
 * The comment is a function that can be formatted based on the number of entries.
 * <p>
 * It will only be added to the index file if there is
 * at least one entry to display,
 * and the order it will be added to the index file is based on its priority.
 *
 * @see LinkIndex
 */
public class Heading implements Comparable<Heading> {

    public static final int DEFAULT_PRIORITY = 1000;

    private final String text;
    private final Function<Heading, String> comment;
    private final List<String> entries;
    private final int priority;

    /**
     * @param text the header text of the section
     * @see #Heading(String, Function, int) Section
     */
    public Heading(String text) {
        this(text, null, DEFAULT_PRIORITY);
    }

    public Heading(String text, String comment) {
        this(text, x -> comment, DEFAULT_PRIORITY);
    }

    /**
     * Calls {@link #Heading(String, Function, int)} with a priority of {@link Heading#DEFAULT_PRIORITY}.
     *
     * @param text  the header text of the section
     * @param comment function that creates the comment based on the number of entries
     * @see #Heading(String, Function, int) Section
     */
    public Heading(String text, Function<Heading, String> comment) {
        this(text, comment, DEFAULT_PRIORITY);
    }

    /**
     * @param text   the header text of the section
     * @param comment  function that creates the comment based on the number of entries
     * @param priority the priority this Section has on the index page for sorting purposes
     */
    public Heading(String text, Function<Heading, String> comment, int priority) {
        this.text = text;
        this.comment = comment;
        this.entries = new ArrayList<>();
        this.priority = priority;
    }

    public static Heading containerIndex(ContainerHolder container) {
        var header = LangHelper.fallback(String.format("groovyscript.wiki.%s.index.title", container.id()), container.name());
        var comment = LangHelper.fallback(String.format("groovyscript.wiki.%s.index.description", container.id()), null);
        return new Heading(I18n.format(header), comment == null ? null : md -> I18n.format(comment, md.entries.size()));
    }

    public static Heading fromContainer(ContainerHolder container) {
        var header = LangHelper.fallback(String.format("groovyscript.wiki.%s.index.subtitle", container.id()), "groovyscript.wiki.categories");
        var comment = LangHelper.fallback(String.format("groovyscript.wiki.%s.index.subtitle.comment", container.id()), "groovyscript.wiki.subcategories_count");
        return fromI18n(header, comment);
    }

    public static Heading fromI18n(String header, String comment) {
        return new Heading(I18n.format(header), md -> I18n.format(comment, md.entries.size()));
    }

    /**
     * @param entry typically a markdown bullet point link
     */
    public void addEntry(String entry) {
        this.entries.add(entry);
    }

    public boolean hasEntries() {
        return !entries.isEmpty();
    }

    public List<String> getEntries() {
        return entries;
    }

    public String get() {
        return get(2);
    }

    public String get(int headingLevel) {
        var sb = new StringBuilder();
        sb.append(Documentation.DEFAULT_FORMAT.header(headingLevel, text)).append("\n\n");
        if (comment != null) {
            var sub = comment.apply(this);
            if (!sub.isEmpty()) sb.append(sub).append("\n\n");
        }
        entries.forEach(entry -> sb.append(entry).append("\n\n"));
        return sb.toString();
    }

    @Override
    public int compareTo(@NotNull Heading o) {
        return ComparisonChain.start()
                .compare(priority, o.priority)
                .compare(text, o.text)
                .result();
    }
}
