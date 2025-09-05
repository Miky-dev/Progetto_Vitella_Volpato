package interfaccia.grafica.controllo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import interfaccia.grafica.vista.ContentPanel;
import modello.Annuncio;
import modello.Bacheca;
import modello.Utente;

public class ControlloBacheca implements ActionListener {
	private Bacheca model;
	private ContentPanel view;
	private Utente utente;

	/**
	 * Inizializza il controller per svolgere le operazioni
	 * 
	 * @param view   La vista per poterla aggiornare dopo ogni operazione
	 * @param model  La bacheca caricata
	 * @param utente L'utente che ha effettuato l'eccesso
	 */
	public ControlloBacheca(ContentPanel view, Bacheca model, Utente utente) {
		this.model = model;
		this.view = view;
		this.utente = utente;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton) e.getSource();
		String scelta = source.getText();

		switch (scelta) {
			case "Aggiungi Annuncio":
				aggiungi();
				break;
			case "Rimuovi Annuncio":
				rimuovi();
				break;
			case "Cerca Annunci":
				cerca();
				break;
			case "Pulisci Bacheca":
				pulisci();
				break;
			case "Aggiungi Parola Chiave ad Annuncio":
				aggiungiParolachiave();
				break;
			default:
				break;
		}

		view.updateUI(); // aggiorna la vista dopo ogni azione
	}

	private void aggiungi() {
		// Finestra di dialogo per ottenere i dettagli dell'annuncio
		JTextField titoloField = new JTextField(20);
		JTextField prezzoField = new JTextField(20);
		JTextField tipologiaField = new JTextField(20);
		JTextField paroleChiaveField = new JTextField(20);
		JTextField dataScadenzaField = new JTextField(20);

		// Creazione di un array con tutti i campi per la finestra di dialogo
		JComponent[] inputs = new JComponent[] {
				new JLabel("Titolo:"),
				titoloField,
				new JLabel("Prezzo: (usare il punto per parte decimale)"),
				prezzoField,
				new JLabel("Tipologia (acquisto/vendita):"),
				tipologiaField,
				new JLabel("Parole chiave (separate da virgola):"),
				paroleChiaveField,
				new JLabel("Data di scadenza (yyyy-MM-dd, lascia vuoto se nessuna scadenza):"),
				dataScadenzaField
		};

		// Mostra il dialogo per l'inserimento
		int result = JOptionPane.showConfirmDialog(null, inputs, "Inserisci nuovo annuncio",
				JOptionPane.OK_CANCEL_OPTION);

		if (result == JOptionPane.OK_OPTION) {
			try {
				String titolo = titoloField.getText();
				float prezzo = Float.parseFloat(prezzoField.getText());
				String tipologia = tipologiaField.getText();
				String paroleChiave = paroleChiaveField.getText();
				String dataScadenza = dataScadenzaField.getText();

				Annuncio annuncio = new Annuncio(utente, titolo, prezzo, tipologia, paroleChiave, dataScadenza);
				model.aggiungiAnnuncio(annuncio);
				model.salvaAnnunciSuFile("src/annunci.txt");

				JOptionPane.showMessageDialog(null, "Annuncio aggiunto con successo!");

				// Se l'annuncio è di tipo acquisto, cerca corrispondenze
				if (tipologia.equals("acquisto")) {
					ArrayList<Annuncio> risultati = model.cercaPerParolaChiave(paroleChiave);

					if (risultati.size() > 1) { // 1 perche viene inserito in risultati anche l'annuncio appena creato
						StringBuilder risultatoStringa = new StringBuilder("Annunci che potrebbero interessarti:\n");
						for (Annuncio corrispondenza : risultati) {
							if (!corrispondenza.equals(annuncio)) {
								risultatoStringa.append(corrispondenza).append("\n");
							}
						}
						JOptionPane.showMessageDialog(null, risultatoStringa.toString(),
								"Annunci che potrebbero interessarti:", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "Errore nell'aggiungere l'annuncio: " + ex.getMessage(), "Errore",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void rimuovi() {
		JTextField idField = new JTextField(20);
		JComponent[] inputs = new JComponent[] { new JLabel("ID:"), idField };

		int result = JOptionPane.showConfirmDialog(null, inputs, "Rimuovi annuncio", JOptionPane.OK_CANCEL_OPTION);

		if (result == JOptionPane.OK_OPTION) {
			try {

				int id = Integer.parseInt(idField.getText());
				model.rimuoviAnnuncio(id, utente);
				model.salvaAnnunciSuFile("src/annunci.txt");

				JOptionPane.showMessageDialog(null, "Annuncio rimosso con successo!");
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "Errore nella rimozione dell'annuncio: " + ex.getMessage(),
						"Errore", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void cerca() {
		JTextField paroleChiaveField = new JTextField(20);
		JComponent[] inputs = new JComponent[] { new JLabel("Parole Chiave (separte da virgola e spazio):"),
				paroleChiaveField };

		int result = JOptionPane.showConfirmDialog(null, inputs, "Cerca annuncio per parole chiave",
				JOptionPane.OK_CANCEL_OPTION);

		if (result == JOptionPane.OK_OPTION) {
			try {
				String paroleChiave = paroleChiaveField.getText();
				ArrayList<Annuncio> risultati = model.cercaPerParolaChiave(paroleChiave);

				StringBuilder risultatoStringa = new StringBuilder();
				if (risultati.isEmpty()) {
					risultatoStringa.append("Nessun annuncio trovato con queste parole chiave.");
				} else {
					risultatoStringa.append("Annunci trovati:\n");
					for (Annuncio annuncio : risultati) {
						risultatoStringa.append(annuncio).append("\n");
					}
				}

				JOptionPane.showMessageDialog(null, risultatoStringa.toString(), "Risultati della ricerca",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "Errore nella ricerca dell'annuncio: " + ex.getMessage(), "Errore",
						JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	private void pulisci() {
		try {
			if (model.pulisciBacheca()) {
				model.salvaAnnunciSuFile("src/annunci.txt");
				JOptionPane.showMessageDialog(null, "Annunci scaduti rimossi con successo!");
			} else {
				JOptionPane.showMessageDialog(null, "Nessun annuncio scaduto trovato!");
			}

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Errore nella rimozione degli annunci scaduti: " + e.getMessage(),
					"Errore", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void aggiungiParolachiave() {
		JTextField idField = new JTextField(20);
		JTextField paroleField = new JTextField(20);
		JComponent[] inputs = new JComponent[] { new JLabel("ID:"), idField, new JLabel("Nuove parole chiave:"),
				paroleField };

		int result = JOptionPane.showConfirmDialog(null, inputs, "aggiungi parola chiave",
				JOptionPane.OK_CANCEL_OPTION);

		if (result == JOptionPane.OK_OPTION) {
			try {
				int id = Integer.parseInt(idField.getText());
				String nuovaParola = paroleField.getText();

				model.aggiungiNuovaParolaChiave(id, utente, nuovaParola);
				model.salvaAnnunciSuFile("src/annunci.txt");

				JOptionPane.showMessageDialog(null, "Annuncio modificato con successo!");
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "Errore nella modifica dell'annuncio: " + ex.getMessage(), "Errore",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
