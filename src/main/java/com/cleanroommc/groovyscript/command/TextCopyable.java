package com.cleanroommc.groovyscript.command;

import com.cleanroommc.groovyscript.helper.StyleConstant;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.HoverEvent;

public class TextCopyable {

    public static Builder string(String copyText, String msg) {
        return new Builder(copyText, msg);
    }

    public static Builder translation(String copyText, String msg, Object... args) {
        return new Builder(copyText, msg).translate(args);
    }

    public static class Builder {

        private final String copyText;
        private final String msg;
        private Object[] args;
        private ITextComponent hoverMsg;

        public Builder(String copyText, String msg) {
            this.copyText = copyText;
            this.msg = msg;
        }

        public Builder translate(Object... args) {
            this.args = args;
            return this;
        }

        public ITextComponent build() {
            Style style = new Style();
            if (hoverMsg == null) {
                hoverMsg = new TextComponentTranslation("groovyscript.command.copy.hover").setStyle(StyleConstant.getEmphasisStyle())
                        .appendSibling(new TextComponentString(" " + copyText));
            }
            style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMsg));
            style.setClickEvent(CustomClickAction.makeCopyEvent(TextFormatting.getTextWithoutFormattingCodes(copyText)));
            ITextComponent textComponent = args == null ? new TextComponentString(msg) : new TextComponentTranslation(msg, args);
            textComponent.setStyle(style);
            return textComponent;
        }
    }
}
