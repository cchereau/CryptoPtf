package ptfManagement;

import global.EnumCrypto;
import global.EnumCrypto.enPositionShortLong;
import stock.StockComposite;

public class Composition {

    String instrument;
    Double quantite;
    Double prixMoyen;
    StockComposite stock;

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instName) {
        this.instrument = instName;
    }

    public enPositionShortLong getPositionShortLong() {
        if (quantite > 0d) return enPositionShortLong.posLong;
        else if (quantite < 0d) return enPositionShortLong.posShort;
        else return enPositionShortLong.posNull;
    }

    public Double getQuantite() {
        return quantite;
    }

    public void setQuantite(Double quantite) {
        this.quantite = quantite;
    }

    public Double getPrixMoyen() {
        return prixMoyen;
    }

    public void setPrixMoyen(Double prixMoyen) {
        this.prixMoyen = prixMoyen;
    }

    public Double getPrixMarche() {
        return this.stock.getQuotation(stock.getPriceTypeDate(EnumCrypto.enTypeDate.lastDate), EnumCrypto.enTypeQuotation.close);
    }

    public EnumCrypto.enSimulationResultat getResultatSimulation() {
        switch (this.getPositionShortLong()) {
            case posLong: // Position longue, ce qui signifie que si le prix de la composition < prix marché il faut vendre
                if (this.getPrixMoyen() < this.getPrixMarche())
                    return EnumCrypto.enSimulationResultat.StandBy;
                else
                    return EnumCrypto.enSimulationResultat.Sell;
            case posShort:
                if (this.getPrixMoyen() < this.getPrixMarche())
                    return EnumCrypto.enSimulationResultat.Buy;
                else
                    return EnumCrypto.enSimulationResultat.StandBy;

            default:
                return EnumCrypto.enSimulationResultat.StandBy;
        }
    }

    public Double getPerformanceAvgMkt() {
        return (global.fonction.FntFinancial.arrondi(this.prixMoyen / this.getPrixMarche(), 2));
    }

    public StockComposite getStock() {
        return this.stock;
    }

    public void setStock(StockComposite stock) {
        this.stock = stock;
    }


}
