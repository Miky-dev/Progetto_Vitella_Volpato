package modello.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.jupiter.api.*;
import modello.*;
import modello.exception.*;

class BachecaTest {

    private Bacheca bacheca;
    Utente utente;

    @BeforeEach
    void setUp() throws Exception {
        bacheca = new Bacheca(); // Costruisco un oggetto bacheca
        utente = new Utente("giovanni.neri@example.com", "giovanniNeri");
    }

    @AfterEach
    void tearDown() {
        bacheca.getAnnunci().clear(); // Svuoto la bacheca dagli annunci
        bacheca.getPoolId().clear(); // Svuoto la pool degli id presenti in bacheca
    }

    @Test
    void testCostruttore() {
        assertTrue(bacheca.getAnnunci().isEmpty());
    }

    @Test
    void testAggiungiAnnuncioVendita() throws AnnuncioException, BachecaException {
        Annuncio annuncio = new Annuncio(utente, "Smartphone", 279, "vendita", "elettronica, touchscreen",
                "2026-09-15");

        // Aggiungo l'annuncio di vendita
        ArrayList<Annuncio> risultati = bacheca.aggiungiAnnuncio(annuncio);
        assertTrue(risultati.isEmpty()); // Non ci sono articoli di acquisto in vendita da restituire
        assertEquals(1, bacheca.getAnnunci().size()); // La bacheca contiene un annuncio
    }

    @Test
    void testAggiungiAnnuncioDuplicato() throws AnnuncioException, BachecaException {
        Annuncio annuncio = new Annuncio(utente, "Smartphone", 279, "vendita", "elettronica, touchscreen",
                "2026-09-15");

        bacheca.aggiungiAnnuncio(annuncio);

        BachecaException e = assertThrows(BachecaException.class, () -> {
            bacheca.aggiungiAnnuncio(annuncio);
        });
        assertEquals("Annuncio già presente", e.getMessage());
    }

    @Test
    void testAggiungiAnnuncioStessoID() throws AnnuncioException, BachecaException {
        // Creo e aggiungo il primo annuncio con id esplicito
        Annuncio annuncio = new Annuncio(4321, utente, "Smartphone", 279, "vendita", "elettronica, touchscreen",
                "2026-09-15");
        bacheca.aggiungiAnnuncio(annuncio);

        // Verifico che la pool degli id contenga l'id e che la bacheca contenga solo
        // questo annuncio
        assertTrue(bacheca.getPoolId().contains(4321), "poolId dovrebbe contenere l'id 4321 dopo la prima aggiunta");
        assertEquals(1, bacheca.getAnnunci().size(), "Deve esserci un solo annuncio dopo il tentativo fallito");
    }

    @Test
    void testRimuoviAnnuncio() throws AnnuncioException, BachecaException, AutoreNonAutorizzatoException {
        Annuncio annuncio = new Annuncio(utente, "Smartphone", 279, "vendita", "elettronica, touchscreen",
                "2026-09-15");

        bacheca.aggiungiAnnuncio(annuncio);

        assertTrue(bacheca.rimuoviAnnuncio(annuncio.getId(), utente));
        assertFalse(bacheca.getPoolId().contains(annuncio.getId()));
        assertEquals(0, bacheca.getAnnunci().size());
    }

    @Test
    void testRimuoviAnnuncioAutoreNonAutorizzato() throws AnnuncioException, BachecaException, UtenteException {
        Utente utente2 = new Utente("carlo.verdi@example.com", "carloVerdi");
        Annuncio annuncio = new Annuncio(utente, "Smartphone", 279, "vendita", "elettronica, touchscreen",
                "2026-09-15");

        bacheca.aggiungiAnnuncio(annuncio);

        AutoreNonAutorizzatoException e = assertThrows(AutoreNonAutorizzatoException.class, () -> {
            bacheca.rimuoviAnnuncio(annuncio.getId(), utente2);
        });
        assertEquals("Non sei autorizzato a rimuovere questo annuncio.", e.getMessage());
    }

    @Test
    void testRimuoviAnnuncioNonTrovato() throws AnnuncioException, BachecaException {
        Annuncio annuncio = new Annuncio(4321, utente, "Smartphone", 279, "vendita", "elettronica, touchscreen",
                "2026-09-15");

        bacheca.aggiungiAnnuncio(annuncio);

        BachecaException e = assertThrows(BachecaException.class, () -> {
            bacheca.rimuoviAnnuncio(9999, utente);
        });
        assertEquals("Annuncio non trovato.", e.getMessage());
    }

