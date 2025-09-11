package interfaccia.grafica.controllo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

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
		javax.swing.JTextField paroleChiaveField = new javax.swing.JTextField(20);
		javax.swing.JComponent[] inputs = new javax.swing.JComponent[] {
				new javax.swing.JLabel("Parole Chiave (separate da virgola e spazio):"),
				paroleChiaveField
		};

		int result = javax.swing.JOptionPane.showConfirmDialog(null, inputs, "Cerca annuncio per parole chiave",
				javax.swing.JOptionPane.OK_CANCEL_OPTION);

		if (result == javax.swing.JOptionPane.OK_OPTION) {
			try {
				final String paroleChiave = paroleChiaveField.getText().trim();
				final java.util.ArrayList<Annuncio> risultati = model.cercaPerParolaChiave(paroleChiave);

				if (risultati == null || risultati.isEmpty()) {
					javax.swing.JOptionPane.showMessageDialog(null, "Nessun annuncio trovato con queste parole chiave.",
							"Risultati della ricerca", javax.swing.JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				// Pattern per evidenziare parole chiave
				final String keywordLower = (paroleChiave == null ? "" : paroleChiave.toLowerCase());
				final java.util.regex.Pattern pattern = keywordLower.isEmpty() ? null
						: java.util.regex.Pattern.compile("(?i)(" + java.util.regex.Pattern.quote(keywordLower) + ")");

				// Panel principale con layout verticale
				javax.swing.JPanel mainPanel = new javax.swing.JPanel();
				mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.Y_AXIS));
				mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
				mainPanel.setBackground(java.awt.Color.WHITE);

				for (Annuncio a : risultati) {
					javax.swing.JPanel card = new javax.swing.JPanel();
					card.setLayout(new javax.swing.BoxLayout(card, javax.swing.BoxLayout.Y_AXIS));
					card.setBorder(javax.swing.BorderFactory.createCompoundBorder(
							javax.swing.BorderFactory.createLineBorder(new java.awt.Color(180, 180, 180), 1),
							javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8)));
					card.setBackground(new java.awt.Color(250, 250, 250));

					// Titolo in grassetto e rosso con evidenziazione parole chiave
					javax.swing.JLabel lblTitolo = new javax.swing.JLabel();
					String titolo = a.getArticolo() == null ? "" : a.getArticolo();
					if (pattern != null && pattern.matcher(titolo).find()) {
						String esc = titolo.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
								.replace("\n", "<br/>");
						String highlighted = esc.replaceAll("(?i)(" + java.util.regex.Pattern.quote(keywordLower) + ")",
								"<span style='background: #fff176;'>$1</span>");
						lblTitolo.setText("<html><b><span style='color:red;'>" + highlighted + "</span></b></html>");
					} else {
						lblTitolo.setText("<html><b><span style='color:red;'>" + titolo + "</span></b></html>");
					}

					javax.swing.JLabel lblPrezzo = new javax.swing.JLabel(
							"Prezzo: " + String.format("%.2f", a.getPrezzo()));
					javax.swing.JLabel lblScadenza = new javax.swing.JLabel(
							"Scadenza: " + (a.getDataScadenza() == null ? "-" : a.getDataScadenza().toString()));
					javax.swing.JLabel lblAutore = new javax.swing.JLabel(
							"Autore: " + (a.getAutore() != null ? a.getAutore().getNome() : "-"));
					javax.swing.JLabel lblParoleChiave = new javax.swing.JLabel("Parole Chiave: "
							+ (a.getParoleChiave() == null ? "-" : String.join(", ", a.getParoleChiave())));

					card.add(lblTitolo);
					card.add(lblPrezzo);
					card.add(lblScadenza);
					card.add(lblAutore);
					card.add(lblParoleChiave);

					card.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
					mainPanel.add(card);
					mainPanel.add(javax.swing.Box.createVerticalStrut(10));
				}

				javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(mainPanel);
				scrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				scrollPane.getVerticalScrollBar().setUnitIncrement(16);
				scrollPane.setBorder(null);

				javax.swing.JDialog dialog = new javax.swing.JDialog((java.awt.Frame) null, "Risultati ricerca", true);
				dialog.setLayout(new java.awt.BorderLayout());
				dialog.add(
						new javax.swing.JLabel(
								"Trovati " + risultati.size() + " risultati per \"" + paroleChiave + "\""),
						java.awt.BorderLayout.NORTH);
				dialog.add(scrollPane, java.awt.BorderLayout.CENTER);

				javax.swing.JPanel bottom = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
				javax.swing.JButton btnClose = new javax.swing.JButton("Chiudi");
				btnClose.addActionListener(ev -> dialog.dispose());
				bottom.add(btnClose);
				dialog.add(bottom, java.awt.BorderLayout.SOUTH);

				dialog.setSize(600, 500);
				dialog.setLocationRelativeTo(null);
				dialog.setVisible(true);

			} catch (Exception ex) {
				javax.swing.JOptionPane.showMessageDialog(null,
						"Errore nella ricerca dell'annuncio: " + ex.getMessage(), "Errore",
						javax.swing.JOptionPane.ERROR_MESSAGE);
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

	public List<Annuncio> cercaAnnunci(String keyword) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'cercaAnnunci'");
	}
}
