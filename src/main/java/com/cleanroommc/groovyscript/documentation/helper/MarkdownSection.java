package com.cleanroommc.groovyscript.documentation.helper;

import com.google.common.collect.ComparisonChain;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

/**
 * This is used to create markdown sections on the wiki index page.
 * <p>
 * Contains a header (which will be formatted as h2 via {@link #get()}, but can be configured {@link #get(int)}),
 * an optional normal text comment below it, and some number of entries.
 * The comment is a function that can be formatted based on the number of entries.
 * <p>
 * It will only be added to the index file if there is
 * at least one entry to display,
 * and the order it will be added to the index file is based on its priority.
 *
 * @see LinkIndex
 */
public class MarkdownSection implements Comparable<MarkdownSection> {

    public static final int DEFAULT_PRIORITY = 1000;

    private final String header;
    private final IntFunction<String> comment;
    private final List<String> entries;
    private final int priority;

    /**
     * Calls {@link #MarkdownSection(String, IntFunction, int)} with a priority of {@link MarkdownSection#DEFAULT_PRIORITY}.
     *
     * @param header  the header text of the section
     * @param comment function that creates the comment based on the number of entries
     * @see #MarkdownSection(String, IntFunction, int) Section
     */
    public MarkdownSection(String header, IntFunction<String> comment) {
        this(header, comment, DEFAULT_PRIORITY);
    }

    /**
     * @param header   the header text of the section
     * @param comment  function that creates the comment based on the number of entries
     * @param priority the priority this Section has on the index page for sorting purposes
     */
    public MarkdownSection(String header, IntFunction<String> comment, int priority) {
        this.header = header;
        this.comment = comment;
        this.entries = new ArrayList<>();
        this.priority = priority;

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

    public String get(int headerLevel) {
        var sb = new StringBuilder();
        sb.append(StringUtils.repeat('#', headerLevel));
        sb.append(" ").append(header).append("\n\n");
        var sub = comment.apply(entries.size());
        if (!sub.isEmpty()) sb.append(sub).append("\n\n");
        entries.forEach(entry -> sb.append(entry).append("\n\n"));
        return sb.toString();
    }

    @Override
    public int compareTo(@NotNull MarkdownSection o) {
        return ComparisonChain.start()
                .compare(priority, o.priority)
                .compare(header, o.header)
                .result();
    }
}