    @Test
    void testCercaPerParolaChiave() throws AnnuncioException, BachecaException {
        Annuncio annuncio1 = new Annuncio(utente, "Televisore", 150, "vendita", "elettronica, TV", "2026-01-20");
        Annuncio annuncio2 = new Annuncio(utente, "Frigorifero", 200, "vendita", "elettronica, cucina", "2026-01-20");

        bacheca.aggiungiAnnuncio(annuncio1);
        bacheca.aggiungiAnnuncio(annuncio2);

        assertEquals(1, bacheca.cercaPerParolaChiave("TV").size());
        assertEquals(2, bacheca.cercaPerParolaChiave("elettronica").size());
        assertEquals(0, bacheca.cercaPerParolaChiave("gaming").size());
    }

    @Test
    void testPulisciBacheca() throws AnnuncioException, BachecaException {
        Annuncio annuncio1 = new Annuncio(utente, "Smartphone", 279, "vendita", "elettronica, touchscreen",
                "2025-05-01");
        Annuncio annuncio2 = new Annuncio(utente, "Frigorifero", 320, "vendita", "elettrodomestici, cucina",
                "2025-05-01");
        Annuncio annuncio3 = new Annuncio(utente, "Laptop", 850, "vendita", "elettronica, informatica", "2026-05-01");

        bacheca.aggiungiAnnuncio(annuncio1);
        bacheca.aggiungiAnnuncio(annuncio2);
        bacheca.aggiungiAnnuncio(annuncio3);

        assertEquals(3, bacheca.getAnnunci().size());

        bacheca.pulisciBacheca();

        // Dopo la pulizia, solo il miglior annuncio (l'ultimo) dovrebbe rimanere
        assertEquals(1, bacheca.getAnnunci().size());
        assertTrue(bacheca.getAnnunci().contains(annuncio3));
        assertFalse(bacheca.getAnnunci().contains(annuncio1));
        assertFalse(bacheca.getAnnunci().contains(annuncio2));
    }

    @Test
    void testAggiungiNuovaParolaChiave()
            throws AnnuncioException, BachecaException, AutoreNonAutorizzatoException, UtenteException {
        Annuncio annuncio = new Annuncio(4321, utente, "Smartphone", 279, "vendita", "elettronica, touchscreen",
                "2025-05-01");
        bacheca.aggiungiAnnuncio(annuncio);

        assertEquals("elettronica, touchscreen", annuncio.getParoleChiave()); // prima dell'aggiunta
        bacheca.aggiungiNuovaParolaChiave(4321, utente, "ricondizionato");
        assertEquals("elettronica, touchscreen, ricondizionato", annuncio.getParoleChiave()); // dopo l'aggiunta

        // Caso non valido: utente non autorizzato
        Utente utente2 = new Utente("marco.ferri@example.com", "marcoFerri");
        assertThrows(AutoreNonAutorizzatoException.class, () -> {
            bacheca.aggiungiNuovaParolaChiave(4321, utente2, "Premium");
        });
    }

    @Test
    void testCaricaSalvaDaFile() throws AnnuncioException, BachecaException, IOException, UtenteException {

        Annuncio annuncio1 = new Annuncio(utente, "Smartphone", 279, "vendita", "elettronica, touchscreen",
                "2026-09-15");
        Annuncio annuncio2 = new Annuncio(utente, "Frigorifero", 320, "vendita", "elettrodomestici, cucina",
                "2026-09-15");

        bacheca.aggiungiAnnuncio(annuncio1);
        bacheca.aggiungiAnnuncio(annuncio2);

        bacheca.salvaAnnunciSuFile("src/modello/test/test.txt"); // salvo su file
        bacheca.getAnnunci().clear(); // svuoto la bacheca
        bacheca.getPoolId().clear(); // svuoto la pool degli id
        bacheca.caricaAnnunciDaFile("src/modello/test/test.txt"); // carico da file

        assertEquals(2, bacheca.getAnnunci().size()); // verifico che ci siano due annunci
        assertTrue(bacheca.getAnnunci().contains(annuncio1)); // controllo che annuncio1 sia presente
        assertTrue(bacheca.getAnnunci().contains(annuncio2));
    }

    @Test
    void testRimuoviAnnuncioConIteratore() throws AnnuncioException, BachecaException, IOException, UtenteException {

        UnsupportedOperationException e = assertThrows(UnsupportedOperationException.class, () -> {
            Annuncio annuncio = new Annuncio(utente, "Smartphone", 279, "vendita", "elettronica, touchscreen",
                    "2026-09-15");
            bacheca.aggiungiAnnuncio(annuncio);
            Iterator<Annuncio> iteratore = bacheca.iterator();
            iteratore.remove(); // dovrebbe lanciare UnsupportedOperationException
        });

        // Verifica che sia stata lanciata UnsupportedOperationException
        assertEquals(UnsupportedOperationException.class, e.getClass());
        assertTrue(e.getMessage() == null || "remove".equals(e.getMessage()));
    }

}
