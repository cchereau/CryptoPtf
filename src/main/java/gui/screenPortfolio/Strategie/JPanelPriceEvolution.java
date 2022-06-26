package gui.screenPortfolio.Strategie;

import global.EnumCrypto.enPeriodePerformance;
import global.fonction.FntGUI;
import gui.commonComponent.JPanelGraphique.JPanelStockGraph;
import stock.StockComposite;

import javax.swing.*;
import java.awt.*;

public class JPanelPriceEvolution extends JPanel {

    private final JPanelPriceEvolutionDetail panelPriceEvolutionDetail;
    private final JPanelStockGraph panelGrahpPrice;

    public JPanelPriceEvolution() {
        super(new GridBagLayout());

        panelPriceEvolutionDetail = new JPanelPriceEvolutionDetail();
        this.setBorder(BorderFactory.createTitledBorder("Detail"));
        GridBagConstraints gbcPanelDetail = new GridBagConstraints();

        // ajout
        gbcPanelDetail.fill = GridBagConstraints.NONE;
        FntGUI.setBagContraint(gbcPanelDetail, 0, 0, 1, 1, 0.2f, 1f);
        this.add(panelPriceEvolutionDetail, gbcPanelDetail);

        //
        panelGrahpPrice = new JPanelStockGraph(false);
        panelGrahpPrice.setPeriodeComboVisible(false);
        gbcPanelDetail.fill = GridBagConstraints.BOTH;
        FntGUI.setBagContraint(gbcPanelDetail, 1, 0, 1, 1, 0.8f, 1f);
        this.add(panelGrahpPrice, gbcPanelDetail);
    }


    public void setStocks(StockComposite stockComposite, enPeriodePerformance periodePerformance) {
        panelPriceEvolutionDetail.setStocks(stockComposite, periodePerformance);
        panelGrahpPrice.setPeriode(periodePerformance);
        panelGrahpPrice.removeSeries();
        panelGrahpPrice.addSeries(stockComposite);
    }


}
