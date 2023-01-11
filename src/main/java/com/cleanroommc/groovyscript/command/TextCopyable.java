package com.cleanroommc.groovyscript.command;

import net.minecraft.util.text.*;
import net.minecraft.util.text.event.HoverEvent;

public class TextCopyable {

    public static Builder string(java.lang.String copyText, java.lang.String msg) {
        return new Builder(copyText, msg);
    }

    public static Builder translation(java.lang.String copyText, java.lang.String msg, Object... args) {
        return new Builder(copyText, msg).translate(args);
    }

    public static class Builder {
        private final java.lang.String copyText;
        private final java.lang.String msg;
        private Object[] args;
        private ITextComponent hoverMsg;

        public Builder(java.lang.String copyText, java.lang.String msg) {
            this.copyText = TextFormatting.getTextWithoutFormattingCodes(copyText);
            this.msg = msg;
        }

        public Builder translate(Object... args) {
            this.args = args;
            return this;
        }

        public ITextComponent build() {
            Style style = new Style();
            if (hoverMsg == null) {
                hoverMsg = new TextComponentTranslation("groovyscript.command.copy.hover")
                        .appendSibling(new TextComponentString(" " + copyText).setStyle(new Style().setColor(TextFormatting.GOLD)));
            }
            style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMsg));
            style.setClickEvent(CustomClickAction.makeCopyEvent(copyText));
            ITextComponent textComponent;
            if (args == null)
                textComponent = new TextComponentString(msg);
            else
                textComponent = new TextComponentTranslation(msg, args);
            textComponent.setStyle(style);
            return textComponent;
        }
    }
}
