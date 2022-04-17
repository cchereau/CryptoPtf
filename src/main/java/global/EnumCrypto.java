package global;

public final class EnumCrypto {

    public enum enPeriodePerformance {DAY, WEEK, MONTH, QUARTER, SEMESTER, YEAR, ALL}

    public enum enTypeQuotation {open, low, high, close, date, volume}

    public enum enActionOnPosition {
        Acheter(1), Vendre(-1), Alleger(-1), StandBy(0), Undefined(0), Renforcer(1);
        private final int poids;

        enActionOnPosition(int ponderation) {
            poids = ponderation;
        }

        public int getPoids() {
            return poids;
        }
    }

    public enum enTypeTransaction {buy, sell, arbitrate, received, send, all, other}

    public enum enTypeDate {lastDate, firstDate}

    public enum enTypeMontant {Initial, Fees, liquidite, Spot, ProfitAndLost, Balance, Average, CumulBalance, MontantWithoutFees, MontantWithFees}

    public enum enTypeAsset {Position, Stock}

    public enum enTypeMouvement {Retrait, Depot}

    public enum enStrategieAction {Evaluate, StandBy, Buy, Sell}

    public enum enJSONPosition {PrixMoyen, SpotQuantite, ProfitAndLost, Transactions, Code, OpenOnStrategie}

    public enum enJSONTransaction {Date, Action, NbreShare, PrixShare, Fees, EurTotal, EurSubTotal, Notes}

    public enum enJSONPortefeuille {Portefeuilles, Positions, MontantInitial, BalanceEuro, LiquiditeEuro, MontantFeesEuro, MontantSpotEuro, Nom}

    public enum enJSONLiquidite {Mouvements, Total, Date, TypeMouvement, Montant}

    public enum enJSONStock {Name, Ticker, Devise, Refresh, Stock}

    public enum enOperateur {Egale, Inferieur, Superieur, InferieurAndEgal, SuperieureAndEgal}

    public enum enSimulationExecute {Evaluate, Action}

    public enum enSimulationResultat {Sell, buy, StandBy}

}

