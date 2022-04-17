package gui.screenQuotation;

import global.EnumCrypto;
import global.GlobalCte;
import global.fonction.FntGUI;
import gui.commonComponent.RenderCellule.RenderCurrencyWithColor;
import stock.Stock;
import stock.StockPrice;
import stock.Stocks;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Objects;

public class JDialogQuotationHistory extends JDialog {

    private final TableModelQuotationHistory tableModelStockPrice;

    public JDialogQuotationHistory(Stocks stocks) {
        super();

        JLabel lblStockName = new JLabel("Nom:");
        JComboBox<?> cmbCurrency = new JComboBox<>(stocks.getStockTickers().toArray());
        JLabel lblStockTicker = new JLabel("Ticker:");
        JTextField textFieldTicker = new JTextField(10);
        JLabel lblRefeshAuto = new JLabel("Refresh Auto:");
        JCheckBox checkBoxUpdate = new JCheckBox();
        JButton btnUpdateQuotation = new JButton("Update");


        ///////////////////////////////////////////////////////////////////////////////////////////////
        // Constitution des données de sélection de la devise
        //////////////////////////////////////////////////////////////////////////////////////////////
        JPanel panelStockSelection = new JPanel();
        panelStockSelection.setBorder(BorderFactory.createTitledBorder("Liste des Devises"));

        cmbCurrency.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // recuperation de la devis gérée
                String instrument = Objects.requireNonNull(cmbCurrency.getSelectedItem()).toString();
                Stock stock = stocks.getStock(instrument);

                // mise a jour des informations
                textFieldTicker.setText(stock.getTicker());
                checkBoxUpdate.setSelected(stock.isUpdateAuto());

                // vérification du type de mise à jour
                //btnAddNewPrice.setEnabled(!stock.isUpdateAuto());
                tableModelStockPrice.addAll(stocks.getStock(instrument).getQuotations().values());
                tableModelStockPrice.fireTableDataChanged();


            }
        });

        btnUpdateQuotation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


            }
        });


        panelStockSelection.setBorder(BorderFactory.createTitledBorder("Currency Identity Card"));
        panelStockSelection.add(lblStockName);
        panelStockSelection.add(cmbCurrency);
        panelStockSelection.add(lblStockTicker);
        panelStockSelection.add(textFieldTicker);
        panelStockSelection.add(lblRefeshAuto);
        panelStockSelection.add(checkBoxUpdate);
        panelStockSelection.add(btnUpdateQuotation);


        ///////////////////////////////////////////////////////////////////////////////////////////////
        // COnstitution du tableau des historique
        //////////////////////////////////////////////////////////////////////////////////////////////

        tableModelStockPrice = new TableModelQuotationHistory();
        JTable tableau = new JTable();
        JScrollPane scrollPaneTable = new JScrollPane(tableau);

        tableau.setModel(tableModelStockPrice);

        tableau.getColumnModel().getColumn(GlobalCte.COL_QUOTATION_HISTORY_CLOSE).setCellRenderer(new RenderCurrencyWithColor(false));
        tableau.getColumnModel().getColumn(GlobalCte.COL_QUOTATION_HISTORY_OPEN).setCellRenderer(new RenderCurrencyWithColor(false));
        tableau.getColumnModel().getColumn(GlobalCte.COL_QUOTATION_HISTORY_LOW).setCellRenderer(new RenderCurrencyWithColor(false));
        tableau.getColumnModel().getColumn(GlobalCte.COL_QUOTATION_HISTORY_HIGH).setCellRenderer(new RenderCurrencyWithColor(false));

        JPanel panelHistorique = new JPanel();
        panelHistorique.setBorder(BorderFactory.createTitledBorder("Historique des valeurs"));
        panelHistorique.add(scrollPaneTable);

        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        FntGUI.setBagContraint(gbc, 0, 0, 1, 1);
        this.add(panelStockSelection, gbc);
        FntGUI.setBagContraint(gbc, 0, 1, 1, 1);
        this.add(panelHistorique, gbc);

        // préselection dans la combo box
        cmbCurrency.setSelectedIndex(0);
    }


    private static class TableModelQuotationHistory extends AbstractTableModel {

        private ArrayList<StockPrice> stockPrice = new ArrayList<>();

        /**
         * Retourne le titre de la colonne et l'indice
         */
        public String getColumnName(int col) {
            return GlobalCte.entetesQuotationhistory[col];
        }

        /**
         * Retourne le nombre de colonnes
         */
        public int getColumnCount() {
            return GlobalCte.entetesQuotationhistory.length;
        }

        /**
         * Retourne le nombre de lignes
         */
        public int getRowCount() {
            return this.stockPrice.size();
        }

        /**
         * Retourne l'objet à l'intersection de ligne et colonne
         */
        @Override
        public Object getValueAt(int row, int col) {

            return switch (col) {
                case GlobalCte.COL_QUOTATION_HISTORY_DATE -> this.stockPrice.get(row).getDate().toString();
                case GlobalCte.COL_QUOTATION_HISTORY_OPEN -> this.stockPrice.get(row).getValue(EnumCrypto.enTypeQuotation.open);
                case GlobalCte.COL_QUOTATION_HISTORY_CLOSE -> this.stockPrice.get(row).getValue(EnumCrypto.enTypeQuotation.close);
                case GlobalCte.COL_QUOTATION_HISTORY_LOW -> this.stockPrice.get(row).getValue(EnumCrypto.enTypeQuotation.low);
                case GlobalCte.COL_QUOTATION_HISTORY_HIGH -> this.stockPrice.get(row).getValue(EnumCrypto.enTypeQuotation.high);
                case GlobalCte.COL_QUOTATION_HISTORY_VOLUME -> this.stockPrice.get(row).getValue(EnumCrypto.enTypeQuotation.volume);
                default -> null;
            };
        }

        public void addAll(Collection<StockPrice> values) {
            this.stockPrice = new ArrayList<>(values);
        }


        @Override
        public Class<?> getColumnClass(int columnIndex) {

            return switch (columnIndex) {
                case GlobalCte.COL_QUOTATION_HISTORY_DATE -> String.class;
                case GlobalCte.COL_QUOTATION_HISTORY_CLOSE, GlobalCte.COL_QUOTATION_HISTORY_HIGH, GlobalCte.COL_QUOTATION_HISTORY_LOW, GlobalCte.COL_QUOTATION_HISTORY_OPEN -> Currency.class;
                case GlobalCte.COL_QUOTATION_HISTORY_VOLUME -> Double.class;
                default -> null;
            };

        }

    }
}
