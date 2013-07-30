package ca.etsmtl.log735.gui;

import java.awt.CardLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import ca.etsmtl.log735.client.Client;

public class ClientGUI extends JFrame implements Observer {

	private static final long serialVersionUID = 3971414849894418778L;
	
	private static final String CARD_REGISTER_CONNECT = "CARD_REGISTER_CONNECT";
	private static final String CARD_CONVERSATIONS = "CARD_CONVERSATIONS";

	private Client client = new Client();

	private CardLayout cardLayout = new CardLayout();
	private JPanel cards = new JPanel(cardLayout);
	
	private JTabbedPane conversations = new JTabbedPane();
	
	public ClientGUI() {
		super();
		client.addObserver(this);
		cards.add(new RegisterConnectPanel(client), CARD_REGISTER_CONNECT);
		cards.add(conversations, CARD_CONVERSATIONS);
		getContentPane().add(cards);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	public static void error(String message) {
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public static void main(String[] args) {
		new ClientGUI();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (client.isConnected()) {
			cardLayout.show(cards, CARD_CONVERSATIONS);
			while (client.hasNewConversations()) {
				// TODO
			}
		} else {
			cardLayout.show(cards, CARD_REGISTER_CONNECT);
		}
	}
}
