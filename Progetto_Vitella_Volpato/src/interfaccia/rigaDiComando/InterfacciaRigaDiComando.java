package interfaccia.rigaDiComando;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import modello.Bacheca;
import modello.Annuncio;
import modello.Utente;
import modello.exception.AnnuncioException;
import modello.exception.AutoreNonAutorizzatoException;
import modello.exception.BachecaException;
import modello.exception.UtenteException;

/**
 * Interfaccia a riga di comando per gestire la Bacheca.
 * - Non chiude lo Scanner passato dal Main.
 * - Usa path relativo per il file degli annunci (costante FILE_NAME).
 */
public class InterfacciaRigaDiComando {

    private static final String FILE_NAME = "annunci.txt"; // file nella working directory

    private final Bacheca bacheca;
    private Utente utente;
    private final Scanner scanner;

    public InterfacciaRigaDiComando(Scanner scanner) throws BachecaException {
        this.scanner = scanner;
        this.bacheca = new Bacheca();

        // Prima il caricamento (se non riesce, si continua con bacheca vuota)
        caricaBacheca();

        // Poi login (permette operazioni dell'utente)
        logIn();

        // Avvia ciclo principale
        run();
    }

    /**
     * Carica gli annunci da file. Se fallisce, continua con la bacheca vuota
     * e mostra un messaggio informativo (non termina il programma).
     * @throws BachecaException 
     */
    private void caricaBacheca() throws BachecaException {
        try {
            this.bacheca.caricaAnnunciDaFile(FILE_NAME);
            System.out.println("Bacheca caricata da file: " + FILE_NAME);
        } catch (IOException | AnnuncioException | UtenteException e) {
            System.out.println("Attenzione: non è stato possibile caricare gli annunci da '" + FILE_NAME + "'.");
            System.out.println("La bacheca partirà vuota. Errore: " + e.getMessage());
            // non facciamo System.exit(0) per non terminare l'app
        }
    }

    /**
     * Richiede nome ed email e crea l'Utente; ripete in caso di errore.
     */
    private void logIn() {
        while (true) {
            System.out.print("Inserisci il tuo nome: ");
            String nome = scanner.nextLine().trim();
            System.out.print("Inserisci la tua email: ");
            String email = scanner.nextLine().trim();

            try {
                this.utente = new Utente(email, nome);
                System.out.println("Benvenuto, " + utente.getNome() + "!");
                break;
            } catch (UtenteException e) {
                System.out.println("Errore nella creazione dell'utente: " + e.getMessage());
                System.out.println("Riprova.");
            }
        }
    }

