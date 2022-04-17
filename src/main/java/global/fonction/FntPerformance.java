package global.fonction;

import global.EnumCrypto.enPeriodePerformance;
import global.EnumCrypto.enTypeDate;
import global.EnumCrypto.enTypeQuotation;
import stock.Stock;

import java.time.LocalDate;

public class FntPerformance {
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // fonction permettant de renvoyer les performances d'une série
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static Double getPerformance(Stock stock, enTypeQuotation typeQuotation, enPeriodePerformance periodePerformance) {
        try {
            LocalDate t0, t1;
            t1 = stock.getPriceTypeDate(enTypeDate.lastDate);
            t0 = FntDates.getDateFromPeriod(t1, periodePerformance, true);
            return getPerformance(stock, t0, t1, typeQuotation);
        } catch (Exception e) {
            System.out.println(gblFunction.class.getCanonicalName() + "-" + e.getMessage());
            return 0d;
        }

    }

    public static double getPerformance(Stock stock, LocalDate t0, LocalDate t1, enTypeQuotation typeQuotaiton) {
        double valeur0, valeur1;
        valeur0 = stock.getQuotation(t0, typeQuotaiton);
        valeur1 = stock.getQuotation(t1, typeQuotaiton);
        return FntFinancial.arrondi(((valeur1 - valeur0) / valeur0), 6);
    }

    public static double getPerformance(Stock stockFrom, Stock stockTo, enPeriodePerformance periodePerformance, enTypeQuotation typeQuotaiton) {

        LocalDate t1 = getCommonDate(stockFrom, stockTo, LocalDate.now(), true);
        LocalDate t0 = getCommonDate(stockFrom, stockTo, FntDates.getDateFromPeriod(t1, periodePerformance, true), true);
        return getPerformance(stockFrom, stockTo, t0, t1, typeQuotaiton);
    }


    public static double getPerformance(Stock stockFrom, Stock stockTo, LocalDate t0, LocalDate t1, enTypeQuotation typeQuotaiton) {
        double valeur0, valeur1;
        valeur0 = stockFrom.getQuotation(t0, typeQuotaiton) / stockTo.getQuotation(t0, typeQuotaiton);
        valeur1 = stockFrom.getQuotation(t1, typeQuotaiton) / stockTo.getQuotation(t1, typeQuotaiton);
        return FntFinancial.arrondi(((valeur1 - valeur0) / valeur0), 6);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // FOnction en charge de chercher la première date commune
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private static LocalDate getCommonDate(Stock stockFrom, Stock stockTo, LocalDate date, boolean forward) {

        try {
            boolean end = false;

            while (!end) {
                if ((stockFrom.getNearDate(date).equals(date)) && (stockTo.getNearDate(date).equals(date)))
                    end = true;
                else if (forward) date = date.minusDays(1);
                else date = date.plusDays(1);
            }
            return date;
        } catch (NullPointerException e) {
            System.out.println(stockFrom.getNom() + "/" + stockTo.getNom() + " : " + e.getMessage());
        }
        return LocalDate.now();
    }


}
