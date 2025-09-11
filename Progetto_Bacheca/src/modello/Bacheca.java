package modello;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;
import modello.exception.*;

/**
 * Classe che rappresenta la bacheca di annunci.
 * Implementa Iterable<Annuncio> per permettere la lettura degli annunci
 * (ma non la loro rimozione tramite l'iterator restituito).
 */
public class Bacheca implements Iterable<Annuncio> {

    /** Lista degli annunci presenti in bacheca (ordine di inserimento). */
    private final List<Annuncio> annunci;

    /** Pool di id presenti; uso Set per evitare duplicati e avere contains O(1). */
    private final Set<Integer> poolId;

    /**
     * Costruttore della bacheca: inizializza le strutture dati.
     */
    public Bacheca() {
        this.annunci = new ArrayList<>();
        this.poolId = new HashSet<>();
    }

    /**
     * Restituisce un iteratore sugli annunci. L'iteratore non permette la rimozione
     * perché è basato su una lista non modificabile.
     */
    @Override
    public Iterator<Annuncio> iterator() {
        return Collections.unmodifiableList(annunci).iterator();
    }

    /**
     * Aggiunge un nuovo annuncio alla bacheca se l'ID non è già presente.
     * Se l'annuncio è di tipo "acquisto" ritorna la lista degli annunci in vendita
     * che condividono parole chiave (intersezione non vuota).
     *
     * @param annuncio annuncio da aggiungere
     * @return lista di annunci trovati (per acquisto) oppure lista vuota
     * @throws BachecaException se annuncio nullo o ID già presente
     */
    public ArrayList<Annuncio> aggiungiAnnuncio(Annuncio annuncio) throws BachecaException {
        if (annunci.contains(annuncio)) {
            throw new BachecaException("Annuncio già presente");
        }
        controlloIdPresente(annuncio.getId());
        annunci.add(annuncio);
        poolId.add(annuncio.getId());

        // Se è acquisto → ritorna SOLO annunci di vendita compatibili
        if ("acquisto".equalsIgnoreCase(annuncio.getTipologia())) {
            return new ArrayList<>(
                    cercaPerParolaChiave(annuncio.getParoleChiave())
                            .stream()
                            .filter(a -> "vendita".equalsIgnoreCase(a.getTipologia()))
                            .toList());
        }
        return new ArrayList<>();
    }

    /**
     * Rimuove un annuncio (specificato dall'id) solo se l'utente è l'autore.
     *
     * @param id     id dell'annuncio da rimuovere
     * @param utente utente che richiede la rimozione
     * @return true se rimosso con successo
     * @throws AutoreNonAutorizzatoException se l'utente non è l'autore
     * @throws BachecaException              se annuncio non trovato
     */
    public boolean rimuoviAnnuncio(int id, Utente utente)
            throws AutoreNonAutorizzatoException, BachecaException {

        Iterator<Annuncio> it = annunci.iterator();
        while (it.hasNext()) {
            Annuncio a = it.next();
            if (a.getId() == id) {
                if (!a.getAutore().equals(utente)) {
                    throw new AutoreNonAutorizzatoException("Non sei autorizzato a rimuovere questo annuncio.");
                }
                it.remove(); // rimuovo dalla lista
                poolId.remove(id); // e dalla pool degli id
                return true;
            }
        }

        throw new BachecaException("Annuncio non trovato.");
    }

