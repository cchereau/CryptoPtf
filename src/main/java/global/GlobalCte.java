package global;

import java.awt.*;

public final class GlobalCte {

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // VARIABLE GUI
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String[] entetesQuotationhistory = {"Date", "Open", "Close", "High", "Low", "Volume"};


    public static final String[] entetesStrategie = {"Action", "From", "To", "Quantité", "Prix", "Performance", "Buy Spot Price", "Buy Avg Price", "Buy Last price"};
    public static final int COL_STRATEGIE_ACTION = 0;
    public static final int COL_STRATEGIE_INST1_TICKER = 1;
    public static final int COL_STRATEGIE_INST2_TICKER = 2;
    public static final int COL_STRATEGIE_QUANTITE = 3;
    public static final int COL_STRATEGIE_PRICE = 4;
    public static final int COL_STRATEGIE_PERFORMANCE = 5;
    public static final int COL_STRATEGIE_INST_TO_SPOT_PRICE = 6;
    public static final int COL_STRATEGIE_INST_TO_AVG_PRICE_BUY = 7;
    public static final int COL_STRATEGIE_INST_TO_LAST_PRICE_BUY = 8;


    public static final String[] entetesPortfeuille = {"Pos. Open", "Positions", "Quantité", "Court Moyen", "Spot Eur", "Montanr Spot", "P&L", "Perf1W", "Perf1M", "Perf3M", "Perf6M", "perf1Y", "Update Auto", "Balance Euro"};
    public static final int COL_PTF_IS_POSTION_OPEN = 0;
    public static final int COL_PTF_POSITION = 1;
    public static final int COL_PTF_QUANTITE = 2;
    public static final int COL_PTF_COURT_MT = 3;
    public static final int COL_PTF_COURT_SPOT = 4;
    public static final int COL_PTF_MOTANT_POSITION = 5;
    public static final int COL_PTF_PL = 6;
    public static final int COL_PTF_PERF_1W = 7;
    public static final int COL_PTF_PERF_1M = 8;
    public static final int COL_PTF_PERF_3M = 9;
    public static final int COL_PTF_PERF_6M = 10;
    public static final int COL_PTF_PERF_1Y = 11;
    public static final int COL_PTF_STOCK_UPDATE_AUTO = 12;
    public static final int COL_PTF_BALANCE_EURO = 13;

    public static final String[] entetesQuotationsUpdate = {"Code", "Date Premiere Quotation", "Date Dernière Quotation", "Valeur Dernière Quotation"};
    public static final int COL_QUOTATIONS_CODE = 0;
    public static final int COL_QUOTATIONS_FIRST_DATE = 1;
    public static final int COL_QUOTATIONS_LAST_DATE = 2;
    public static final int COL_QUOTATIONS_LAST_VALEUR = 3;
    public static final int COL_QUOTATION_HISTORY_DATE = 0;
    public static final int COL_QUOTATION_HISTORY_OPEN = 1;
    public static final int COL_QUOTATION_HISTORY_CLOSE = 2;
    public static final int COL_QUOTATION_HISTORY_LOW = 3;
    public static final int COL_QUOTATION_HISTORY_HIGH = 4;
    public static final int COL_QUOTATION_HISTORY_VOLUME = 5;

    public static final String[] entetesComposition = {"Short/Long", "Action", "Code", "Quantite", "Prix Avg", "Prix Marche", "Performance", "Montant Transaction", "P&L"};
    public static final int COL_COMPOSITION_CODE = 2;
    public static final int COL_COMPOSITION_SHORT_LONG = 0;
    public static final int COL_COMPOSITION_QUANTITE = 3;
    public static final int COL_COMPOSITION_PRIX_MOYEN = 4;
    public static final int COL_COMPOSITION_PRIX_MARCHE = 5;
    public static final int COL_COMPOSITION_PRIX_PERFORMANCE = 6;
    public static final int COL_COMPOSITION_PRIX_PROPAL_BUY_SELL = 1;
    public static final int COL_COMPOSITION_PRIX_PROPAL_MONTANT_TRANSACTION = 7;
    public static final int COL_COMPOSITION_PRIX_PROPAL_PL = 8;


    public static final String[] entetesPortfeuilleDetail = {"Nom", "Liquidite", "Montant Initial", "P&L", "Fees"};
    public static final int COL_PTF_DETAIL_NAME = 0;
    public static final int COL_PTF_DETAIL_LIQUIDITE = 1;
    public static final int COL_PTF_DETAIL_MONTANT_INITIAL = 2;
    public static final int COL_PTF_DETAIL_PL = 3;
    public static final int COL_PTF_DETAIL_FEES = 4;

    public static final String[] entetesPortfeuilleSythese = {"Date", "mnt initial", "mnt Final", "Liquidite", "Valorisation", "P&L", "Fees"};
    public static final int COL_PTF_SYNTHESE_DATE = 0;
    public static final int COL_PTF_SYNTHESE_MONTANT_INITIAL = 1;
    public static final int COL_PTF_SYNTHESE_MONTANT_FINAL = 2;
    public static final int COL_PTF_SYNTHESE_LIQUIDITE = 3;
    public static final int COL_PTF_SYNTHESE_VALORISATION = 4;
    public static final int COL_PTF_SYNTHESE_PL = 5;
    public static final int COL_PTF_SYNTHESE_FEES = 6;

    public static final String[] entetesTransactions = {"Nom", "Date", "Actions", "Quantité", "Court", "Sous Total", "fees", "Total", "Balance Position", "Balance Share", "Balance Price", "Note"};
    public static final int COL_TRADE_INSTNAME = 0;
    public static final int COL_TRAFDE_DATE = 1;
    public static final int COL_TRADE_ACTION = 2;
    public static final int COL_TRADE_QUANTITE = 3;
    public static final int COL_TRADE_COURT = 4;
    public static final int COL_TRADE_SOUS_TOTAL = 5;
    public static final int COL_TRADE_FEES = 6;
    public static final int COL_TRADE_TOTAL = 7;
    public static final int COL_TRADE_BALANCE_POSITION = 8;
    public static final int COL_TRADE_BALANCE_SHARE = 9;
    public static final int COL_TRADE_BALANCE_PRICE = 10;
    public static final int COL_TRADE_NOTE = 11;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // FICHIER DE CONFIGURATION
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static String config_FichierName = "crypto.config";
    // VARIABLE PORTEFEUILLE
    public static String config_ptfDirectoryUpdate = "ptfUpdateFile";
    public static String config_ptfDirectory = "ptfFile";
    public static String config_ptfDirectoryBackup = "ptfFileBackup";
    public static String config_ptfFileName = "ptfFileName";
    // VARIABLE STOVK PRICE
    public static String config_stockPriceDirectory = "stockPrice";
    public static String config_stockPriceIndex = "stockPriceIndex";
    public static String config_stockPriceExtension = "stockPriceExtension";


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GESTIOND DES COULEURS
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static Color colorDARK_GREEN = new Color(0, 153, 0);
    public static Color colorDARK_RED = new Color(204, 0, 0);
    public static Color colorDARK_ORANGE = new Color(220, 153, 0);
    public static Color colorDARK_BLACK = new Color(128, 128, 128);

    public enum typeData {Pourcentage, Montant, Quantite, Date}

}




