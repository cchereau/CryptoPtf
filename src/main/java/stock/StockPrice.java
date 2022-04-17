package stock;

import global.EnumCrypto.enTypeQuotation;

import java.time.LocalDate;

public class StockPrice {

    private LocalDate date;
    private Double high;
    private Double low;
    private Double open;
    private Double close;
    private Double volume;

    public StockPrice() {
        this.date = null;
        this.high = null;
        this.low = null;
        this.open = null;
        this.close = null;
        this.volume = null;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setValue(enTypeQuotation typeQuotation, Double value) {
        switch (typeQuotation) {
            case close:
                this.close = value;
                break;
            case low:
                this.low = value;
                break;
            case high:
                this.high = value;
                break;
            case open:
                this.open = value;
                break;
            case volume:
                this.volume = value;
                break;
            default:
                break;
        }
    }

    public Double getValue(enTypeQuotation typeQuotation) {
        switch (typeQuotation) {
            case high:
                return this.high;
            case low:
                return this.low;
            case open:
                return this.open;
            case close:
                return this.close;
            case volume:
                return this.volume;
            default:
                return 0d;
        }
    }

}
