/*
//http://www.javapractices.com/topic/TopicAction.do?Id=168
 */

package gui.commonComponent.RenderCellule;

import javax.swing.*;
import java.text.NumberFormat;
import java.util.Locale;

public final class RenderCurrencyWithColor extends RenderWithColor {
    public RenderCurrencyWithColor(boolean colorFont) {
        super(colorFont);
        setHorizontalAlignment(SwingConstants.RIGHT);
    }

    @Override
    public void setValue(Object aValue) {
        Object result = aValue;
        if ((aValue instanceof Number)) {
            Number numberValue = (Number) aValue;
            NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.FRANCE);
            formatter.setMaximumFractionDigits(8);
            formatter.setMinimumFractionDigits(2);
            result = formatter.format(numberValue.doubleValue());
        }
        super.setValue(result);
    }

}