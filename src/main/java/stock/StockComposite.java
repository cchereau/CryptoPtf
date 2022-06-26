package stock;

import global.EnumCrypto.enTypeDate;
import global.EnumCrypto.enTypeQuotation;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.TreeMap;

public class StockComposite extends Stock {
    Stock stock1;
    Stock stock2;

    public StockComposite(Stock stock1, Stock stock2) {
        super(stock1.getNom() + "/" + stock2.getNom(),
                stock1.getTicker() + "/" + stock2.getTicker(),
                false);
        this.stock1 = stock1;
        this.stock2 = stock2;
    }

    public Stock getStockFrom() {
        return this.stock1;
    }

    public Stock getStockTo() {
        return this.stock2;
    }


    @Override
    public LocalDate getPriceTypeDate(enTypeDate typeDate) {
        if (stock1.getPriceTypeDate(typeDate).compareTo(stock2.getPriceTypeDate(typeDate)) >= 0)
            return stock2.getPriceTypeDate(typeDate);
        else
            return stock1.getPriceTypeDate(typeDate);
    }

    @Override
    public Double getQuotation(LocalDate dateTime, enTypeQuotation typeQuotation) {
        double result;
        try {
            result = stock1.getQuotation(dateTime, typeQuotation) / stock2.getQuotation(dateTime, typeQuotation);
        } catch (Exception e) {
            result = 0d;
        }
        return result;
    }

    @Override
    public TreeMap<LocalDate, Double> getQuotation(enTypeQuotation typeQuotation) {
        // vérifie que l'historique monté en mmémoire
        if (super.getQuotations().size() == 0)
            this.getQuotations();
        return super.getQuotation(typeQuotation);
    }

    @Override
    public TreeMap<LocalDate, StockPrice> getQuotations() {
        if (super.getQuotations().size() == 0) {
            HashSet<LocalDate> date = new HashSet<>();
            date.addAll(stock1.getQuotations().keySet());
            date.addAll(stock2.getQuotations().keySet());

            // parcours de la nouvelle liste
            for (LocalDate entry : date) {
                try {
                    StockPrice stockPrice = new StockPrice();
                    stockPrice.setDate(entry);
                    stockPrice.setValue(enTypeQuotation.open, stock1.getQuotation(entry, enTypeQuotation.open) / stock2.getQuotation(entry, enTypeQuotation.open));
                    stockPrice.setValue(enTypeQuotation.close, stock1.getQuotation(entry, enTypeQuotation.close) / stock2.getQuotation(entry, enTypeQuotation.close));
                    stockPrice.setValue(enTypeQuotation.high, stock1.getQuotation(entry, enTypeQuotation.high) / stock2.getQuotation(entry, enTypeQuotation.high));
                    stockPrice.setValue(enTypeQuotation.low, stock1.getQuotation(entry, enTypeQuotation.low) / stock2.getQuotation(entry, enTypeQuotation.low));
                    stockPrice.setValue(enTypeQuotation.open, -1d);
                    this.addQuotation(stockPrice, false);
                } catch (Exception e) {
                    System.out.println("Erreur StockPrice Convertion" + entry.toString());
                }
            }
        }
        return super.getQuotations();
    }

    @Override
    public boolean isUpdateAuto() {
        return true;
    }


}
