package gui.screenPosition;

import global.EnumCrypto.*;
import global.GlobalCte;
import global.fonction.FntFinancial;
import global.fonction.FntGUI;
import gui.commonComponent.Component.JLabelCustomized;
import ptfManagement.Position;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class JPanelPositionSynthese extends JPanel {

    private final JLabelCustomized txtNbreTrade = new JLabelCustomized(GlobalCte.typeData.Quantite);
    private final JLabelCustomized txtNbreTradeBuy = new JLabelCustomized(GlobalCte.typeData.Quantite);
    private final JLabelCustomized txtNbreTradeSell = new JLabelCustomized(GlobalCte.typeData.Quantite);


    private final JLabelCustomized txtPrixMoyen = new JLabelCustomized(GlobalCte.typeData.Montant);
    private final JLabelCustomized txtPrixMoyenBuy = new JLabelCustomized(GlobalCte.typeData.Montant);
    private final JLabelCustomized txtPrixMoyenSell = new JLabelCustomized(GlobalCte.typeData.Montant);

    private final JLabelCustomized txtSpotQuantite = new JLabelCustomized(GlobalCte.typeData.Quantite);
    private final JLabelCustomized txtSpotPrice = new JLabelCustomized(GlobalCte.typeData.Montant);
    private final JLabelCustomized txtSpotMontant = new JLabelCustomized(GlobalCte.typeData.Montant);

    private final JLabelCustomized txtPL = new JLabelCustomized(GlobalCte.typeData.Montant);
    private final JLabelCustomized txtTotalFees = new JLabelCustomized(GlobalCte.typeData.Montant);

    private final JLabelCustomized txtDateFirstTrade = new JLabelCustomized(GlobalCte.typeData.Date);
    private final JLabelCustomized txtDateLastTrade = new JLabelCustomized(GlobalCte.typeData.Date);

    private final JCheckBox chkPositionOpen = new JCheckBox("Position Ouverte");

    private Position position;

    public JPanelPositionSynthese() {
        super();
        JPanel panelTrade = new JPanel();
        panelTrade.setBorder(BorderFactory.createTitledBorder("Trade"));
        JLabel lblDateFirstTrade = new JLabel("date first");
        panelTrade.add(lblDateFirstTrade);
        panelTrade.add(txtDateFirstTrade);
        JLabel lblDateLastTrade = new JLabel("date last");
        panelTrade.add(lblDateLastTrade);
        panelTrade.add(txtDateLastTrade);
        JLabel lblNbreTrade = new JLabel("Nbre Total");
        panelTrade.add(lblNbreTrade);
        panelTrade.add(txtNbreTrade);
        JLabel lblNbreTradeBuy = new JLabel("Nbre Buy");
        panelTrade.add(lblNbreTradeBuy);
        panelTrade.add(txtNbreTradeBuy);
        JLabel lblNbreTradeSell = new JLabel("Nbre Sell");
        panelTrade.add(lblNbreTradeSell);
        panelTrade.add(txtNbreTradeSell);

        JPanel panelPrix = new JPanel();
        panelPrix.setBorder(BorderFactory.createTitledBorder("Prix"));
        JLabel lblPrixMoyenSell = new JLabel("Vendeur");
        panelPrix.add(lblPrixMoyenSell);
        panelPrix.add(txtPrixMoyenSell);
        JLabel lblPrixMoyen = new JLabel("Position");
        panelPrix.add(lblPrixMoyen);
        panelPrix.add(txtPrixMoyen);
        JLabel lblPrixMoyenBuy = new JLabel(" Acheteur");
        panelPrix.add(lblPrixMoyenBuy);
        panelPrix.add(txtPrixMoyenBuy);

        JPanel panelPosition = new JPanel();
        panelPosition.setBorder(BorderFactory.createTitledBorder("Spot Position"));
        JLabel lblSpotQuantite = new JLabel("Quantite");
        panelPosition.add(lblSpotQuantite);
        panelPosition.add(txtSpotQuantite);
        JLabel lblSpotPrice = new JLabel("Price");
        panelPosition.add(lblSpotPrice);
        panelPosition.add(txtSpotPrice);
        JLabel lblSpotMontant = new JLabel("Montant");
        panelPosition.add(lblSpotMontant);
        panelPosition.add(txtSpotMontant);

        JPanel panelTotaux = new JPanel();
        panelTotaux.setBorder(BorderFactory.createTitledBorder("Totaux"));
        JLabel lblPL = new JLabel("P&L");
        panelTotaux.add(lblPL);
        panelTotaux.add(txtPL);
        JLabel lblTotalFees = new JLabel("Fees");
        panelTotaux.add(lblTotalFees);
        panelTotaux.add(txtTotalFees);
        panelTotaux.add(chkPositionOpen);

        this.setLayout(new GridBagLayout());
        GridBagConstraints gbcStrategie = new GridBagConstraints();
        gbcStrategie.fill = GridBagConstraints.BOTH;
        FntGUI.setBagContraint(gbcStrategie, 0, 0, 1, 1, 1, 0.1f);
        this.add(panelTrade, gbcStrategie);
        FntGUI.setBagContraint(gbcStrategie, 1, 0, 1, 1, 1, 0.1f);
        this.add(panelPrix, gbcStrategie);
        FntGUI.setBagContraint(gbcStrategie, 2, 0, 1, 1, 1, 0.1f);
        this.add(panelPosition, gbcStrategie);
        FntGUI.setBagContraint(gbcStrategie, 3, 0, 1, 1, 1, 0.1f);
        this.add(panelTotaux, gbcStrategie);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Configuration des Couleur des labels
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////

        // Gestion des listnet
        chkPositionOpen.addActionListener(e -> {
            AbstractButton abstractButton = (AbstractButton) e.getSource();
            position.isPositionOpen(abstractButton.getModel().isSelected());
        });


    }

    public void setPosition(Position position) {

        this.position = position;
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // alimentation des données de synthèse de la position
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // date
        txtDateFirstTrade.setValue(position.getPositionTypeAssetTypeDate(enTypeAsset.Position, enTypeDate.firstDate).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        txtDateLastTrade.setValue(position.getPositionTypeAssetTypeDate(enTypeAsset.Position, enTypeDate.lastDate).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // trade
        int nbreTradeSell, nbreTradeBuy, nbreTrade;
        nbreTrade = position.getNbreTrade(enTypeTransaction.all);
        this.txtNbreTrade.setValue(nbreTrade);
        nbreTradeBuy = position.getNbreTrade(enTypeTransaction.buy);
        this.txtNbreTradeBuy.setValue(nbreTradeBuy);
        nbreTradeSell = position.getNbreTrade(enTypeTransaction.sell);
        this.txtNbreTradeSell.setValue(nbreTradeSell);
        // gestion des couleurs
        txtNbreTradeBuy.removeColorRange();
        this.txtNbreTradeBuy.configColorRange(-9999999d, GlobalCte.colorDARK_GREEN);
        this.txtNbreTradeBuy.configColorRange((double) nbreTradeSell, GlobalCte.colorDARK_RED);
        txtNbreTradeSell.removeColorRange();
        this.txtNbreTradeSell.configColorRange(-9999999d, GlobalCte.colorDARK_GREEN);
        this.txtNbreTradeSell.configColorRange((double) nbreTradeBuy, GlobalCte.colorDARK_RED);


        // Prix 100->PrixmoyenPos --> x --> PrixMoyenBuy
        Double prixMoyenBuy, prixMoyenSell, prixMoyen;
        prixMoyenBuy = FntFinancial.arrondi(position.getPositionAvgPrice(enTypeTransaction.buy), 8);
        this.txtPrixMoyenBuy.setValue(prixMoyenBuy);
        prixMoyenSell = FntFinancial.arrondi(position.getPositionAvgPrice(enTypeTransaction.sell), 8);
        this.txtPrixMoyenSell.setValue(prixMoyenSell);
        prixMoyen = FntFinancial.arrondi(position.getPositionAvgPrice(enTypeTransaction.all), 8);
        this.txtPrixMoyen.setValue(prixMoyen);
        // gestion des couleurs
        this.txtPrixMoyenBuy.removeColorRange();
//        this.txtPrixMoyenBuy.configColorRange(prixMoyen,GlobalCte.colorDARK_ORANGE);
        this.txtPrixMoyenBuy.configColorRange(prixMoyenSell, GlobalCte.colorDARK_GREEN);
        this.txtPrixMoyenSell.removeColorRange();
        this.txtPrixMoyenSell.configColorRange(prixMoyenBuy, GlobalCte.colorDARK_GREEN);
//        this.txtPrixMoyenSell.configColorRange(prixMoyenBuy,GlobalCte.colorDARK_GREEN);

        // alimentation checkBox
        chkPositionOpen.setSelected(position.isPositionOpen());

        // position
        txtSpotQuantite.setValue(Double.toString(position.getPositionQuantiteShare(enTypeTransaction.all)));
        txtSpotPrice.setValue(position.getPositionSpotPrice(enTypeQuotation.close));
        txtSpotMontant.setValue(position.getMontant(enTypeTransaction.all, enTypeMontant.Spot));

        // Totaux
        txtPL.setValue(position.getMontant(enTypeTransaction.all, enTypeMontant.ProfitAndLost));
        this.txtPL.configColorRange(0d, GlobalCte.colorDARK_ORANGE);
        this.txtPL.configColorRange(0d, GlobalCte.colorDARK_GREEN);


        txtTotalFees.setValue(position.getMontant(enTypeTransaction.all, enTypeMontant.Fees));
    }


}
