package gui.commonComponent.JPanelTable.Strategie;

import global.GlobalCte;
import gui.commonComponent.RenderCellule.RenderCurrencyWithColor;
import gui.commonComponent.RenderCellule.RenderNumberWithColor;
import gui.commonComponent.RenderCellule.RenderPercentageWithColor;
import ptfAnalyse.ptfStrategieOld.StrategyAction;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;

public class JPanelTableActionOnPosition extends JPanel {

    public String[] instrumentSelected = new String[2];
    protected TableModeleActionOnPosition tableModeleActionOnPosition;
    protected ListnerStrategieAction listenerLineSelected;

    public JPanelTableActionOnPosition() {
        super(new BorderLayout());
        JTable tableauActionOnPosition = new JTable();
        tableModeleActionOnPosition = new TableModeleActionOnPosition();
        tableauActionOnPosition.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableauActionOnPosition.setModel(tableModeleActionOnPosition);
        tableauActionOnPosition.getColumnModel().getColumn(GlobalCte.COL_STRATEGIE_PRICE).setCellRenderer(new RenderCurrencyWithColor(false));
        tableauActionOnPosition.getColumnModel().getColumn(GlobalCte.COL_STRATEGIE_QUANTITE).setCellRenderer(new RenderNumberWithColor(false));
        tableauActionOnPosition.getColumnModel().getColumn(GlobalCte.COL_STRATEGIE_PERFORMANCE).setCellRenderer(new RenderPercentageWithColor(false));
        tableauActionOnPosition.getColumnModel().getColumn(GlobalCte.COL_STRATEGIE_INST_TO_LAST_PRICE_BUY).setCellRenderer(new RenderCurrencyWithColor(false));
        tableauActionOnPosition.getColumnModel().getColumn(GlobalCte.COL_STRATEGIE_INST_TO_AVG_PRICE_BUY).setCellRenderer(new RenderCurrencyWithColor(false));
        tableauActionOnPosition.getColumnModel().getColumn(GlobalCte.COL_STRATEGIE_INST_TO_SPOT_PRICE).setCellRenderer(new RenderCurrencyWithColor(false));

        tableauActionOnPosition.setAutoCreateRowSorter(true);
        tableauActionOnPosition.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.add(tableauActionOnPosition);
        scrollPane.setViewportView(tableauActionOnPosition);
        this.add(scrollPane);

        ///////////////////// Ajout du Listener ///////////////////////////
        ListSelectionModel selectionModel = tableauActionOnPosition.getSelectionModel();
        selectionModel.addListSelectionListener(event -> {
            if (tableauActionOnPosition.getSelectedRow() > -1) {
                instrumentSelected[0] = tableauActionOnPosition.getValueAt(tableauActionOnPosition.getSelectedRow(), GlobalCte.COL_STRATEGIE_INST1_TICKER).toString();
                instrumentSelected[1] = tableauActionOnPosition.getValueAt(tableauActionOnPosition.getSelectedRow(), GlobalCte.COL_STRATEGIE_INST2_TICKER).toString();
                listenerLineSelected.onLineSelected();
            }
        });
    }

    public void setListenerLineSelected(ListnerStrategieAction listener) {
        this.listenerLineSelected = listener;
    }

    public void addData(ArrayList<StrategyAction> data) {
        this.tableModeleActionOnPosition.addAll(data);
        tableModeleActionOnPosition.fireTableDataChanged();
    }

    public void clearData() {
        this.tableModeleActionOnPosition.addAll(new ArrayList<>());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static class TableModeleActionOnPosition extends AbstractTableModel {
        private final ArrayList<StrategyAction> strategyActions = new ArrayList<>();

        public TableModeleActionOnPosition() {

        }

        /**
         * Retourne le titre de la colonne et l'indice
         */
        public String getColumnName(int col) {
            return GlobalCte.entetesStrategie[col];
        }

        /**
         * Retourne le nombre de colonnes
         */
        public int getColumnCount() {
            return GlobalCte.entetesStrategie.length;
        }

        /**
         * Retourne le nombre de lignes
         */
        public int getRowCount() {
            return this.strategyActions.size();
        }

        /**
         * Retourne l'objet à l'intersection de ligne et colonne
         */
        @Override
        public Object getValueAt(int row, int col) {
            StrategyAction p = strategyActions.get(row);
            return switch (col) {
                case GlobalCte.COL_STRATEGIE_ACTION -> p.getActionInstFrom().toString();
                case GlobalCte.COL_STRATEGIE_INST1_TICKER -> p.getInstrumentFrom();
                case GlobalCte.COL_STRATEGIE_INST2_TICKER -> p.getInstrumentTo();
                case GlobalCte.COL_STRATEGIE_QUANTITE -> p.getTransactionQuantity();
                case GlobalCte.COL_STRATEGIE_PRICE -> p.getTransactionPrice();
                case GlobalCte.COL_STRATEGIE_PERFORMANCE -> p.getPerformance();
                case GlobalCte.COL_STRATEGIE_INST_TO_AVG_PRICE_BUY -> p.getInstrumentToAvgPriceBuy();
                case GlobalCte.COL_STRATEGIE_INST_TO_LAST_PRICE_BUY -> p.getInstrumentToLastPriceTypeTransaction();
                case GlobalCte.COL_STRATEGIE_INST_TO_SPOT_PRICE -> p.getInstrumentToSpotPrice();
                default -> null;
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case GlobalCte.COL_STRATEGIE_ACTION -> Enum.class;
                case GlobalCte.COL_STRATEGIE_INST1_TICKER, GlobalCte.COL_STRATEGIE_INST2_TICKER -> String.class;
                case GlobalCte.COL_STRATEGIE_PRICE, GlobalCte.COL_STRATEGIE_QUANTITE, GlobalCte.COL_STRATEGIE_PERFORMANCE, GlobalCte.COL_STRATEGIE_INST_TO_AVG_PRICE_BUY, GlobalCte.COL_STRATEGIE_INST_TO_LAST_PRICE_BUY, GlobalCte.COL_STRATEGIE_INST_TO_SPOT_PRICE -> Double.class;
                default -> Object.class;
            };
        }


        /**
         * Modifier l'objet à l'intersection de ligne et colonne
         */

        public void addAll(ArrayList<StrategyAction> list) {
            strategyActions.clear();
            strategyActions.addAll(list);
            fireTableDataChanged();
        }
    }
}
