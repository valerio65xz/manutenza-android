package android.manutenza.com.manutenzaandroid;

public class ElencoProposte {

    private int idRichiesta;
    private String titoloRichiesta;
    private String categoriaRichiesta;
    private String fotoRichiesta;
    private int idProposta;
    private double prezzoProposta;
    private String nomeUtente;

    public ElencoProposte(){}

    public ElencoProposte(int idRichiesta, String titoloRichiesta, String categoriaRichiesta, String fotoRichiesta, int idProposta, double prezzoProposta, String nomeUtente) {
        this.idRichiesta = idRichiesta;
        this.titoloRichiesta = titoloRichiesta;
        this.categoriaRichiesta = categoriaRichiesta;
        this.fotoRichiesta = fotoRichiesta;
        this.idProposta = idProposta;
        this.prezzoProposta = prezzoProposta;
        this.nomeUtente = nomeUtente;
    }

    public int getIdRichiesta() {
        return idRichiesta;
    }

    public void setIdRichiesta(int idRichiesta) {
        this.idRichiesta = idRichiesta;
    }

    public String getTitoloRichiesta() {
        return titoloRichiesta;
    }

    public void setTitoloRichiesta(String titoloRichiesta) {
        this.titoloRichiesta = titoloRichiesta;
    }

    public String getCategoriaRichiesta() {
        return categoriaRichiesta;
    }

    public void setCategoriaRichiesta(String categoriaRichiesta) {
        this.categoriaRichiesta = categoriaRichiesta;
    }

    public String getFotoRichiesta() {
        return fotoRichiesta;
    }

    public void setFotoRichiesta(String fotoRichiesta) {
        this.fotoRichiesta = fotoRichiesta;
    }

    public int getIdProposta() {
        return idProposta;
    }

    public void setIdProposta(int idProposta) {
        this.idProposta = idProposta;
    }

    public double getPrezzoProposta() {
        return prezzoProposta;
    }

    public void setPrezzoProposta(double prezzoProposta) {
        this.prezzoProposta = prezzoProposta;
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }
}
