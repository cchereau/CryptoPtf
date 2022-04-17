package ptfAnalyse.ptfStrategieOld;

import global.EnumCrypto.enActionOnPosition;
import global.EnumCrypto.enTypeMontant;
import global.EnumCrypto.enTypeTransaction;
import ptfManagement.Portefeuille;
import ptfManagement.Position;
import stock.Stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/*********************************************************************************************************************
 *DEFINITION D' STRATEGY :
 * LES LIQUIDITY : quant entrer et comment, quand sortir et comment
 * LES POSITION : Quand arbitrer l'allegement, quand arbitrer le renforcement
 * L'ARBITRAGE : quel politique d'arbitarge
 *********************************************************************************************************************/
public class Strategie {
    private final HashMap<String, StrategieActionInstrument> listInstrument;
    private final ArrayList<StrategyAction> strategyActions;
    private final StrategieParam strategieParam;

    public Strategie(Portefeuille ptf, StrategieParam strategieParam) {
        this.strategieParam = strategieParam;

        // récupération de la liste des instrument du portefeuilles
        listInstrument = new HashMap<>();
        getListInstrument(ptf, strategieParam);

        // récupération des couples de données à oéprer
        strategyActions = new ArrayList<>();
        getListCoupleOfInstrument();
    }

    public enActionOnPosition getActionOnPosition(Position position) {
        return listInstrument.get(position.getInstName()).getActionOnInstrument();
    }

    public ArrayList<StrategyAction> getAction(Position position) {
        ArrayList<StrategyAction> actions = getActions();
        // récupération de toutes les actions
        Predicate<StrategyAction> selectionActionFrom = (StrategyAction p) -> p.getInstrumentFrom().compareTo(position.getInstName()) == 0;
        actions = actions.stream().filter(selectionActionFrom).collect(Collectors.toCollection(ArrayList<StrategyAction>::new));
        return actions.stream().filter(selectionActionFrom).collect(Collectors.toCollection(ArrayList<StrategyAction>::new));
    }

    public ArrayList<StrategyAction> getActions() {
        ArrayList<StrategyAction> actions;

        // application des paramétrages
        Predicate<StrategyAction> typeAction = (StrategyAction p) -> (p.getActionInstFrom() == enActionOnPosition.Alleger);
        Predicate<StrategyAction> periodeSansTransaction = (StrategyAction p) -> p.isLastDateTransactionUnderPeriod(strategieParam.getPeriodeWhitoutTransaction());
        Predicate<StrategyAction> tolerance = StrategyAction::isActionsOnPosition;
        Predicate<StrategyAction> performance = (StrategyAction p) -> p.getPerformance() * 100 >= strategieParam.getPerformanceMinimulToArbitrate();
        Predicate<StrategyAction> fullPredicate = typeAction.and(periodeSansTransaction.and(tolerance.and(performance)));
        actions = strategyActions.stream().filter(fullPredicate).collect(Collectors.toCollection(ArrayList<StrategyAction>::new));

        return actions;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getListCoupleOfInstrument() {
        // creation des combinaison possible achat / vente
        for (Map.Entry<String, StrategieActionInstrument> instrumentFrom : listInstrument.entrySet())
            if (instrumentFrom.getValue().getActionOnInstrument().compareTo(enActionOnPosition.StandBy) != 0)
                for (Map.Entry<String, StrategieActionInstrument> instrumentTo : listInstrument.entrySet())
                    if (instrumentTo.getValue().getActionOnInstrument().compareTo(enActionOnPosition.StandBy) != 0)
                        if (instrumentFrom.getValue().getActionOnInstrument().compareTo(instrumentTo.getValue().getActionOnInstrument()) != 0)
                            strategyActions.add(new StrategyAction(instrumentFrom.getValue(), instrumentTo.getValue(), strategieParam.getTypeQuotation(), strategieParam.getPeriodePerformance()));
    }

    private void getListInstrument(Portefeuille ptf, StrategieParam strategieParam) {
        double averagePtfPosition = ptf.getMontant(enTypeTransaction.all, enTypeMontant.Average);

        Predicate<Position> openPosition = Position::isPositionOpen;
        ArrayList<Position> filterPosition = ptf.getPositions().stream().filter(openPosition).collect(Collectors.toCollection(ArrayList<Position>::new));

        for (Position position : filterPosition) {
            StrategieActionInstrument strategieActionInstrument = new StrategieActionInstrument(averagePtfPosition, position);
            strategieActionInstrument.setActionOnInstument(strategieParam.getTolerancePositionInPtf());
            listInstrument.put(position.getInstName(), strategieActionInstrument);
        }
    }


    public ArrayList<Stock> getStockPrice(enActionOnPosition actionOnPosition) {
        ArrayList<Stock> stocks = new ArrayList<>();

        for (Map.Entry<String, StrategieActionInstrument> strategieActionInstrumentEntry : this.listInstrument.entrySet())
            if (strategieActionInstrumentEntry.getValue().getActionOnInstrument().equals(actionOnPosition))
                stocks.add(strategieActionInstrumentEntry.getValue().getStockPrice());
        return stocks;
    }
}
