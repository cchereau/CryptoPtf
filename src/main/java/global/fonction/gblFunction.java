package global.fonction;

import global.EnumCrypto;
import global.GlobalData;
import ptfManagement.Composition;
import ptfManagement.Portefeuille;
import ptfManagement.Position;
import ptfManagement.Transaction;
import stock.StockComposite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class gblFunction {

    public static ArrayList<Transaction> getTransactionFromPtf(Portefeuille ptf) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        // chargement des transactions de l'ensemble des transactions du portfeuille intial
        for (Position position : ptf.getPositions())
            for (Transaction transaction : position.getTransactions(EnumCrypto.enTypeTransaction.all))
                transactions.add(transaction.clone());
        transactions.sort(Transaction.ComparatorDate);
        return transactions;
    }

    public static ArrayList<Transaction> getListTransactionBetweenDate(ArrayList<Transaction> values, LocalDate dateDeb, LocalDate dateFin) {
        ArrayList<Transaction> transactions;
        Predicate<Transaction> predicateBefore = (Transaction p) -> p.getDateTransaction().toLocalDate().isBefore(dateFin.plusDays(1));
        Predicate<Transaction> predicateAfter = (Transaction p) -> p.getDateTransaction().toLocalDate().isAfter(dateDeb.minusDays(1));
        transactions = values.stream().filter(predicateBefore.and(predicateAfter)).collect(Collectors.toCollection(ArrayList<Transaction>::new));
        return transactions;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fonction permettant de renvoyer sous une forme d'ArrayList de transaction de type InstParam/InstVariable
    // Les transactions du portefeuille qui sont sous la forme InstVariable/EUR
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static ArrayList<Composition> getPositionComposition(Portefeuille ptf, String stringInst) {
        class Quantity {
            double shareFrom = 0d;
            double shareTo = 0d;
        }

        HashMap<String, Quantity> map = new HashMap<>();

        // filtre sur l'ensemble des transactions qui concernent l'instrument pour récupérer les dates qui serviront de bases
        ArrayList<Transaction> ptfInitialTransactions = getTransactionFromPtf(ptf);
        Predicate<Transaction> predicateTransaction = (Transaction p) -> (p.getInstrumentName().compareTo(stringInst) == 0);
        ArrayList<Transaction> transactionsBase = ptfInitialTransactions.stream().filter(predicateTransaction).collect(Collectors.toCollection(ArrayList<Transaction>::new));

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Agregation de toutes positions
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        for (Transaction transactionIndexBase : transactionsBase) {
            // récupération de la transaction en alterEgo
            Predicate<Transaction> predicateDateTransaction = (Transaction p) -> (p.getDateTransaction().equals(transactionIndexBase.getDateTransaction()));
            Predicate<Transaction> predicateInstrumentTransaction = (Transaction p) -> (p.getInstrumentName().compareTo(transactionIndexBase.getInstrumentName()) != 0);
            ArrayList<Transaction> transactionsAlter = ptfInitialTransactions.stream().filter(predicateDateTransaction.and(predicateInstrumentTransaction)).collect(Collectors.toCollection(ArrayList<Transaction>::new));
            if (transactionsAlter.size() != 0) {
                Transaction transactionMiroire = transactionsAlter.get(0);
                Quantity quantity;
                if (!map.containsKey(transactionMiroire.getInstrumentName()))
                    map.put(transactionMiroire.getInstrumentName(), new Quantity());
                quantity = map.get(transactionMiroire.getInstrumentName());
                quantity.shareFrom -= transactionIndexBase.getMouvementShare();
                quantity.shareTo += transactionMiroire.getMouvementShare();
            }
        }

        ArrayList<Composition> compositions = new ArrayList<>();
        for (Map.Entry<String, Quantity> quantityEntry : map.entrySet()) {
            Composition composition = new Composition();
            composition.setInstrument(quantityEntry.getKey());
            composition.setPrixMoyen(quantityEntry.getValue().shareTo / quantityEntry.getValue().shareFrom);
            composition.setQuantite(quantityEntry.getValue().shareTo);
            StockComposite stockComposite = new StockComposite(GlobalData.stocks.getStock(stringInst), GlobalData.stocks.getStock(quantityEntry.getKey()));
            stockComposite.getQuotations();
            composition.setStock(stockComposite);
            compositions.add(composition);
        }

        return compositions;

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fonction permettant à partir d'un nom de fichier d'ouvrir le JSon et de le renvoyer sous forme d'un String
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static String getFileStrockPrice(String fullNameFile) throws IOException {
        // Chargement du fichier des historiques
        StringBuilder data = new StringBuilder();

        // vérificaition existance fichier
        File f = new File(fullNameFile);
        if (!f.exists())
            return "";
        BufferedReader in = new BufferedReader(new FileReader(fullNameFile));
        String line;
        while ((line = in.readLine()) != null)
            data.append(line);
        in.close();
        return data.toString();
    }


}