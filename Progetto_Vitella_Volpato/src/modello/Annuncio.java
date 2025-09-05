package modello;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import modello.exception.AnnuncioException;

/**
 * Rappresenta un annuncio pubblicato sulla bacheca.
 * 
 * Implementazione robusta e compatibile con la Bacheca e Utente fornite.
 * Le parole chiave sono memorizzate internamente come Set<String> (ordine d'inserimento),
 * ma getParoleChiave() restituisce una stringa separata da virgole per compatibilità
 * con il formato di salvataggio/caricamento.
 */
public class Annuncio {

    /** Regex per validare l'intera lista di parole chiave (es. "auto, bici, ricambio") */
    private static final String PAROLE_CHIAVE_REGEX = "^([\\p{L}0-9 ]+)(,\\s*[\\p{L}0-9 ]+)*$";

    /** Regex per validare una singola parola chiave (lettere/numeri e spazi) */
    private static final String PAROLA_SINGOLA_REGEX = "^[\\p{L}0-9 ]+$";

    /** Regex per data nel formato yyyy-MM-dd */
    private static final String DATA_REGEX = "^\\d{4}-\\d{2}-\\d{2}$";

    private final int id;
    private final Utente autore;
    private final String articolo;
    private final float prezzo;
    private final String tipologia; // "acquisto" o "vendita" (lowercase)
    private final Set<String> paroleChiave; // evita duplicati, mantiene ordine
    private final LocalDate dataScadenza; // null per acquisto

    private static final Random RAND = new Random();

    /**
     * Costruttore con id esplicito (usato per il caricamento da file).
     *
     * @param id identificativo univoco
     * @param autore autore (non null)
     * @param articolo nome articolo (non null/non blank)
     * @param prezzo prezzo > 0
     * @param tipologia "acquisto" o "vendita"
     * @param paroleChiave stringa parole separate da virgola (può essere null/empty)
     * @param dataScadenza stringa data (yyyy-MM-dd) o null
     * @throws AnnuncioException se le validazioni falliscono
     */
    public Annuncio(int id, Utente autore, String articolo, float prezzo, String tipologia, String paroleChiave, String dataScadenza)
            throws AnnuncioException {

        if (autore == null) {
            throw new AnnuncioException("Autore non può essere null");
        }
        this.autore = autore;

        if (articolo == null || articolo.trim().isEmpty()) {
            throw new AnnuncioException("Nome articolo non può essere vuoto");
        }
        this.articolo = articolo.trim();

        if (prezzo <= 0.0f) {
            throw new AnnuncioException("Il prezzo deve essere maggiore di zero");
        }
        this.prezzo = prezzo;

        if (tipologia == null) {
            throw new AnnuncioException("Tipologia non può essere null");
        }
        String t = tipologia.trim().toLowerCase();
        if (!"acquisto".equals(t) && !"vendita".equals(t)) {
            throw new AnnuncioException("Tipologia non valida, deve essere 'acquisto' o 'vendita'");
        }
        this.tipologia = t;

        // Parole chiave: se null/empty -> set vuoto, altrimenti valida la lista
        this.paroleChiave = new LinkedHashSet<>();
        if (paroleChiave != null && !paroleChiave.trim().isEmpty()) {
            String s = paroleChiave.trim();
            if (!s.matches(PAROLE_CHIAVE_REGEX)) {
                throw new AnnuncioException("Formato parole chiave errato, usare parole separate da virgola");
            }
            // split e normalizzazione (trim)
            String[] parts = s.split(",");
            for (String p : parts) {
                String pTrim = p.trim();
                if (!pTrim.isEmpty()) {
                    this.paroleChiave.add(pTrim);
                }
            }
        }

        // Data scadenza: richiesta per "vendita", nulla per "acquisto"
        if ("vendita".equals(this.tipologia)) {
            if (dataScadenza == null || dataScadenza.trim().isEmpty() || !dataScadenza.matches(DATA_REGEX)) {
                throw new AnnuncioException("Data di scadenza non valida; usare il formato: yyyy-MM-dd");
            }
            try {
                this.dataScadenza = LocalDate.parse(dataScadenza.trim());
            } catch (DateTimeParseException e) {
                throw new AnnuncioException("Formato della data non valido. Usare il formato yyyy-MM-dd.");
            }
        } else {
            this.dataScadenza = null;
        }

        // assegna id (unicità gestita esternamente dalla Bacheca)
        this.id = id;
    }

