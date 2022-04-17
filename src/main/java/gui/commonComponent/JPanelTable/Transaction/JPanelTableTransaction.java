package gui.commonComponent.JPanelTable.Transaction;

import global.EnumCrypto;
import global.GlobalCte;
import gui.commonComponent.RenderCellule.RenderCurrencyWithColor;
import gui.commonComponent.RenderCellule.RenderNumberWithColor;
import ptfManagement.Transaction;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class JPanelTableTransaction extends JPanel {

    protected JTable tableau;
    protected TableModeleTrade tableModeleTrade;
    protected ArrayList<Transaction> listValues;
    protected ListnerInstrument listenerLineSelected;
    protected Transaction transactionSelected;

    public JPanelTableTransaction() {

        super(new BorderLayout());
        tableau = new JTable();
        tableModeleTrade = new TableModeleTrade();
        tableau.setModel(tableModeleTrade);
        tableau.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModeleTrade);
        tableau.setRowSorter(sorter);

        ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(GlobalCte.COL_TRAFDE_DATE, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        tableau.getColumnModel().getColumn(GlobalCte.COL_TRADE_BALANCE_POSITION).setCellRenderer(new RenderCurrencyWithColor(true));
        tableau.getColumnModel().getColumn(GlobalCte.COL_TRADE_BALANCE_SHARE).setCellRenderer(new RenderNumberWithColor(true));
        tableau.getColumnModel().getColumn(GlobalCte.COL_TRADE_BALANCE_PRICE).setCellRenderer(new RenderCurrencyWithColor(true));
        tableau.getColumnModel().getColumn(GlobalCte.COL_TRADE_COURT).setCellRenderer(new RenderCurrencyWithColor(false));
        tableau.getColumnModel().getColumn(GlobalCte.COL_TRADE_FEES).setCellRenderer(new RenderCurrencyWithColor(false));
        tableau.getColumnModel().getColumn(GlobalCte.COL_TRADE_SOUS_TOTAL).setCellRenderer(new RenderCurrencyWithColor(false));
        tableau.getColumnModel().getColumn(GlobalCte.COL_TRADE_TOTAL).setCellRenderer(new RenderCurrencyWithColor(false));
        tableau.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.add(tableau);
        scrollPane.setViewportView(tableau);
        this.add(scrollPane, BorderLayout.CENTER);

        ///////////////////// Ajout du Listener ///////////////////////////
        ListSelectionModel selectionModel = tableau.getSelectionModel();
        selectionModel.addListSelectionListener(event -> {
            if (tableau.getSelectedRow() > -1) {
                try {
                    transactionSelected = new Transaction();
                    transactionSelected.setInstrumentName((String) tableau.getValueAt(tableau.getSelectedRow(), GlobalCte.COL_TRADE_INSTNAME));
                    transactionSelected.setDateTransation((LocalDateTime) tableau.getValueAt(tableau.getSelectedRow(), GlobalCte.COL_TRAFDE_DATE));
                    transactionSelected.setTransactionType((EnumCrypto.enTypeTransaction) tableau.getValueAt(tableau.getSelectedRow(), GlobalCte.COL_TRADE_ACTION));

                    transactionSelected.setNbreShare((Double) tableau.getValueAt(tableau.getSelectedRow(), GlobalCte.COL_TRADE_QUANTITE));
                    transactionSelected.setCumulNbreShare((Double) tableau.getValueAt(tableau.getSelectedRow(), GlobalCte.COL_TRADE_BALANCE_SHARE));
                    transactionSelected.setEurPrice((Double) tableau.getValueAt(tableau.getSelectedRow(), GlobalCte.COL_TRADE_COURT));

                    transactionSelected.setMontant(EnumCrypto.enTypeMontant.MontantWithFees, (Double) tableau.getValueAt(tableau.getSelectedRow(), GlobalCte.COL_TRADE_TOTAL));
                    transactionSelected.setMontant(EnumCrypto.enTypeMontant.MontantWithoutFees, (Double) tableau.getValueAt(tableau.getSelectedRow(), GlobalCte.COL_TRADE_SOUS_TOTAL));
                    transactionSelected.setMontant(EnumCrypto.enTypeMontant.Fees, (Double) tableau.getValueAt(tableau.getSelectedRow(), GlobalCte.COL_TRADE_FEES));
                    transactionSelected.setMontant(EnumCrypto.enTypeMontant.CumulBalance, (Double) tableau.getValueAt(tableau.getSelectedRow(), GlobalCte.COL_TRADE_BALANCE_POSITION));
                    transactionSelected.setMontant(EnumCrypto.enTypeMontant.CumulBalance, (Double) tableau.getValueAt(tableau.getSelectedRow(), GlobalCte.COL_TRADE_BALANCE_PRICE));

                    transactionSelected.setNotes((String) tableau.getValueAt(tableau.getSelectedRow(), GlobalCte.COL_TRADE_NOTE));
                    listenerLineSelected.onLineSelected();
                } catch (Exception e) {
                    System.out.println(JPanelTableTransaction.class.getCanonicalName() + "-" + e.getMessage());
                }
            }
        });
    }

    public void setListenerLineSelected(ListnerInstrument listener) {
        this.listenerLineSelected = listener;
    }

    public void isNameColumnVisible(boolean isVisible) {
        if (!isVisible)
            tableau.getColumnModel().removeColumn(tableau.getColumnModel().getColumn(GlobalCte.COL_TRADE_INSTNAME));
    }

    public void addTransactions(ArrayList<Transaction> transactions) {
        this.listValues = transactions;
        // alimentation du tableau des positions
        tableModeleTrade.addAll(this.listValues);
        tableModeleTrade.fireTableDataChanged();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fonction de renvoie de la transaction selectionnée dans le tableau
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Transaction getSlectedTransaction() {
        return transactionSelected;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static class TableModeleTrade extends AbstractTableModel {
        private final ArrayList<Transaction> transactions = new ArrayList<>();

        public TableModeleTrade() {
        }

        /**
         * Retourne le titre de la colonne et l'indice
         */
        public String getColumnName(int col) {
            return GlobalCte.entetesTransactions[col];
        }

        /**
         * Retourne le nombre de colonnes
         */
        public int getColumnCount() {
            return GlobalCte.entetesTransactions.length;
        }

        /**
         * Retourne le nombre de lignes
         */
        public int getRowCount() {
            return this.transactions.size();
        }

        /**
         * Retourne l'objet à l'intersection de ligne et colonne
         */
        @Override
        public Object getValueAt(int row, int col) {
            Transaction p = transactions.get(row);
            switch (col) {
                case GlobalCte.COL_TRAFDE_DATE:
                    return p.getDateTransaction();
                case GlobalCte.COL_TRADE_INSTNAME:
                    return p.getInstrumentName();
                case GlobalCte.COL_TRADE_ACTION:
                    return p.getTransactionType();
                case GlobalCte.COL_TRADE_QUANTITE:
                    return p.getNbreShare();
                case GlobalCte.COL_TRADE_COURT:
                    return p.getEurPrice();
                case GlobalCte.COL_TRADE_SOUS_TOTAL:
                    return p.getMontant(EnumCrypto.enTypeMontant.MontantWithoutFees);
                case GlobalCte.COL_TRADE_FEES:
                    return p.getMontant(EnumCrypto.enTypeMontant.Fees);
                case GlobalCte.COL_TRADE_TOTAL:
                    return p.getMontant(EnumCrypto.enTypeMontant.MontantWithFees);
                case GlobalCte.COL_TRADE_BALANCE_POSITION:
                    return p.getMontant(EnumCrypto.enTypeMontant.CumulBalance);
                case GlobalCte.COL_TRADE_BALANCE_PRICE:
                    if (p.getCumulNbreShart() != null)
                        return p.getMontant(EnumCrypto.enTypeMontant.CumulBalance) / p.getCumulNbreShart();
                    else
                        return 0d;
                case GlobalCte.COL_TRADE_BALANCE_SHARE:
                    return p.getCumulNbreShart();
                case GlobalCte.COL_TRADE_NOTE:
                    return p.getNotes();
                default:
                    return null;
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case GlobalCte.COL_TRAFDE_DATE -> LocalDateTime.class;
                case GlobalCte.COL_TRADE_ACTION -> Enum.class;
                case GlobalCte.COL_TRADE_INSTNAME, GlobalCte.COL_TRADE_NOTE -> String.class;
                case GlobalCte.COL_TRADE_QUANTITE, GlobalCte.COL_TRADE_COURT, GlobalCte.COL_TRADE_SOUS_TOTAL, GlobalCte.COL_TRADE_FEES, GlobalCte.COL_TRADE_TOTAL, GlobalCte.COL_TRADE_BALANCE_POSITION, GlobalCte.COL_TRADE_BALANCE_PRICE, GlobalCte.COL_TRADE_BALANCE_SHARE -> Double.class;
                default -> Object.class;
            };
        }


        /**
         * Modifier l'objet à l'intersection de ligne et colonne
         */

        public void addAll(ArrayList<Transaction> list) {
            transactions.clear();
            transactions.addAll(list);
        }
    }

}



