package modello.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import modello.Utente;
import modello.exception.UtenteException;

class UtenteTest {

    @Test
    void testCostruttoreValido() throws UtenteException {
        // Test creazione utente valido
        Utente u = new Utente("giulia.verdi@example.com", "giuliaVerdi");
        assertEquals("giulia.verdi@example.com", u.getEmail());
        assertEquals("giuliaVerdi", u.getNome());
    }

    @Test
    void testCostruttoreEmailNonValida() {
        // Test creazione utente con email non valida
        UtenteException exp1 = assertThrows(UtenteException.class, () -> {
            new Utente("giulia.verdiexample.com", "giuliaVerdi");
        });
        assertEquals("Formato email errato, deve essere del tipo 'username@dominio.it'", exp1.getMessage());

        UtenteException exp2 = assertThrows(UtenteException.class, () -> {
            new Utente("giulia.verdi&example.com", "giuliaVerdi");
        });
        assertEquals("Formato email errato, deve essere del tipo 'username@dominio.it'", exp2.getMessage());

        UtenteException exp3 = assertThrows(UtenteException.class, () -> {
            new Utente("@example.com", "giuliaVerdi");
        });
        assertEquals("Formato email errato, deve essere del tipo 'username@dominio.it'", exp3.getMessage());
    }

    @Test
    void testCostruttoreNomeNonValido() {
        // Test creazione utente con nome non valido
        UtenteException exp1 = assertThrows(UtenteException.class, () -> {
            new Utente("giulia.verdi@example.com", "_giulia_123");
        });
        assertEquals("Formato nome errato, accetta solo caratteri alfanumerici", exp1.getMessage());

        UtenteException exp2 = assertThrows(UtenteException.class, () -> {
            new Utente("giulia.verdi@example.com", "#giulia");
        });
        assertEquals("Formato nome errato, accetta solo caratteri alfanumerici", exp2.getMessage());

        UtenteException exp3 = assertThrows(UtenteException.class, () -> {
            new Utente("giulia.verdi@example.com", "giulia rossi");
        });
        assertEquals("Formato nome errato, accetta solo caratteri alfanumerici", exp3.getMessage());
    }
}
