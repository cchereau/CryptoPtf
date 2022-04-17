package ptfManagement;

import java.util.ArrayList;


public class Liquidite {
    ArrayList<Mouvement> mouvements;

    public Liquidite() {
        mouvements = new ArrayList<>();
    }

    public void addMouvement(Mouvement mouvement) {
        this.mouvements.add(mouvement);
    }

    public Double getSolde() {
        return mouvements.stream().mapToDouble(Mouvement::getBalancedMontantMouvement).sum();
    }

    public ArrayList<Mouvement> getMouvements() {
        return this.mouvements;
    }


}