package modello;

import java.util.Objects;
import modello.exception.UtenteException;

/**
 * La classe {@code Utente} rappresenta un utente del sistema di bacheca
 * annunci.
 * Ogni utente è identificato da un nome e da un indirizzo email.
 * 
 * <p>
 * La classe prevede controlli di validità su nome ed email
 * e utilizza eccezioni personalizzate per segnalare eventuali errori.
 * </p>
 */
public class Utente {

    /** Indirizzo email dell'utente (immutabile). */
    private final String email;

    /** Nome dell'utente (immutabile). */
    private final String nome;

    /**
     * Espressione regolare per validare un indirizzo email.
     * Accetta la forma: username@dominio.estensione
     * Esempio valido: mario.rossi@mail.it
     */
    private final String emailRegex = "[\\w\\.]+@[\\w\\.]+\\.[\\w]{2,}";

    /**
     * Espressione regolare per validare un nome.
     * Consente solo lettere (anche accentate) e spazi.
     * Non accetta numeri o caratteri speciali.
     * Esempi validi: "Mario Rossi", "Giuseppe"
     */
    private static final String nomeRegex = "^[a-zA-Z0-9]+$";

    /**
     * Costruttore della classe {@code Utente}.
     * 
     * @param email l'email dell'utente
     * @param nome  il nome dell'utente
     * @throws UtenteException se email o nome non rispettano i formati richiesti
     */
    public Utente(String email, String nome) throws UtenteException {

        // Validazione email
        if (!isEmailValida(email)) {
            throw new UtenteException("Formato email errato, deve essere del tipo 'username@dominio.it'");
        }
        this.email = email;

        // Validazione nome
        if (!isNomeValido(nome)) {
            throw new UtenteException("Formato nome errato, accetta solo caratteri alfanumerici");
        }
        this.nome = nome;
    }

    /**
     * Restituisce l'email dell'utente.
     * 
     * @return email dell'utente
     */
    public String getEmail() {
        return email;
    }

    /**
     * Restituisce il nome dell'utente.
     * 
     * @return nome dell'utente
     */
    public String getNome() {
        return nome;
    }

    /**
     * Verifica se un indirizzo email rispetta l'espressione regolare definita.
     * 
     * @param email l'email da verificare
     * @return true se l'email è valida, false altrimenti
     */
    private boolean isEmailValida(String email) {
        return email.matches(emailRegex);
    }

    /**
     * Verifica se un nome rispetta l'espressione regolare definita.
     * 
     * @param nome il nome da verificare
     * @return true se il nome è valido, false altrimenti
     */
    private boolean isNomeValido(String nome) {
        return nome.matches(nomeRegex);
    }

    /**
     * Restituisce una rappresentazione testuale dell'utente.
     * 
     * @return stringa con email e nome
     */
    @Override
    public String toString() {
        return "[email= " + email + ", nome= " + nome + "]";
    }

    /**
     * Confronta l'utente corrente con un altro oggetto.
     * Due utenti sono uguali se hanno stessa email e stesso nome.
     * 
     * @param obj l'oggetto da confrontare
     * @return true se uguali, false altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true; // stesso riferimento
        if (obj == null)
            return false; // oggetto nullo
        if (getClass() != obj.getClass())
            return false; // classi diverse
        Utente other = (Utente) obj;
        return Objects.equals(email, other.email) && Objects.equals(nome, other.nome);
    }

    /**
     * Genera il codice hash coerente con il metodo equals.
     * 
     * @return hash code calcolato su email e nome
     */
    @Override
    public int hashCode() {
        return Objects.hash(email, nome);
    }
}