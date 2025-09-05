package interfaccia.grafica.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import interfaccia.grafica.controllo.ControlloBacheca;
import modello.*;

@SuppressWarnings("serial")
public class BachecaPanel extends JPanel {

	/**
	 * Inizializza il pannello con il titolo in alto, sotto le operazioni e sotto il
	 * contenuto della bacheca (all'interno di uno JScrollPane). Il pannello utente
	 * viene posizionato in fondo.
	 * 
	 * @param model  La bacheca caricata da file
	 * @param utente L'utente che ha fatto l'accesso
	 */
	public BachecaPanel(Bacheca model, Utente utente) {
		// Layout principale
		setLayout(new BorderLayout(10, 10));
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(new Color(240, 240, 240)); // grigio chiaro

		// Titolo
		JLabel titleLabel = new JLabel("La Mia Bacheca", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
		titleLabel.setForeground(new Color(51, 51, 51));
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setOpaque(false);
		titlePanel.add(titleLabel, BorderLayout.CENTER);

		// Componenti della bacheca
		ContentPanel contenutoBacheca = new ContentPanel(model);
		contenutoBacheca.setOpaque(false);

		ControlloBacheca controllo = new ControlloBacheca(contenutoBacheca, model, utente);

		OpsPanel operazioniBacheca = new OpsPanel(controllo);
		operazioniBacheca.setOpaque(false);

		UtentePanel utenteBacheca = new UtentePanel(utente, controllo);
		utenteBacheca.setOpaque(false);

		// Inserisci il pannello dei contenuti in uno JScrollPane
		JScrollPane scrollPane = new JScrollPane(contenutoBacheca);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		// Crea un pannello centrale con layout verticale (BoxLayout)
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setOpaque(false);

		// Aggiungi il pannello delle operazioni e, sotto, lo scrollPane con i contenuti
		centerPanel.add(operazioniBacheca);
		centerPanel.add(Box.createRigidArea(new Dimension(0, 10))); // spazio fra operazioni e contenuto
		centerPanel.add(scrollPane);

		// Disposizione dei pannelli nel layout principale
		add(titlePanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(utenteBacheca, BorderLayout.SOUTH);
	}
}
