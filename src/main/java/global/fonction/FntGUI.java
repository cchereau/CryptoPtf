package global.fonction;

import java.awt.*;

public class FntGUI {
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void setBagContraint(GridBagConstraints gbc, int x, int y, int width, int height) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
    }

    public static void setBagContraint(GridBagConstraints gbc, int x, int y, int width, int height, float weightx, float weighty) {
        setBagContraint(gbc, x, y, width, height);
        gbc.weightx = weightx;
        gbc.weighty = weighty;
    }

}
