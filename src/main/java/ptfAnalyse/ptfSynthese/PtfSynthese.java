package ptfAnalyse.ptfSynthese;

import global.EnumCrypto.enTypeMontant;

import java.time.LocalDate;

public class PtfSynthese {
    private Double dblLiquidite;
    private Double dblProfitAndLost;
    private LocalDate dateSynthese;
    private Double dblCumulBamance;
    private Double dblFees;
    private Double dblInitial;
    private Double dblLastValoisatiion;

    public PtfSynthese() {
    }

    public LocalDate getDateSythese() {
        return dateSynthese;
    }

    public void setDateSythese(LocalDate date) {
        this.dateSynthese = date;
    }

    public void setMontant(Double montant, enTypeMontant typeMontant) {
        switch (typeMontant) {
            case CumulBalance -> this.dblCumulBamance = montant;
            case liquidite -> this.dblLiquidite = montant;
            case ProfitAndLost -> this.dblProfitAndLost = montant;
            case Fees -> this.dblFees = montant;
            case Initial -> this.dblInitial = montant;
            case Spot -> this.dblLastValoisatiion = montant;
            default -> throw new IllegalStateException("PTFSYNTHSESE setMontant Unexpected value: " + typeMontant);
        }
    }

    public Double getMontant(enTypeMontant typeMontant) {
        return switch (typeMontant) {
            case Spot -> this.dblLastValoisatiion;
            case CumulBalance -> this.dblCumulBamance;
            case liquidite -> this.dblLiquidite;
            case ProfitAndLost -> this.dblProfitAndLost;
            case Fees -> this.dblFees;
            case Initial -> this.dblInitial;
            default -> throw new IllegalStateException("PTFSYNTHSESE getMontant Unexpected value: " + typeMontant);
        };
    }
}
