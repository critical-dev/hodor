package ca.etsmtl.log735.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ca.etsmtl.log735.client.Client;
import ca.etsmtl.log735.model.Group;

public class GroupPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 1343800150279499040L;
	
	private Group group;
	private Client client;
	private JTextArea inputArea = new JTextArea(5, 40);

	public GroupPanel(Group group, Client client) {
		super(new BorderLayout());
		this.group = group;
		this.client = client;
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(inputArea);
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(this);
		bottomPanel.add(sendButton);
		add(bottomPanel, BorderLayout.SOUTH);
		JList userlist = new JList(group.getUserlist());
		add(new JScrollPane(userlist), BorderLayout.EAST);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		client.sendMessage(group, inputArea.getText());
	}
}
