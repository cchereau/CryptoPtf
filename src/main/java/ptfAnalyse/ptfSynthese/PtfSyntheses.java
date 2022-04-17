package ptfAnalyse.ptfSynthese;

import global.EnumCrypto;
import global.EnumCrypto.enPeriodePerformance;
import global.EnumCrypto.enTypeDate;
import global.EnumCrypto.enTypeMontant;
import global.EnumCrypto.enTypeTransaction;
import global.GlobalData;
import global.fonction.FntDates;
import global.fonction.FntFinancial;
import global.fonction.gblFunction;
import ptfManagement.Portefeuille;
import ptfManagement.Position;
import ptfManagement.Transaction;
import stock.Stock;

import java.time.LocalDate;
import java.util.ArrayList;

public class PtfSyntheses {


    public PtfSyntheses() {

    }

    public ArrayList<PtfSynthese> getSyntheses(Portefeuille ptf, enPeriodePerformance periodePerformance) {

        ArrayList<PtfSynthese> ptfSyntheses = new ArrayList<>();
        ArrayList<Transaction> transactions = gblFunction.getTransactionFromPtf(ptf);

        // Chargement du segment des transactions en fonction des données disponibles
        LocalDate datePivotDeb = ptf.getDateTrade(enTypeDate.firstDate).toLocalDate();
        LocalDate datePivotFin = LocalDate.now();
        LocalDate datePivot = FntDates.getDateFromPeriod(datePivotDeb, periodePerformance, false);

        double profitAndLost;
        Double valorisation = 0d;
        double balanceFin;
        Double balanceDeb = ptf.getMontant(enTypeTransaction.all, enTypeMontant.Initial);
        Double liquidite;
        Double fees;

        boolean fin = false;
        while (!fin) {
            ArrayList<Transaction> tmpTransactions = gblFunction.getListTransactionBetweenDate(transactions, datePivotDeb, datePivot);

            // Création d'un nouveau portefeuille virtuel
            Portefeuille ptfVirtuel = new Portefeuille();
            for (Transaction trans : tmpTransactions)
                ptfVirtuel.addTransaction(trans);

            // sauvegarde du niveau des balance Mouvement
            balanceFin = ptfVirtuel.getMontant(enTypeTransaction.all, enTypeMontant.CumulBalance) - balanceDeb;
            valorisation += this.getSpotValorisation(ptfVirtuel, datePivot);
            liquidite = ptfVirtuel.getMontant(enTypeTransaction.all, enTypeMontant.CumulBalance);
            profitAndLost = valorisation + liquidite;
            fees = ptfVirtuel.getMontant(enTypeTransaction.all, enTypeMontant.Fees);

            // Enregistrement
            PtfSynthese ptfSynthese = new PtfSynthese();
            ptfSynthese.setMontant(balanceDeb, enTypeMontant.Initial);
            ptfSynthese.setMontant(balanceFin, enTypeMontant.CumulBalance);
            ptfSynthese.setMontant(liquidite, enTypeMontant.liquidite);
            ptfSynthese.setMontant(valorisation, enTypeMontant.Spot);
            ptfSynthese.setMontant(profitAndLost, enTypeMontant.ProfitAndLost);
            ptfSynthese.setMontant(fees, enTypeMontant.Fees);
            ptfSynthese.setDateSythese(datePivot);
            ptfSyntheses.add(ptfSynthese);

            // Incremente le nouvau segement OU sortie de la boucle
            if (datePivot.compareTo(datePivotFin) != 0) {
                datePivotDeb = datePivot.plusDays(1);
                datePivot = FntDates.getDateFromPeriod(datePivot, periodePerformance, false);
            } else fin = true;

            // si la date Pivot > Date Fin, force date pivot à date fin
            if (datePivot.compareTo(datePivotFin) >= 0)
                datePivot = datePivotFin;

            // preparation de la prochaine itération
            balanceDeb = balanceFin;

        }
        return ptfSyntheses;
    }


    private Double getSpotValorisation(Portefeuille ptf, LocalDate date) {
        double valorisation = 0d;
        Double spotPrice;

        // foncction qui va rechercher la valorisation des instrument
        for (Position position : ptf.getPositions()) {
            // récupération du StockPrice
            try {
                Stock stock = GlobalData.stocks.getStock(position.getInstName());
                spotPrice = stock.getQuotation(date, EnumCrypto.enTypeQuotation.close);
            } catch (Exception e) {
                spotPrice = position.getTransaction(enTypeDate.lastDate, enTypeTransaction.all).getEurPrice();
            }

            if (spotPrice == 0)
                spotPrice = position.getTransaction(enTypeDate.lastDate, enTypeTransaction.all).getEurPrice();


            valorisation += (spotPrice * position.getPositionQuantiteShare(enTypeTransaction.all));
        }
        return FntFinancial.arrondi(valorisation, 2);
    }


}
