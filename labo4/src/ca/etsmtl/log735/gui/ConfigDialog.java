package ca.etsmtl.log735.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ca.etsmtl.log735.client.Client;

public class ConfigDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 7254370786985318480L;
	
	private final static String DEFAULT_SERVER = "localhost";
	private final static int DEFAULT_PORT = 6668;
	
	private Client client;
	
	private JTextField usernameField = new JTextField(20); 
	private JPasswordField passwordField = new JPasswordField(20);
	private JTextField serverField = new JTextField(DEFAULT_SERVER, 20);
	private JTextField portField = new JTextField(String.valueOf(DEFAULT_PORT), 20);
	
	public ConfigDialog(Frame owner, Client client) {
		super(owner, true);
		this.client = client;
		JPanel content = new JPanel(new MigLayout());
		content.add(new JLabel("Username"));
		content.add(usernameField, "wrap");
		content.add(new JLabel("Password"));
		content.add(passwordField, "wrap");
		content.add(new JLabel("Server IP"));
		content.add(serverField, "wrap");
		content.add(new JLabel("Server port"));
		content.add(portField, "wrap");
		JButton okButton = new JButton("OK");
		okButton.addActionListener(this);
		content.add(okButton);
		setContentPane(content);
		this.pack();
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		try {
			client.setUsername(usernameField.getText());
			client.setPassword(new String(passwordField.getPassword()));
			client.setServerIp(InetAddress.getByName(serverField.getText()));
			client.setServerPort(Integer.parseInt(portField.getText()));
			client.start();
			setVisible(false);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
