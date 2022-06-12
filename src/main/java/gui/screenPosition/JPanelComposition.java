package gui.screenPosition;

import global.EnumCrypto;
import global.GlobalCte;
import gui.commonComponent.RenderCellule.RenderCurrencyWithColor;
import ptfManagement.Composition;
import ptfManagement.Portefeuille;
import ptfManagement.Position;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;

public class JPanelComposition extends JPanel {
    private final TableModelPositionComposition tableModelPositionComposition = new TableModelPositionComposition();

    public JPanelComposition() {
        super(new GridBagLayout());
        JTable tableauComposition = new JTable();
        JScrollPane scrollTablePosition = new JScrollPane(tableauComposition);

        //RowSorter<TableModel> sorter = new TableRowSorter(tableauComposition);
        //tableauComposition.setRowSorter(sorter);
        tableauComposition.setModel(tableModelPositionComposition);
        tableauComposition.setEnabled(true);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        // PAGE DETAIL POSITIONS - scrollTablePosition
        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        // alimentation du tableau des positions
        //tableauComposition.getColumnModel().getColumn(GlobalCte.COL_COMPOSITION_CODE).setCellRenderer(new RenderNumberWithColor(false));
        //tableauComposition.getColumnModel().getColumn(GlobalCte.COL_COMPOSITION_SHORT_LONG).setCellRenderer(new RenderCurrencyWithColor(false));
        tableauComposition.getColumnModel().getColumn(GlobalCte.COL_COMPOSITION_QUANTITE).setCellRenderer(new RenderCurrencyWithColor(false));
        tableauComposition.getColumnModel().getColumn(GlobalCte.COL_COMPOSITION_PRIX_MOYEN).setCellRenderer(new RenderCurrencyWithColor(false));
        tableauComposition.getColumnModel().getColumn(GlobalCte.COL_COMPOSITION_PRIX_MARCHE).setCellRenderer(new RenderCurrencyWithColor(true));
        //tableauComposition.getColumnModel().getColumn(GlobalCte.COL_COMPOSITION_PRIX_PROPAL_BUY_SELL).setCellRenderer(new RenderPercentageWithColor(true));
        //tableauComposition.getColumnModel().getColumn(GlobalCte.COL_COMPOSITION_PRIX_PROPAL_PL).setCellRenderer(new RenderPercentageWithColor(true));

        this.setLayout(new GridBagLayout());
        GridBagConstraints gbcPositions = new GridBagConstraints();
        gbcPositions.fill = GridBagConstraints.BOTH;
        gbcPositions.weightx = gbcPositions.weighty = 1;
        gbcPositions.gridx = 0;
        gbcPositions.gridy = 0;
        this.add(tableauComposition.getTableHeader(), gbcPositions);
        gbcPositions.gridx = 0;
        gbcPositions.gridy = 1;
        this.add(scrollTablePosition, gbcPositions);
        gbcPositions.gridx = 0;
        gbcPositions.gridy = 2;
        this.add(scrollTablePosition, gbcPositions);
    }

    public void setPosition(Portefeuille ptf, Position position) {

        // chatrgement des données
        ArrayList<Composition> compositions = global.fonction.gblFunction.getPositionComposition(ptf, position.getInstName());


        tableModelPositionComposition.addAll(compositions);
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

            switch (col) {
                case GlobalCte.COL_COMPOSITION_CODE:
                    result = p.getInstrument();
                    break;
                case GlobalCte.COL_COMPOSITION_SHORT_LONG:
                    result = p.getPositionShortLong().toString();
                    break;
                case GlobalCte.COL_COMPOSITION_QUANTITE:
                    result = Math.abs(p.getQuantite());
                    break;
                case GlobalCte.COL_COMPOSITION_PRIX_MOYEN:
                    result = p.getPrixMoyen();
                    break;
                case GlobalCte.COL_COMPOSITION_PRIX_MARCHE:
                    result = p.getPrixMarche();
                    break;
                case GlobalCte.COL_COMPOSITION_PRIX_PROPAL_BUY_SELL:
                    result = EnumCrypto.enSimulationResultat.StandBy;
                    break;
                case GlobalCte.COL_COMPOSITION_PRIX_PROPAL_QUANTITE:
                    result = 0d;
                    break;
                case GlobalCte.COL_COMPOSITION_PRIX_PROPAL_PL:
                    result = 0d;
                    break;
                default:
                    result = null;
                    break;
            }
            return result;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case GlobalCte.COL_COMPOSITION_CODE, GlobalCte.COL_COMPOSITION_SHORT_LONG, GlobalCte.COL_COMPOSITION_PRIX_PROPAL_BUY_SELL -> String.class;
                case GlobalCte.COL_COMPOSITION_PRIX_PROPAL_PL, GlobalCte.COL_COMPOSITION_PRIX_PROPAL_QUANTITE, GlobalCte.COL_COMPOSITION_PRIX_MOYEN, GlobalCte.COL_COMPOSITION_PRIX_MARCHE, GlobalCte.COL_COMPOSITION_QUANTITE -> Double.class;
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
