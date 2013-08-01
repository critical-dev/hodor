package ca.etsmtl.log735.gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ca.etsmtl.log735.client.Client;
import ca.etsmtl.log735.model.Group;
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
public class RoomPanel extends GroupPanel {

	private static final long serialVersionUID = -631741915529606778L;

	public RoomPanel(Room room, Client client) {
		super(new Group(room.getName()+"-defaultGroup",room.getUserlist()), client);
		JPanel topPanel = new JPanel();
		topPanel.add(new JLabel(room.getName()));
		add(topPanel, BorderLayout.NORTH);
	}
}
