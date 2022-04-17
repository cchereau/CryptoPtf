package gui.screenStrategie;

import global.EnumCrypto;
import global.fonction.FntGUI;
import ptfAnalyse.ptfStrategieOld.StrategieParam;

import javax.swing.*;
import java.awt.*;

public class JPanelSimulationParam extends JPanel {

    public JPanelSimulationParam(JInternalFrameStrategieStrategieAction parent, StrategieParam param) {
        super(new GridBagLayout());
        JComboBox<EnumCrypto.enPeriodePerformance> cmbPerformance = new JComboBox<>(EnumCrypto.enPeriodePerformance.values());
        JComboBox<EnumCrypto.enPeriodePerformance> cmbTransactionLatence = new JComboBox<>(EnumCrypto.enPeriodePerformance.values());
        JSlider sliderPerformance = new JSlider(JSlider.HORIZONTAL, 0, 30, 10);
        sliderPerformance.setMinorTickSpacing(5);
        sliderPerformance.setMajorTickSpacing(10);
        sliderPerformance.setPaintTicks(true);
        sliderPerformance.setPaintLabels(true);

        JSlider sliderToleranceAverage = new JSlider(JSlider.HORIZONTAL, 0, 50, 10);
        sliderToleranceAverage.setMinorTickSpacing(5);
        sliderToleranceAverage.setMajorTickSpacing(10);
        sliderToleranceAverage.setPaintTicks(true);
        sliderToleranceAverage.setPaintLabels(true);

        GridBagConstraints gbc = new GridBagConstraints();
        // Libelle de l'action sur la position
        gbc.fill = GridBagConstraints.HORIZONTAL;

        FntGUI.setBagContraint(gbc, 0, 0, 1, 1, 0.25f, 0.75f);
        this.add(new JLabel("Perfromance"), gbc);
        FntGUI.setBagContraint(gbc, 1, 0, 1, 1, 0.25f, 0.75f);
        this.add(cmbPerformance, gbc);

        FntGUI.setBagContraint(gbc, 2, 0, 1, 1, 0.25f, 0.75f);
        this.add(new Label("Performance Seuil"), gbc);
        FntGUI.setBagContraint(gbc, 3, 0, 3, 1, 0.25f, 0.75f);
        this.add(sliderPerformance, gbc);

        FntGUI.setBagContraint(gbc, 0, 1, 1, 1, 0.25f, 0.75f);
        this.add(new JLabel("Latence Performance"), gbc);
        FntGUI.setBagContraint(gbc, 1, 1, 1, 1, 0.25f, 0.75f);
        this.add(cmbTransactionLatence, gbc);

        FntGUI.setBagContraint(gbc, 2, 1, 1, 1, 0.25f, 0.75f);
        this.add(new JLabel("Ptf Average Tolerance"), gbc);
        FntGUI.setBagContraint(gbc, 3, 1, 1, 1, 0.25f, 0.75f);
        this.add(sliderToleranceAverage, gbc);

        /////////////////////////////////////////////////////////////////////////////////////////////
        // Initialisation de la stratégie Param
        /////////////////////////////////////////////////////////////////////////////////////////////
        cmbPerformance.setSelectedItem(param.getPeriodePerformance());
        cmbTransactionLatence.setSelectedItem(param.getPeriodeWhitoutTransaction());
        sliderPerformance.setValue(param.getPerformanceMinimulToArbitrate());
        sliderToleranceAverage.setValue(param.getTolerancePositionInPtf());

        ///////////////////////////////////////////////////////////////////////////////////////////////
        // Ajout des lsitner
        //////////////////////////////////////////////////////////////////////////////////////////////
        cmbPerformance.addActionListener(e -> parent.setPerformance(cmbPerformance.getItemAt(cmbPerformance.getSelectedIndex())));
        cmbTransactionLatence.addActionListener(e -> parent.setTransactionLatence(cmbTransactionLatence.getItemAt(cmbTransactionLatence.getSelectedIndex())));
        sliderPerformance.addChangeListener(e -> parent.setPerformanceValue(sliderPerformance.getValue()));
        sliderToleranceAverage.addChangeListener(e -> parent.setPortfolioAverageTolerance(sliderToleranceAverage.getValue()));
    }

}
