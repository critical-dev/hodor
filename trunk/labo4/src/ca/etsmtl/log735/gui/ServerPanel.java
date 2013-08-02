package ca.etsmtl.log735.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import net.miginfocom.swing.MigLayout;
import ca.etsmtl.log735.client.Client;
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
public class ServerPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -735104005540609924L;
	
	private final String COMMAND_CREATE = "COMMAND_CREATE";
	private final String COMMAND_JOIN = "COMMAND_JOIN";
	
	private DefaultListModel roomListModel = new DefaultListModel();
	private JList roomList = new JList(roomListModel);
	private CreateRoomPanel createRoomPanel; 
	
	private class RoomListPanel extends JPanel {
		
		private static final long serialVersionUID = -114536750269650523L;
		
		public RoomListPanel(ActionListener listener) {
			super();
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			roomList = new JList(roomListModel);
			roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			add(new JScrollPane(roomList));
			JButton joinButton = new JButton("Join room");
			joinButton.addActionListener(listener);
			joinButton.setActionCommand(COMMAND_JOIN);
			add(joinButton);
		}
	}
	
	private class CreateRoomPanel extends JPanel {

		private static final long serialVersionUID = -8843520780953018031L;
		
		public JTextField roomNameField = new JTextField();
		public JPasswordField roomPasswordField = new JPasswordField();
		
		public CreateRoomPanel(ActionListener listener) {
			super(new MigLayout("fillx, wrap 2"));
			add(new JLabel("Create room"), "growx, span 2");
			add(new JLabel("Room name"), "shrink");
			add(roomNameField, "pushx, growx");
			add(new JLabel("Room password (optional)"), "shrink");
			add(roomPasswordField, "pushx, growx");
			JButton createButton = new JButton("Create room");
			createButton.addActionListener(listener);
			createButton.setActionCommand(COMMAND_CREATE);
			add(createButton);
		}
	}
	
	private Client client;
	
	public ServerPanel(Client client) {
		super(new BorderLayout());
		this.client = client;
		RoomListPanel roomListPanel = new RoomListPanel(this);
		createRoomPanel = new CreateRoomPanel(this);
		add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, roomListPanel, createRoomPanel), BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(COMMAND_JOIN)) {
			if (roomList.getSelectedValue() != null) {
				String input = JOptionPane.showInputDialog((Component) e.getSource(), "Password :", "");
				if(!input.isEmpty()){
					client.sendJoinRoom(new Room((String) roomList.getSelectedValue()), input);
				}
				else
					client.sendJoinRoom(new Room((String) roomList.getSelectedValue()));
			}
		}
		if (e.getActionCommand().equals(COMMAND_CREATE)) {
			String roomName = createRoomPanel.roomNameField.getText();
			String roomPassword = null;
			if(createRoomPanel.roomPasswordField.getPassword().length > 0){
				roomPassword = "";
				for(int i = 0; i < createRoomPanel.roomPasswordField.getPassword().length; i++)
				roomPassword += createRoomPanel.roomPasswordField.getPassword()[i];
			}
			
			if (!roomName.isEmpty()) {
				if(roomPassword != null && !roomPassword.isEmpty()){
					System.out.println("Client sending request to create room with password " + roomPassword);
					client.sendCreateRoom(createRoomPanel.roomNameField.getText(), roomPassword);
				}
				else
					client.sendCreateRoom(createRoomPanel.roomNameField.getText());
			}
		}
	}

	public void setRooms(List<Room> serverRooms) {
		roomListModel.clear();
		for (Room room: serverRooms) {
			roomListModel.addElement(room.getName());
		}
	}
}
