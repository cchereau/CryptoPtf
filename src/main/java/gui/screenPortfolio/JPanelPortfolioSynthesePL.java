package gui.screenPortfolio;

import global.EnumCrypto;
import global.GlobalCte;
import global.fonction.FntGUI;
import gui.commonComponent.RenderCellule.RenderCurrencyWithColor;
import ptfAnalyse.ptfSynthese.PtfSynthese;
import ptfAnalyse.ptfSynthese.PtfSyntheses;
import ptfManagement.Portefeuille;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class JPanelPortfolioSynthesePL extends JPanel implements ListnerChangePtf {

    private final JComboBox<EnumCrypto.enPeriodePerformance> cmbPeriodePtfSyntheses = new JComboBox<>(EnumCrypto.enPeriodePerformance.values());
    private final TableModelePtfSynthese tableModelePtfSynthese;
    private Portefeuille ptf;

    public JPanelPortfolioSynthesePL() {
        super(new GridBagLayout());

        this.setBorder(BorderFactory.createTitledBorder("Detail du portefeuille"));

        GridBagConstraints gbcPtfSynthese = new GridBagConstraints();
        cmbPeriodePtfSyntheses.removeItem(EnumCrypto.enPeriodePerformance.DAY);

        tableModelePtfSynthese = new TableModelePtfSynthese();
        JTable tableauSynthesePtf = new JTable();
        JScrollPane scrollPanelSynthesesPtf = new JScrollPane(tableauSynthesePtf);
        tableauSynthesePtf.setModel(tableModelePtfSynthese);
        tableauSynthesePtf.getColumnModel().getColumn(GlobalCte.COL_PTF_SYNTHESE_MONTANT_INITIAL).setCellRenderer(new RenderCurrencyWithColor(true));
        tableauSynthesePtf.getColumnModel().getColumn(GlobalCte.COL_PTF_SYNTHESE_MONTANT_FINAL).setCellRenderer(new RenderCurrencyWithColor(true));
        tableauSynthesePtf.getColumnModel().getColumn(GlobalCte.COL_PTF_SYNTHESE_LIQUIDITE).setCellRenderer(new RenderCurrencyWithColor(true));
        tableauSynthesePtf.getColumnModel().getColumn(GlobalCte.COL_PTF_SYNTHESE_VALORISATION).setCellRenderer(new RenderCurrencyWithColor(true));
        tableauSynthesePtf.getColumnModel().getColumn(GlobalCte.COL_PTF_SYNTHESE_PL).setCellRenderer(new RenderCurrencyWithColor(true));
        tableauSynthesePtf.getColumnModel().getColumn(GlobalCte.COL_PTF_SYNTHESE_FEES).setCellRenderer(new RenderCurrencyWithColor(true));

        gbcPtfSynthese.fill = GridBagConstraints.NONE;
        FntGUI.setBagContraint(gbcPtfSynthese, 0, 0, 1, 1, 1, 0.1f);
        this.add(cmbPeriodePtfSyntheses, gbcPtfSynthese);
        gbcPtfSynthese.fill = GridBagConstraints.BOTH;
        FntGUI.setBagContraint(gbcPtfSynthese, 0, 1, 1, 1, 1, 0.9f);
        this.add(scrollPanelSynthesesPtf, gbcPtfSynthese);

        cmbPeriodePtfSyntheses.addActionListener(e -> OnPtfChanged(this.ptf));
    }

    @Override
    public void OnPtfChanged(Portefeuille ptf) {
        this.ptf = ptf;
        PtfSyntheses ptfSyntheses = new PtfSyntheses();
        tableModelePtfSynthese.addAll(ptfSyntheses.getSyntheses(this.ptf, cmbPeriodePtfSyntheses.getItemAt(cmbPeriodePtfSyntheses.getSelectedIndex())));
        tableModelePtfSynthese.fireTableDataChanged();

    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // TABLE MODELE ABSTRACT TABLE MODEL
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static class TableModelePtfSynthese extends AbstractTableModel {
        private ArrayList<PtfSynthese> ptfSyntheses = new ArrayList<>();

        @Override
        public String getColumnName(int column) {
            return GlobalCte.entetesPortfeuilleSythese[column];
        }

        @Override
        public int getRowCount() {
            return ptfSyntheses.size();
        }

        @Override
        public int getColumnCount() {
            return GlobalCte.entetesPortfeuilleSythese.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return switch (columnIndex) {
                case GlobalCte.COL_PTF_SYNTHESE_DATE -> ptfSyntheses.get(rowIndex).getDateSythese();
                case GlobalCte.COL_PTF_SYNTHESE_MONTANT_INITIAL -> ptfSyntheses.get(rowIndex).getMontant(EnumCrypto.enTypeMontant.Initial);
                case GlobalCte.COL_PTF_SYNTHESE_MONTANT_FINAL -> ptfSyntheses.get(rowIndex).getMontant(EnumCrypto.enTypeMontant.CumulBalance);
                case GlobalCte.COL_PTF_SYNTHESE_LIQUIDITE -> ptfSyntheses.get(rowIndex).getMontant(EnumCrypto.enTypeMontant.liquidite);
                case GlobalCte.COL_PTF_SYNTHESE_PL -> ptfSyntheses.get(rowIndex).getMontant(EnumCrypto.enTypeMontant.ProfitAndLost);
                case GlobalCte.COL_PTF_SYNTHESE_VALORISATION -> ptfSyntheses.get(rowIndex).getMontant(EnumCrypto.enTypeMontant.Spot);
                case GlobalCte.COL_PTF_SYNTHESE_FEES -> ptfSyntheses.get(rowIndex).getMontant(EnumCrypto.enTypeMontant.Fees);
                default -> null;
            };
        }

        public void addAll(Collection<PtfSynthese> values) {
            this.ptfSyntheses = new ArrayList<>(values);
        }

    }

}
