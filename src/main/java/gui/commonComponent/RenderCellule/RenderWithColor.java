package gui.commonComponent.RenderCellule;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class RenderWithColor extends DefaultTableCellRenderer {
    private final boolean colorFont;

    public RenderWithColor(boolean colorFont) {
        setHorizontalAlignment(SwingConstants.RIGHT);
        this.colorFont = colorFont;
    }

    @Override
    public Component getTableCellRendererComponent(JTable aTable, Object aNumberValue, boolean aIsSelected,
                                                   boolean aHasFocus, int aRow, int aColumn) {

        if (aNumberValue == null) return this;
        Component renderer = super.getTableCellRendererComponent(aTable, aNumberValue, aIsSelected, aHasFocus, aRow, aColumn);
        Number value = (Number) aNumberValue;
        if (colorFont)
            if (value.doubleValue() < 0) {
                renderer.setForeground(Color.red.darker());
            } else {
                renderer.setForeground(Color.GREEN.darker());
            }
        return this;
    }

}
