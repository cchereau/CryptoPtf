package ptfAnalyse.ptfStrategieOld;

import global.EnumCrypto.enPeriodePerformance;
import global.EnumCrypto.enTypeQuotation;


public class StrategieParam {

    private enTypeQuotation typeQuotation;

    private enPeriodePerformance periodePerformance;
    private int tolerancePositionInPtf;
    private enPeriodePerformance periodeWhitoutTransaction;
    private int performanceMinimumToArbitrate;


    public StrategieParam() {
        this.typeQuotation = enTypeQuotation.close;
        this.tolerancePositionInPtf = 5;                            // par default la tolerance de variation des positions dans le ptf est de 5%
        this.performanceMinimumToArbitrate = 5;                    // pas de transaction arbitrage si la performance est < x
        this.periodePerformance = enPeriodePerformance.WEEK;        // par default la performance est prise sur la semaine
        this.periodeWhitoutTransaction = enPeriodePerformance.WEEK; // par default la perdiode sans transaction est d'une semaine
    }

    public enTypeQuotation getTypeQuotation() {
        return this.typeQuotation;
    }

    public void setTypeQuotation(enTypeQuotation typeQuotation) {
        this.typeQuotation = typeQuotation;
    }

    public int getTolerancePositionInPtf() {
        return this.tolerancePositionInPtf;
    }

    public void setTolerancePositionInPtf(int i) {
        this.tolerancePositionInPtf = i;
    }

    public enPeriodePerformance getPeriodePerformance() {
        return this.periodePerformance;
    }

    public void setPeriodePerformance(enPeriodePerformance periodPerformance) {
        this.periodePerformance = periodPerformance;
    }

    public void setPeriodWithoutTransaction(enPeriodePerformance periodeWithoutTransaction) {
        this.periodeWhitoutTransaction = periodeWithoutTransaction;
    }

    public enPeriodePerformance getPeriodeWhitoutTransaction() {
        return this.periodeWhitoutTransaction;
    }

    public void setPerformanceMinimumToArbitrate(int pallier) {
        this.performanceMinimumToArbitrate = pallier;
    }

    public int getPerformanceMinimulToArbitrate() {
        return this.performanceMinimumToArbitrate;
    }


}
