package com.hisun.codeassistant.codecompletions;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class InlayInlineElementRenderer extends InlayElementRenderer{
    protected InlayInlineElementRenderer(String inlayText) {
        super(inlayText);
    }

    @Override
    public void paint(
            @NotNull Inlay inlay,
            @NotNull Graphics2D g,
            @NotNull Rectangle2D targetRegion,
            @NotNull TextAttributes textAttributes) {
        Editor editor = inlay.getEditor();
        Font font = editor.getColorsScheme()
                .getFont(EditorFontType.PLAIN)
                .deriveFont(Font.ITALIC);
        g.setFont(font);
        g.setColor(JBColor.GRAY);
        g.drawString(
                inlayText,
                (int) targetRegion.getX(),
                (int) targetRegion.getY() + editor.getAscent());
    }
}
