package ptfManagement;


import global.EnumCrypto.enTypeAsset;
import global.EnumCrypto.enTypeDate;
import global.EnumCrypto.enTypeMontant;
import global.EnumCrypto.enTypeTransaction;
import global.fonction.FntFinancial;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Portefeuille {
    private final ArrayList<Position> positions;
    private Liquidite liquidite;
    private String ptfName;

    public Portefeuille() {
        positions = new ArrayList<>();
        liquidite = new Liquidite();
    }

    public String getNom() {
        return ptfName;
    }

    public void setNom(String ptfName) {
        this.ptfName = ptfName;
    }

    public Position getPosition(Object instName) {
        return positions.stream()
                .filter(positions1 -> instName.equals(positions1.getInstName()))
                .findAny()
                .orElse(null);
    }

    public ArrayList<Position> getPositions() {
        return this.positions;
    }

    public void addPosition(Position position) {
        // recherche si un position similaire existe
        Position pos = getPosition(position.getInstName());
        if (pos == null) {
            this.positions.add(position);
            positions.sort(Position.ComparatorInstName);
            return;
        }
        // sinon récpération de la postion et recopie de toutes les transaction
        for (Transaction transaction : position.getTransactions(enTypeTransaction.all))
            pos.addTransaction(transaction);
        // remet la liste des positions en ordre
        positions.sort(Position.ComparatorInstName);
    }

    public List<String> getPositionListInstrument() {
        return positions.stream()
                .map(Position::getInstName)
                .collect(Collectors.toList());
    }

    public void removePosition(String asset) {
        positions.removeIf(position -> position.getInstName().compareTo(asset) == 0);
    }

    public void addTransaction(Transaction trans) {

        if (positions.size() == 0) {
            this.addPosition(new Position(trans.getInstrumentName()));
            positions.sort(Position.ComparatorInstName);
        }

        Position position;
        position = this.getPosition(trans.getInstrumentName());
        if (position == null) {
            this.addPosition(new Position(trans.getInstrumentName()));
            position = this.getPosition(trans.getInstrumentName());
        }

        position.addTransaction(trans);
    }

    public LocalDateTime getDateTrade(enTypeDate typeDate) {
        Position position;
        if (typeDate.compareTo(enTypeDate.firstDate) == 0)
            position = positions.stream().min(Comparator.comparing(pos -> pos.getPositionTypeAssetTypeDate(enTypeAsset.Position, typeDate))).get();
        else
            position = positions.stream().max(Comparator.comparing(pos -> pos.getPositionTypeAssetTypeDate(enTypeAsset.Position, typeDate))).get();

        return position.getPositionTypeAssetTypeDate(enTypeAsset.Position, typeDate);
    }

    public Double getMontant(enTypeTransaction typeTransaction, enTypeMontant typeMontant) {
        Double montant;
        switch (typeMontant) {
            case Average -> {
                montant = getMontant(typeTransaction, enTypeMontant.Spot);
                montant = montant / this.getPositions().size();
            }
            case Initial -> {
                // récupération de la première transaction
                Transaction transaction = this.getDateTypeTransaction(enTypeDate.firstDate);
                if (transaction == null) montant = 0d;
                else montant = transaction.getMontant(enTypeMontant.Balance);
            }
            case liquidite -> {
                // la liquidité est montant de la somme de toutes les positions en fin - le montant initial
                Double montantInitial = this.getMontant(enTypeTransaction.all, enTypeMontant.Initial);
                Double montantFinal = this.getMontant(enTypeTransaction.all, enTypeMontant.CumulBalance);
                montant = montantFinal - montantInitial;
            }
            case ProfitAndLost -> montant = this.getMontant(typeTransaction, enTypeMontant.Spot) - this.getMontant(typeTransaction, enTypeMontant.Initial);
            case Fees, Spot, CumulBalance, MontantWithoutFees, MontantWithFees -> montant = positions.stream().mapToDouble(sum -> sum.getMontant(typeTransaction, typeMontant)).sum();
            default -> throw new IllegalStateException("PORTEFEUILLE getMontant Unexpected value: " + typeMontant);
        }
        return FntFinancial.arrondi(montant, 2);
    }

    public int getNbreTrade(enTypeTransaction typeTransaction) {
        int nbreTrade = 0;
        for (Position eachPosition : positions)
            nbreTrade += eachPosition.getNbreTrade(typeTransaction);
        return nbreTrade;
    }


    private Transaction getDateTypeTransaction(enTypeDate searchDate) {

        Transaction transaction = null;
        LocalDateTime pivotDate;
        // définition de la date de pivot
        if (enTypeDate.firstDate.compareTo(searchDate) == 0)
            pivotDate = LocalDateTime.now().plusYears(10);
        else
            pivotDate = LocalDateTime.now().minusYears(10);

        // recherche de la transation concernée
        for (Position position : positions) {
            Transaction tmptransaction = position.getTransaction(position.getPositionTypeAssetTypeDate(enTypeAsset.Position, enTypeDate.firstDate));
            if (searchDate.equals(enTypeDate.firstDate)) {
                if (tmptransaction.getDateTransaction().isBefore(pivotDate)) {
                    pivotDate = tmptransaction.getDateTransaction();
                    transaction = tmptransaction;
                }
            } else {
                if (tmptransaction.getDateTransaction().isAfter(pivotDate)) {
                    pivotDate = tmptransaction.getDateTransaction();
                    transaction = tmptransaction;
                }
            }
        }
        return transaction;
    }

    public ArrayList<Mouvement> getLiquiditeMouvements() {
        return liquidite.getMouvements();
    }

    public double getSoldeMouvementLiquidite() {
        return liquidite.getSolde();
    }

    public void addMouvement(Liquidite liquidite) {
        this.liquidite = liquidite;
    }
}

