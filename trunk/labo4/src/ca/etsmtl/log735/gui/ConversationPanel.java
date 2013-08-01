package ca.etsmtl.log735.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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
public class ConversationPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 1343800150279499040L;
	
	private JTextArea inputArea = new JTextArea(5, 40);
	private JTextArea conversationArea = new JTextArea();
	
	private Conversation conv;
	private Client client;
	private DefaultListModel userListModel = new DefaultListModel();
	private JList userlist = new JList(userListModel);

	public ConversationPanel(Conversation conv, Client client) {
		super(new BorderLayout());
		this.client = client;
		this.conv = conv;
		JPanel topPanel = new JPanel();
		topPanel.add(new JLabel(conv.getName()));
		add(topPanel, BorderLayout.NORTH);
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(inputArea);
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(this);
		bottomPanel.add(sendButton);
		add(bottomPanel, BorderLayout.SOUTH);
		add(new JScrollPane(userlist), BorderLayout.EAST);
		conversationArea.setEditable(false);
		add(conversationArea, BorderLayout.CENTER);
		refreshUserList(conv);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		client.sendMessage(inputArea.getText(), conv);
	}

	public void refreshUserList(Conversation conv) {
		userListModel.clear();
		for (String user: conv.getUserlist()) {
			userListModel.addElement(user);
		}
	}
}
