package gui.screenStrategie;

import global.EnumCrypto;
import global.GlobalData;
import global.fonction.FntGUI;
import gui.commonComponent.JPanelGraphique.JPanelStockGraph;
import gui.commonComponent.JPanelTable.Strategie.JPanelTableActionOnPosition;
import gui.commonComponent.JPanelTable.Strategie.ListnerStrategieAction;
import ptfAnalyse.ptfStrategieOld.Strategie;
import ptfAnalyse.ptfStrategieOld.StrategieParam;
import ptfManagement.Portefeuille;
import stock.StockComposite;

import javax.swing.*;
import java.awt.*;
import java.util.TreeMap;

public class JInternalFrameStrategieStrategieAction extends JFrame implements ListnerStrategieAction {

    private final StrategieParam strategieParam;
    private final TreeMap<String, Portefeuille> portefeuilleTreeMap;
    private final JPanelTableActionOnPosition panelTableActionOnPosition;
    private final JComboBox<?> comboPtf;
    private final JPanelStockGraph panelJPanelStockGraphGraphique;

    public JInternalFrameStrategieStrategieAction(TreeMap<String, Portefeuille> portefeuilles) {
        super("Strategie Portefeuille");

        portefeuilleTreeMap = portefeuilles;
        strategieParam = new StrategieParam();
        panelTableActionOnPosition = new JPanelTableActionOnPosition();
        comboPtf = new JComboBox<>(portefeuilleTreeMap.keySet().toArray());

        // CReation d'un JPanel pour la selection du portrfeuille
        JPanel panelPtf = new JPanel();
        panelPtf.setLayout(new GridBagLayout());
        GridBagConstraints gbcPanel = new GridBagConstraints();

        FntGUI.setBagContraint(gbcPanel, 0, 0, 1, 1);
        panelPtf.add(new JLabel("Portefeuille"), gbcPanel);
        FntGUI.setBagContraint(gbcPanel, 1, 0, 1, 1);
        panelPtf.add(comboPtf, gbcPanel);

        // cration du JPanel des paramtétrage
        JPanelSimulationParam panelSimulationParam = new JPanelSimulationParam(this, strategieParam);

        // creation du JPanel du graphic
        this.panelJPanelStockGraphGraphique = new JPanelStockGraph(false);

        // creation du JPanel Final et de l'affichage
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Libelle de l'action sur la position
        FntGUI.setBagContraint(gbc, 0, 0, 1, 1, 0.1f, 0.1f);
        this.add(panelPtf, gbc);

        FntGUI.setBagContraint(gbc, 0, 1, 1, 1, 0.1f, 0.2f);
        this.add(panelSimulationParam, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        FntGUI.setBagContraint(gbc, 0, 2, 1, 2, 0.1f, 0.3f);
        this.add(this.panelTableActionOnPosition, gbc);

        FntGUI.setBagContraint(gbc, 0, 4, 1, 3, 0.1f, 0.4f);
        this.add(this.panelJPanelStockGraphGraphique, gbc);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Ajout des listners
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        this.panelTableActionOnPosition.setListenerLineSelected(this);
        comboPtf.setSelectedItem(0);
        computeStrategie();

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        //this.setPreferredSize(new Dimension(1800, 1000));
        this.pack();
        this.setLocationRelativeTo(this.getParent());
        this.setAlwaysOnTop(false);
        this.pack();

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Ajout des Listners
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void onLineSelected() {
        this.panelJPanelStockGraphGraphique.removeSeries();
        String instFrom = panelTableActionOnPosition.instrumentSelected[0];
        String instTo = panelTableActionOnPosition.instrumentSelected[1];
        StockComposite stockComposite = new StockComposite(GlobalData.stocks.getStock(instFrom), GlobalData.stocks.getStock(instTo));
        this.panelJPanelStockGraphGraphique.addSeries(stockComposite);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fonction de création de la stratégie
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void computeStrategie() {
        Portefeuille ptf = portefeuilleTreeMap.get(comboPtf.getItemAt(comboPtf.getSelectedIndex()));
        Strategie strategie = new Strategie(ptf, this.strategieParam);
        panelTableActionOnPosition.addData(strategie.getActions());
    }

    public void setPerformance(EnumCrypto.enPeriodePerformance periodPerformance) {
        strategieParam.setPeriodePerformance(periodPerformance);
        computeStrategie();
    }

    public void setTransactionLatence(EnumCrypto.enPeriodePerformance periodLatence) {
        strategieParam.setPeriodWithoutTransaction(periodLatence);
        computeStrategie();
    }

    public void setPerformanceValue(int value) {
        strategieParam.setPerformanceMinimumToArbitrate(value);
        computeStrategie();
    }

    public void setPortfolioAverageTolerance(int value) {
        strategieParam.setTolerancePositionInPtf(value);
        computeStrategie();
    }

}

