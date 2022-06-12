package ptfManagement;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// GESTION DES POSITIONS ISSUES D'UNE LISTE DE TRANSACTION SUR UN INSTRUMENT
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import global.EnumCrypto;
import global.EnumCrypto.*;
import global.GlobalData;
import global.fonction.FntFinancial;
import stock.Stock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Position implements Cloneable {

    public static Comparator<Position> ComparatorInstName = Comparator.comparing(Position::getInstName);
    private final ArrayList<Transaction> transactions = new ArrayList<>();
    String instName;
    private Boolean isPositionOpen;
    private Stock stock;

    public Position(String instName) {
        this.instName = instName;
        this.isPositionOpen = false;
        this.stock = GlobalData.stocks.getStock(instName);
    }

    public Transaction getTransaction(LocalDateTime dateTime) {
        try {
            Predicate<Transaction> predicate = (Transaction p) -> p.getDateTransaction().compareTo(dateTime) == 0;
            return transactions.stream().filter(predicate).findFirst().get();
        } catch (Exception e) {
            System.out.println(Position.class.getCanonicalName() + "-" + e.getMessage() + "-" + this.getInstName());
            return null;
        }
    }
    public Transaction getTransaction(enTypeDate typeDate, enTypeTransaction typeTransaction) {
        // récupération de la liste des transaction d'un type
        ArrayList<Transaction> arrTransactions = getTransactions(typeTransaction);
        switch (typeDate) {
            case firstDate:
                return arrTransactions.stream().min(Transaction.ComparatorDate).get();
            case lastDate:
                return arrTransactions.stream().max(Transaction.ComparatorDate).get();
            default:
                return null;
        }
    }
    public ArrayList<Transaction> getTransactions(enTypeTransaction typeTransaction) {
        if (typeTransaction.equals(enTypeTransaction.all))
            return transactions;
        Predicate<Transaction> typeTrade = (Transaction p) -> p.getTransactionType().equals(typeTransaction);
        return transactions.stream().filter(typeTrade).collect(Collectors.toCollection(ArrayList<Transaction>::new));
    }

    public void addTransaction(ArrayList<Transaction> transactions) {
        for (Transaction transaction : transactions)
            addTransaction(transaction);
    }
    public void addTransaction(Transaction transaction) {
        // récupération de la dernière valeur de la transaction
        if (transactions.size() != 0) {
            Transaction lastTransaction = this.getTransaction(enTypeDate.lastDate, enTypeTransaction.all);
            transaction.setMontant(enTypeMontant.CumulBalance, lastTransaction.getMontant(enTypeMontant.CumulBalance) + transaction.getMontant(enTypeMontant.Balance));
            transaction.setCumulNbreShare(lastTransaction.getCumulNbreShart() + transaction.getMouvementShare());
        } else {
            transaction.setMontant(enTypeMontant.CumulBalance, transaction.getMontant(enTypeMontant.Balance));
            transaction.setCumulNbreShare(transaction.getMouvementShare());
        }

        this.transactions.add(transaction);
        // ajout de l'ordre dans les transactions
        transactions.sort(Transaction.ComparatorDate);
    }
    public void removeTransaction(LocalDateTime dateDeb, LocalDateTime dateFin, ArrayList<EnumCrypto.enTypeTransaction> typeTransactionsInScope) {
        Predicate<Transaction> transactionPredicateDeb = (Transaction p) -> p.getDateTransaction().isAfter(dateDeb.minusSeconds(1));
        Predicate<Transaction> transactionPredicateFin = (Transaction p) -> p.getDateTransaction().isBefore(dateFin.plusSeconds(1));
        Predicate<Transaction> transacationInScope = (Transaction p) -> typeTransactionsInScope.contains(p.getTransactionType());
        transactions.removeIf(transactionPredicateFin.and(transactionPredicateDeb).and(transacationInScope));
    }

    // rattachement au pricing de l'instrument
    public Stock getStockPrice() {
        return this.stock;
    }
    public void setStockPrice(Stock stock) {
        this.stock = stock;
    }

    // récuépration des montants
    public Double getMontant(enTypeTransaction typeTransaction, enTypeMontant typeMontant) {
        Double montant = 0d;
        switch (typeMontant) {
            case CumulBalance:
                Transaction lastTransaction = getTransaction(enTypeDate.lastDate, typeTransaction);
                montant = lastTransaction.getMontant(typeMontant);
                break;
            case Fees, MontantWithFees, MontantWithoutFees:
                montant = this.getTransactions(typeTransaction).stream().mapToDouble(sum -> sum.getMontant(typeMontant)).sum();
                break;
            case Spot: // c'est le montant du nombre de share totale * le montant spot
                Double spotPrice;
                if (this.getStockPrice() == null)
                    spotPrice = this.getTransaction(enTypeDate.lastDate, enTypeTransaction.all).getEurPrice();
                else
                    spotPrice = this.getStockPrice().getQuotation(this.getStockPrice().getPriceTypeDate(enTypeDate.lastDate), enTypeQuotation.close);
                montant = this.getPositionQuantiteShare(typeTransaction) * spotPrice;
                break;
            case ProfitAndLost: // c'est la différence entre la balance en euro et le prix spot
                Double montantBalance = getMontant(typeTransaction, enTypeMontant.CumulBalance);
                Double montantSPot = getMontant(typeTransaction, enTypeMontant.Spot);
                montant = montantSPot + montantBalance;
                break;
            case liquidite: // c'est la différence entre la dernière balance claculée et l'investissement initial sur la première position
                LocalDateTime dateTime;
                dateTime = this.getPositionTypeAssetTypeDate(enTypeAsset.Position, enTypeDate.firstDate);
                Double mntInitial = this.getTransaction(dateTime).getMontant(enTypeMontant.Balance);
                dateTime = this.getPositionTypeAssetTypeDate(enTypeAsset.Position, enTypeDate.lastDate);
                Double mntFinal = this.getTransaction(dateTime).getMontant(enTypeMontant.Balance);
                montant = mntFinal - mntInitial;
                break;
            case Initial:
                Transaction firstTransaction = getTransaction(enTypeDate.firstDate, typeTransaction);
                montant = firstTransaction.getMontant(enTypeMontant.Balance);
                break;
            default:
                throw new IllegalStateException("POSITION getMontant Unexpected value: " + typeMontant);
        }
        return FntFinancial.arrondi(montant, 2);
    }

    // récupération des prix
    public Double getPositionAvgPrice(enTypeTransaction typeTransaction) {
        try {
            Double montant = getMontant(typeTransaction, enTypeMontant.MontantWithFees);
            Double nbreShare = getPositionQuantiteShare(typeTransaction);
            return Math.abs(FntFinancial.arrondi(montant / nbreShare, 6));
        } catch (Exception e) {
            System.out.println(Position.class.getCanonicalName() + "-" + e.getMessage() + "-" + typeTransaction.toString());
            return 0d;
        }
    }
    public Double getPositionSpotPrice(enTypeQuotation typeQuotation) {
        try {
            return FntFinancial.arrondi(stock.getQuotation(stock.getPriceTypeDate(enTypeDate.lastDate), typeQuotation), 6);
        } catch (Exception e) {
            System.out.println(this.getClass().getCanonicalName() + "-" + e.getMessage() + "-" + getInstName());
            return 0d;
        }
    }

    // récupération du Quantité
    public Double getPositionQuantiteShare(enTypeTransaction typeTransaction) {
        Double quantite = 0d;
        ArrayList<Transaction> arrTransactions = this.getTransactions(typeTransaction);
        for (Transaction transaction : arrTransactions)
            quantite += transaction.getMouvementShare();
        return FntFinancial.arrondi(quantite, 6);
    }

    // récupération du nombre de transaction
    public int getNbreTrade(enTypeTransaction typeTransaction) {
        return getTransactions(typeTransaction).size();
    }

    public LocalDateTime getPositionTypeAssetTypeDate(enTypeAsset typeAsset, enTypeDate typeDate) {
        switch (typeAsset) {
            case Position:
                return getTransaction(typeDate, enTypeTransaction.all).getDateTransaction();
            case Stock:
                return this.stock.getPriceTypeDate(typeDate).atStartOfDay();
        }
        return LocalDateTime.now();
    }
    public Boolean isPositionOpen() {
        return isPositionOpen;
    }
    public void isPositionOpen(Boolean positionOpen) {
        isPositionOpen = positionOpen;
    }

    public String getInstName() {
        return instName;
    }

    public Position clone() {
        Position position = null;
        try {
            // On récupère l'instance à renvoyer par l'appel de la méthode super.clone()
            position = (Position) super.clone();
        } catch (CloneNotSupportedException cnse) {
            // Ne devrait jamais arriver car nous implémentons l'interface Cloneable
            cnse.printStackTrace(System.err);
        }
        // on renvoie le clone
        return position;
    }

}
