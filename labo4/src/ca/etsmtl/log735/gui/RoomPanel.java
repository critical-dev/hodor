package ca.etsmtl.log735.gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ca.etsmtl.log735.model.Client;
import ca.etsmtl.log735.model.Room;

public class RoomPanel extends GroupPanel {

	private static final long serialVersionUID = -631741915529606778L;

	public RoomPanel(Room room, Client client) {
		super(room, client);
		JPanel topPanel = new JPanel();
		topPanel.add(new JLabel(room.getName()));
		add(topPanel, BorderLayout.NORTH);
	}
}
