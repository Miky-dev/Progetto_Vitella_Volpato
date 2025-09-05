package interfaccia.grafica.vista;

import java.awt.*;
import javax.swing.*;
import modello.Utente;
import interfaccia.grafica.controllo.ControlloBacheca;

@SuppressWarnings("serial")
public class UtentePanel extends JPanel {

	private Utente utente;

	public UtentePanel(Utente utente, ControlloBacheca controllo) {
		this.utente = utente;
		initialize();
	}

	private void initialize() {
		// Imposta uno sfondo più scuro per il footer
		setBackground(new Color(210, 210, 210));
		setOpaque(true);
		setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

		// Carica l'icona desiderata per il footer (modifica il percorso se necessario)
		ImageIcon userIcon = null;
		try {
			userIcon = new ImageIcon(getClass().getResource("/icon/user_footer.png"));
		} catch (Exception ex) {
			// Se non viene trovata l'icona, usa una di default
			userIcon = (ImageIcon) UIManager.getIcon("OptionPane.informationIcon");
		}
		if (userIcon != null && userIcon.getIconWidth() > 32) {
			Image img = userIcon.getImage();
			Image newImg = img.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
			userIcon = new ImageIcon(newImg);
		}
		JLabel iconLabel = new JLabel(userIcon);

		// Crea il pannello di testo che mostrerà nome e email
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		textPanel.setOpaque(false);

		JLabel nameLabel = new JLabel(utente.getNome());
		nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		nameLabel.setForeground(new Color(51, 51, 51));

		JLabel emailLabel = new JLabel(utente.getEmail());
		emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		emailLabel.setForeground(new Color(89, 89, 89));

		textPanel.add(nameLabel);
		textPanel.add(emailLabel);

		// Aggiunge l'icona e il pannello testuale
		add(iconLabel);
		add(textPanel);
	}
}