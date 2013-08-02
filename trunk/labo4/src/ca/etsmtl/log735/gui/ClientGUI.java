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
import ca.etsmtl.log735.model.Room;
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
	public static final Integer UPDATE_PANEL_CONVERSATION_ACTION = 0;

	private Client client = new Client();

	private CardLayout cardLayout = new CardLayout();
	private JPanel cards = new JPanel(cardLayout);
	private JTabbedPane conversations = new JTabbedPane();
	private ServerPanel serverPanel;
	
	public ClientGUI() {
		super();
		client.addObserver(this);
		cards.add(new RegisterConnectPanel(client), CARD_REGISTER_CONNECT);
		cards.add(conversations, CARD_CONVERSATIONS);
		serverPanel = new ServerPanel(client);
		conversations.add("Server Home", serverPanel);
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
			if(arg != null && arg instanceof Conversation){
				if(arg instanceof Room){
					//update the msg for that room
					Room roomToUpdate = ((Room) arg);
					System.out.println("GUI attempting to find matching panel room");
					System.out.println("Found match !");
					int i = conversations.indexOfTab(roomToUpdate.getName());
					if(i != -1){
						ConversationPanel panel = (ConversationPanel) conversations.getComponentAt(i);
						String panelText = panel.getConversationArea().getText();
						panel.getConversationArea().setText(panelText + "\n" + client.getLastConversationMessageToUpdate());
						panel.updateUI();
					}
					else System.out.println("Client GUI was unable to find tab for room");
				}
			}
			else{
				cardLayout.show(cards, CARD_CONVERSATIONS);
				for (Conversation conv = client.nextJoinedConv(); conv != null; conv = client.nextJoinedConv()) {
					conversations.addTab(conv.toString(), new ConversationPanel(conv, client));
				}
				for (Room room = client.nextServerRoom(); room != null; room = client.nextServerRoom()) {
					serverPanel.serverRoomAdd(room.getName());
				}
				for (Room room = client.nextRoomWithNewUsers(); room != null; room = client.nextRoomWithNewUsers()) {
					int i = conversations.indexOfTab(room.getName());
					ConversationPanel panel = (ConversationPanel) conversations.getComponentAt(i);
					System.out.println("refreshing room : " + room.getName());
					if (panel != null) {
						panel.refreshUserList(room);
					} else {
						System.err.println("Asked to refresh a userlist of a conversation that we don't have - something went horribly wrong.");
					}
				}
			}
		} else {
			conversations.removeAll();
			serverPanel = new ServerPanel(client);
			conversations.add("Server Home", serverPanel);
			cardLayout.show(cards, CARD_REGISTER_CONNECT);
		}
	}
}
