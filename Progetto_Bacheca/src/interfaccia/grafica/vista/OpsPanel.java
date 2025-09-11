package interfaccia.grafica.vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import interfaccia.grafica.controllo.ControlloBacheca;

@SuppressWarnings("serial")
public class OpsPanel extends JPanel {

	/**
	 * Inizializza il pannello operazioniBacheca inserendo i vari bottoni e li
	 * aggiunge al listener.
	 * 
	 * @param controllo il controller delle operazioni
	 */
	public OpsPanel(ControlloBacheca controllo) {
		setLayout(new FlowLayout());

		JButton addAnnuncio = createStyledButton("Aggiungi Annuncio");
		add(addAnnuncio);
		addAnnuncio.addActionListener(controllo);

		JButton removeAnnuncio = createStyledButton("Rimuovi Annuncio");
		add(removeAnnuncio);
		removeAnnuncio.addActionListener(controllo);

		JButton cercaAnnunci = createStyledButton("Cerca Annunci");
		add(cercaAnnunci);
		cercaAnnunci.addActionListener(controllo);

		JButton pulisciBacheca = createStyledButton("Pulisci Bacheca");
		add(pulisciBacheca);
		pulisciBacheca.addActionListener(controllo);

		JButton addParolaChiave = createStyledButton("Aggiungi Parola Chiave ad Annuncio");
		add(addParolaChiave);
		addParolaChiave.addActionListener(controllo);
	}

	private JButton createStyledButton(String text) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				// Disegna uno sfondo stondato con il colore corrente del button
				g2.setColor(getBackground());
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
				g2.dispose();
				super.paintComponent(g);
			}

			@Override
			public void setContentAreaFilled(boolean b) {
				// Non usare il contenuto pieno di default
			}
		};

		// Imposta le proprietà del bottone
		button.setFont(new Font("Roboto", Font.BOLD, 16));
		button.setForeground(Color.WHITE);
		button.setBackground(new Color(70, 130, 180)); // colore di base (steel blue)
		button.setFocusPainted(false);
		// Aggiunge padding interno per rendere il bottone più grande
		button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		// Rende il button non opaco in modo da gestire il disegno custom
		button.setOpaque(false);

		// Aggiunge il MouseListener per il cambio di colore durante le interazioni
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(new Color(255, 165, 0)); // arancione al passaggio del mouse
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(new Color(70, 130, 180)); // torna al colore di base
			}

			@Override
			public void mousePressed(MouseEvent e) {
				button.setBackground(new Color(255, 140, 0)); // arancione più scuro se premuto
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (button.getBounds().contains(e.getPoint())) {
					button.setBackground(new Color(255, 165, 0));
				} else {
					button.setBackground(new Color(70, 130, 180));
				}
			}
		});
		return button;
	}
}
