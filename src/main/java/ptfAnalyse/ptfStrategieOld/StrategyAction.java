package ptfAnalyse.ptfStrategieOld;

import global.EnumCrypto.*;
import global.fonction.FntDates;
import global.fonction.FntPerformance;
import stock.Stock;

import java.time.LocalDate;


public class StrategyAction {

    private final StrategieActionInstrument instrumentFrom;
    private final StrategieActionInstrument instrumentTo;
    private final enTypeQuotation typeQuotation;
    private final enPeriodePerformance periodPerformance;

    public StrategyAction(StrategieActionInstrument instrumentFrom, StrategieActionInstrument instrumentTo, enTypeQuotation typeQuotation, enPeriodePerformance periodPerformance) {
        this.instrumentFrom = instrumentFrom;
        this.instrumentTo = instrumentTo;
        this.typeQuotation = typeQuotation;
        this.periodPerformance = periodPerformance;
    }

    public double getPerformance() {
        LocalDate t1;
        LocalDate t0;
        t1 = FntDates.getMinDate(instrumentFrom.getDateQuotation(enTypeAsset.Stock, enTypeDate.lastDate), instrumentTo.getDateQuotation(enTypeAsset.Stock, enTypeDate.lastDate));
        t0 = FntDates.getDateFromPeriod(t1, this.periodPerformance, true);
        return FntPerformance.getPerformance(instrumentFrom.getStockPrice(), instrumentTo.getStockPrice(), t0, t1, this.typeQuotation);
    }

    public boolean isLastDateTransactionUnderPeriod(enPeriodePerformance periodWhiteoutTransaction) {
        LocalDate datMax;
        LocalDate dateLastTrade;
        dateLastTrade = FntDates.getMaxDate(instrumentFrom.getDateQuotation(enTypeAsset.Position, enTypeDate.lastDate), instrumentTo.getDateQuotation(enTypeAsset.Position, enTypeDate.lastDate));
        datMax = FntDates.getDateFromPeriod(LocalDate.now(), periodWhiteoutTransaction, true);
        return FntDates.isDate1GreaterThanDate2(datMax, dateLastTrade);
    }

    public boolean isActionsOnPosition() {
        enActionOnPosition actionOnInstFrom = instrumentFrom.getActionOnInstrument();
        enActionOnPosition actionOnInstTo = instrumentTo.getActionOnInstrument();
        if ((actionOnInstFrom == enActionOnPosition.StandBy) || (actionOnInstTo == enActionOnPosition.StandBy))
            return false;
        else return actionOnInstFrom != actionOnInstTo;
    }

    public enActionOnPosition getActionInstFrom() {
        return instrumentFrom.getActionOnInstrument();
    }

    public String getInstrumentFrom() {
        return instrumentFrom.getStockPrice().getNom();
    }

    public Stock getStockPriceFrom() {
        return instrumentFrom.getStockPrice();
    }

    public String getInstrumentTo() {
        return instrumentTo.getStockPrice().getNom();
    }

    public Stock getStockPriceTo() {
        return instrumentTo.getStockPrice();
    }


    public Double getTransactionQuantity() {
        // recuperation de la plus petite valeur entre le Delat du From et le Delat du To
        Double deltaValue = Math.min(Math.abs(instrumentFrom.getDifMontantPositionVsAvgPtf()), Math.abs(instrumentTo.getDifMontantPositionVsAvgPtf()));
        return Math.abs(deltaValue / instrumentTo.getSpotPrice(enTypeQuotation.close));


    }

    public Double getTransactionPrice() {
        LocalDate t;
        t = FntDates.getMinDate(instrumentFrom.getStockPrice().getPriceTypeDate(enTypeDate.lastDate), instrumentTo.getStockPrice().getPriceTypeDate(enTypeDate.lastDate));
        return instrumentFrom.getStockPrice().getQuotation(t, typeQuotation) / instrumentTo.getStockPrice().getQuotation(t, this.typeQuotation);
    }

    public Double getInstrumentToLastPriceTypeTransaction() {
        return instrumentTo.getLastPriceTypeTransaction();
    }

    public Double getInstrumentToAvgPriceBuy() {
        return instrumentTo.getAvgPricePosition(enTypeTransaction.buy);
    }

    public Double getInstrumentToSpotPrice() {
        return instrumentTo.getSpotPrice(typeQuotation);
    }


}