    /**
     * Costruttore che genera automaticamente un id (usabile per creazione runtime).
     */
    public Annuncio(Utente autore, String articolo, float prezzo, String tipologia, String paroleChiave, String dataScadenza)
            throws AnnuncioException {
        this(generaId(), autore, articolo, prezzo, tipologia, paroleChiave, dataScadenza);
    }

    /**
     * Genera un id pseudo-casuale positivo. La garanzia di unicità deve essere fatta
     * dalla Bacheca (pool di id).
     */
    private static int generaId() {
        // range ampio per ridurre collisioni; la Bacheca controlla comunque i duplicati
        return Math.abs(RAND.nextInt(Integer.MAX_VALUE - 1)) + 1;
    }

    /* ------------------ METODI DI UTILITÀ ------------------ */

    /**
     * Restituisce true se l'annuncio (vendita) è scaduto rispetto a oggi.
     * Per gli annunci di tipo acquisto ritorna false.
     */
    public boolean isScaduto() {
        return dataScadenza != null && dataScadenza.isBefore(LocalDate.now());
    }

    /**
     * Aggiunge una singola parola chiave (valida) all'annuncio.
     *
     * @param nuovaParola parola da aggiungere (non null/empty)
     * @throws AnnuncioException se la parola non rispetta il formato
     */
    public void aggiungiParola(String nuovaParola) throws AnnuncioException {
        if (nuovaParola == null || nuovaParola.trim().isEmpty()) {
            throw new AnnuncioException("Parola chiave vuota");
        }
        String p = nuovaParola.trim();
        if (!p.matches(PAROLA_SINGOLA_REGEX)) {
            throw new AnnuncioException("Parola chiave non valida (solo lettere, numeri e spazi ammessi)");
        }
        this.paroleChiave.add(p);
    }

    /* ------------------ GETTERS ------------------ */

    public int getId() {
        return id;
    }

    public Utente getAutore() {
        return autore;
    }

    public String getArticolo() {
        return articolo;
    }

    public float getPrezzo() {
        return prezzo;
    }

    /**
     * Restituisce la tipologia in lowercase ("acquisto" o "vendita").
     */
    public String getTipologia() {
        return tipologia;
    }

    /**
     * Restituisce le parole chiave come stringa separata da virgole (es. "auto,bici,ricambio").
     * È compatibile con split(",") usato dalla Bacheca (che spesso fa anche trim()).
     */
    public String getParoleChiave() {
        // restituisce le parole separate da virgola + spazio per essere compatibile
        // con le aspettative dei test (es. "elettronica, TV")
        return paroleChiave.stream().collect(Collectors.joining(", "));
    }

    /**
     * Restituisce la data di scadenza (null per acquisto).
     */
    public LocalDate getDataScadenza() {
        return dataScadenza;
    }

    /* ------------------ toString / equals / hashCode ------------------ */

    /**
     * Formato testuale consigliato (compatibile con parsing usato in Bacheca):
     * id;autoreToString;articolo;prezzo;tipologia;paroleChiave;dataScadenza
     */
    @Override
    public String toString() {
        String data = (dataScadenza != null) ? dataScadenza.toString() : "null";
        return String.format("%d;%s;%s;%.2f;%s;%s;%s",
                id,
                autore.toString(),
                articolo,
                prezzo,
                tipologia,
                getParoleChiave(),
                data);
    }

    /**
     * Due annunci sono identificati univocamente dall'id. Questo semplifica l'uso
     * in collezioni e la gestione della pool di id nella Bacheca.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Annuncio other = (Annuncio) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
