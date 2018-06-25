package android.manutenza.com.manutenzaandroid;

public class AndroidInfo {

    private int id_richiesta;
    private String titolo_richiesta;
    private String categoria_richiesta;
    private String foto_richiesta;
    private int id_proposta;
    private float prezzo_proposta;
    private String nome_utente;

    public AndroidInfo() {

    }

    public AndroidInfo(int id_richiesta, String titolo_richiesta, String categoria_richiesta, String foto_richiesta, int id_proposta, float prezzo_proposta, String nome_utente) {
        this.id_richiesta = id_richiesta;
        this.titolo_richiesta = titolo_richiesta;
        this.categoria_richiesta = categoria_richiesta;
        this.foto_richiesta = foto_richiesta;
        this.id_proposta = id_proposta;
        this.prezzo_proposta = prezzo_proposta;
        this.nome_utente = nome_utente;
    }

    public int getId_richiesta() {
        return id_richiesta;
    }

    public void setId_richiesta(int id_richiesta) {
        this.id_richiesta = id_richiesta;
    }

    public String getTitolo_richiesta() {
        return titolo_richiesta;
    }

    public void setTitolo_richiesta(String titolo_richiesta) {
        this.titolo_richiesta = titolo_richiesta;
    }

    public String getCategoria_richiesta() {
        return categoria_richiesta;
    }

    public void setCategoria_richiesta(String categoria_richiesta) {
        this.categoria_richiesta = categoria_richiesta;
    }

    public String getFoto_richiesta() {
        return foto_richiesta;
    }

    public void setFoto_richiesta(String foto_richiesta) {
        this.foto_richiesta = foto_richiesta;
    }

    public int getId_proposta() {
        return id_proposta;
    }

    public void setId_proposta(int id_proposta) {
        this.id_proposta = id_proposta;
    }

    public float getPrezzo_proposta() {
        return prezzo_proposta;
    }

    public void setPrezzo_proposta(float prezzo_proposta) {
        this.prezzo_proposta = prezzo_proposta;
    }

    public String getNome_utente() {
        return nome_utente;
    }

    public void setNome_utente(String nome_utente) {
        this.nome_utente = nome_utente;
    }
}


