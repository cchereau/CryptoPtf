/*
//http://www.javapractices.com/topic/TopicAction.do?Id=168
 */

package gui.commonComponent.RenderCellule;

import java.text.NumberFormat;

public final class RenderPercentageWithColor extends RenderWithColor {

    //private final boolean colorFont;


    public RenderPercentageWithColor(boolean colorFont) {
        super(colorFont);
    }

    @Override
    public void setValue(Object aValue) {
        Object result = aValue;
        if ((aValue instanceof Number)) {
            Number numberValue = (Number) aValue;
            NumberFormat formatter = NumberFormat.getPercentInstance();
            formatter.setMaximumFractionDigits(2);
            formatter.setMinimumFractionDigits(2);
            result = formatter.format(numberValue.doubleValue());
        }
        super.setValue(result);
    }

}