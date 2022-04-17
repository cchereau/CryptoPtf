package gui.screenPosition;

import global.EnumCrypto;
import global.EnumCrypto.enPeriodePerformance;
import global.EnumCrypto.enTypeMontant;
import global.EnumCrypto.enTypeQuotation;
import global.GlobalData;
import global.fonction.FntFinancial;
import global.fonction.FntGUI;
import gui.commonComponent.JPanelGraphique.JPanelStockGraph;
import gui.commonComponent.JPanelTable.Strategie.JPanelTableActionOnPosition;
import gui.commonComponent.JPanelTable.Strategie.ListnerStrategieAction;
import ptfAnalyse.ptfStrategieOld.Strategie;
import ptfAnalyse.ptfStrategieOld.StrategieParam;
import ptfAnalyse.ptfStrategieOld.StrategyAction;
import ptfManagement.Portefeuille;
import ptfManagement.Position;
import stock.StockComposite;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class JPanelPositionStrategieStrategieAction extends JPanel implements ListnerStrategieAction {

    private final JTextField txtDeltaPosAvgPtf = new JTextField(10);
    private final JTextField txtActionOnPosition = new JTextField(10);
    private final JPanelStockGraph panelJPanelStockGraphGraphique = new JPanelStockGraph(false);
    private final JPanelTableActionOnPosition panelTableActionOnPosition;

    private final StrategieParam strategieParam = new StrategieParam();
    protected Strategie strategiePosition;
    private ArrayList<StrategyAction> strategyActions;
    private boolean isStartegieAvailable;


    public JPanelPositionStrategieStrategieAction() {
        super(new GridBagLayout());
        strategyActions = new ArrayList<>();
        strategieParam.setPeriodePerformance(enPeriodePerformance.WEEK);
        strategieParam.setTolerancePositionInPtf(10);
        strategieParam.setPerformanceMinimumToArbitrate(20);
        strategieParam.setPeriodWithoutTransaction(enPeriodePerformance.WEEK);
        strategieParam.setTypeQuotation(enTypeQuotation.close);
        panelTableActionOnPosition = new JPanelTableActionOnPosition();

        ///////////////////////////////////// PANEL STRATEGIE //////////////////////////////////////////
        JPanel panelStrategie = new JPanel();
        panelStrategie.setBorder(BorderFactory.createTitledBorder("Strategie"));
        panelStrategie.setLayout(new GridBagLayout());
        GridBagConstraints gbcStrategie = new GridBagConstraints();

        JLabel lblStrategiePeriode = new JLabel("Per. sans deal");
        FntGUI.setBagContraint(gbcStrategie, 0, 0, 1, 1, 0.10f, 0.1f);
        panelStrategie.add(lblStrategiePeriode, gbcStrategie);
        JTextField txtStrategiePeriode = new JTextField(strategieParam.getPeriodeWhitoutTransaction().toString());
        FntGUI.setBagContraint(gbcStrategie, 1, 0, 1, 1, 0.10f, 0.1f);
        panelStrategie.add(txtStrategiePeriode, gbcStrategie);

        JLabel lblStrategiePeriodePerformance = new JLabel("Per. de perf.");
        FntGUI.setBagContraint(gbcStrategie, 2, 0, 1, 1, 0.10f, 0.1f);
        panelStrategie.add(lblStrategiePeriodePerformance, gbcStrategie);
        JTextField txtStrategiePeriodePerformance = new JTextField(strategieParam.getPeriodePerformance().toString());
        FntGUI.setBagContraint(gbcStrategie, 3, 0, 1, 1, 0.10f, 0.1f);
        panelStrategie.add(txtStrategiePeriodePerformance, gbcStrategie);

        JLabel lblStrategieToleranceAvgPtf = new JLabel("Tol. Avg. ptf");
        FntGUI.setBagContraint(gbcStrategie, 0, 1, 1, 1, 0.10f, 0.1f);
        panelStrategie.add(lblStrategieToleranceAvgPtf, gbcStrategie);
        JTextField txtStrategieToleranceAvgPtf = new JTextField(strategieParam.getTolerancePositionInPtf() + "%");
        FntGUI.setBagContraint(gbcStrategie, 1, 1, 1, 1, 0.10f, 0.1f);
        panelStrategie.add(txtStrategieToleranceAvgPtf, gbcStrategie);

        JLabel lblStrategiePerformanceMin = new JLabel("Perf. Min");
        FntGUI.setBagContraint(gbcStrategie, 2, 1, 1, 1, 0.10f, 0.1f);
        panelStrategie.add(lblStrategiePerformanceMin, gbcStrategie);
        JTextField txtStrategiePerformanceMin = new JTextField(strategieParam.getPerformanceMinimulToArbitrate() + "%");
        FntGUI.setBagContraint(gbcStrategie, 3, 1, 1, 1, 0.10f, 0.1f);
        panelStrategie.add(txtStrategiePerformanceMin, gbcStrategie);

        txtStrategiePerformanceMin.setEnabled(false);
        txtStrategiePeriode.setEnabled(false);
        txtStrategiePeriodePerformance.setEnabled(false);
        txtStrategieToleranceAvgPtf.setEnabled(false);

        ///////////////////////////////////// PANEL POSITION //////////////////////////////////////////
        JPanel panelPosition = new JPanel();
        GridBagLayout layoutPosition = new GridBagLayout();
        panelPosition.setLayout(layoutPosition);

        panelPosition.setBorder(BorderFactory.createTitledBorder("Position"));
        GridBagConstraints gbcPosition = new GridBagConstraints();

        JLabel lblDeltaPosAvgPtf = new JLabel("Delta Pos. Avg. Ptf");
        FntGUI.setBagContraint(gbcPosition, 0, 0, 1, 1, 0.1f, 0.1f);
        panelPosition.add(lblDeltaPosAvgPtf, gbcPosition);
        FntGUI.setBagContraint(gbcPosition, 1, 0, 1, 1, 0.1f, 0.1f);
        panelPosition.add(txtDeltaPosAvgPtf, gbcPosition);

        JLabel lblActionOnPosition = new JLabel("Action On Pos.");
        FntGUI.setBagContraint(gbcPosition, 2, 0, 1, 1, 0.1f, 0.1f);
        panelPosition.add(lblActionOnPosition, gbcPosition);
        FntGUI.setBagContraint(gbcPosition, 3, 0, 1, 1, 0.1f, 0.1f);
        panelPosition.add(txtActionOnPosition, gbcPosition);

        txtActionOnPosition.setEnabled(false);
        txtDeltaPosAvgPtf.setEnabled(false);

        ///////////////////////////////////// PANEL GLOBAL //////////////////////////////////////////
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        FntGUI.setBagContraint(gbc, 0, 0, 1, 1, 0.1f, 0.1f);
        this.add(panelStrategie, gbc);

        FntGUI.setBagContraint(gbc, 0, 1, 1, 1, 0.1f, 0.1f);
        this.add(panelPosition, gbc);

        FntGUI.setBagContraint(gbc, 0, 2, 1, 1, 0.1f, 0.3f);
        this.add(panelTableActionOnPosition, gbc);

        FntGUI.setBagContraint(gbc, 0, 3, 1, 1, 0.1f, 0.3f);
        this.add(panelJPanelStockGraphGraphique, gbc);

        ////////////////////////////////////   AJOUT DU LISTNER ///////////////////////////////////////
        this.panelTableActionOnPosition.setListenerLineSelected(this);
    }

    public void setPosition(Portefeuille ptf, Position position) {

        isStartegieAvailable = false;
        panelTableActionOnPosition.clearData();
        panelJPanelStockGraphGraphique.removeSeries();

        if (!position.isPositionOpen())
            return;

        strategiePosition = new Strategie(ptf, strategieParam);
        txtDeltaPosAvgPtf.setText(FntFinancial.arrondi(position.getMontant(EnumCrypto.enTypeTransaction.all, enTypeMontant.Spot) -
                ptf.getMontant(EnumCrypto.enTypeTransaction.all, enTypeMontant.Average), 2).toString());
        txtActionOnPosition.setText(strategiePosition.getActionOnPosition(position).toString());
        strategyActions = strategiePosition.getAction(position);

        if (strategyActions.size() == 0)
            return;

        panelTableActionOnPosition.addData(strategyActions);
        isStartegieAvailable = true;
    }

    public Boolean isStrategieAvailable() {
        return isStartegieAvailable;
    }


    public void onLineSelected() {
        String instFrom = panelTableActionOnPosition.instrumentSelected[0];
        String instTo = panelTableActionOnPosition.instrumentSelected[1];
        StockComposite stockComposite = new StockComposite(GlobalData.stocks.getStock(instFrom), GlobalData.stocks.getStock(instTo));
        panelJPanelStockGraphGraphique.removeSeries();
        panelJPanelStockGraphGraphique.addSeries(stockComposite);
    }
}
