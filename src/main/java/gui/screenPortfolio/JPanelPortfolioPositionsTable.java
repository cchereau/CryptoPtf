package gui.screenPortfolio;

import dataExchangeIO.CryptoParser;
import global.EnumCrypto;
import global.GlobalCte;
import global.GlobalData;
import global.fonction.FntFinancial;
import global.fonction.FntPerformance;
import gui.commonComponent.RenderCellule.RenderCurrencyWithColor;
import gui.commonComponent.RenderCellule.RenderNumberWithColor;
import gui.commonComponent.RenderCellule.RenderPercentageWithColor;
import gui.screenPosition.JFramePosition;
import gui.screenQuotation.JDialogQutotationSpotPrice;
import gui.screenTransaction.JDialogTrade;
import ptfManagement.Portefeuille;
import ptfManagement.Position;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class JPanelPortfolioPositionsTable extends JPanel implements ListnerChangePtf {
    private final TableModelePositions tableModelePositions = new TableModelePositions();
    private Portefeuille ptf;

    public JPanelPortfolioPositionsTable() {
        super(new GridBagLayout());

        JTable tableauPosition = new JTable();
        JScrollPane scrollTablePosition = new JScrollPane(tableauPosition);

        // creation du menu Contectuel
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItemWatchPosition = new JMenuItem("Watch Position");
        JMenuItem menuItemRemove = new JMenuItem("Remove Position");
        popupMenu.add(menuItemWatchPosition);
        popupMenu.add(menuItemRemove);
        popupMenu.add(new JSeparator());
        JMenuItem menuItemAddTransaction = new JMenuItem("Add Transaction");
        popupMenu.add(menuItemAddTransaction);
        popupMenu.add(new JSeparator());
        JMenuItem menuItemAddPrice = new JMenuItem("Add Spot Price");
        popupMenu.add(menuItemAddPrice);
        popupMenu.add(new JSeparator());

        JMenu menuPtfList = new JMenu("Portefeuille");
        popupMenu.add(menuPtfList);

        tableauPosition.setComponentPopupMenu(popupMenu);
        RowSorter<TableModel> sorter = new TableRowSorter<>(tableModelePositions);
        tableauPosition.setRowSorter(sorter);
        tableauPosition.setModel(tableModelePositions);
        tableauPosition.setEnabled(true);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        // PAGE DETAIL POSITIONS - scrollTablePosition
        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        // alimentation du tableau des positions
        tableauPosition.getColumnModel().getColumn(GlobalCte.COL_PTF_QUANTITE).setCellRenderer(new RenderNumberWithColor(false));
        tableauPosition.getColumnModel().getColumn(GlobalCte.COL_PTF_COURT_MT).setCellRenderer(new RenderCurrencyWithColor(false));
        tableauPosition.getColumnModel().getColumn(GlobalCte.COL_PTF_COURT_SPOT).setCellRenderer(new RenderCurrencyWithColor(false));
        tableauPosition.getColumnModel().getColumn(GlobalCte.COL_PTF_MOTANT_POSITION).setCellRenderer(new RenderCurrencyWithColor(false));
        tableauPosition.getColumnModel().getColumn(GlobalCte.COL_PTF_PL).setCellRenderer(new RenderCurrencyWithColor(true));
        tableauPosition.getColumnModel().getColumn(GlobalCte.COL_PTF_PERF_1W).setCellRenderer(new RenderPercentageWithColor(true));
        tableauPosition.getColumnModel().getColumn(GlobalCte.COL_PTF_PERF_1M).setCellRenderer(new RenderPercentageWithColor(true));
        tableauPosition.getColumnModel().getColumn(GlobalCte.COL_PTF_PERF_3M).setCellRenderer(new RenderPercentageWithColor(true));
        tableauPosition.getColumnModel().getColumn(GlobalCte.COL_PTF_PERF_6M).setCellRenderer(new RenderPercentageWithColor(true));
        tableauPosition.getColumnModel().getColumn(GlobalCte.COL_PTF_PERF_1Y).setCellRenderer(new RenderPercentageWithColor(true));
        tableauPosition.getColumnModel().getColumn(GlobalCte.COL_PTF_BALANCE_EURO).setCellRenderer(new RenderCurrencyWithColor(true));

        this.setLayout(new GridBagLayout());
        GridBagConstraints gbcPositions = new GridBagConstraints();
        gbcPositions.fill = GridBagConstraints.BOTH;
        gbcPositions.weightx = gbcPositions.weighty = 1;
        gbcPositions.gridx = 0;
        gbcPositions.gridy = 0;
        this.add(tableauPosition.getTableHeader(), gbcPositions);
        gbcPositions.gridx = 0;
        gbcPositions.gridy = 1;
        this.add(scrollTablePosition, gbcPositions);
        gbcPositions.gridx = 0;
        gbcPositions.gridy = 2;
        this.add(scrollTablePosition, gbcPositions);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // AJOUT DES LISTNER
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        tableauPosition.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    String Asset = tableauPosition.getValueAt(tableauPosition.getSelectedRow(), GlobalCte.COL_PTF_POSITION).toString();
                    JFramePosition framePosition = new JFramePosition(ptf, Asset);
                    framePosition.setVisible(true);
                }
            }
        });


        menuItemAddTransaction.addActionListener(e -> {
            // chargement de la forme dialogue
            JDialogTrade dlg = new JDialogTrade(true, ptf.getNom());
            dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dlg.setTitle("Gestion des Prix Positions");
            dlg.pack();
            dlg.setLocationRelativeTo(getParent());
            dlg.setAlwaysOnTop(true);
            dlg.setVisible(true);

            // libération de la dialogue forme
            dlg.dispose();
            CryptoParser.SavePtfJSON(GlobalData.portfeuilles);
            // rafrchisseùent du teableau
            tableModelePositions.fireTableDataChanged();
        });

        menuItemWatchPosition.addActionListener(e -> {
            String Asset = tableauPosition.getValueAt(tableauPosition.getSelectedRow(), 0).toString();
            JFramePosition framePosition = new JFramePosition(ptf, Asset);
            framePosition.setVisible(true);
        });

        menuItemRemove.addActionListener(e -> {
            int selectedRow = tableauPosition.getSelectedRow();
            if (selectedRow == -1)
                JOptionPane.showMessageDialog(this.getRootPane(), "Aucune Postion selectionnée");
            else {
                String instrument = (String) tableModelePositions.getValueAt(selectedRow, GlobalCte.COL_PTF_POSITION);
                int dialog = JOptionPane.showConfirmDialog(this.getRootPane(),
                        "Vous allez supprimer dans le Portfeuille: " + ptf.getNom() + " la Position : " + instrument);
                if (dialog == JOptionPane.NO_OPTION)
                    return;
                // destruction de la postion courante
                ptf.removePosition(instrument);
                tableModelePositions.addAll(new ArrayList<>(ptf.getPositions()));
                tableModelePositions.fireTableDataChanged();
            }
        });

        menuItemAddPrice.addActionListener(e -> {
            String Asset = tableauPosition.getValueAt(tableauPosition.getSelectedRow(), GlobalCte.COL_PTF_POSITION).toString();
            Position position = ptf.getPosition(Asset);
            JDialogQutotationSpotPrice dlgSportPrice = new JDialogQutotationSpotPrice(position);
            dlgSportPrice.setModal(true);
            dlgSportPrice.setLocationRelativeTo(getParent());
            dlgSportPrice.setAlwaysOnTop(true);
            dlgSportPrice.setVisible(true);
        });
    }

    @Override
    public void OnPtfChanged(Portefeuille ptf) {
        this.ptf = ptf;
        // chatrgement des données
        tableModelePositions.addAll(this.ptf.getPositions());
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // TABLE MODEL --> ABSTRACT TABLE POUR L'AFFICHAGE DANS LE JTABLE
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static class TableModelePositions extends AbstractTableModel {
        private ArrayList<Position> positionList = new ArrayList<>();


        public TableModelePositions() {
        }

        /**
         * Retourne le titre de la colonne et l'indice
         */
        public String getColumnName(int col) {
            return GlobalCte.entetesPortfeuille[col];
        }

        /**
         * Retourne le nombre de colonnes
         */
        public int getColumnCount() {
            return GlobalCte.entetesPortfeuille.length;
        }

        /**
         * Retourne le nombre de lignes
         */
        public int getRowCount() {
            return this.positionList.size();
        }

        /**
         * Retourne l'objet à l'intersection de ligne et colonne
         */
        @Override
        public Object getValueAt(int row, int col) {
            Object result;
            Position p = positionList.get(row);

            switch (col) {
                case GlobalCte.COL_PTF_POSITION:
                    result = p.getInstName();
                    break;
                case GlobalCte.COL_PTF_QUANTITE:
                    result = FntFinancial.arrondi(p.getPositionQuantiteShare(EnumCrypto.enTypeTransaction.all), 6);
                    break;
                case GlobalCte.COL_PTF_COURT_MT: // si la position est proche de 0, renvoit le cours moyen de l'achat
                    if (FntFinancial.arrondi(p.getPositionQuantiteShare(EnumCrypto.enTypeTransaction.all) * 1000, 0) == 0) {
                        result = FntFinancial.arrondi(p.getPositionAvgPrice(EnumCrypto.enTypeTransaction.buy), 2);
                    } else {
                        result = FntFinancial.arrondi(p.getPositionAvgPrice(EnumCrypto.enTypeTransaction.all), 6);
                    }
                    break;
                case GlobalCte.COL_PTF_COURT_SPOT:
                    result = FntFinancial.arrondi(p.getPositionSpotPrice(EnumCrypto.enTypeQuotation.close), 6);
                    break;
                case GlobalCte.COL_PTF_MOTANT_POSITION:
                    result = FntFinancial.arrondi(p.getMontant(EnumCrypto.enTypeTransaction.all, EnumCrypto.enTypeMontant.Spot), 2);
                    break;
                case GlobalCte.COL_PTF_PL:
                    result = FntFinancial.arrondi(p.getMontant(EnumCrypto.enTypeTransaction.all, EnumCrypto.enTypeMontant.ProfitAndLost), 2);
                    break;
                case GlobalCte.COL_PTF_PERF_1W:
                    result = FntPerformance.getPerformance(p.getStockPrice(), EnumCrypto.enTypeQuotation.close, EnumCrypto.enPeriodePerformance.WEEK);
                    break;
                case GlobalCte.COL_PTF_PERF_1M:
                    result = FntPerformance.getPerformance(p.getStockPrice(), EnumCrypto.enTypeQuotation.close, EnumCrypto.enPeriodePerformance.MONTH);
                    break;
                case GlobalCte.COL_PTF_PERF_3M:
                    result = FntPerformance.getPerformance(p.getStockPrice(), EnumCrypto.enTypeQuotation.close, EnumCrypto.enPeriodePerformance.QUARTER);
                    break;
                case GlobalCte.COL_PTF_PERF_6M:
                    result = FntPerformance.getPerformance(p.getStockPrice(), EnumCrypto.enTypeQuotation.close, EnumCrypto.enPeriodePerformance.SEMESTER);
                    break;
                case GlobalCte.COL_PTF_PERF_1Y:
                    result = FntPerformance.getPerformance(p.getStockPrice(), EnumCrypto.enTypeQuotation.close, EnumCrypto.enPeriodePerformance.YEAR);
                    break;
                case GlobalCte.COL_PTF_STOCK_UPDATE_AUTO:
                    result = p.getStockPrice().isUpdateAuto();
                    break;
                case GlobalCte.COL_PTF_BALANCE_EURO:
                    result = p.getMontant(EnumCrypto.enTypeTransaction.all, EnumCrypto.enTypeMontant.CumulBalance);
                    break;
                case GlobalCte.COL_PTF_IS_POSTION_OPEN:
                    result = p.isPositionOpen();
                    break;
                default:
                    result = "";
                    break;
            }
            return result;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case GlobalCte.COL_PTF_POSITION -> String.class;
                case GlobalCte.COL_PTF_QUANTITE, GlobalCte.COL_PTF_PERF_1W, GlobalCte.COL_PTF_PERF_1M, GlobalCte.COL_PTF_PERF_3M, GlobalCte.COL_PTF_PERF_6M, GlobalCte.COL_PTF_PERF_1Y, GlobalCte.COL_PTF_BALANCE_EURO, GlobalCte.COL_PTF_MOTANT_POSITION, GlobalCte.COL_PTF_COURT_MT, GlobalCte.COL_PTF_COURT_SPOT, GlobalCte.COL_PTF_PL -> Double.class;
                case GlobalCte.COL_PTF_IS_POSTION_OPEN, GlobalCte.COL_PTF_STOCK_UPDATE_AUTO -> Boolean.class;
                default -> null;
            };
        }


        /**
         * Modifier l'objet à l'intersection de ligne et colonne
         */


        public void addAll(ArrayList<Position> list) {
            positionList = new ArrayList<>();
            positionList.addAll(list);
            fireTableDataChanged();
        }
    }

}
