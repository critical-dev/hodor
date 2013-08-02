package ca.etsmtl.log735.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import net.miginfocom.swing.MigLayout;
import ca.etsmtl.log735.client.Client;
import ca.etsmtl.log735.model.Conversation;
import ca.etsmtl.log735.model.Message;
import ca.etsmtl.log735.server.Server;
/******************************************************
Cours : LOG735
Session : Été 2013
Groupe : 01
Laboratoire : Laboratoire #4
Étudiants : Artom Lifshitz, Chrystophe Chabert
Code(s) perm. : LIFA29108505, CHAC12098902
Date création : 01/07/2013
******************************************************/
public class ConversationPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 1343800150279499040L;
	
	private final String COMMAND_LEAVE = "COMMAND_LEAVE";
	private final String COMMAND_SEND = "COMMAND_SEND";
	private final String COMMAND_CREATE_GROUP = "COMMAND_CREATE_GROUP";
	
	private JTextArea inputArea = new JTextArea();
	private JTextArea conversationArea = new JTextArea();
	
	private Conversation conv;
	private Client client;
	private DefaultListModel userListModel = new DefaultListModel();
	private JList userlist = new JList(userListModel);
	
	private class CreateGroupMenu extends JPopupMenu {
		
		private static final long serialVersionUID = -1519302139441263050L;

		public CreateGroupMenu(ActionListener listener) {
			super();
			JMenuItem createGroupItem = new JMenuItem("Send private message");
			createGroupItem.setActionCommand(COMMAND_CREATE_GROUP);
			createGroupItem.addActionListener(listener);
			add(createGroupItem);
		}
	}

	public ConversationPanel(Conversation conv, Client client) {
		super(new BorderLayout(5, 5));
		this.client = client;
		this.conv = conv;
		inputArea.setRows(5);
		userlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JToolBar toolbar = new JToolBar();
		JButton leaveButton = new JButton("Leave");
		leaveButton.setActionCommand(COMMAND_LEAVE);
		leaveButton.addActionListener(this);
		leaveButton.setEnabled(!conv.getName().equals(Server.DEFAULT_ROOM_NAME));
		toolbar.add(leaveButton);
		toolbar.setFloatable(false);
		add(toolbar, BorderLayout.NORTH);
		JPanel bottomPanel = new JPanel(new MigLayout("wrap 2, fillx"));
		bottomPanel.add(new JScrollPane(inputArea), "growx, pushx");
		JButton sendButton = new JButton("Send");
		sendButton.setActionCommand(COMMAND_SEND);
		sendButton.addActionListener(this);
		bottomPanel.add(sendButton, "shrinkx");
		add(bottomPanel, BorderLayout.SOUTH);
		add(new JScrollPane(userlist), BorderLayout.EAST);
		conversationArea.setEditable(false);
		add(new JScrollPane(conversationArea), BorderLayout.CENTER);
		userlist.setComponentPopupMenu(new CreateGroupMenu(this));
		refreshUserList(conv);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(COMMAND_SEND)) {
			client.sendMessage(inputArea.getText(), conv);
			inputArea.setText("");
		}
		if (e.getActionCommand().equals(COMMAND_LEAVE)) {
			client.leaveConversation(conv);
		}
		if (e.getActionCommand().equals(COMMAND_CREATE_GROUP)) {
			Vector<String> usersInGroup = new Vector<String>();
			Object[] users = userlist.getSelectedValues();
			for (int i = 0; i < users.length; i++) {
				usersInGroup.add((String) users[i]);
			}
			client.sendCreateGroup(usersInGroup);
		}
	}

	public void refreshUserList(Conversation conv) {
		userListModel.clear();
		this.conv = conv;
		for (String user: conv.getUserlist()) {
			userListModel.addElement(user);
		}
	}

	public void newMessage(Message msg) {
		conversationArea.append(msg.getMessage());
	}
}
