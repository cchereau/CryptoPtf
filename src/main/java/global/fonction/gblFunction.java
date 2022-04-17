package global.fonction;

import global.EnumCrypto;
import ptfManagement.Portefeuille;
import ptfManagement.Position;
import ptfManagement.Transaction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
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


    /////////////////////////////////////////////////////////////////////////////////////////////////
    // FOnction qui va renvoyer un HashMap de l'ensemble de la compostion d'une position
    // a partir des achats / ventes intervenues sur cet instrument par les autres instruments du portefeuille
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public static Portefeuille getPositionComposition(String instrument, Portefeuille ptf, EnumCrypto.enTypeMontant typeMontant) {
        Portefeuille ptfOutput = new Portefeuille();

        ArrayList<Transaction> transactions = getTransactionFromPtf(ptf);
        // récuépration de la liste des transaction qui concernent cette instrument
        Predicate<Transaction> predicateTransaction = (Transaction p) -> (p.getInstrumentName().compareTo(instrument) == 0);
        ArrayList<Transaction> tansactionInstruments = transactions.stream().filter(predicateTransaction).collect(Collectors.toCollection(ArrayList<Transaction>::new));

        // parcours de la liste des transaction et recherche de la transaction en regard
        for (Transaction transaction : tansactionInstruments) {
            // récupération de la liste des transaction qui se sont déroulée à la même heure
            Predicate<Transaction> predicateDateTransaction = (Transaction p) -> (p.getDateTransaction().equals(transaction.getDateTransaction()));
            ArrayList<Transaction> transactionsAlter = transactions.stream().filter(predicateDateTransaction).collect(Collectors.toCollection(ArrayList<Transaction>::new));
            // exlusion des transaction qui sont sur l'instrument
            Predicate<Transaction> predicateInstrumentTransaction = (Transaction p) -> (p.getInstrumentName().compareTo(transaction.getInstrumentName()) != 0);
            transactionsAlter = transactionsAlter.stream().filter(predicateInstrumentTransaction).collect(Collectors.toCollection(ArrayList<Transaction>::new));

            if (transactionsAlter.size() != 0)
                ptfOutput.addTransaction(transactionsAlter.get(0));
        }


        return ptfOutput;
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