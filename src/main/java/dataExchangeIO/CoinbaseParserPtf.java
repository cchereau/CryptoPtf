package dataExchangeIO;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import global.EnumCrypto;
import ptfManagement.Transaction;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class CoinbaseParserPtf {

    private static final String separator = ",";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static ArrayList<Transaction> ParseFileFromCoinBase(String directory, String file) {

        // On récupère la liste des données sous forme d'arrayListe
        String filename = directory + "\\" + file;
        ArrayList<String[]> data = getCSVData(filename);

        filename = directory + "clean_" + file;
        saveFileData(filename, data);

        // récupérationd des données du fichier csv
        ArrayList<String[]> dataClean = getCleanData(data);
        filename = directory + "Compatible_" + file;
        saveFileData(filename, dataClean);

        ArrayList<Transaction> transactions = new ArrayList<>();
        for (String[] dataTransac : dataClean)
            transactions.add(getTransaction(dataTransac));
        return transactions;
    }

    private static Transaction getTransaction(String[] data) {
        Transaction transaction = new Transaction();
        String type = getData(data, enFileStruct.type).toString();

        if (type.compareTo("Receive") == 0)
            transaction.setTransactionType(EnumCrypto.enTypeTransaction.received);
        else if (type.compareTo("Sell") == 0)
            transaction.setTransactionType(EnumCrypto.enTypeTransaction.sell);
        else if (type.compareTo("Send") == 0)
            transaction.setTransactionType(EnumCrypto.enTypeTransaction.send);
        else if (type.compareTo("Buy") == 0)
            transaction.setTransactionType(EnumCrypto.enTypeTransaction.buy);
        else if (type.compareTo("Rewards Income") == 0)
            transaction.setTransactionType(EnumCrypto.enTypeTransaction.received);
        else if (type.compareTo("Coinbase Earn") == 0)
            transaction.setTransactionType(EnumCrypto.enTypeTransaction.received);
        else
            transaction.setTransactionType(EnumCrypto.enTypeTransaction.other);

        transaction.setMontant(EnumCrypto.enTypeMontant.MontantWithFees, (Double) getData(data, enFileStruct.eurTotal));
        transaction.setMontant(EnumCrypto.enTypeMontant.MontantWithoutFees, (Double) getData(data, enFileStruct.eurSousTotal));
        transaction.setNbreShare((Double) getData(data, enFileStruct.quantity));
        transaction.setInstrumentName((String) getData(data, enFileStruct.asset));
        transaction.setDateTransation((LocalDateTime) getData(data, enFileStruct.timeStamp));
        transaction.setNotes((String) getData(data, enFileStruct.note));
        transaction.setEurPrice((Double) getData(data, enFileStruct.eurSpot));
        transaction.setMontant(EnumCrypto.enTypeMontant.Fees,
                transaction.getMontant(EnumCrypto.enTypeMontant.MontantWithFees) - transaction.getMontant(EnumCrypto.enTypeMontant.MontantWithoutFees));
        return transaction;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GESTION DES DONNEE EN ENTREE ET AJOUT POUR LE CAS D'UNE TRANSACTION DE CONVERSION
    // LA PREMIERE TRANSACTION NORMALE? MAIS LA SECONDE EST ISSUE DU BLOC NOTE
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static ArrayList<String[]> getCleanData(ArrayList<String[]> dataInput) {
        ArrayList<String[]> dataOutput = new ArrayList<>();
        String convert = "Convert";
        for (String[] data : dataInput) {
            String typeAction = (String) getData(data, enFileStruct.type);
            // Opération de converison, la sdeconde partie de l'opération est dans le blocnote.
            if (typeAction.compareTo(convert) == 0) {
                try {
                    // intégration de la première opération de vente
                    data[enFileStruct.type.ordinal()] = "Sell";
                    data[enFileStruct.eurSpot.ordinal()] = String.valueOf(
                            Double.parseDouble(data[enFileStruct.eurSousTotal.ordinal()])
                                    / Double.parseDouble(data[enFileStruct.quantity.ordinal()]));
                    dataOutput.add(data);

                    // intégration de la Seconde opération d'achat
                    String[] newData = new String[enFileStruct.values().length];
                    String[] blocNote = data[enFileStruct.note.ordinal()].split("to", -1);
                    StringBuilder blocNoteInstTo = new StringBuilder(blocNote[1]).reverse();
                    int pos = blocNoteInstTo.indexOf(" ");
                    String assetTo = blocNoteInstTo.substring(0, pos);
                    String quantiteTo = blocNoteInstTo.substring(pos, blocNoteInstTo.length() - 1);
                    assetTo = new StringBuilder(assetTo).reverse().toString().replace(" ", "");
                    quantiteTo = new StringBuilder(quantiteTo).reverse().toString().replace(" ", "");

                    newData[enFileStruct.type.ordinal()] = "Buy";
                    newData[enFileStruct.timeStamp.ordinal()] = data[enFileStruct.timeStamp.ordinal()];
                    newData[enFileStruct.asset.ordinal()] = assetTo;
                    newData[enFileStruct.quantity.ordinal()] = quantiteTo;

                    // !!! EVITER DE DOUBLE COMPTER LES FEES !!!
                    newData[enFileStruct.eurFees.ordinal()] = "0";
                    newData[enFileStruct.eurTotal.ordinal()] = data[enFileStruct.eurSousTotal.ordinal()];
                    newData[enFileStruct.eurSousTotal.ordinal()] = data[enFileStruct.eurSousTotal.ordinal()];
                    newData[enFileStruct.eurSpot.ordinal()] = String.valueOf(Double.parseDouble(newData[enFileStruct.eurSousTotal.ordinal()]) /
                            Double.parseDouble(newData[enFileStruct.quantity.ordinal()]));

                    newData[enFileStruct.note.ordinal()] = convert + " " +
                            newData[enFileStruct.quantity.ordinal()] + " " + newData[enFileStruct.asset.ordinal()] + " to " +
                            data[enFileStruct.quantity.ordinal()] + " " + data[enFileStruct.asset.ordinal()];
                    dataOutput.add(newData);

                } catch (Exception e) {
                    System.out.println("ERROR PARSING [" + String.join(", ", data) + "]");
                }
            } else
                dataOutput.add(data);
        }
        return dataOutput;
    }

    private static Object getData(String[] data, enFileStruct column) {
        switch (column) {
            case type:
            case asset:
            case note:
                return data[column.ordinal()];
            case quantity:
            case eurFees:
            case eurSpot:
            case eurTotal:
            case eurSousTotal: {
                String strValue = data[column.ordinal()];
                if ((strValue == null) || (strValue.isEmpty()))
                    return 0d;
                else
                    return Double.valueOf(strValue);
            }
            case timeStamp: {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                return LocalDateTime.parse(data[column.ordinal()], formatter);
            }
            default:
                return new IllegalStateException("From Coin Base- getData-" + Arrays.toString(data) + "*" + column);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // FONCTION CHARGEE DE NETTOYER LE FICHIER
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static ArrayList<String[]> getCSVData(String fileName) {
        ArrayList<String[]> data = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(Paths.get(fileName));
             CSVReader csvReader = new CSVReader(reader)) {
            // read one record at a time
            String[] record;
            for (int i = 0; i <= 7; i++) csvReader.readNext();

            while ((record = csvReader.readNext()) != null) {
                // nettoyage du bloc note
                String blocNote = record[enFileStruct.note.ordinal()];
                record[enFileStruct.note.ordinal()] = blocNote.replace(',', '.');
                data.add(record);
            }

        } catch (IOException | CsvValidationException ex) {
            ex.printStackTrace();
        }
        return data;
    }

    private static void saveFileData(String fileName, ArrayList<String[]> dataFile) {
        FileWriter fw;
        try {
            fw = new FileWriter(fileName, false);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            for (Object[] data : dataFile) {
                String strData = String.join(separator, Arrays.asList(data).toString()) + "\n";
                out.write(strData);
            }
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private enum enFileStruct {timeStamp, type, asset, quantity, Currency, eurSpot, eurSousTotal, eurTotal, eurFees, note}


}
