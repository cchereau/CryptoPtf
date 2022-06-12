package stock;

import dataExchangeIO.YahooStockMarketParser;
import global.EnumCrypto.enTypeDate;
import global.EnumCrypto.enTypeQuotation;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import java.util.TreeMap;

public class Stock {
    private final TreeMap<LocalDate, StockPrice> quotations;
    private final String ticker;
    private final String nom;
    private final Boolean updateAuto;

    public Stock(String nom, String ticker, Boolean updateAuto) {
        this.nom = nom;
        this.ticker = ticker;
        this.updateAuto = updateAuto;
        quotations = new TreeMap<>();
    }

    public Stock() {
        this.nom = "";
        this.ticker = "";
        this.updateAuto = false;
        quotations = new TreeMap<>();
    }

    public String getTicker() {
        return ticker;
    }

    public String getNom() {
        return nom;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fonction permettant de recnvoyer des dates première date de quotation, dernière date de quoitation,date proche
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public LocalDate getNearDate(LocalDate date) {
        return this.quotations.floorKey(date);
    }

    public boolean isUpdateAuto() {
        return updateAuto;
    }

    public Double getQuotation(LocalDate dateTime, enTypeQuotation typeQuotation) {
        LocalDate date = this.getNearDate(dateTime);
        try {
            return quotations.get(date).getValue(typeQuotation);
        } catch (Exception e) {
            System.out.println("Error StockPrice.getQuotation - Currency:" + this.ticker);
        }
        return 0d;
    }

    public TreeMap<LocalDate, Double> getQuotation(enTypeQuotation typeQuotation) {
        TreeMap<LocalDate, Double> values = new TreeMap<>();
        for (Map.Entry<LocalDate, StockPrice> entry : this.quotations.entrySet())
            values.put(entry.getKey(), entry.getValue().getValue(typeQuotation));
        return values;
    }
    public TreeMap<LocalDate, StockPrice> getQuotations() {
        return this.quotations;
    }

    public void addQuotation(StockPrice stockPrice, Boolean saveFile) {

        this.quotations.put(stockPrice.getDate(), stockPrice);
        if (saveFile)
            try {
                YahooStockMarketParser.CreateJSONFile(this);
            } catch (IOException e) {
                System.out.println(e.getCause());
            }
    }

    public void addQuotations(TreeMap<LocalDate, StockPrice> quotations) {
        this.quotations.putAll(quotations);
    }

    public LocalDate getPriceTypeDate(enTypeDate typeDate) {
        if (this.quotations.size() == 0)
            return LocalDate.of(2020, Month.JANUARY, 1);

        if (typeDate.equals(enTypeDate.lastDate))
            return this.quotations.lastKey();
        else
            return this.quotations.firstKey();

    }


}
