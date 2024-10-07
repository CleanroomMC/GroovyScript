package com.cleanroommc.groovyscript.compat.vanilla.command.infoparser;

import com.cleanroommc.groovyscript.api.infocommand.InfoParser;
import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.command.TextCopyable;
import com.cleanroommc.groovyscript.helper.StyleConstant;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class GenericInfoParser<T> implements InfoParser {

    @Override
    public int priority() {
        return 100;
    }

    /**
     * The name of the parser to display in-game. Should be capitalized
     * and in the singular form, see {@link #plural()}.
     *
     * @return the name of the parser
     */
    public abstract String name();

    /**
     * The name of the parser to display in-game if the number of entries is greater than 1.
     * Defaults to {@link #name()}s.
     *
     * @return the plural form of the name
     */
    public String plural() {
        return name() + "s";
    }


    /**
     * A lang key for the text that appears when hovering over the name in chat.
     * Defaults to "{@code groovyscript.infoparser.}{@link #id()}"
     *
     * @return the lang key for the hover text
     */
    public String description() {
        return String.format("groovyscript.infoparser.%s", id());
    }

    /**
     * The text that appears when hovering over the name in chat.
     * Typically, a description of the command, with a fallback value of the name.
     *
     * @return the hover text
     * @see #description()
     */
    public ITextComponent hoverTitle() {
        return new TextComponentTranslation(description());
    }

    /**
     * The formatted header of the parser. Uses the {@link StyleConstant#TITLE_STYLE}, with hover text from {@link #hoverTitle()}.
     *
     * @param plural if the name should be in {@link #plural()} or singular {@link #name()} form.
     * @return the header for the parser
     */
    public ITextComponent header(boolean plural) {
        String name = plural ? plural() : name();
        Style style = StyleConstant.TITLE_STYLE.createShallowCopy().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverTitle()));
        return new TextComponentString(name + ":").setStyle(style);
    }

    /**
     * Adds the header to the message list.
     *
     * @see #header(boolean)
     */
    public void header(List<ITextComponent> messages, boolean plural) {
        messages.add(header(plural));
    }

    /**
     * Combines the default {@link #msg} and {@link #copyText} code.
     *
     * @param entry     the entry to be parsed
     * @param colored   if the text should be colored or not
     * @param prettyNbt if nbt data, if any, should be formatted "prettily"
     * @return the message that appears in chat and is copied, depending on the value of {@code colored}
     */
    public abstract String text(@NotNull T entry, boolean colored, boolean prettyNbt);

    /**
     * The text that will display in chat.
     * The primary difference from {@link #copyText} is containing formatting codes.
     * Typically valid GroovyScript code.
     *
     * @param entry     the entry to be parsed
     * @param prettyNbt if nbt data, if any, should be formatted "prettily"
     * @return the message that appears in chat
     */
    public String msg(@NotNull T entry, boolean prettyNbt) {
        return text(entry, true, prettyNbt);
    }

    /**
     * The text that will be copied when the entry is clicked on.
     * The primary difference from {@link #msg} is lacking formatting codes.
     * Typically valid GroovyScript code.
     *
     * @param entry     the entry to be parsed
     * @param prettyNbt if nbt data, if any, should be formatted "prettily"
     * @return the text that is copied when clicking on the message
     */
    public String copyText(@NotNull T entry, boolean prettyNbt) {
        return text(entry, false, prettyNbt);
    }

    /**
     * The copyable component that will be added to the message list.
     *
     * @param copyText the text that is copied when clicking on the message
     * @param msg      the message that appears in chat
     * @return the chat component
     */
    public ITextComponent information(String copyText, String msg) {
        return TextCopyable.string(copyText, msg).build();
    }

    /**
     * The copyable component that will be added to the message list.
     * Calls {@link #information(String, String)} with the values from {@link #copyText(Object, boolean)} and {@link #msg(Object, boolean)}.
     * Also adds a {@literal - } to the message for formatting reasons.
     *
     * @param entry     the entry to be parsed
     * @param prettyNbt if nbt data, if any, should be formatted "prettily"
     * @return the chat component
     */
    public ITextComponent information(@NotNull T entry, boolean prettyNbt) {
        return information(copyText(entry, prettyNbt), " - " + msg(entry, prettyNbt));
    }

    /**
     * Iterates through the entries, adding them to the message list as it goes.
     *
     * @param messages  the message list that will be printed to chat
     * @param entries   a list of the entries to print
     * @param prettyNbt if nbt data, if any, should be formatted "prettily"
     */
    public void iterate(List<ITextComponent> messages, @NotNull Iterator<T> entries, boolean prettyNbt) {
        while (entries.hasNext()) messages.add(information(entries.next(), prettyNbt));
    }

    /**
     * Adds the {@link #header(List, boolean)} and {@link #iterate(List, Iterator, boolean)} to the message list.
     * Only does so if the list isn't empty.
     *
     * @param messages  the message list that will be printed to chat
     * @param entries   a list of the entries to print
     * @param prettyNbt if nbt data, if any, should be formatted "prettily"
     */
    public void add(List<ITextComponent> messages, @NotNull Collection<T> entries, boolean prettyNbt) {
        if (entries.isEmpty()) return;
        header(messages, entries.size() != 1);
        iterate(messages, entries.iterator(), prettyNbt);
    }

    /**
     * Wraps the entry in a singleton list for {@link #add(List, Collection, boolean)}.
     *
     * @see #add(List, Collection, boolean)
     */
    public void add(List<ITextComponent> messages, @NotNull T entry, boolean prettyNbt) {
        add(messages, Collections.singletonList(entry), prettyNbt);
    }

    /**
     * Parses the {@link InfoParserPackage}, and runs {@link #add} if it passes validation.
     *
     * @param info the info package, containing all the information of the command
     * @see InfoParserPackage
     */
    public abstract void parse(InfoParserPackage info);

    /**
     * Determines if the parser will be prevented from running.
     * Simply checks if {@code -}{@link #id()} is in the args list.
     *
     * @param args list of arguments passed into the command
     * @return if the parser is prevented from being displayed
     */
    public boolean blocked(List<String> args) {
        return args.contains("-" + id());
    }

    /**
     * Determines if the parser will be run.
     * Simply checks if {@link #id()} is in the args list.
     *
     * @param args list of arguments passed into the command
     * @return if the parser is allowed to be displayed
     */
    public boolean allowed(List<String> args) {
        return args.contains(id());
    }

    @Override
    public void parse(InfoParserPackage info, boolean enabled) {
        if (blocked(info.getArgs())) return;
        if (enabled || allowed(info.getArgs())) parse(info);
    }

}