    /**
     * Menu principale: cicla fino a scelta di uscita.
     */
    private void run() {
        while (true) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Aggiungi annuncio");
            System.out.println("2. Rimuovi annuncio");
            System.out.println("3. Cerca annuncio");
            System.out.println("4. Pulisci bacheca (annunci scaduti)");
            System.out.println("5. Visualizza bacheca");
            System.out.println("6. Aggiungi Parola Chiave ad Annuncio");
            System.out.println("7. Esci");
            System.out.print("Scelta: ");

            String line = scanner.nextLine().trim();
            int scelta;
            try {
                scelta = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Errore: inserire un numero valido fra 1 e 7.");
                continue;
            }

            switch (scelta) {
                case 1:
                    aggiungiAnnuncio();
                    break;
                case 2:
                    rimuoviAnnuncio();
                    break;
                case 3:
                    cercaAnnuncio();
                    break;
                case 4:
                    pulisciBacheca();
                    break;
                case 5:
                    visualizzaBacheca();
                    break;
                case 6:
                    aggiungiNuovaParolaChiave();
                    break;
                case 7:
                    System.out.println("Uscita dalla CLI. Arrivederci!");
                    return;
                default:
                    System.out.println("Scelta non valida, riprova.");
            }
        }
    }

    /**
     * Aggiunge un annuncio; effettua validazioni di base e salva su file.
     */
    private void aggiungiAnnuncio() {
        while (true) {
            try {
                System.out.print("Inserisci il titolo dell'articolo: ");
                String articolo = scanner.nextLine().trim();
                if (articolo.isEmpty()) {
                    System.out.println("Titolo non valido. Riprova.");
                    continue;
                }

                System.out.print("Inserisci il prezzo dell'articolo: ");
                float prezzo;
                try {
                    prezzo = Float.parseFloat(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Prezzo non valido. Riprova.");
                    continue;
                }

                System.out.print("Inserisci la tipologia (acquisto/vendita): ");
                String tipologia = scanner.nextLine().trim();
                if (tipologia.isEmpty()) {
                    System.out.println("Tipologia non valida. Riprova.");
                    continue;
                }

                System.out.print("Inserisci le parole chiave (separate da virgola): ");
                String paroleChiave = scanner.nextLine().trim();

                System.out.print("Inserisci la data di scadenza (yyyy-MM-dd) o lasciare vuoto per nessuna scadenza: ");
                String dataScadenza = scanner.nextLine().trim();
                if (dataScadenza.isEmpty()) dataScadenza = null;

                Annuncio annuncio = new Annuncio(utente, articolo, prezzo, tipologia, paroleChiave, dataScadenza);

                ArrayList<Annuncio> risultato = bacheca.aggiungiAnnuncio(annuncio);

                try {
                    bacheca.salvaAnnunciSuFile(FILE_NAME);
                } catch (IOException ioe) {
                    System.out.println("Attenzione: annuncio aggiunto ma non è stato possibile salvare su file: " + ioe.getMessage());
                }

                System.out.println("Annuncio aggiunto con successo! (ID: " + annuncio.getId() + ")");

                // se è acquisto, mostriamo i match in vendita
                if ("acquisto".equalsIgnoreCase(tipologia) && risultato != null && !risultato.isEmpty()) {
                    System.out.println("\nAnnunci che potrebbero interessarti:");
                    for (Annuncio ann : risultato) {
                        if ("vendita".equalsIgnoreCase(ann.getTipologia())) {
                            System.out.println(ann);
                        }
                    }
                }
                break;
            } catch (AnnuncioException e) {
                System.out.println("Errore nella creazione dell'annuncio: " + e.getMessage());
                System.out.println("Riprovare...");
            } catch (BachecaException e) {
                System.out.println("Errore nell'aggiunta: " + e.getMessage());
                System.out.println("Riprovare...");
            } catch (Exception e) {
                System.out.println("Errore imprevisto: " + e.getMessage());
                e.printStackTrace();
                System.out.println("Riprovare...");
            }
        }
    }

    /**
     * Rimuove un annuncio se l'utente è l'autore.
     */
    private void rimuoviAnnuncio() {
        while (true) {
            try {
                System.out.print("Inserisci l'ID dell'annuncio da rimuovere o 0 per uscire: ");
                int id = Integer.parseInt(scanner.nextLine().trim());
                if (id == 0) return;

                bacheca.rimuoviAnnuncio(id, utente);
                try {
                    bacheca.salvaAnnunciSuFile(FILE_NAME);
                } catch (IOException ioe) {
                    System.out.println("Attenzione: annuncio rimosso ma non è stato possibile salvare su file: " + ioe.getMessage());
                }
                System.out.println("Annuncio rimosso con successo!");
                return;
            } catch (NumberFormatException e) {
                System.out.println("Errore: Formato ID non valido, riprova...");
            } catch (AutoreNonAutorizzatoException e) {
                System.out.println("Errore: " + e.getMessage());
                return;
            } catch (BachecaException e) {
                System.out.println("Errore: " + e.getMessage());
                return;
            }
        }
    }

    /**
     * Cerca annunci per parole chiave (stringa separata da virgola).
     */
    private void cercaAnnuncio() {
        System.out.print("Inserisci le parole chiave da cercare (separate da virgola): ");
        String paroleChiave = scanner.nextLine().trim();
        ArrayList<Annuncio> risultati = bacheca.cercaPerParolaChiave(paroleChiave);
        if (risultati == null || risultati.isEmpty()) {
            System.out.println("Nessun annuncio trovato.");
            return;
        }
        System.out.println("Annunci trovati:");
        for (Annuncio annuncio : risultati) {
            System.out.println(annuncio);
        }
    }

    /**
     * Pulisce la bacheca dagli annunci scaduti.
     */
    private void pulisciBacheca() {
        boolean rimosso = bacheca.pulisciBacheca();
        if (rimosso) {
            try {
                bacheca.salvaAnnunciSuFile(FILE_NAME);
            } catch (IOException ioe) {
                System.out.println("Annunci rimossi ma errore nel salvataggio: " + ioe.getMessage());
            }
            System.out.println("Annunci scaduti rimossi.");
        } else {
            System.out.println("Nessun annuncio scaduto.");
        }
    }

    /**
     * Visualizza tutti gli annunci.
     */
    private void visualizzaBacheca() {
        ArrayList<Annuncio> annunci = bacheca.getAnnunci();
        if (annunci.isEmpty()) {
            System.out.println("La bacheca è vuota.");
            return;
        }
        System.out.println("Annunci nella bacheca:");
        for (Annuncio a : annunci) {
            System.out.println(a);
        }
    }

    /**
     * Aggiunge una o più parole chiave a un annuncio. Se l'utente inserisce più parole
     * separate da virgola, vengono aggiunte singolarmente (chiamando più volte il metodo della Bacheca).
     */
    private void aggiungiNuovaParolaChiave() {
        while (true) {
            try {
                System.out.print("Inserisci l'ID dell'annuncio da modificare o 0 per uscire: ");
                int id = Integer.parseInt(scanner.nextLine().trim());
                if (id == 0) return;

                System.out.print("Inserisci le nuove parole chiave (separate da virgola): ");
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    System.out.println("Nessuna parola inserita. Annullato.");
                    return;
                }

                // Supporto per più parole: split e chiamata per ciascuna
                String[] parole = line.split(",");
                boolean anyAdded = false;
                for (String p : parole) {
                    String parola = p.trim();
                    if (parola.isEmpty()) continue;
                    try {
                        if (bacheca.aggiungiNuovaParolaChiave(id, utente, parola)) {
                            anyAdded = true;
                        }
                    } catch (AutoreNonAutorizzatoException | AnnuncioException e) {
                        System.out.println("Impossibile aggiungere '" + parola + "': " + e.getMessage());
                    }
                }

                if (anyAdded) {
                    try {
                        bacheca.salvaAnnunciSuFile(FILE_NAME);
                    } catch (IOException ioe) {
                        System.out.println("Parole aggiunte ma errore nel salvataggio: " + ioe.getMessage());
                    }
                    System.out.println("Operazione completata.");
                } else {
                    System.out.println("Nessuna parola aggiunta.");
                }
                return;
            } catch (NumberFormatException e) {
                System.out.println("ID non valido, riprova...");
            }
        }
    }
}
