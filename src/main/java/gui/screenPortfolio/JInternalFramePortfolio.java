package gui.screenPortfolio;

import global.GlobalData;
import ptfManagement.Portefeuille;

import javax.swing.*;
import java.awt.*;

public class JInternalFramePortfolio extends JInternalFrame implements ListnerChangePtf {

    private final JPanelPortfolioSynthesePL panelPortfoliofSynthesePL = new JPanelPortfolioSynthesePL();
    private final JPanelPortfolioPositionsTable panelPortfolioPositions = new JPanelPortfolioPositionsTable();
    private final JPanelPortfolioStrategiePrice panelPortfolioStrategie = new JPanelPortfolioStrategiePrice();

    public JInternalFramePortfolio() {
        super();
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Position", panelPortfolioPositions);
        tabbedPane.add("Synthese P&L", panelPortfoliofSynthesePL);
        tabbedPane.add("Strategie", panelPortfolioStrategie);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Position de la fenêtre
        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        JPanelPortfolioDescription panelPortfolioDescription = new JPanelPortfolioDescription();
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelPortfolioDescription, tabbedPane);
        mainSplitPane.setResizeWeight(0.1d);
        mainSplitPane.setSize(new Dimension());

        // initialisation des listner
        panelPortfolioDescription.setListenerChangePtf(this);
        panelPortfolioDescription.setPortfeuille(GlobalData.portfeuilles.firstKey());

        // intilisation des contrôle
        this.add(mainSplitPane);
        this.setEnabled(true);
        this.pack();
    }

    @Override
    public void OnPtfChanged(Portefeuille ptf) {
        this.panelPortfolioPositions.OnPtfChanged(ptf);
        this.panelPortfoliofSynthesePL.OnPtfChanged(ptf);
        this.panelPortfolioStrategie.OnPtfChanged(ptf);
    }


}


