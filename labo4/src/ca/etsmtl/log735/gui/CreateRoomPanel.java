package ca.etsmtl.log735.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ca.etsmtl.log735.client.Client;

public class CreateRoomPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -8843520780953018031L;
	
	private Client client;
	private JTextField roomNameField;
	private JPasswordField roomPasswordField;
	
	public CreateRoomPanel(Client client) {
		super(new MigLayout());
		add(new JLabel("Create room"), "wrap");
		add(new JLabel("Room name"));
		roomNameField = new JTextField(20);
		add(roomNameField, "wrap");
		add(new JLabel("Room password (optional)"));
		roomPasswordField = new JPasswordField(20);
		add(roomPasswordField, "wrap");
		JButton createButton = new JButton("Create room");
		createButton.addActionListener(this);
		add(createButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		client.createRoom(roomNameField.getText(), new String(roomPasswordField.getPassword()));
	}
}
