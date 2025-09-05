package modello.test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import modello.Annuncio;
import modello.Utente;
import modello.exception.AnnuncioException;

class AnnuncioTest {

    @Test
    void testCostruttoreConVendita() throws Exception {
        // Caso valido: tipo "vendita"
        LocalDate data = LocalDate.parse("2027-04-25");
        Utente utente = new Utente("alessandro.rossi@example.com", "alessandroRossi");
        Annuncio annuncio = new Annuncio(utente, "Laptop Ultraleggero", 950, "vendita", "informatica, tecnologia",
                "2027-04-25");

        assertEquals("Laptop Ultraleggero", annuncio.getArticolo());
        assertEquals(950, annuncio.getPrezzo());
        assertEquals("vendita", annuncio.getTipologia());
        assertEquals("informatica, tecnologia", annuncio.getParoleChiave());
        assertEquals(data, annuncio.getDataScadenza());
        assertEquals(utente, annuncio.getAutore());
    }

    @Test
    void testCostruttoreConVenditaConId() throws Exception {
        // Caso valido: tipo "vendita" passando anche id
        LocalDate data = LocalDate.parse("2027-04-25");
        Utente utente = new Utente("alessandro.rossi@example.com", "alessandroRossi");
        Annuncio annuncio = new Annuncio(9876, utente, "Laptop Ultraleggero", 950, "vendita", "informatica, tecnologia",
                "2027-04-25");

        assertEquals(9876, annuncio.getId());
        assertEquals("Laptop Ultraleggero", annuncio.getArticolo());
        assertEquals(950.0, annuncio.getPrezzo());
        assertEquals("vendita", annuncio.getTipologia());
        assertEquals("informatica, tecnologia", annuncio.getParoleChiave());
        assertEquals(data, annuncio.getDataScadenza());
        assertEquals(utente, annuncio.getAutore());
    }

    @Test
    void testCostruttoreConAcquisto() throws Exception {
        // Caso valido: tipo "acquisto" senza data scadenza
        Utente utente = new Utente("claudia.verdi@example.com", "claudiaVerdi");
        Annuncio annuncio = new Annuncio(utente, "Smart TV 50\"", 620, "acquisto", "elettronica, intrattenimento",
                null);

        assertEquals("Smart TV 50\"", annuncio.getArticolo());
        assertEquals(620.0, annuncio.getPrezzo());
        assertEquals("acquisto", annuncio.getTipologia());
        assertEquals("elettronica, intrattenimento", annuncio.getParoleChiave());
        assertNull(annuncio.getDataScadenza());
        assertEquals(utente, annuncio.getAutore());
    }

    @Test
    void testDataScadenzaNonValida() throws Exception {
        // Caso non valido: tipo "vendita" senza data scadenza
        Utente utente = new Utente("claudia.verdi@example.com", "claudiaVerdi");

        AnnuncioException e = assertThrows(AnnuncioException.class, () -> {
            new Annuncio(utente, "Smart TV 50\"", 620, "vendita", "elettronica, intrattenimento", null);
        });
        assertEquals("Data di scadenza non valida; usare il formato: yyyy-MM-dd", e.getMessage());

        // Caso non valido: tipo "vendita" con data di scadenza non valida
        AnnuncioException e1 = assertThrows(AnnuncioException.class, () -> {
            new Annuncio(utente, "Smart TV 50\"", 620, "vendita", "elettronica, intrattenimento", "2027-15-04");
        });
        assertEquals("Formato della data non valido. Usare il formato yyyy-MM-dd.", e1.getMessage());
    }

    @Test
    void testPrezzoNonValido() throws Exception {
        // Caso non valido: prezzo negativo
        Utente utente = new Utente("alessandro.rossi@example.com", "alessandroRossi");
        AnnuncioException e = assertThrows(AnnuncioException.class, () -> {
            new Annuncio(utente, "Laptop Ultraleggero", -10, "vendita", "informatica, tecnologia", "2027-04-25");
        });
        assertEquals("Il prezzo deve essere maggiore di zero", e.getMessage());
    }

    @Test
    void testTipologiaNonValida() throws Exception {
        // Caso non valido: tipologia non riconosciuta
        Utente utente = new Utente("alessandro.rossi@example.com", "alessandroRossi");
        AnnuncioException e = assertThrows(AnnuncioException.class, () -> {
            new Annuncio(utente, "Laptop Ultraleggero", 950, "affitto", "informatica, tecnologia", "2027-04-25");
        });
        assertEquals("Tipologia non valida, deve essere 'acquisto' o 'vendita'", e.getMessage());
    }

    @Test
    void testParoleChiaveNonValide() throws Exception {
        Utente utente = new Utente("claudia.verdi@example.com", "claudiaVerdi");
        // Caso valido: stringa vuota
        assertDoesNotThrow(() -> {
            new Annuncio(utente, "Smart TV 50\"", 620, "vendita", "", "2027-04-25");
        });

        // Caso non valido: formato parole chiave errato
        AnnuncioException e = assertThrows(AnnuncioException.class, () -> {
            new Annuncio(utente, "Smart TV 50\"", 620, "vendita", "elettronica|intrattenimento", "2027-04-25");
        });
        assertEquals("Formato parole chiave errato, usare parole separate da virgola", e.getMessage());
    }
}
