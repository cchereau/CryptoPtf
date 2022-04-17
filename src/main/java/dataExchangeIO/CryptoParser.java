package dataExchangeIO;

import com.mashape.unirest.http.exceptions.UnirestException;
import global.EnumCrypto.*;
import global.GlobalData;
import global.fonction.gblFunction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import ptfManagement.*;
import stock.Stock;
import stock.Stocks;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class CryptoParser {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fonction de LECTURE ET SAUVEGARDE FICHIER JSON - PORTEFEUILLE
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*
    public static TreeMap<String, Portefeuille> ReadPtfJSON() {
        return ReadPtfJSON(GlobalData.configPortefeuilleDirectory + GlobalData.configPortefeuilleJSON);
    }

    public static TreeMap<String, Portefeuille> ReadPtfJSON(String ReadFile) {
        TreeMap<String, Portefeuille> portefeuilles = new TreeMap<>();
        String positionCode;
        try {

            String data = gblFunction.getFileStrockPrice(ReadFile);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObjectPortefeuille = (JSONObject) jsonParser.parse(data);
            JSONArray portfeuillesJSON = (JSONArray) jsonObjectPortefeuille.get(enJSONPortefeuille.Portefeuilles.toString());

            for (Object portefeuilleJSON : portfeuillesJSON) {
                // récupération du nom du portefeuille
                Portefeuille portefeuille = new Portefeuille();
                JSONObject innerObjPositionPtf = (JSONObject) portefeuilleJSON;
                portefeuille.setNom(innerObjPositionPtf.get(enJSONPortefeuille.Nom.toString()).toString());

                ///////////////////////////////////////////////////////////////////////////////////////////////////////
                // Get Position values
                ///////////////////////////////////////////////////////////////////////////////////////////////////////
                JSONArray positionsJSON = (JSONArray) innerObjPositionPtf.get(enJSONPortefeuille.Positions.toString());
                for (Object positionJSON : positionsJSON) {
                    JSONObject innerObjPosition = (JSONObject) positionJSON;
                    positionCode = innerObjPosition.get(enJSONPosition.Code.toString()).toString();
                    Position position = new Position(positionCode);
                    position.isPositionOpen(Boolean.parseBoolean(innerObjPosition.get(enJSONPosition.OpenOnStrategie.toString()).toString()));
                    JSONArray transactionsJSON = (JSONArray) innerObjPosition.get(enJSONPosition.Transactions.toString());
                    for (Object transactionJSON : transactionsJSON) {
                        Transaction transaction = new Transaction();
                        JSONObject innerObjTransaction = (JSONObject) transactionJSON;

                        transaction.setInstrumentName(positionCode);
                        transaction.setDateTransation(LocalDateTime.parse(innerObjTransaction.get(enJSONTransaction.Date.toString()).toString(), formatter));
                        transaction.setTransactionType(enTypeTransaction.valueOf(innerObjTransaction.get(enJSONTransaction.Action.toString()).toString()));
                        double nbreShare = Double.parseDouble(innerObjTransaction.get(enJSONTransaction.NbreShare.toString()).toString());
                        transaction.setNbreShare(nbreShare);
                        double prixShare = Double.parseDouble(innerObjTransaction.get(enJSONTransaction.PrixShare.toString()).toString());
                        transaction.setEurPrice(prixShare);
                        double montantFees = Double.parseDouble(innerObjTransaction.get(enJSONTransaction.Fees.toString()).toString());
                        transaction.setMontant(enTypeMontant.Fees, montantFees);

                        transaction.setMontant(enTypeMontant.MontantWithoutFees, nbreShare * prixShare);
                        transaction.setMontant(enTypeMontant.MontantWithFees, (nbreShare * prixShare) + montantFees);
                        transaction.setNotes(String.valueOf(innerObjTransaction.get(enJSONTransaction.Notes.toString())));
                        position.addTransaction(transaction);
                    }
                    portefeuille.addPosition(position);
                }

                ///////////////////////////////////////////////////////////////////////////////////////////////////////
                // Get Liquidite Mouvement values
                ///////////////////////////////////////////////////////////////////////////////////////////////////////
                JSONObject innerObjLiquiditePtf = (JSONObject) portefeuilleJSON;
                JSONObject innerObjMouvement = (JSONObject) innerObjLiquiditePtf.get(enJSONPortefeuille.LiquiditeEuro.toString());
                JSONArray liquiditesJSON = (JSONArray) innerObjMouvement.get(enJSONLiquidite.Mouvements.toString());
                Liquidite liquidite = new Liquidite();
                for (Object liquiditeJSON : liquiditesJSON) {
                    JSONObject innerObjLiquidite = (JSONObject) liquiditeJSON;
                    LocalDateTime dateTime = LocalDateTime.parse(innerObjLiquidite.get(enJSONLiquidite.Date.toString()).toString(), formatter);
                    enTypeMouvement typeMouvement = enTypeMouvement.valueOf(innerObjLiquidite.get(enJSONLiquidite.TypeMouvement.toString()).toString());
                    Double Montant = Double.parseDouble(innerObjLiquidite.get(enJSONLiquidite.Montant.toString()).toString());
                    Mouvement mouvement = new Mouvement(dateTime, typeMouvement, Montant);
                    liquidite.addMouvement(mouvement);
                }
                portefeuille.addMouvement(liquidite);


                //////////////////////////////////////////////////////////////////////////////////////////////////////
                // Chargement du portefeulle dans la liste des portefeuilles actifs.
                //////////////////////////////////////////////////////////////////////////////////////////////////////

                portefeuilles.put(portefeuille.getNom(), portefeuille);
            }
        } catch (IOException | org.json.simple.parser.ParseException ioException) {
            System.out.println("IOPoertfeuille.ReadPtfJson" + ioException.getLocalizedMessage());
        }
        return portefeuilles;
    }

    public static void SavePtfJSON(TreeMap<String, Portefeuille> portefeuilles) {
        String fileName = GlobalData.configPortefeuilleDirectory + GlobalData.configPortefeuilleJSON;
        File file = new File(fileName);

        try {

            ////////////////////////////////////////////////////////////////////////////////////////
            // Sauvegarde du précédent fichier
            ///////////////////////////////////////////////////////////////////////////////////////
            SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String fileCopy = GlobalData.configPortefeuilleDirectoryBackup + f.format(new Date()) + "_" + GlobalData.configPortefeuilleJSON;
            Files.copy(Paths.get(fileName), Paths.get(fileCopy));
            System.out.println("! Sauvegarde fichier ---- " + fileCopy);

            ////////////////////////////////////////////////////////////////////////////////////////
            // Ecriture du nouveau fichier JAVA JSON Portefeuille
            ///////////////////////////////////////////////////////////////////////////////////////
            FileWriter writer = new FileWriter(file);

            JSONObject portefeuillesJSON = new JSONObject();
            JSONArray portefeuillesArray = new JSONArray();

            for (Map.Entry<String, Portefeuille> mapEntry : portefeuilles.entrySet()) {
                Portefeuille ptf = mapEntry.getValue();

                //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                // ajout des positions
                //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                JSONArray positionJSonArray = new JSONArray();
                for (Position position : ptf.getPositions()) {
                    JSONArray transactionsJSonArray = new JSONArray();
                    // on assure le trie sur les transaction
                    position.getTransactions(enTypeTransaction.all).sort(Transaction.ComparatorDate);

                    for (Transaction transaction : position.getTransactions(enTypeTransaction.all)) {
                        JSONObject transactionJson = new JSONObject();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                        transactionJson.put(enJSONTransaction.Date.toString(), transaction.getDateTransaction().format(formatter));
                        transactionJson.put(enJSONTransaction.Action.toString(), transaction.getTransactionType().toString());
                        transactionJson.put(enJSONTransaction.NbreShare.toString(), Double.toString(transaction.getNbreShare()));
                        transactionJson.put(enJSONTransaction.PrixShare.toString(), Double.toString(transaction.getEurPrice()));
                        transactionJson.put(enJSONTransaction.Fees.toString(), Double.toString(Math.abs(transaction.getMontant(enTypeMontant.Fees))));
                        transactionJson.put(enJSONTransaction.Notes.toString(), transaction.getNotes());
                        transactionJson.put(enJSONTransaction.EurSubTotal.toString(), transaction.getMontant(enTypeMontant.MontantWithoutFees));
                        transactionJson.put(enJSONTransaction.EurTotal.toString(), transaction.getMontant(enTypeMontant.MontantWithFees));
                        transactionsJSonArray.add(transactionJson);
                    }
                    JSONObject positionJson = new JSONObject();
                    positionJson.put(enJSONPosition.Code, position.getInstName());
                    positionJson.put(enJSONPosition.PrixMoyen, Double.toString(position.getPositionAvgPrice(enTypeTransaction.all)));
                    positionJson.put(enJSONPosition.SpotQuantite, Double.toString(position.getPositionQuantiteShare(enTypeTransaction.all)));
                    positionJson.put(enJSONPosition.ProfitAndLost, Double.toString(position.getMontant(enTypeTransaction.all, enTypeMontant.ProfitAndLost)));
                    positionJson.put(enJSONPosition.OpenOnStrategie, position.isPositionOpen());
                    positionJson.put(enJSONPosition.Transactions, Collections.unmodifiableList(transactionsJSonArray));
                    positionJSonArray.add(positionJson);
                }

                //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                // ajout des liquidites
                //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                JSONArray liquiditeJSonArray = new JSONArray();
                // Assure la gestion des liquidite dans l'ordre des dates
                for (Mouvement mouvement : ptf.getLiquiditeMouvements()) {
                    JSONObject liquiditeJson = new JSONObject();
                    liquiditeJSonArray.add(liquiditeJson);
                }
                JSONObject liquiditeJson = new JSONObject();
                liquiditeJson.put(enJSONLiquidite.Total, Double.toString(ptf.getSoldeMouvementLiquidite()));
                liquiditeJson.put(enJSONLiquidite.Mouvements, Collections.unmodifiableList(liquiditeJSonArray));

                // création de l'objet JSON Portfeuille
                JSONObject portefeuillefJSON = new JSONObject();
                portefeuillefJSON.put(enJSONPortefeuille.Nom, ptf.getNom());
                portefeuillefJSON.put(enJSONPortefeuille.MontantInitial, Double.toString(ptf.getMontant(enTypeTransaction.all, enTypeMontant.Initial)));
                portefeuillefJSON.put(enJSONPortefeuille.BalanceEuro, Double.toString(ptf.getMontant(enTypeTransaction.all, enTypeMontant.CumulBalance)));
                portefeuillefJSON.put(enJSONPortefeuille.MontantFeesEuro, Double.toString(ptf.getMontant(enTypeTransaction.all, enTypeMontant.Fees)));
                portefeuillefJSON.put(enJSONPortefeuille.MontantSpotEuro, Double.toString(ptf.getMontant(enTypeTransaction.all, enTypeMontant.Spot)));
                portefeuillefJSON.put(enJSONPortefeuille.Positions, positionJSonArray);
                portefeuillefJSON.put(enJSONPortefeuille.LiquiditeEuro, liquiditeJson);

                portefeuillesArray.add(portefeuillefJSON);
            }
            portefeuillesJSON.put(enJSONPortefeuille.Portefeuilles.toString(), portefeuillesArray);
            writer.write(portefeuillesJSON.toJSONString());
            writer.flush();
            writer.close();

        } catch (IOException ioException) {
            System.out.println("IOportfeueille.savePortefeuille" + ioException.fillInStackTrace());
        }
    }


    public static void SaveStrockJSON() {
        String fileName = GlobalData.configStockPriceDirectory + "temp_" + GlobalData.configStockPriceFileIndex;
        File file = new File(fileName);

        try {
            FileWriter writer = new FileWriter(file);

            JSONObject stockRootJSON = new JSONObject();
            JSONArray stockArray = new JSONArray();
            for (Stock stock : GlobalData.stocks.getStocks()) {
                JSONObject stockJSON = new JSONObject();
                stockJSON.put(enJSONStock.Stock.toString(), stock.getNom());
                stockJSON.put(enJSONStock.Ticker.toString(), stock.getTicker());
                stockJSON.put(enJSONStock.Refresh.toString(), stock.isUpdateAuto());
                stockJSON.put(enJSONStock.Devise.toString(), "Eur");
                stockArray.add(stockJSON);
            }
            stockRootJSON.put("Stocks", stockArray);
            writer.write(stockRootJSON.toJSONString());
            writer.flush();
            writer.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            System.out.println("IOStcok.saveStockJson");

        }
    }

    public static Stocks ReadStockJSON() throws IOException, ParseException, org.json.simple.parser.ParseException, UnirestException {
        String DIR_FULL_SOCK_PRICE_INDEX = GlobalData.configStockPriceDirectory + GlobalData.configStockPriceFileIndex;

        // Ouverture du fichier Index
        StringBuilder data = new StringBuilder();
        File f = new File(DIR_FULL_SOCK_PRICE_INDEX);
        if (!f.exists())
            return null;
        BufferedReader in = new BufferedReader(new FileReader(DIR_FULL_SOCK_PRICE_INDEX));
        String line;
        while ((line = in.readLine()) != null)
            data.append(line);
        in.close();

        // chargement des données dans Stcks
        Stocks stocks = new Stocks();
        // Récupération de toutes les données
        String stockName;
        String stockTicker;
        boolean refresh;
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(String.valueOf(data));
        JSONArray jsonArrayStock = (JSONArray) jsonObject.get("Stocks");

        for (Object stock : jsonArrayStock) {
            JSONObject innerObj = (JSONObject) stock;
            stockName = innerObj.get("Stock").toString();
            stockTicker = innerObj.get("Ticker").toString();
            refresh = Boolean.parseBoolean(innerObj.get(enJSONStock.Refresh.toString()).toString());
            // creation d'un object Stock Price
            Stock stockPrice = new Stock(stockName, stockTicker, refresh);
            // chargement des données historiques
            stockPrice.addQuotations(YahooStockMarketParser.getStockHQuotation(stockPrice, false));
            stocks.setStock(stockName, stockPrice);
        }
        return stocks;
    }


}


