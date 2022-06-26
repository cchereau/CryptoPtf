package gui.screenPortfolio.Strategie;

import global.EnumCrypto.enPeriodePerformance;
import global.EnumCrypto.enTypeQuotation;
import global.fonction.FntPerformance;
import gui.commonComponent.RenderCellule.RenderPercentageWithColor;
import stock.Stock;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JPanelPriceEvolutionTable extends JPanel {

    private final JTable table;
    private final JScrollPane panelTable;
    private ListnerPriceEvolution listenerCellSelected;
    private ArrayList<Stock> stockFrom;
    private ArrayList<Stock> stockTo;

    public JPanelPriceEvolutionTable() {
        super(new BorderLayout());
        this.setBorder(BorderFactory.createTitledBorder("Performance"));
        table = new JTable();
        table.setDefaultRenderer(Double.class, new RenderPercentageWithColor(true));
        table.setCellSelectionEnabled(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        panelTable = new JScrollPane(table);
        ScrollPaneLayout layoutTable = new ScrollPaneLayout();
        panelTable.setLayout(layoutTable);
        this.add(panelTable, BorderLayout.CENTER);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        // ajout du listner
        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();
                    listenerCellSelected.OnCellSelected(stockFrom.get(row), stockTo.get(col));
                }
            }
        });

        final ListSelectionModel lsm = table.getSelectionModel();
        lsm.addListSelectionListener(e -> {
            lsm.setAnchorSelectionIndex(lsm.getLeadSelectionIndex());
            table.getSelectedColumn();
        }); // end of added code

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        // Sélection par default de la cell
        ///////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    public void setListenerLineSelected(ListnerPriceEvolution listener) {
        this.listenerCellSelected = listener;
    }

    public void addData(ArrayList<Stock> stockPricesFrom, ArrayList<Stock> stockPricesTo, enPeriodePerformance periodePerformance) {
        Predicate<Stock> pricePredicate = Stock::isUpdateAuto;
        stockFrom = stockPricesFrom.stream().filter(pricePredicate).collect(Collectors.toCollection(ArrayList<Stock>::new));
        stockTo = stockPricesTo.stream().filter(pricePredicate).collect(Collectors.toCollection(ArrayList<Stock>::new));

        // récupération de la liste des Titres de colonnes
        TableModelMatrixQuotation tableModelMatrixQuotation = new TableModelMatrixQuotation();
        tableModelMatrixQuotation.addData(this.stockFrom, this.stockTo, periodePerformance);
        table.setModel(tableModelMatrixQuotation);
        tableModelMatrixQuotation.fireTableDataChanged();
        ListModel lm = new AbstractListModel() {
            public int getSize() {
                return stockPricesFrom.size();
            }
            public Object getElementAt(int index) {
                return stockPricesFrom.get(index).getNom();
            }
        };
        JList<Object> rowHeader = new JList<Object>(lm);
        rowHeader.setFixedCellWidth(50);
        rowHeader.setFixedCellHeight(table.getRowHeight() + table.getRowMargin());
        rowHeader.setCellRenderer(new RowHeaderRenderer(table));
        panelTable.setRowHeaderView(rowHeader);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GESTION DES ROW
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static class RowHeaderRenderer extends JLabel implements ListCellRenderer {
        RowHeaderRenderer(JTable table) {
            JTableHeader header = table.getTableHeader();
            setOpaque(true);
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            setHorizontalAlignment(CENTER);
            setForeground(header.getForeground());
            setBackground(header.getBackground());
            setFont(header.getFont());
        }

        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // TABLE MODEL MATRIX QUOTATION
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static class TableModelMatrixQuotation extends AbstractTableModel {

        private ArrayList<Stock> stockFrom;
        private ArrayList<Stock> stockTo;
        private enPeriodePerformance periodePerformance;

        public TableModelMatrixQuotation() {
        }

        /**
         * Retourne le titre de la colonne et l'indice
         */
        public String getColumnName(int col) {
            return this.stockTo.get(col).getNom();
        }

        /**
         * Retourne le nombre de colonnes
         */
        public int getColumnCount() {
            return this.stockTo.size();
        }

        /**
         * Retourne le nombre de lignes
         */
        public int getRowCount() {
            return stockFrom.size();
        }

        /**
         * Retourne l'objet à l'intersection de ligne et colonne
         */
        @Override
        public Object getValueAt(int row, int col) {
            return FntPerformance.getPerformance(stockFrom.get(row),
                    stockTo.get(col),
                    this.periodePerformance, enTypeQuotation.close);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Double.class;
        }

        public void addData(ArrayList<Stock> pHeaderRow, ArrayList<Stock> pHeaderColumn, enPeriodePerformance periodePerformance) {
            this.stockFrom = pHeaderRow;
            this.stockTo = pHeaderColumn;
            this.periodePerformance = periodePerformance;
        }
    }

}
