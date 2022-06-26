package gui.screenPosition;

import global.EnumCrypto;
import global.GlobalCte;
import global.fonction.FntGUI;
import gui.commonComponent.RenderCellule.RenderCurrencyWithColor;
import gui.commonComponent.RenderCellule.RenderPercentageWithColor;
import gui.screenPortfolio.Strategie.JPanelPriceEvolution;
import ptfManagement.Composition;
import ptfManagement.Portefeuille;
import ptfManagement.Position;
import stock.StockComposite;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;

public class JPanelComposition extends JPanel {
    private final TableModelPositionComposition tableModelPositionComposition = new TableModelPositionComposition();
    private final JPanelPriceEvolution panelPriceEvolution;
    private ArrayList<Composition> compositions = new ArrayList<>();


    public JPanelComposition() {
        super(new GridBagLayout());

        // Tableau des compostions
        JTable tableauComposition = new JTable();
        JScrollPane scrollTablePosition = new JScrollPane(tableauComposition);
        tableauComposition.setModel(tableModelPositionComposition);
        tableauComposition.setEnabled(true);

        // Graphique des prix
        panelPriceEvolution = new JPanelPriceEvolution();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        // PAGE DETAIL POSITIONS - scrollTablePosition
        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        // alimentation du tableau des positions
        tableauComposition.getColumnModel().getColumn(GlobalCte.COL_COMPOSITION_QUANTITE).setCellRenderer(new RenderCurrencyWithColor(false));
        tableauComposition.getColumnModel().getColumn(GlobalCte.COL_COMPOSITION_PRIX_MOYEN).setCellRenderer(new RenderCurrencyWithColor(false));
        tableauComposition.getColumnModel().getColumn(GlobalCte.COL_COMPOSITION_PRIX_MARCHE).setCellRenderer(new RenderCurrencyWithColor(true));
        tableauComposition.getColumnModel().getColumn(GlobalCte.COL_COMPOSITION_PRIX_PERFORMANCE).setCellRenderer(new RenderPercentageWithColor(true));

        this.setLayout(new GridBagLayout());
        GridBagConstraints gbcPositions = new GridBagConstraints();
        gbcPositions.fill = GridBagConstraints.BOTH;
        FntGUI.setBagContraint(gbcPositions, 0, 0, 1, 1, 0.8f, 1f);
        this.add(tableauComposition.getTableHeader(), gbcPositions);
        FntGUI.setBagContraint(gbcPositions, 0, 1, 1, 1, 0.8f, 1f);
        this.add(scrollTablePosition, gbcPositions);
        FntGUI.setBagContraint(gbcPositions, 0, 2, 1, 1, 0.8f, 1f);
        this.add(scrollTablePosition, gbcPositions);
        FntGUI.setBagContraint(gbcPositions, 0, 3, 1, 1, 0.8f, 1f);
        this.add(panelPriceEvolution, gbcPositions);

        // ajout des listner
        tableauComposition.getSelectionModel().addListSelectionListener(event -> OnSelectLine(tableauComposition.getSelectedRow()));

    }


    public void setPosition(Portefeuille ptf, Position position) {
        // chatrgement des données
        compositions = global.fonction.gblFunction.getPositionComposition(ptf, position.getInstName());
        tableModelPositionComposition.addAll(compositions);
    }


    private void OnSelectLine(int row) {
        StockComposite stockComposite = compositions.get(row).getStock();
        panelPriceEvolution.setStocks(stockComposite, EnumCrypto.enPeriodePerformance.MONTH);
        this.repaint();
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // TABLE MODEL --> ABSTRACT TABLE POUR L'AFFICHAGE DANS LE JTABLE
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static class TableModelPositionComposition extends AbstractTableModel {
        private ArrayList<Composition> compositions = new ArrayList<>();

        public TableModelPositionComposition() {
        }

        /**
         * Retourne le titre de la colonne et l'indice
         */
        public String getColumnName(int col) {
            return GlobalCte.entetesComposition[col];
        }

        /**
         * Retourne le nombre de colonnes
         */
        public int getColumnCount() {
            return GlobalCte.entetesComposition.length;
        }

        /**
         * Retourne le nombre de lignes
         */
        public int getRowCount() {
            return this.compositions.size();
        }

        /**
         * Retourne l'objet à l'intersection de ligne et colonne
         */
        @Override
        public Object getValueAt(int row, int col) {
            Object result;
            Composition p = compositions.get(row);

            result = switch (col) {
                case GlobalCte.COL_COMPOSITION_CODE -> p.getInstrument();
                case GlobalCte.COL_COMPOSITION_SHORT_LONG -> p.getPositionShortLong().toString();
                case GlobalCte.COL_COMPOSITION_QUANTITE -> Math.abs(p.getQuantite());
                case GlobalCte.COL_COMPOSITION_PRIX_MOYEN -> p.getPrixMoyen();
                case GlobalCte.COL_COMPOSITION_PRIX_MARCHE -> p.getPrixMarche();
                case GlobalCte.COL_COMPOSITION_PRIX_PERFORMANCE -> p.getPerformanceAvgMkt();
                case GlobalCte.COL_COMPOSITION_PRIX_PROPAL_BUY_SELL -> p.getResultatSimulation();
                case GlobalCte.COL_COMPOSITION_PRIX_PROPAL_MONTANT_TRANSACTION -> p.getQuantite() * p.getPrixMarche();
                case GlobalCte.COL_COMPOSITION_PRIX_PROPAL_PL -> p.getQuantite() * (p.getPrixMarche() - p.getPrixMoyen());
                default -> null;
            };
            return result;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case GlobalCte.COL_COMPOSITION_CODE, GlobalCte.COL_COMPOSITION_SHORT_LONG, GlobalCte.COL_COMPOSITION_PRIX_PROPAL_BUY_SELL -> String.class;
                case GlobalCte.COL_COMPOSITION_PRIX_PROPAL_PL, GlobalCte.COL_COMPOSITION_PRIX_PROPAL_MONTANT_TRANSACTION, GlobalCte.COL_COMPOSITION_PRIX_MOYEN, GlobalCte.COL_COMPOSITION_PRIX_MARCHE, GlobalCte.COL_COMPOSITION_QUANTITE -> Double.class;
                default -> null;
            };
        }


        /**
         * Modifier l'objet à l'intersection de ligne et colonne
         */
        public void addAll(ArrayList<Composition> compositions) {
            this.compositions = new ArrayList<>();
            this.compositions.addAll(compositions);
            fireTableDataChanged();
        }
    }

}
