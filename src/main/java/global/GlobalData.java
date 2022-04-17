package global;

import ptfManagement.Portefeuille;
import stock.Stocks;

import java.util.TreeMap;

public class GlobalData {
    public static TreeMap<String, Portefeuille> portfeuilles = new TreeMap<>();
    public static Stocks stocks;


    public static String configPortfeuilleDirectoryUpdate;
    public static String configPortefeuilleDirectory;
    public static String configPortefeuilleDirectoryBackup;
    public static String configPortefeuilleJSON;

    public static String configStockPriceDirectory;
    public static String configStockPriceFileIndex;
    public static String configStockPriceExtension;


}
