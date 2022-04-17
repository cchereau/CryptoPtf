package stock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

public class Stocks {
    private final TreeMap<String, Stock> stocks;

    public Stocks() {
        stocks = new TreeMap<>();
    }

    public Collection<Stock> getStocks() {
        return this.stocks.values();
    }

    public Stock getStock(String stockName) {
        return this.stocks.get(stockName);
    }

    public void setStock(String stockName, Stock stock) {
        this.stocks.put(stockName, stock);
    }

    public ArrayList<String> getStockTickers() {
        return new ArrayList<>(stocks.keySet());
    }

}
