package ptfManagement;

import global.EnumCrypto.enTypeMontant;
import global.EnumCrypto.enTypeTransaction;
import global.fonction.FntFinancial;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Comparator;

public class Transaction implements Cloneable {
    public static Comparator<Transaction> ComparatorDate = new Comparator<Transaction>() {
        @Override
        public int compare(Transaction t1, Transaction t2) {
            return (t1.getDateTransaction().compareTo(t2.dateTransaction));
        }
    };
    private String instrumentName;
    private LocalDateTime dateTransaction;
    private enTypeTransaction transactionType;
    private Double nbreShare;
    private Double eurPrice;
    private Double dblMontantWithoutFees;
    private Double dblMontantWithFees;
    private Double dblMontantFees;
    private String notes;
    private Double dblCumulBalance;
    private Double dblCumulNbreShart;

    public Transaction(String IntrumentName, LocalDateTime dateTransaction, enTypeTransaction transactionType, Double nbreShare, Double eurSpotPrice, Double eurSubTotal, Double eurTotal, Double eurFees, String notes) {
        this.instrumentName = IntrumentName;
        this.dateTransaction = dateTransaction;
        this.transactionType = transactionType;
        this.nbreShare = nbreShare;
        this.eurPrice = eurSpotPrice;
        this.dblMontantWithoutFees = eurSubTotal;
        this.dblMontantWithFees = eurTotal;
        this.dblMontantFees = eurFees;
        this.notes = notes;
        this.dblCumulBalance = 0d;
        this.dblCumulNbreShart = 0d;
    }

    public Transaction() {
        this.instrumentName = null;
        this.dateTransaction = null;
        this.transactionType = null;
        this.nbreShare = null;
        this.eurPrice = null;
        this.dblMontantWithoutFees = null;
        this.dblMontantWithFees = null;
        this.dblMontantFees = null;
        this.notes = null;
        this.dblCumulBalance = null;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    public enTypeTransaction getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(enTypeTransaction transactionType) {
        this.transactionType = transactionType;
    }

    public Double getEurPrice() {
        return FntFinancial.arrondi(this.eurPrice, 6);
    }

    public void setEurPrice(Double eurPrice) {
        this.eurPrice = eurPrice;
    }

    public Double getNbreShare() {
        return FntFinancial.arrondi(nbreShare, 6);
    }

    public void setNbreShare(Double nbreShare) {
        this.nbreShare = nbreShare;
    }

    public Double getCumulNbreShart() {
        return dblCumulNbreShart;
    }

    public void setCumulNbreShare(Double cumulNbreShare) {
        this.dblCumulNbreShart = cumulNbreShare;
    }

    public Double getMouvementShare() {
        switch (this.getTransactionType()) {
            case buy, received:
                return this.nbreShare;
            case sell, send:
                return -this.nbreShare;
            default:
                //throw new IllegalStateException(Transaction.class.getCanonicalName() + getTransactionType().toString());
                return this.nbreShare;
        }
    }

    public void setMontant(@NotNull enTypeMontant typeMontant, Double montant) {
        switch (typeMontant) {
            case CumulBalance:
                this.dblCumulBalance = montant;
                break;
            case Fees:
                this.dblMontantFees = montant;
                break;
            case MontantWithoutFees:
                this.dblMontantWithoutFees = montant;
                break;
            case MontantWithFees:
                this.dblMontantWithFees = montant;
                break;
            default:
                throw new IllegalStateException("TRANSACTION.setMontant Unexpected value: " + typeMontant);
        }
    }

    public Double getMontant(@org.jetbrains.annotations.NotNull enTypeMontant typeMontant) {
        Double montant = 0d;
        switch (typeMontant) {
            case CumulBalance:
                montant = this.dblCumulBalance;
                break;
            case Fees:
                montant = this.dblMontantFees;
                break;
            case Balance:
                switch (this.getTransactionType()) {
                    case buy, send:
                        montant = -dblMontantWithFees;
                        break;
                    case sell, received:
                        montant = dblMontantWithFees;
                        break;
                    default:
                        montant = 0d;
                        break;
                }
                break;
            case MontantWithFees:
                montant = dblMontantWithFees;
                break;
            case MontantWithoutFees:
                montant = this.dblMontantWithoutFees;
                break;
            default:
                throw new IllegalStateException("TRANSACTION.getMontant Unexpected value: " + typeMontant);
        }
        return montant;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getDateTransaction() {
        return this.dateTransaction;
    }

    public void setDateTransation(LocalDateTime dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    public Transaction clone() {
        Transaction transaction = null;
        try {
            // On récupère l'instance à renvoyer par l'appel de la
            // méthode super.clone()
            transaction = (Transaction) super.clone();
        } catch (CloneNotSupportedException cnse) {
            // Ne devrait jamais arriver car nous implémentons
            // l'interface Cloneable
            cnse.printStackTrace(System.err);
        }
        // on renvoie le clone
        return transaction;
    }

    @Override
    public String toString() {
        return this.getDateTransaction().toString() + ";" + this.getInstrumentName() + ";" + this.getTransactionType() + ";" +
                this.getNbreShare().toString() + ";" + this.getEurPrice().toString();
    }

}

