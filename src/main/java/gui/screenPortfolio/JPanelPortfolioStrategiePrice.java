package gui.screenPortfolio;

import global.EnumCrypto.enActionOnPosition;
import global.EnumCrypto.enPeriodePerformance;
import global.EnumCrypto.enTypeQuotation;
import global.fonction.FntGUI;
import gui.screenPortfolio.Strategie.JPanelPriceEvolution;
import gui.screenPortfolio.Strategie.JPanelPriceEvolutionTable;
import gui.screenPortfolio.Strategie.ListnerPriceEvolution;
import ptfAnalyse.ptfStrategieOld.Strategie;
import ptfAnalyse.ptfStrategieOld.StrategieParam;
import ptfAnalyse.ptfStrategieOld.StrategyAction;
import ptfManagement.Portefeuille;
import stock.Stock;
import stock.StockComposite;

import javax.swing.*;
import java.awt.*;

public class JPanelPortfolioStrategiePrice extends JPanel implements ListnerChangePtf, ListnerPriceEvolution {

    private final JPanelPriceEvolutionTable panelPriceEvolutionTable = new JPanelPriceEvolutionTable();
    private final JPanelPriceEvolution panelPriceEvolution = new JPanelPriceEvolution();
    private final JComboBox<enPeriodePerformance> cmbPerformance = new JComboBox<>(enPeriodePerformance.values());
    private Stock stockFrom, stockTo;
    private StockComposite stockComposite;

    public JPanelPortfolioStrategiePrice() {

        super(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        FntGUI.setBagContraint(gbc, 0, 0, 1, 1, 1, 0.1f);
        cmbPerformance.addActionListener(e -> refresh());
        this.add(cmbPerformance, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        FntGUI.setBagContraint(gbc, 0, 1, 1, 1, 1, 0.3f);
        this.add(panelPriceEvolutionTable, gbc);

        FntGUI.setBagContraint(gbc, 0, 2, 1, 1, 1, 0.6f);
        this.add(panelPriceEvolution, gbc);

        // ajout du listner
        panelPriceEvolutionTable.setListenerLineSelected(this);

        // paramétrage par defaut des données
        //this.cmbPerformance.setSelectedItem(enPeriodePerformance.MONTH);
    }

    //    @Override
    public void OnCellSelected(Stock from, Stock to) {
        stockFrom = from;
        stockTo = to;
        stockComposite = new StockComposite(stockFrom, stockTo);
        stockComposite.getQuotations();
        refresh();
    }

    public void refresh() {
        if ((stockFrom != null) || (stockTo != null))
            panelPriceEvolution.setStocks(stockFrom, stockTo, stockComposite, this.cmbPerformance.getItemAt(this.cmbPerformance.getSelectedIndex()));
    }


    @Override
    public void OnPtfChanged(Portefeuille ptf) {
        ////////////////////////////////////////////////////////////////////////////////////
        // AJOUT DES LISTNER
        ////////////////////////////////////////////////////////////////////////////////////
        // Application de la stratégie en entrée
        StrategieParam strategieParam = new StrategieParam();
        strategieParam.setPeriodePerformance(cmbPerformance.getItemAt(cmbPerformance.getSelectedIndex()));
        strategieParam.setPeriodWithoutTransaction(cmbPerformance.getItemAt(cmbPerformance.getSelectedIndex()));
        strategieParam.setTolerancePositionInPtf(10);
        strategieParam.setTypeQuotation(enTypeQuotation.close);
        strategieParam.setPerformanceMinimumToArbitrate(5);

        Strategie strategie = new Strategie(ptf, strategieParam);

        // parcours de toutes les actions à faire
        for (StrategyAction action : strategie.getActions())
            if (action.isActionsOnPosition())
                panelPriceEvolutionTable.addData(strategie.getStockPrice(enActionOnPosition.Alleger),
                        strategie.getStockPrice(enActionOnPosition.Renforcer),
                        strategieParam.getPeriodePerformance());
    }


}
