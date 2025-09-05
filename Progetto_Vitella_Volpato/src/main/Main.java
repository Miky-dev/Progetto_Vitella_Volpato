package main;

import java.util.Scanner;
import javax.swing.SwingUtilities;

import interfaccia.grafica.InterfacciaGrafica;
import interfaccia.rigaDiComando.InterfacciaRigaDiComando;

/**
 * Classe Main: punto d'ingresso dell'applicazione.
 * Permette di scegliere tra interfaccia grafica e interfaccia da riga di
 * comando.
 *
 * Nota:
 * - L'interfaccia grafica viene avviata sulla Event Dispatch Thread (Swing).
 * - Lo Scanner viene chiuso in modo sicuro alla terminazione del programma.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            while (true) {
                stampaMenu();

                String input = scanner.nextLine();
                if (input == null) {
                    System.out.println("Input nullo, esco.");
                    break;
                }
                input = input.trim();

                // Permetto anche 'q' per uscire rapidamente
                if (input.equalsIgnoreCase("q") || input.equals("3")) {
                    System.out.println("Uscita dal programma...");
                    break;
                }

                int scelta;
                try {
                    scelta = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println(
                            "Errore: formato scelta non valido. Riprova con un numero tra 1 e 3 (o 'q' per uscire).");
                    continue;
                }

                switch (scelta) {
                    case 1:
                        System.out.println("Avvio dell'interfaccia grafica...");
                        // Avvia la GUI nel EDT (buona pratica per Swing)
                        SwingUtilities.invokeLater(() -> {
                            try {
                                new InterfacciaGrafica();
                            } catch (Exception e) {
                                // Se l'interfaccia lancia eccezioni, le logghiamo
                                System.err.println("Errore nell'avvio dell'interfaccia grafica:");
                                e.printStackTrace();
                            }
                        });
                        // Dopo l'avvio della GUI, torniamo al menu in attesa di altre scelte
                        break;

                    case 2:
                        System.out.println("Avvio dell'interfaccia da riga di comando...");
                        try {
                            // Si passa lo stesso scanner all'interfaccia CLI per evitare di aprirne uno
                            // nuovo.
                            // Si presume che la CLI gestisca internamente il proprio ciclo e ritorni quando
                            // l'utente esce.
                            new InterfacciaRigaDiComando(scanner);
                        } catch (Exception e) {
                            System.err.println("Errore nell'avvio dell'interfaccia da riga di comando:");
                            e.printStackTrace();
                        }
                        // dopo il ritorno dall'InterfacciaRigaDiComando si mostra di nuovo il menu
                        break;

                    default:
                        System.out.println("Scelta non valida. Scegli un'opzione tra 1 e 3 (o 'q' per uscire).");
                        break;
                }
            } // fine loop
        } finally {
            // Chiudo lo scanner alla fine del programma (evita leak di risorse).
            // Attenzione: se l'interfaccia CLI memorizza lo scanner e lo usa dopo il
            // ritorno,
            // assicurati che la CLI abbia terminato quando arrivi qui.
            try {
                scanner.close();
            } catch (Exception e) {
                // non dovrebbe succedere, ma logghiamo in caso
                System.err.println("Attenzione: errore nella chiusura dello Scanner:");
                e.printStackTrace();
            }
        }
    }

    /** Stampa il menu principale. */
    private static void stampaMenu() {
        System.out.println();
        System.out.println("Benvenuto! Scegli l'interfaccia che vuoi utilizzare:");
        System.out.println("1. Interfaccia Grafica");
        System.out.println("2. Interfaccia da Riga di Comando");
        System.out.println("3. Esci (o premi 'q')");
        System.out.print("Scelta: ");
    }
}
