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


    /////////////////////////////////////////////////////////////////////////////////////////////////
    // FOnction qui va renvoyer un HashMap de l'ensemble de la compostion d'une position
    // a partir des achats / ventes intervenues sur cet instrument par les autres instruments du portefeuille
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /*public static ArrayList<Composition> getPositionComposition(String instrument, Portefeuille ptf, EnumCrypto.enTypeMontant typeMontant) {

        // vérification du P&L de la position dans la ptf
        EnumCrypto.enStrategieAction strategieAction;

        Position position = ptf.getPosition(instrument);
        if (position.getMontant(EnumCrypto.enTypeTransaction.all, EnumCrypto.enTypeMontant.ProfitAndLost) > 0)
            strategieAction = EnumCrypto.enStrategieAction.Evaluate;
        else
            strategieAction = EnumCrypto.enStrategieAction.StandBy;

        Portefeuille ptfOutput = new Portefeuille();
        ArrayList<Transaction> transactions = getTransactionFromPtf(ptf);

        // récuépration de la liste des transaction qui concernent cet instrument
        Predicate<Transaction> predicateTransaction = (Transaction p) -> (p.getInstrumentName().compareTo(instrument) == 0);
        ArrayList<Transaction> tansactionInstruments = transactions.stream().filter(predicateTransaction).collect(Collectors.toCollection(ArrayList<Transaction>::new));

        // parcours de la liste des transaction et recherche de la transaction en regard et mise en place d'un portefeuille virtuelle
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

        getInitialTransaction(ptf,instrument);

        ArrayList<Composition> compositions = new ArrayList<>();

        // lecture du Portefeuille virtuelle et ajout Position / position de la situation
        for (Position curPosition : ptfOutput.getPositions()) {
            Composition composition = new Composition();
            composition.setInstrument(curPosition.getInstName());
            composition.setPositionShortLong(curPosition.getPositionShortOrLong(EnumCrypto.enTypeTransaction.all));
            composition.setQuantite(curPosition.getPositionQuantiteShare(EnumCrypto.enTypeTransaction.all));
            composition.setPrixMoyen(curPosition.getPositionAvgPrice(EnumCrypto.enTypeTransaction.all));
            composition.setPrixMarche(position.getPositionSpotPrice(EnumCrypto.enTypeQuotation.close)/curPosition.getPositionSpotPrice(EnumCrypto.enTypeQuotation.close));

            // Recherche de toutes les transactions qui sont sous le prix
            if (strategieAction == EnumCrypto.enStrategieAction.StandBy)
                composition.setSimulationResultat(EnumCrypto.enSimulationResultat.StandBy);
            else // si position Short et prix moyen < prixMarche alors
            {
                if (composition.getPositionShortLong() == EnumCrypto.enPositionShortLong.posLong)
                    if (composition.getPrixMarche() < composition.getPrixMoyen())
                        composition.setSimulationResultat(EnumCrypto.enSimulationResultat.Sell);
                    else
                        composition.setSimulationResultat(EnumCrypto.enSimulationResultat.buy);
                else if (composition.getPrixMarche() < composition.getPrixMoyen())
                    composition.setSimulationResultat(EnumCrypto.enSimulationResultat.Sell);
                else
                    composition.setSimulationResultat(EnumCrypto.enSimulationResultat.buy);
            }

            compositions.add(composition);
        }


        return compositions;
    }
    */

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Fonction permettant de renvoyer sous une forme d'ArrayList de transaction de type InstParam/InstVariable
    // Les transactions du portefeuille qui sont sous la forme InstVariable/EUR
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static ArrayList<Composition> getPositionComposition(Portefeuille ptf, String stringInst) {
        class Quantity {
            double ShareFrom = 0d;
            double shareTo = 0d;
            double amount = 0d;
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

                switch (transactionIndexBase.getTransactionType()) {
                    case buy:
                        quantity.ShareFrom += transactionIndexBase.getMouvementShare();
                        quantity.shareTo -= transactionMiroire.getMouvementShare();
                        quantity.amount += transactionIndexBase.getMontant(EnumCrypto.enTypeMontant.MontantWithFees);
                        break;

                    case sell:
                        quantity.ShareFrom -= transactionIndexBase.getMouvementShare();
                        quantity.shareTo += transactionMiroire.getMouvementShare();
                        quantity.amount -= transactionIndexBase.getMontant(EnumCrypto.enTypeMontant.MontantWithFees);
                        break;
                }
            }
        }

        ArrayList<Composition> compositions = new ArrayList<>();
        for (Map.Entry<String, Quantity> quantityEntry : map.entrySet()) {
            Composition composition = new Composition();
            composition.setInstrument(quantityEntry.getKey());
            composition.setPrixMoyen(quantityEntry.getValue().ShareFrom / quantityEntry.getValue().shareTo * quantityEntry.getValue().amount);
            composition.setQuantite(quantityEntry.getValue().shareTo);

            StockComposite stockComposite = new StockComposite(GlobalData.stocks.getStock(stringInst), GlobalData.stocks.getStock(quantityEntry.getKey()));
            composition.setPrixMarche(stockComposite.getQuotation(stockComposite.getPriceTypeDate(EnumCrypto.enTypeDate.lastDate), EnumCrypto.enTypeQuotation.close));

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