    /**
     * Cerca gli annunci che condividono almeno una parola chiave con la stringa
     * `paroleChiave` (attesa come lista separata da virgole). Il confronto è
     * case-insensitive e lavora su parole "trimmed".
     *
     * @param paroleChiave stringa con parole chiave separate da virgola
     * @return lista di annunci che hanno intersezione con le parole cercate
     */
    public ArrayList<Annuncio> cercaPerParolaChiave(String paroleChiave) {
        ArrayList<Annuncio> risultati = new ArrayList<>();
        if (paroleChiave == null || paroleChiave.isBlank()) {
            return risultati;
        }

        // Normalizzo la richiesta in un Set (lowercase, trimmed)
        Set<String> chiaviRicerca = Arrays.stream(paroleChiave.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        for (Annuncio a : annunci) {
            String paroleAnnuncio = a.getParoleChiave();
            if (paroleAnnuncio == null || paroleAnnuncio.isBlank())
                continue;

            Set<String> chiaviAnnuncio = Arrays.stream(paroleAnnuncio.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());

            // verifica intersezione
            boolean interseca = chiaviRicerca.stream().anyMatch(chiaviAnnuncio::contains);
            if (interseca) {
                risultati.add(a);
            }
        }
        return risultati;
    }

    /**
     * Pulisce la bacheca rimuovendo gli annunci scaduti.
     *
     * @return true se è stata rimossa almeno un'entrata, false altrimenti
     */
    public boolean pulisciBacheca() {
        boolean rimosso = false;
        Iterator<Annuncio> it = annunci.iterator();
        while (it.hasNext()) {
            Annuncio a = it.next();
            if (a.isScaduto()) {
                it.remove();
                poolId.remove(a.getId());
                rimosso = true;
            }
        }
        return rimosso;
    }

    /**
     * Aggiunge una nuova parola chiave a un annuncio se chi la richiede è l'autore.
     *
     * @param id          id annuncio
     * @param utente      utente che richiede l'aggiunta
     * @param nuovaParola parola da aggiungere
     * @return true se aggiunta con successo
     * @throws AutoreNonAutorizzatoException se l'utente non è autore
     * @throws AnnuncioException             se annuncio non trovato o parola non
     *                                       valida
     */
    public boolean aggiungiNuovaParolaChiave(int id, Utente utente, String nuovaParola)
            throws AutoreNonAutorizzatoException, AnnuncioException {

        if (nuovaParola == null || nuovaParola.trim().isEmpty()) {
            throw new AnnuncioException("Parola chiave non valida");
        }

        for (Annuncio a : annunci) {
            if (a.getId() == id) {
                if (!a.getAutore().equals(utente)) {
                    throw new AutoreNonAutorizzatoException("Non sei autorizzato a rimuovere questo annuncio.");
                }
                a.aggiungiParola(nuovaParola.trim());
                return true;
            }
        }
        throw new AnnuncioException("Annuncio non trovato");
    }

    /**
     * Restituisce la lista degli annunci (modificabile, come nei test JUnit).
     */
    public ArrayList<Annuncio> getAnnunci() {
        return new ArrayList<>(annunci);
    }

    /**
     * Restituisce il poolId (modificabile, come nei test JUnit).
     */
    public Set<Integer> getPoolId() {
        return poolId;
    }

    /**
     * Salva gli annunci su file. Formato di riga:
     * id;autoreToString;articolo;prezzo;tipologia;paroleChiave;dataScadenza
     *
     * @param fileName percorso file dove salvare
     * @throws IOException in caso di errore I/O
     */
    public void salvaAnnunciSuFile(String fileName) throws IOException {
        try (PrintWriter output = new PrintWriter(new FileWriter(fileName))) {
            for (Annuncio a : annunci) {
                String data = (a.getDataScadenza() != null) ? a.getDataScadenza().toString() : "null";
                // formato: id;email;nome;articolo;prezzo;tipologia;paroleChiave;dataScadenza
                output.printf("%d;%s;%s;%s;%.2f;%s;%s;%s%n",
                        a.getId(),
                        a.getAutore().getEmail(),
                        a.getAutore().getNome(),
                        a.getArticolo(),
                        a.getPrezzo(),
                        a.getTipologia(),
                        a.getParoleChiave(),
                        data);
            }
        } catch (IOException e) {
            throw new IOException("Errore nel salvataggio della bacheca su file", e);
        }
    }

    /**
     * Carica gli annunci da file. Svuota la bacheca prima di caricare e resettare
     * poolId.
     *
     * @param fileName percorso file
     * @throws IOException       in caso di I/O
     * @throws AnnuncioException se errore nella creazione annuncio
     * @throws UtenteException   se errore nella creazione utente
     * @throws BachecaException
     */
    public void caricaAnnunciDaFile(String fileName)
            throws IOException, AnnuncioException, UtenteException, BachecaException {

        try (BufferedReader input = new BufferedReader(new FileReader(fileName))) {
            annunci.clear();
            poolId.clear();

            String linea;
            while ((linea = input.readLine()) != null) {
                // split con -1 per preservare campi vuoti
                String[] dati = linea.split(";", -1);

                if (dati.length == 8) {
                    // nuovo formato: id;email;nome;articolo;prezzo;tipologia;parole;data
                    int id = Integer.parseInt(dati[0].trim());
                    String email = dati[1].trim();
                    String nome = dati[2].trim();
                    Utente utente = new Utente(email, nome);

                    String articolo = dati[3].trim();
                    double prezzo = Double.parseDouble(dati[4].trim().replace(",", "."));
                    String tipologia = dati[5].trim();
                    String paroleChiave = dati[6].trim();
                    LocalDate scadenza = ("null".equalsIgnoreCase(dati[7].trim()) || dati[7].trim().isEmpty())
                            ? null
                            : LocalDate.parse(dati[7].trim());

                    Annuncio annuncio = new Annuncio(
                            id,
                            utente,
                            articolo,
                            (float) prezzo,
                            tipologia,
                            paroleChiave,
                            scadenza != null ? scadenza.toString() : null);

                    controlloIdPresente(id);
                    annunci.add(annuncio);
                    poolId.add(id);

                } else if (dati.length == 7) {
                    // vecchio formato (compatibilità):
                    // id;autoreToString;articolo;prezzo;tipologia;parole;data
                    int id = Integer.parseInt(dati[0].trim());

                    Utente utente = generaAutore(dati[1].trim()); // fallback sul parsing precedente

                    String articolo = dati[2].trim();
                    double prezzo = Double.parseDouble(dati[3].trim().replace(",", "."));
                    String tipologia = dati[4].trim();
                    String paroleChiave = dati[5].trim();
                    LocalDate scadenza = ("null".equalsIgnoreCase(dati[6].trim()) || dati[6].trim().isEmpty())
                            ? null
                            : LocalDate.parse(dati[6].trim());

                    Annuncio annuncio = new Annuncio(
                            id,
                            utente,
                            articolo,
                            (float) prezzo,
                            tipologia,
                            paroleChiave,
                            scadenza != null ? scadenza.toString() : null);

                    controlloIdPresente(id);
                    annunci.add(annuncio);
                    poolId.add(id);
                } else {
                    // riga malformata: la ignoro (o potresti lanciare eccezione se preferisci)
                    continue;
                }
            }
        } catch (IOException e) {
            throw new IOException("Errore nel caricamento della bacheca", e);
        }
    }

    /**
     * Estrae email e nome dalla stringa fornita da Utente.toString() in modo
     * robusto.
     * Cerca pattern tipo: ...email=qualcosa,...nome=qualcosa...
     *
     * @param autoreString stringa contenente la rappresentazione di Utente
     * @return Utente costruito
     * @throws UtenteException se il parsing o la creazione falliscono
     */
    private Utente generaAutore(String autoreString) throws UtenteException {
        if (autoreString == null || autoreString.isBlank()) {
            throw new UtenteException("Stringa autore vuota");
        }

        // Rimuovo il prefisso "Utente[" e il suffisso "]" se presenti
        if (autoreString.startsWith("Utente[") && autoreString.endsWith("]")) {
            autoreString = autoreString.substring(7, autoreString.length() - 1);
        }

        // Pattern che cerca "email=<qualcosa>, nome=<qualcosa>"
        Pattern p = Pattern.compile("email\\s*=\\s*(.+?)\\s*,\\s*nome\\s*=\\s*(.+)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(autoreString);

        if (m.find()) {
            String email = m.group(1).trim();
            String nome = m.group(2).trim();
            return new Utente(email, nome);
        } else {
            // Fallback: prova a eseguire una semplice estrazione
            String cleaned = autoreString.replaceAll("[\\[\\]]", "").replace("email=", "").replace("nome=", "");
            String[] parti = cleaned.split(",", 2);
            if (parti.length >= 2) {
                String email = parti[0].trim();
                String nome = parti[1].trim();
                return new Utente(email, nome);
            }
        }

        throw new UtenteException("Formato autore non riconosciuto: " + autoreString);
    }

    /**
     * Controlla se l'id è già presente nella pool. Se presente lancia
     * BachecaException,
     * altrimenti lo aggiunge al pool e ritorna true.
     *
     * @param id id da controllare
     * @return true se aggiunto con successo
     * @throws BachecaException se id già presente
     */
    private boolean controlloIdPresente(int id) throws BachecaException {
        if (poolId.contains(id)) {
            throw new BachecaException("ID già presente");
        }
        return true;
    }

    /**
     * Rappresentazione testuale della bacheca (tutti gli annunci, separati da
     * newline).
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Annuncio a : annunci) {
            sb.append(a).append(System.lineSeparator());
        }
        return sb.toString();
    }
}
