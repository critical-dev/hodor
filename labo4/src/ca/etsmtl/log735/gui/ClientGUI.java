package ca.etsmtl.log735.gui;

import java.awt.CardLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import ca.etsmtl.log735.client.Client;
import ca.etsmtl.log735.model.Conversation;
/******************************************************
Cours : LOG735
Session : Été 2013
Groupe : 01
Laboratoire : Laboratoire #4
Étudiants : Artom Lifshitz, Chrystophe Chabert
Code(s) perm. : LIFA29108505, CHAC12098902
Date création : 01/07/2013
******************************************************/
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
		conversations.add(new ServerPanel(client));
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
			for (Conversation conv = client.nextConversation(); conv != null; conv = client.nextConversation()) {
				conversations.addTab(conv.toString(), new ConversationPanel(conv, client));
			}
		} else {
			cardLayout.show(cards, CARD_REGISTER_CONNECT);
		}
	}
}
