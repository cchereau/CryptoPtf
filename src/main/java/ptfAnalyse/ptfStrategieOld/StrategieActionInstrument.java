package ptfAnalyse.ptfStrategieOld;

import global.EnumCrypto.*;
import ptfManagement.Position;
import ptfManagement.Transaction;
import stock.Stock;

import java.time.LocalDate;

public class StrategieActionInstrument {
    private final Position position;
    private final Double averagePtfMontant;
    private enActionOnPosition actionOnPosition;

    public StrategieActionInstrument(Double averagePtfMontant, Position position) {
        this.position = position;
        this.averagePtfMontant = averagePtfMontant;
    }

    public enActionOnPosition getActionOnInstrument() {
        return actionOnPosition;
    }

    public void setActionOnInstument(int tolerancePositionInPtf) {
        double valeur = ((position.getMontant(enTypeTransaction.all, enTypeMontant.Spot) - averagePtfMontant) / averagePtfMontant) * 100;
        if (Math.abs(valeur) < tolerancePositionInPtf)
            this.actionOnPosition = enActionOnPosition.StandBy;
        else if (valeur > 0)
            this.actionOnPosition = enActionOnPosition.Alleger;
        else
            this.actionOnPosition = enActionOnPosition.Renforcer;
    }

    public Stock getStockPrice() {
        return this.position.getStockPrice();
    }

    public LocalDate getDateQuotation(enTypeAsset typeAsset, enTypeDate typeDate) {
        return position.getPositionTypeAssetTypeDate(typeAsset, typeDate).toLocalDate();
    }

    public double getDifMontantPositionVsAvgPtf() {
        return this.averagePtfMontant - position.getMontant(enTypeTransaction.all, enTypeMontant.Spot);
    }

    public Double getAvgPricePosition(enTypeTransaction typeTransaction) {
        return position.getPositionAvgPrice(typeTransaction);
    }

    public Double getLastPriceTypeTransaction() {
        Transaction transaction;
        transaction = getLastTransactionTypeTransaction();

        if (transaction != null)
            return transaction.getEurPrice();
        return 0d;
    }

    public Double getSpotPrice(enTypeQuotation typeQuotation) {
        return position.getPositionSpotPrice(typeQuotation);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fonction qui renvoie la dernière transaction dans un type spécifique
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private Transaction getLastTransactionTypeTransaction() {
        enTypeTransaction typeTransaction = switch (this.getActionOnInstrument()) {
            case Acheter, Renforcer -> enTypeTransaction.buy;
            case Alleger, Vendre -> enTypeTransaction.sell;
            default -> enTypeTransaction.other;
        };
        return position.getTransaction(enTypeDate.lastDate, typeTransaction);
    }


}
