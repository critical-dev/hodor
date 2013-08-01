package ca.etsmtl.log735.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import net.miginfocom.swing.MigLayout;
import ca.etsmtl.log735.client.Client;
/******************************************************
Cours : LOG735
Session : Été 2013
Groupe : 01
Laboratoire : Laboratoire #4
Étudiants : Artom Lifshitz, Chrystophe Chabert
Code(s) perm. : LIFA29108505, CHAC12098902
Date création : 01/07/2013
******************************************************/
public class ServerPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -735104005540609924L;
	
	private class RoomListPanel extends JPanel {
		
		private static final long serialVersionUID = -114536750269650523L;
		
		public JList roomList;
		
		public RoomListPanel(Vector<String> roomListModel, ActionListener listener) {
			super();
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			roomList = new JList(roomListModel);
			roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			add(roomList);
			JButton joinButton = new JButton("Join room");
			joinButton.addActionListener(listener);
			add(joinButton);
		}
	}
	
	private class CreateRoomPanel extends JPanel {

		private static final long serialVersionUID = -8843520780953018031L;
		
		public JTextField roomNameField = new JTextField();
		public JPasswordField roomPasswordField = new JPasswordField();
		
		public CreateRoomPanel(ActionListener listener) {
			super(new MigLayout("wrap 2"));
			add(new JLabel("Create room"), "span 2");
			add(new JLabel("Room name"));
			add(roomNameField);
			add(new JLabel("Room password (optional)"));
			add(roomPasswordField);
			JButton createButton = new JButton("Create room");
			createButton.addActionListener(listener);
			add(createButton);
		}
	}
	
	private Client client;
	
	public ServerPanel(Client client) {
		super();
		this.client = client;
		RoomListPanel roomListPanel = new RoomListPanel(client.getRoomList(), this);
		CreateRoomPanel createRoomPanel = new CreateRoomPanel(this);
		add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, roomListPanel, createRoomPanel));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO
	}
}
