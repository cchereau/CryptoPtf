package gui.screenStockComposite;

import global.EnumCrypto.enPeriodePerformance;
import global.EnumCrypto.enTypeQuotation;
import global.EnumCrypto.enTypeTransaction;
import global.GlobalData;
import global.fonction.FntGUI;
import gui.screenPortfolio.Strategie.JPanelPriceEvolution;
import gui.screenPortfolio.Strategie.JPanelPriceEvolutionTable;
import ptfManagement.Portefeuille;
import ptfManagement.Position;
import stock.Stock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JDialogStockComposite extends JFrame {

    private final JComboBox<enPeriodePerformance> cmbPerformance;
    private final JPanelPriceEvolutionTable panelPriceEvolutionTable;


    public JDialogStockComposite() {
        super();
        JPanelPriceEvolution panelStockCompare = new JPanelPriceEvolution();

        /////////////////////////////////////////CONFIG////////////////////////////////////////////////
        JPanel panelConfig = new JPanel();
        GridBagLayout layoutConfig = new GridBagLayout();
        panelConfig.setLayout(layoutConfig);
        GridBagConstraints gbcPanelConfig = new GridBagConstraints();
        gbcPanelConfig.fill = GridBagConstraints.BOTH;

        JLabel lblPeriode = new JLabel("Period");
        FntGUI.setBagContraint(gbcPanelConfig, 0, 0, 1, 1);
        panelConfig.add(lblPeriode, gbcPanelConfig);

        cmbPerformance = new JComboBox<>(enPeriodePerformance.values());
        FntGUI.setBagContraint(gbcPanelConfig, 1, 0, 1, 1);
        panelConfig.add(cmbPerformance, gbcPanelConfig);

        JLabel lblTypeTransaction = new JLabel("Type transaction");
        FntGUI.setBagContraint(gbcPanelConfig, 2, 0, 1, 1);
        panelConfig.add(lblTypeTransaction, gbcPanelConfig);

        JComboBox<enTypeTransaction> cmbTypeTransaction = new JComboBox<>(enTypeTransaction.values());
        FntGUI.setBagContraint(gbcPanelConfig, 3, 0, 1, 1);
        panelConfig.add(cmbTypeTransaction, gbcPanelConfig);

        JLabel lblPositionActive = new JLabel("Position Active");
        FntGUI.setBagContraint(gbcPanelConfig, 4, 0, 1, 1);
        panelConfig.add(lblPositionActive);

        JCheckBox checkBoxPositionActive = new JCheckBox();
        FntGUI.setBagContraint(gbcPanelConfig, 5, 0, 1, 1);
        panelConfig.add(checkBoxPositionActive, gbcPanelConfig);

        /////////////////////////////////////////TABLE////////////////////////////////////////////////
        this.panelPriceEvolutionTable = new JPanelPriceEvolutionTable();
        //this.panelPriceEvolutionTable.setListenerLineSelected(this);

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // COMPOSITION DE LA PAGE
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        FntGUI.setBagContraint(gbc, 0, 0, 1, 1);
        this.add(panelConfig, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        FntGUI.setBagContraint(gbc, 0, 1, 1, 1, 0.7f, 0.35f);
        this.add(panelPriceEvolutionTable, gbc);
        gbc.fill = GridBagConstraints.BOTH;
        FntGUI.setBagContraint(gbc, 0, 4, 1, 1, 0.7f, 0.35f);
        this.add(panelStockCompare, gbc);

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // PARAMETER DE LA PAGE
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // AJOUT DES LISTENER
        cmbTypeTransaction.setSelectedIndex(enTypeTransaction.all.ordinal());
        cmbTypeTransaction.addActionListener(this::setFilterAction);
        cmbPerformance.addActionListener(this::setFilterAction);
        cmbPerformance.setSelectedIndex(enPeriodePerformance.WEEK.ordinal());
        checkBoxPositionActive.setSelected(false);
        checkBoxPositionActive.addActionListener(this::setFilterAction);

        this.setPreferredSize(new Dimension(900, 800));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
    }

    private void setFilterAction(ActionEvent actionEvent) {

        Portefeuille ptf = GlobalData.portfeuilles.get("CoinBase");
        ArrayList<Position> positionFilter = ptf.getPositions();

        // creation de la list des Stock From -> C'est à dire celle qui seront les header des lignes
        // donc les stocks qui auront un prix > au prix moyen
        Predicate<Position> predicateFrom = (Position p) -> (p.getPositionAvgPrice(enTypeTransaction.sell).compareTo(p.getPositionSpotPrice(enTypeQuotation.close)) > 0);
        ArrayList<Position> positionsFrom = positionFilter.stream().filter(predicateFrom).collect(Collectors.toCollection(ArrayList<Position>::new));
        ArrayList<Stock> stockPricesFrom = new ArrayList<>();
        for (Position position : positionsFrom)
            stockPricesFrom.add(position.getStockPrice());

        // création de la liste des Stock To --> Celle qui seront les header des colonnes
        // donc les stocks qui auront un prix < au prix moyen
        Predicate<Position> predicateTo = (Position p) -> (p.getPositionAvgPrice(enTypeTransaction.buy).compareTo(p.getPositionSpotPrice(enTypeQuotation.close)) < 0);
        ArrayList<Position> positionsTo = positionFilter.stream().filter(predicateTo).collect(Collectors.toCollection(ArrayList<Position>::new));
        ArrayList<Stock> stockPricesTo = new ArrayList<>();
        for (Position position : positionsTo)
            stockPricesTo.add(position.getStockPrice());

        panelPriceEvolutionTable.addData(stockPricesFrom, stockPricesTo, cmbPerformance.getItemAt(cmbPerformance.getSelectedIndex()));
    }


}
