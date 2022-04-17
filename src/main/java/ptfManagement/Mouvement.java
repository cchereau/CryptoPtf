package ptfManagement;

import global.EnumCrypto;

import java.time.LocalDateTime;

public class Mouvement {
    EnumCrypto.enTypeMouvement typeMouvement;
    Double montant;
    LocalDateTime dateTime;

    public Mouvement(LocalDateTime dateTime, EnumCrypto.enTypeMouvement typeMouvement, Double montant) {
        this.dateTime = dateTime;
        this.montant = montant;
        this.typeMouvement = typeMouvement;
    }

    public EnumCrypto.enTypeMouvement getTypeMouvement() {
        return typeMouvement;
    }

    public Double getMontantMouvement() {
        return montant;
    }

    public Double getBalancedMontantMouvement() {
        if (typeMouvement.equals(EnumCrypto.enTypeMouvement.Depot))
            return montant;
        else return -montant;

    }

    public LocalDateTime getDateMouvement() {
        return this.dateTime;
    }

}
