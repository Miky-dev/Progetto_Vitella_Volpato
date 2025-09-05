package interfaccia.grafica.vista;

import java.awt.*;
import javax.swing.*;
import modello.Annuncio;
import modello.Bacheca;

public class ContentPanel extends JPanel {

	private Bacheca model;

	public ContentPanel(Bacheca model) {
		this.model = model;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(new Color(255, 255, 255)); // sfondo bianco
		updateDisplay();

		// Timer che chiama updateDisplay() ogni 2 secondi per aggiornare la UI
		Timer timer = new Timer(2000, e -> updateDisplay());
		timer.start();
	}

	public void updateDisplay() {
		removeAll();
		// Per ogni annuncio, crea un pannello personalizzato con angoli stondati e
		// ombra
		for (Annuncio a : model) {
			JPanel annuncioPanel = new JPanel(new BorderLayout()) {
				@Override
				protected void paintComponent(Graphics g) {
					Graphics2D g2 = (Graphics2D) g.create();
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					// Colore di sfondo
					g2.setColor(getBackground());
					// Disegna un rettangolo stondato
					g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
					// Disegna l'ombra in basso a destra
					g2.setColor(new Color(0, 0, 0, 50));
					g2.fillRoundRect(0, getHeight() - 5, getWidth(), 5, 30, 30);
					g2.dispose();
					// Non chiama super.paintComponent() per evitare il riempimento standard
				}
			};
			annuncioPanel.setBackground(new Color(250, 250, 250));
			// Imposta un bordo interno (padding)
			annuncioPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
			// Imposta la larghezza massima per rendere il pannello più stretto
			annuncioPanel.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));

			// Parte alta: Visualizza l'articolo in grassetto e colorato
			String articleHTML = "<html>"
					+ "<div style='font-family:Segoe UI; font-size:18px; text-align:center;'>"
					+ "<b style='color:#D32F2F;'>" + a.getArticolo() + "</b>"
					+ "</div></html>";
			JLabel articleLabel = new JLabel(articleHTML, SwingConstants.CENTER);
			articleLabel.setOpaque(false);

			// Parte centrale: Visualizza dettagli (prezzo, tipologia, parole chiave,
			// scadenza)
			String detailsHTML = "<html>"
					+ "<div style='font-family:Segoe UI; font-size:14px; color:#212121; text-align:center;'>"
					+ "Prezzo: €" + a.getPrezzo() + "<br/>"
					+ "Tipologia: " + a.getTipologia() + "<br/>"
					+ "Parole chiave: " + a.getParoleChiave();
			if (a.getDataScadenza() != null) {
				detailsHTML += "<br/>Scadenza: " + a.getDataScadenza().toString();
			}
			detailsHTML += "</div></html>";
			JLabel detailsLabel = new JLabel(detailsHTML, SwingConstants.CENTER);
			detailsLabel.setOpaque(false);

			// Raggruppa article e dettagli
			JPanel infoPanel = new JPanel(new BorderLayout());
			infoPanel.setOpaque(false);
			infoPanel.add(articleLabel, BorderLayout.NORTH);
			infoPanel.add(detailsLabel, BorderLayout.CENTER);

			// Parte inferiore: pannello autore con icona e ID affianco al nome
			JPanel authorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
			authorPanel.setOpaque(false);
			ImageIcon authorIcon = null;
			try {
				authorIcon = new ImageIcon(getClass().getResource("/icon/user.png"));
			} catch (Exception ex) {
				authorIcon = (ImageIcon) UIManager.getIcon("OptionPane.informationIcon");
			}
			if (authorIcon != null && authorIcon.getIconWidth() > 32) {
				Image img = authorIcon.getImage();
				Image newImg = img.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
				authorIcon = new ImageIcon(newImg);
			}
			String authorText = a.getAutore().getNome() + " - ID: " + a.getId();
			JLabel authorLabel = new JLabel(authorText, authorIcon, SwingConstants.LEFT);
			authorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
			authorLabel.setForeground(new Color(34, 34, 34));
			authorPanel.add(authorLabel);

			annuncioPanel.add(infoPanel, BorderLayout.CENTER);
			annuncioPanel.add(authorPanel, BorderLayout.SOUTH);
			annuncioPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

			add(annuncioPanel);
			add(Box.createRigidArea(new Dimension(0, 8))); // spaziatura tra annunci
		}
		revalidate();
		repaint();
	}
}