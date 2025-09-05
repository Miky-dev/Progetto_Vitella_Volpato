package modello.exception;

@SuppressWarnings("serial")
/**
 * Eccezione personalizzata lanciata in caso di errori
 * durante la creazione o validazione di un oggetto Utente.
 */
public class UtenteException extends Exception {

    /**
     * Costruttore che accetta un messaggio descrittivo.
     * 
     * @param msg il messaggio che descrive l'errore
     */
    public UtenteException(String msg) {
        super(msg);
    }

    /**
     * Costruttore che accetta un messaggio e la causa originale.
     * 
     * @param msg il messaggio che descrive l'errore
     * @param cause l'eccezione che ha causato l'errore
     */
    public UtenteException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
