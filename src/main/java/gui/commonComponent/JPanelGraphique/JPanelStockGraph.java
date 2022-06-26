package gui.commonComponent.JPanelGraphique;

import global.EnumCrypto.enPeriodePerformance;
import global.EnumCrypto.enTypeQuotation;
import global.fonction.FntGUI;
import stock.Stock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public class JPanelStockGraph extends JPanel {
    private final JComboBox<enPeriodePerformance> cmbPeriod = new JComboBox<>(enPeriodePerformance.values());
    private final JCheckBox checkBoxMM20 = new JCheckBox("Moyenne Mobile 20j", false);
    private final JCheckBox checkBoxMM50 = new JCheckBox("Moyenne Mobile 50j", false);
    private final Boolean isCandel;
    private final JAbstractGraph panelGraph;
    private final JLabel lblSelectionPeriod;
    private Stock stock;

    public JPanelStockGraph(Boolean isCandel) {
        super(new BorderLayout());

        this.isCandel = isCandel;
        ////////////////////////////////////////////////////////////////////////
        // Panel de selection des combo box
        ////////////////////////////////////////////////////////////////////////
        JPanel panelSelection = new JPanel();
        panelSelection.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.VERTICAL;
        FntGUI.setBagContraint(gbc, 0, 0, 1, 1);
        lblSelectionPeriod = new JLabel("Période");
        panelSelection.add(lblSelectionPeriod, gbc);
        FntGUI.setBagContraint(gbc, 1, 0, 1, 1);
        panelSelection.add(cmbPeriod, gbc);
        FntGUI.setBagContraint(gbc, 0, 1, 1, 1);
        panelSelection.add(checkBoxMM20, gbc);
        FntGUI.setBagContraint(gbc, 1, 1, 1, 1);
        panelSelection.add(checkBoxMM50, gbc);
        cmbPeriod.setSelectedItem(enPeriodePerformance.MONTH);

        ////////////////////////////////////////////////////////////////////////
        // Ajout des listners
        ////////////////////////////////////////////////////////////////////////
        checkBoxMM20.addActionListener(this::OnActionCheckBox);
        checkBoxMM20.setName("20");
        checkBoxMM50.addActionListener(this::OnActionCheckBox);
        checkBoxMM50.setName("50");
        cmbPeriod.addActionListener(this::OnActionPeriode);

        ////////////////////////////////////////////////////////////////////////
        // AJout du composant Graphique
        ////////////////////////////////////////////////////////////////////////
        if (isCandel)
            panelGraph = new JGraphOHCL();
        else
            panelGraph = new JGraphChart();

        this.add(panelSelection, BorderLayout.NORTH);
        this.add(panelGraph, BorderLayout.CENTER);
    }

    public void setPeriode(Object periodePerformance) {
        this.cmbPeriod.setSelectedItem(periodePerformance);
    }

    public void setPeriodeComboVisible(Boolean isVisible) {
        this.cmbPeriod.setVisible(isVisible);
        this.lblSelectionPeriod.setVisible(isVisible);
    }

    private void OnActionPeriode(ActionEvent actionEvent) {
        enPeriodePerformance periodePerformance = cmbPeriod.getItemAt(cmbPeriod.getSelectedIndex());
        try {

            if (this.stock.isUpdateAuto()) {
                LocalDate datFin = this.stock.getQuotation(enTypeQuotation.close).lastKey();
                LocalDate datDeb = global.fonction.FntDates.getDateFromPeriod(datFin, periodePerformance, true);
                panelGraph.drawSeries(datDeb, datFin);
            }
        } catch (NullPointerException | NoSuchElementException e) {
            System.out.println(getClass() + "-" + getUIClassID() + "OnActionPeriode" + "- " + e.getMessage());
        }
    }

    public void addSeries(Stock stock) {
        this.stock = stock;
        if (isCandel) {
            // serie de type OHLC
            panelGraph.addSeries(enTypeQuotation.open.toString(), this.stock.getQuotation(enTypeQuotation.open));
            panelGraph.addSeries(enTypeQuotation.high.toString(), this.stock.getQuotation(enTypeQuotation.high));
            panelGraph.addSeries(enTypeQuotation.low.toString(), this.stock.getQuotation(enTypeQuotation.low));
            panelGraph.addSeries(enTypeQuotation.close.toString(), this.stock.getQuotation(enTypeQuotation.close));
            panelGraph.addSeries(enTypeQuotation.volume.toString(), this.stock.getQuotation(enTypeQuotation.volume));
        } else
            panelGraph.addSeries(this.stock.getNom(), stock.getQuotation(enTypeQuotation.close));

        cmbPeriod.setSelectedIndex(cmbPeriod.getSelectedIndex());
    }

    private void OnActionCheckBox(ActionEvent actionEvent) {
        JCheckBox checkBox = (JCheckBox) actionEvent.getSource();
        String name = "MM " + checkBox.getName() + " " + this.stock.getNom();
        if (checkBox.isSelected()) {
            TreeMap<LocalDate, Double> data;
            data = global.fonction.FntFinancial.getMoyenneMobile(this.stock.getQuotation(enTypeQuotation.close), Integer.parseInt(checkBox.getName()));
            panelGraph.addSeries(name, data);
        } else
            panelGraph.removeSerie(false, name);
        cmbPeriod.setSelectedIndex(cmbPeriod.getSelectedIndex());
    }

    public void removeSeries() {
        checkBoxMM20.setSelected(false);
        checkBoxMM50.setSelected(false);
        panelGraph.removeSerie(true, null);

    }
}
