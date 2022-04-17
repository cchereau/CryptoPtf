package gui.screenPortfolio;

import global.EnumCrypto;
import global.GlobalCte;
import global.GlobalData;
import global.fonction.FntGUI;
import gui.commonComponent.Component.JLabelCustomized;
import ptfManagement.Portefeuille;

import javax.swing.*;
import java.awt.*;

public class JPanelPortfolioDescription extends JPanel {

    private final JComboBox<?> cmbPortefeuille = new JComboBox<>(GlobalData.portfeuilles.descendingKeySet().toArray());
    private final JTextField txtPtdDatePtfCreation = new JTextField();
    private final JLabelCustomized txtMntInitial;
    private final JLabelCustomized txtMntSpot;
    private final JLabelCustomized txtMntFees;
    private final JLabelCustomized txtMntProfitAndLost;
    private final JLabelCustomized txtMntLiquidite;
    private final JTextField txtNmbreMvt;
    private ListnerChangePtf listnerChangePtf;
    private Portefeuille portefeuille;

    public JPanelPortfolioDescription() {
        super(new GridBagLayout());
        JLabel lblMntLiquidite = new JLabel("Liquidite");
        JLabel lblNmbreMvt = new JLabel("Nombre de Mouvement");
        txtMntInitial = new JLabelCustomized(GlobalCte.typeData.Montant);
        txtMntInitial.setEnabled(false);
        txtMntSpot = new JLabelCustomized(GlobalCte.typeData.Montant);
        txtMntSpot.setEnabled(false);
        txtMntFees = new JLabelCustomized(GlobalCte.typeData.Montant);
        txtMntFees.setEnabled(false);
        txtMntProfitAndLost = new JLabelCustomized(GlobalCte.typeData.Montant);
        txtMntProfitAndLost.setEnabled(false);
        txtMntLiquidite = new JLabelCustomized(GlobalCte.typeData.Montant);
        txtMntLiquidite.setEnabled(false);
        txtNmbreMvt = new JTextField("89");
        txtNmbreMvt.setEnabled(false);
        txtNmbreMvt.setColumns(4);
        txtPtdDatePtfCreation.setEnabled(false);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        //
        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        JPanel panelMouvement = new JPanel();
        JPanel panelMontants = new JPanel();
        JPanel panelPtfIdentite = new JPanel();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        // PAGE SYNTHESE PTF
        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        // DEFINITION DE BASE
        //PREMIERE LIGNE
        //https://www.mediaforma.com/java-mise-en-page-gridbaglayout/
        panelPtfIdentite.setBorder(BorderFactory.createTitledBorder("Portfolio Identity"));
        JLabel lblPtfName = new JLabel("Portfolio");
        panelPtfIdentite.add(lblPtfName);
        panelPtfIdentite.add(cmbPortefeuille);
        JLabel lblDatePtfCreation = new JLabel("Date de création");
        panelPtfIdentite.add(lblDatePtfCreation);
        panelPtfIdentite.add(txtPtdDatePtfCreation);
        panelMontants.setBorder(BorderFactory.createTitledBorder("Portfolio Montant"));
        JLabel lblMntInitial = new JLabel("Initial");
        lblMntInitial.setLabelFor(txtMntInitial);
        panelMontants.add(lblMntInitial);
        panelMontants.add(txtMntInitial);

        JLabel lblMntSpot = new JLabel(" Actuel");
        panelMontants.add(lblMntSpot);
        panelMontants.add(txtMntSpot);
        JLabel lblMntFees = new JLabel("Fees");
        panelMontants.add(lblMntFees);
        panelMontants.add(txtMntFees);
        JLabel lblMntProfitAndLost = new JLabel("P&L");
        panelMontants.add(lblMntProfitAndLost);
        panelMontants.add(txtMntProfitAndLost);
        panelMontants.add(lblMntLiquidite);
        panelMontants.add(txtMntLiquidite);
        panelMouvement.setBorder(BorderFactory.createTitledBorder("Mouvements"));
        panelMouvement.add(lblNmbreMvt);
        panelMouvement.add(txtNmbreMvt);

        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        FntGUI.setBagContraint(gbc, 0, 0, 1, 1, 1, 1);
        this.add(panelPtfIdentite, gbc);

        FntGUI.setBagContraint(gbc, 1, 0, 1, 1, 1, 1);
        this.add(panelMontants, gbc);

        FntGUI.setBagContraint(gbc, 2, 0, 1, 1, 1, 1);
        this.add(panelMouvement, gbc);

        // ajout d'un code Listner sur la combo box
        cmbPortefeuille.addActionListener(e -> {
            // récupération du nom du poirtefeuille
            Object ptfName = cmbPortefeuille.getItemAt(cmbPortefeuille.getSelectedIndex());
            this.portefeuille = GlobalData.portfeuilles.get(ptfName);
            this.displayPortefeuilleData();
            listnerChangePtf.OnPtfChanged(this.portefeuille);
        });

        cmbPortefeuille.setSelectedItem(0);
    }

    public void setListenerChangePtf(ListnerChangePtf listener) {
        this.listnerChangePtf = listener;
    }


    private void displayPortefeuilleData() {
        // alimentation des données de synthèse du portefeuille
        txtPtdDatePtfCreation.setText(this.portefeuille.getDateTrade(EnumCrypto.enTypeDate.firstDate).toString());
        txtNmbreMvt.setText(String.valueOf(this.portefeuille.getNbreTrade(EnumCrypto.enTypeTransaction.all)));
        txtMntFees.setValue(this.portefeuille.getMontant(EnumCrypto.enTypeTransaction.all, EnumCrypto.enTypeMontant.Fees));
        txtMntInitial.setValue(this.portefeuille.getMontant(EnumCrypto.enTypeTransaction.all, EnumCrypto.enTypeMontant.Initial));
        txtMntSpot.setValue(this.portefeuille.getMontant(EnumCrypto.enTypeTransaction.all, EnumCrypto.enTypeMontant.Spot));
        txtMntProfitAndLost.setValue(this.portefeuille.getMontant(EnumCrypto.enTypeTransaction.all, EnumCrypto.enTypeMontant.ProfitAndLost));
        txtMntLiquidite.setValue(this.portefeuille.getMontant(EnumCrypto.enTypeTransaction.all, EnumCrypto.enTypeMontant.liquidite));
    }

    public void setPortfeuille(String portfolioName) {
        this.cmbPortefeuille.setSelectedItem(portfolioName);
    }
}
