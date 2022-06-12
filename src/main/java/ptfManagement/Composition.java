package ptfManagement;

import global.EnumCrypto.enPositionShortLong;

public class Composition {

    String instrument;
    Double quantite;
    Double prixMoyen;
    Double prixMarche;

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instName) {
        this.instrument = instName;
    }

    public enPositionShortLong getPositionShortLong() {

        if (quantite > 0d) return enPositionShortLong.posLong;
        else if (quantite < 0d) return enPositionShortLong.posShort;
        else return enPositionShortLong.posNull;
    }

    public Double getQuantite() {
        return quantite;
    }

    public void setQuantite(Double quantite) {
        this.quantite = quantite;
    }

    public Double getPrixMoyen() {
        return prixMoyen;
    }

    public void setPrixMoyen(Double prixMoyen) {
        this.prixMoyen = prixMoyen;
    }

    public Double getPrixMarche() {
        return this.prixMarche;
    }

    public void setPrixMarche(Double prixMarche) {
        this.prixMarche = prixMarche;
    }

}
