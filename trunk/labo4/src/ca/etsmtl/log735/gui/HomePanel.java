package ca.etsmtl.log735.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import ca.etsmtl.log735.model.Client;

public class HomePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -735104005540609924L;
	
	private final String COMMAND_JOIN = "JOIN";
	
	private Client client;
	private JList roomlist;
	
	public HomePanel(Client client) {
		super();
		this.client = client;
		roomlist = new JList(client.getRoomList());
		roomlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.add(roomlist);
		JButton joinButton = new JButton("Join room");
		joinButton.setActionCommand(COMMAND_JOIN);
		joinButton.addActionListener(this);
		leftPanel.add(joinButton);
		JPanel rightPanel = new CreateRoomPanel(client);
		add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals(COMMAND_JOIN)) {
			client.joinRoom((String) roomlist.getSelectedValue());
		}
	}

}
