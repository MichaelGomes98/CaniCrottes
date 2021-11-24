public class Caninette {
    private int id;
    private String adresse, numero, etat, remarque;
    private double positionE, positionN;

    public Caninette(int id, String adresse, String numero, String etat, String remarque, double positionE, double positionN) {
        this.id = id;
        this.adresse = adresse;
        this.numero = numero;
        this.etat = etat;
        this.remarque = remarque;
        this.positionE = positionE;
        this.positionN = positionN;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    public double getPositionE() {
        return positionE;
    }

    public void setPositionE(double positionE) {
        this.positionE = positionE;
    }

    public double getPositionN() {
        return positionN;
    }

    public void setPositionN(double positionN) {
        this.positionN = positionN;
    }

    @Override
    public String toString() {
        return
                "Caninette n°: " + id + "\n" +
                        "Adresse: " + adresse + "\n" +
                        "Numero: " + numero + "\n" +
                        "Etat: " + etat + "\n" +
                        "Remarque: " + remarque + "\n" +
                        "======================" + "\n";
    }
}
