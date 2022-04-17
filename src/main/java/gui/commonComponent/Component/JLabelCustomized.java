package gui.commonComponent.Component;

import global.GlobalCte;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.TreeMap;

public class JLabelCustomized extends JLabel {
    private final GlobalCte.typeData typeData;
    private final ConfigColor configColor;

    public JLabelCustomized(GlobalCte.typeData typeData) {
        super();
        configColor = new ConfigColor();
        this.typeData = typeData;
        NumberFormat format = new DecimalFormat("### ###.### ###");
        format.setMaximumFractionDigits(10);
        format.setMinimumFractionDigits(8);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Currency.class);
        formatter.setAllowsInvalid(false);
        formatter.setOverwriteMode(true);
        this.setHorizontalAlignment(SwingConstants.RIGHT);
    }

    public void configColorRange(Double valueMax, Color color) {
        this.configColor.addConfig(valueMax, color);
    }

    public void removeColorRange() {
        this.configColor.removeColors();
    }


    public void setValue(Object text) {
        Double data;

        switch (this.typeData) {
            case Date:
                this.setText((String) text);
                break;
            case Quantite:
                data = Double.parseDouble(text.toString());
                this.setForeground(configColor.getColor(data));
                this.setText(NumberFormat.getNumberInstance().format(data));
                break;
            case Pourcentage:
                data = Double.parseDouble(text.toString());
                this.setForeground(configColor.getColor(data));
                this.setText(NumberFormat.getPercentInstance().format(data));
                break;
            case Montant:
                Locale locale = new Locale("fr", "FR");
                data = Double.parseDouble(text.toString());
                this.setForeground(configColor.getColor(data));
                this.setText(NumberFormat.getCurrencyInstance(locale).format(text));
                break;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Class permettant de traiter la couleur du Label en fonction de paramètre
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private class ConfigColor {
        private TreeMap<Double, Color> colors;

        private ConfigColor() {
            this.removeColors();
        }

        private Color getColor(Double value) {
            return colors.floorEntry(value).getValue();
        }

        private void addConfig(Double value, Color color) {
            this.colors.put(value, color);
        }

        public void removeColors() {
            colors = new TreeMap<Double, Color>();
            colors.put(-999999999d, GlobalCte.colorDARK_BLACK);
        }


    }


}
