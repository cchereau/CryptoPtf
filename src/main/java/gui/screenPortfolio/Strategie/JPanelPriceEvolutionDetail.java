package gui.screenPortfolio.Strategie;

import global.EnumCrypto;
import global.EnumCrypto.enPeriodePerformance;
import global.GlobalCte;
import global.fonction.FntDates;
import global.fonction.FntGUI;
import gui.commonComponent.Component.JLabelCustomized;
import stock.Stock;
import stock.StockComposite;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class JPanelPriceEvolutionDetail extends JPanel {

    private final JLabel lblDevise1;
    private final JLabel lblDevise2;
    private final JLabel lblDevise3;
    private final JLabel lblEnd;
    private final JLabel lblStart;

    private final JLabelCustomized valDevise1End;
    private final JLabelCustomized valDevise1Start;
    private final JLabelCustomized valDevise2End;
    private final JLabelCustomized valDevise2Start;
    private final JLabelCustomized valDevise3End;
    private final JLabelCustomized valDevise3Start;

    public JPanelPriceEvolutionDetail() {
        super(new GridBagLayout());
        this.setBorder(BorderFactory.createTitledBorder("Cours "));
        lblDevise1 = new JLabel("Devise1");
        lblDevise2 = new JLabel("Devise2");
        lblDevise3 = new JLabel("Devise3");
        lblEnd = new JLabel("End");
        lblStart = new JLabel("Start");

        valDevise1End = new JLabelCustomized(GlobalCte.typeData.Montant);
        valDevise1Start = new JLabelCustomized(GlobalCte.typeData.Montant);
        valDevise2End = new JLabelCustomized(GlobalCte.typeData.Montant);
        valDevise2Start = new JLabelCustomized(GlobalCte.typeData.Montant);
        valDevise3End = new JLabelCustomized(GlobalCte.typeData.Montant);
        valDevise3Start = new JLabelCustomized(GlobalCte.typeData.Montant);

        GridBagConstraints gbcPanelDetail = new GridBagConstraints();
        gbcPanelDetail.fill = GridBagConstraints.BOTH;

        FntGUI.setBagContraint(gbcPanelDetail, 1, 0, 1, 1);
        this.add(lblEnd, gbcPanelDetail);
        FntGUI.setBagContraint(gbcPanelDetail, 2, 0, 1, 1);
        this.add(lblStart, gbcPanelDetail);

        FntGUI.setBagContraint(gbcPanelDetail, 0, 1, 1, 1);
        this.add(lblDevise1, gbcPanelDetail);
        FntGUI.setBagContraint(gbcPanelDetail, 1, 1, 1, 1);
        this.add(valDevise1End, gbcPanelDetail);
        FntGUI.setBagContraint(gbcPanelDetail, 2, 1, 1, 1);
        this.add(valDevise1Start, gbcPanelDetail);

        FntGUI.setBagContraint(gbcPanelDetail, 0, 2, 1, 1);
        this.add(lblDevise2, gbcPanelDetail);
        FntGUI.setBagContraint(gbcPanelDetail, 1, 2, 1, 1);
        this.add(valDevise2End, gbcPanelDetail);
        FntGUI.setBagContraint(gbcPanelDetail, 2, 2, 1, 1);
        this.add(valDevise2Start, gbcPanelDetail);

        FntGUI.setBagContraint(gbcPanelDetail, 0, 3, 1, 1);
        this.add(lblDevise3, gbcPanelDetail);
        FntGUI.setBagContraint(gbcPanelDetail, 1, 3, 1, 1);
        this.add(valDevise3End, gbcPanelDetail);
        FntGUI.setBagContraint(gbcPanelDetail, 2, 3, 1, 1);
        this.add(valDevise3Start, gbcPanelDetail);
    }

    public void setStocks(Stock from, Stock to, StockComposite stockComposite, enPeriodePerformance periodePerformance) {
        lblDevise1.setText(from.getNom());
        lblDevise2.setText(to.getNom());
        lblDevise3.setText(from.getNom() + "/" + to.getNom());
        LocalDate dateEnd = stockComposite.getNearDate(LocalDate.now());
        LocalDate datStart = FntDates.getDateFromPeriod(dateEnd, periodePerformance, true);
        lblEnd.setText(dateEnd.toString());
        lblStart.setText(datStart.toString());
        valDevise1End.setValue(from.getQuotation(dateEnd, EnumCrypto.enTypeQuotation.close));
        valDevise1Start.setValue(from.getQuotation(datStart, EnumCrypto.enTypeQuotation.close));
        valDevise2End.setValue(to.getQuotation(dateEnd, EnumCrypto.enTypeQuotation.close));
        valDevise2Start.setValue(to.getQuotation(datStart, EnumCrypto.enTypeQuotation.close));
        valDevise3End.setValue(stockComposite.getQuotation(dateEnd, EnumCrypto.enTypeQuotation.close));
        valDevise3Start.setValue(stockComposite.getQuotation(datStart, EnumCrypto.enTypeQuotation.close));
    }
}

