package interfaccia.grafica;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import modello.Bacheca;
import modello.Utente;
import interfaccia.grafica.vista.BachecaPanel;
import java.text.Normalizer;

public class InterfacciaGrafica extends JFrame {

    private Bacheca model;
    private Utente utente;

    public InterfacciaGrafica() {
        this.model = new Bacheca();

        // Creazione dell'utente (con sanitizzazione input dalla GUI)
        LogIn();

        // Caricamento degli annunci da file
        caricaBacheca();

        // Configurazione del JFrame
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Bacheca Annunci");

        JPanel bachecaPanel = new BachecaPanel(model, utente);
        setContentPane(bachecaPanel);

        pack();
        setSize(1300, 400);
        setLocationRelativeTo(null);
        setVisible(true);

        System.out.println("[DEBUG] La GUI è stata caricata e resa visibile.");
    }

    private void caricaBacheca() {
        try {
            this.model.caricaAnnunciDaFile("src/annunci.txt");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Errore nel caricamento degli annunci: " + e.getMessage(), "Errore",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void LogIn() {
        JTextField nomeField = new JTextField(20);
        JTextField emailField = new JTextField(20);

        JComponent[] inputs = new JComponent[] {
                new JLabel("Inserisci il tuo nome utente:"),
                nomeField,
                new JLabel("Inserisci la tua email:"),
                emailField,
        };

        while (true) {
            int result = JOptionPane.showConfirmDialog(null, inputs, "Log In", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                // SANITIZZA input prima di creare Utente
                String rawNome = nomeField.getText();
                String rawEmail = emailField.getText();

                String nomeSanitizzato = sanitizeString(rawNome);
                String emailSanitizzata = sanitizeEmail(rawEmail);

                try {
                    // debug: puoi commentare dopo i test
                    // System.out.println("DEBUG: nome raw='" + rawNome + "' -> sanitizzato='" +
                    // nomeSanitizzato + "'");
                    this.utente = new Utente(emailSanitizzata, nomeSanitizzato);
                    break;
                } catch (Exception e) {
                    // Stampa codepoint per capire caratteri invisibili se necessario
                    System.err.println("[DEBUG] Errore creazione Utente: " + e.getMessage());
                    debugPrintCodepoints(rawNome, "nome (raw)");
                    debugPrintCodepoints(nomeSanitizzato, "nome (sanitizzato)");
                    debugPrintCodepoints(rawEmail, "email (raw)");
                    debugPrintCodepoints(emailSanitizzata, "email (sanitizzata)");

                    JOptionPane.showMessageDialog(this,
                            "Errore nella creazione dell'utente: " + e.getMessage()
                                    + "\nVerifica nome e email (evita caratteri speciali o spazi invisibili).",
                            "Errore", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                System.exit(0);
            }
        }
    }

    /**
     * Normalizza e pulisce una stringa di input per il nome.
     * - normalizza Unicode (NFC)
     * - sostituisce NBSP con spazio normale
     * - rimuove zero-width e BOM
     * - rimuove caratteri di controllo
     * - collassa spazi multipli e trim
     */
    private static String sanitizeString(String s) {
        if (s == null)
            return "";
        // Normalizza
        s = Normalizer.normalize(s, Normalizer.Form.NFC);
        // Replace NBSP with normal space
        s = s.replace('\u00A0', ' ');
        // Remove zero-width and BOM
        s = s.replaceAll("[\\u200B-\\u200D\\uFEFF]", "");
        // Remove control chars (tabs/newlines optionally)
        s = s.replaceAll("[\\p{Cc}]", "");
        // Collapse whitespace and trim
        s = s.replaceAll("\\s+", " ").trim();
        return s;
    }

    /**
     * Sanitizza l'email: trim e rimozione caratteri di controllo
     */
    private static String sanitizeEmail(String email) {
        if (email == null)
            return "";
        email = email.trim();
        email = Normalizer.normalize(email, Normalizer.Form.NFC);
        email = email.replaceAll("[\\p{Cc}]", "");
        // rimuoviamo NBSP se presente
        email = email.replace('\u00A0', ' ');
        return email;
    }

    /**
     * Stampa su stderr i codepoint Unicode della stringa per debug
     */
    private static void debugPrintCodepoints(String s, String label) {
        if (s == null) {
            System.err.println("[DEBUG] " + label + " = null");
            return;
        }
        System.err.println("[DEBUG] " + label + " -> raw: '" + s + "'");
        System.err.print("[DEBUG] Codepoints: ");
        s.codePoints().forEach(cp -> System.err.print(String.format("U+%04X ", cp)));
        System.err.println("\n[DEBUG] Length: " + s.length());
    }
}
