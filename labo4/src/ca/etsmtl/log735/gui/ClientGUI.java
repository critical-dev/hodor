package ca.etsmtl.log735.gui;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import ca.etsmtl.log735.client.Client;

public class ClientGUI extends JFrame implements Observer {

	private static final long serialVersionUID = 3971414849894418778L;

	private Client client;
	
	JTabbedPane tabbedPane;
	
	public ClientGUI() {
		super();
		client = new Client();
		client.addObserver(this);
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Home", new HomePanel(client));
		setContentPane(tabbedPane);
		ConfigDialog configDialog = new ConfigDialog(this, client);
		configDialog.setVisible(true);
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new ClientGUI();
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
}
