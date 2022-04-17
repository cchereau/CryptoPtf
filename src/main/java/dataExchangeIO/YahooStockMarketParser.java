package dataExchangeIO;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import global.EnumCrypto.enTypeQuotation;
import global.GlobalData;
import global.fonction.gblFunction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import stock.Stock;
import stock.StockPrice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

public class YahooStockMarketParser {

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Chargement à partir d'un Ticker de l'historique des données stockée dans les répertoire FILE_STOCK...
    // si Boleen est vrai, alors récupération des données provenant de ExternalLink.Yahoo.
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static TreeMap<LocalDate, StockPrice> getStockHQuotation(Stock stock, Boolean refreshData) throws IOException, UnirestException, ParseException {

        String fullNameFile = GlobalData.configStockPriceDirectory + stock.getTicker() + GlobalData.configStockPriceExtension;
        TreeMap<LocalDate, StockPrice> stockHistorical = new TreeMap<>();
        String data;

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // récupération des données provenant du fichier
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        data = gblFunction.getFileStrockPrice(fullNameFile);
        LocalDate dateDeb;

        // initialisation treeMap
        if (!data.isEmpty())
            stockHistorical.putAll(tranformJsonToStockPrice(data));

        // si pas update des historiques renvoie les données;
        if (!refreshData)
            return stockHistorical;

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Récupération des données provenant de ExternalLink.Yahoo
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // récupération des valeurs de début et de fin
        if (stockHistorical.size() == 0)
            dateDeb = LocalDate.of(2020, Month.JANUARY, 1);
        else
            dateDeb = stockHistorical.lastKey();

        LocalDate datFin = LocalDate.now();
        if (stock.isUpdateAuto()) {
            try {
                data = getStockYahooFromTo(stock.getTicker(), dateDeb.atStartOfDay(), datFin.atStartOfDay());
                if (data != null) stockHistorical.putAll(tranformJsonToStockPrice(data));
            } catch (UnirestException e) {
                System.out.println("ERREUR:" + stock.getTicker() + "-" + YahooStockMarketParser.class.getCanonicalName() + "-" + e.getMessage());
            }

        }
        return stockHistorical;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Sauvegarde des données Stock
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void CreateJSONFile(Stock stock) throws IOException {
        String cheminDuFichier = GlobalData.configStockPriceDirectory + stock.getTicker() + GlobalData.configStockPriceExtension;
        File file = new File(cheminDuFichier);
        if (!file.exists())
            file.createNewFile();

        FileWriter writer = new FileWriter(file);

        JSONObject obj = new JSONObject();
        obj.put("name", stock.getNom());
        obj.put("code", stock.getTicker());

        JSONArray quotations = new JSONArray();
        for (Map.Entry<LocalDate, StockPrice> entry1 : stock.getQuotations().entrySet()) {

            JSONObject objQuotation = new JSONObject();
            objQuotation.put("date", entry1.getValue().getDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() / 1000);
            objQuotation.put("open", entry1.getValue().getValue(enTypeQuotation.open));
            objQuotation.put("close", entry1.getValue().getValue(enTypeQuotation.close));
            objQuotation.put("high", entry1.getValue().getValue(enTypeQuotation.high));
            objQuotation.put("low", entry1.getValue().getValue(enTypeQuotation.low));
            objQuotation.put("volume", entry1.getValue().getValue(enTypeQuotation.volume));
            quotations.add(objQuotation);
        }

        obj.put("prices", quotations);
        writer.write(obj.toJSONString());
        writer.flush();
        writer.close();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // RECUPERATION DES DONNEE PROVENANT DE YAHOO
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static String getStockYahooFromTo(String yahooInstrument, LocalDateTime from, LocalDateTime to) throws UnirestException {
        //https://rapidapi.com/developer/dashboard

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // {
        //  "prices":
        //  [
        //      {"date":1590966000,"open":8512.427734375,"high":9164.1865234375,"low":8491.755859375,"close":9138.7880859375,"volume":31638321031,"adjclose":9138.7880859375},
        //
        //   ],
        //   "isPending":false,
        //   "firstTradeDate":1410908400,
        //   "id":"1d15778698301590999030",
        //   "timeZone":
        //   {"gmtOffset":3600},"eventsData":[]
        //  }
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        int minusDay = 3;

        String returnData;

        String API_URL = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-historical-data?frequency=1d&filter=history&";
        String API_HOST = "apidojo-yahoo-finance-v1.p.rapidapi.com";

        //String API_URL ="https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v3/get-historical-data?frequency=1d&filter=history&";
        //String API_HOST = "yh-finance.p.rapidapi.com";

        String API_KEY = "0f67e7f9edmshb8e87fc9b3aea96p196abbjsn48a46b99f53a";

        long _fromTs = from.minusDays(minusDay).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() / 1000;
        long _toTs = to.toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() / 1000;

        String urlAPI = API_URL +
                "period1=" + _fromTs +
                "&period2=" + _toTs +
                "&symbol=" + yahooInstrument;

        HttpResponse<String> response = Unirest.get(urlAPI)
                .header("x-rapidapi-host", API_HOST)
                .header("x-rapidapi-key", API_KEY)
                .asString();
        System.out.println(response.getBody());
        returnData = response.getBody();
        return returnData;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fonction permettant de parser les data en entrée étant un Stirng contenant le json
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static Map<? extends LocalDate, ? extends StockPrice> tranformJsonToStockPrice(String data) throws ParseException {

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(data);
        TreeMap<LocalDate, StockPrice> quotations = new TreeMap<>();
        // get an array from the JSON object
        JSONArray prices = (JSONArray) jsonObject.get("prices");

        // take each value from the json array separately
        for (Object price : prices) {
            JSONObject innerObj = (JSONObject) price;
            StockPrice stockPrice = new StockPrice();

            long lngTimeStamp = Long.parseLong(innerObj.get("date").toString());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(lngTimeStamp * 1000);
            stockPrice.setDate(LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId()).toLocalDate());

            try {
                stockPrice.setValue(enTypeQuotation.open, Double.parseDouble(innerObj.get("open").toString()));
                stockPrice.setValue(enTypeQuotation.close, Double.parseDouble(innerObj.get("close").toString()));
                stockPrice.setValue(enTypeQuotation.high, Double.parseDouble(innerObj.get("high").toString()));
                stockPrice.setValue(enTypeQuotation.low, Double.parseDouble(innerObj.get("low").toString()));
                stockPrice.setValue(enTypeQuotation.volume, Double.parseDouble(innerObj.get("volume").toString()));
                quotations.put(stockPrice.getDate(), stockPrice);
            } catch (NullPointerException e) {
                System.out.println("Erreur sur une date");
            }

        }
        return quotations;
    }


}
