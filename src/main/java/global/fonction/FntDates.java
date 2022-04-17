package global.fonction;

import global.EnumCrypto.enPeriodePerformance;
import global.EnumCrypto.enTypeDate;
import stock.Stock;

import java.time.LocalDate;
import java.util.ArrayList;

public class FntDates {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Outil de gestion des dates
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static LocalDate getDateLastQuotations(ArrayList<Stock> stocks) {
        LocalDate date = getDateFromPeriod(LocalDate.now(), enPeriodePerformance.YEAR, true);
        for (Stock stock : stocks)
            if (date.compareTo(stock.getPriceTypeDate(enTypeDate.lastDate)) < 0)
                date = stock.getPriceTypeDate(enTypeDate.lastDate);
        return date;
    }

    public static LocalDate getDateFromPeriod(LocalDate date, enPeriodePerformance periodePerformance, Boolean reverseTime) {

        if (reverseTime)
            return switch (periodePerformance) {
                case DAY -> date.minusDays(1);
                case WEEK -> date.minusDays(7);
                case MONTH -> date.minusMonths(1);
                case QUARTER -> date.minusMonths(3);
                case SEMESTER -> date.minusMonths(6);
                case YEAR -> date.minusYears(1);
                default -> date;
            };
        else
            return switch (periodePerformance) {
                case DAY -> date.plusDays(1);
                case WEEK -> date.plusDays(7);
                case MONTH -> date.plusMonths(1);
                case QUARTER -> date.plusMonths(3);
                case SEMESTER -> date.plusMonths(6);
                case YEAR -> date.plusYears(1);
                default -> date;
            };

    }

    public static LocalDate getMaxDate(LocalDate date1, LocalDate date2) {
        if (date1.compareTo(date2) > 0)
            return date1;
        else
            return date2;
    }

    public static LocalDate getMinDate(LocalDate date1, LocalDate date2) {
        if (date1.compareTo(date2) > 0)
            return date2;
        else
            return date1;
    }

    public static Boolean isDate1GreaterThanDate2(LocalDate date1, LocalDate date2) {
        return date1.compareTo(date2) >= 0;
    }


}
