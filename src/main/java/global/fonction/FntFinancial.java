package global.fonction;

import global.EnumCrypto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class FntFinancial {
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fonction commune
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static Double arrondi(double A, int B) {
        return (Math.floor(A * Math.pow(10, B))) / Math.pow(10, B);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fonction de calcul d'une moyenne mobile
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static TreeMap<LocalDate, Double> getMoyenneMobile(TreeMap<LocalDate, Double> quotations, int dureeMoyenneMobile) {

        TreeMap<LocalDate, Double> values = new TreeMap<>();

        for (Map.Entry<LocalDate, Double> entry : quotations.entrySet()) {
            try {
                // récupération du segment de valeur
                TreeMap<LocalDate, Double> mapValeur = getData(entry.getKey(), quotations, dureeMoyenneMobile, true);
                values.put(entry.getKey(), MoyenneSurSegement(mapValeur));
            } catch (IndexOutOfBoundsException ignored) {
                //System.out.println("getMoyenneMobile : Erreur");
            }
        }
        return values;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Fonction de calcul de la performance en base 100 d'une serie
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static TreeMap<LocalDate, Double> getBase100(TreeMap<LocalDate, Double> quotation) {
        TreeMap<LocalDate, Double> values = new TreeMap<>();
        if (quotation.isEmpty())
            return values;
        Double v0 = quotation.get(quotation.firstKey());
        for (Map.Entry<LocalDate, Double> entry : quotation.entrySet())
            values.put(entry.getKey(), FntFinancial.arrondi(((entry.getValue() - v0) / v0) * 100, 2));
        return values;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fonction de calcul d'une extraction de données. Cette fonction permet de prendre n'importe quelle série en entrée
    // et ressortir un nombre de quotation déterminée. Chaque valeur représente la moyenne du segment qu'il représente
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static TreeMap<LocalDate, Double> getData(TreeMap<LocalDate, Double> data, EnumCrypto.enPeriodePerformance periode) {
        if (data.isEmpty())
            return new TreeMap<>();

        LocalDate t1 = data.lastKey();
        LocalDate t0 = switch (periode) {
            case WEEK -> t1.minusDays(7);
            case MONTH -> t1.minusDays(30);
            case QUARTER -> t1.minusDays(90);
            case SEMESTER -> t1.minusDays(180);
            case YEAR -> t1.minusDays(360);
            default -> data.firstKey();
        };
        if (t0.compareTo(data.firstKey()) < 0)
            t0 = data.firstKey();

        return getData(t0, t1, data);
    }

    private static TreeMap<LocalDate, Double> getData(LocalDate t, TreeMap<LocalDate, Double> data, int nbreValeur, Boolean toBegin) throws IndexOutOfBoundsException {
        // récupération
        ArrayList<LocalDate> listDate = new ArrayList<>(data.keySet());
        LocalDate t0, t1;
        int index = listDate.indexOf(t);
        if (toBegin) {
            t0 = listDate.get(index - nbreValeur);
            t1 = t;
        } else {
            t0 = t;
            t1 = listDate.get(index + nbreValeur);
        }
        return getData(t0, t1, data);
    }

    private static TreeMap<LocalDate, Double> getData(LocalDate t0, LocalDate t1, TreeMap<LocalDate, Double> data) {
        // récupération de la valeur proche de date deb dans la serie
        t0 = data.ceilingKey(t0);

        // par defintion on va ajouter un jour à la endate
        t1 = data.floorKey(t1);
        return new TreeMap<>(data.subMap(t0, true, t1, true));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Moyenne sur un segement
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static Double MoyenneSurSegement(TreeMap<LocalDate, Double> data) {
        double res = 0d;
        for (Map.Entry<LocalDate, Double> entrySegement : data.entrySet())
            res = res + entrySegement.getValue();
        return FntFinancial.arrondi(res / data.size(), 6);
    }

    private static Double MoyennePondereeSurSegement(TreeMap<LocalDate, Double> data) {
        double res = 0d;
        int x = 1;
        for (Map.Entry<LocalDate, Double> entrySegement : data.entrySet()) {
            res = res + (Math.round(x / data.size()) * entrySegement.getValue());
            x++;
        }
        return FntFinancial.arrondi(res, 6);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fonction de recherche des points de croisement de deux serie
    //  resulat = 1 lorsque Serie 1 est > à serie 2
    // resultat =-1 lorsque Serie 1 est < à serie 2
    // resultat = 0 lorsque Serie 1 est = Serie 2
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static TreeMap<LocalDate, Double> getCroisementsCourbes(TreeMap<LocalDate, Double> serie1, TreeMap<LocalDate, Double> serie2) {
        TreeMap<LocalDate, Double> values = new TreeMap<>();
        for (Map.Entry<LocalDate, Double> entry : serie1.entrySet()) {
            LocalDate date = serie2.floorKey(entry.getKey());
            if (date != null)
                if (entry.getValue() > serie2.get(date))
                    values.put(entry.getKey(), 1d);
                else if (entry.getValue() < serie2.get(date))
                    values.put(entry.getKey(), -1d);
                else values.put(entry.getKey(), 0d);
        }
        return values;
    }

    public static Double getDerivee(LocalDate date, TreeMap<LocalDate, Double> data) {
        LocalDate t0, t1;
        t0 = data.floorKey(date.minusDays(1));
        t1 = data.ceilingKey(date.plusDays(1));
        if (t1 == null)
            return 0d;
        if (t1.compareTo(data.lastKey()) > 0)
            return 0d;
        // recuperation des données sur une semaine
        TreeMap<LocalDate, Double> segmentData = getData(t0, t1, data);
        // calcul de la derivee
        return (segmentData.get(segmentData.firstKey()) - segmentData.get(segmentData.lastKey())) / (segmentData.lastKey().compareTo(segmentData.firstKey()));
    }


}
