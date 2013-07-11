package ca.etsmtl.log735.gui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
		super(owner, "Configuration", Dialog.ModalityType.APPLICATION_MODAL);
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
			if (!usernameField.getText().isEmpty()) {
				client.setUsername(usernameField.getText());
			} else {
				JOptionPane.showMessageDialog(this, "Username cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String password = new String(passwordField.getPassword());
			if (!password.isEmpty()) {
				client.setPassword(password);
			} else {
				JOptionPane.showMessageDialog(this, "Password cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			client.setServerIp(InetAddress.getByName(serverField.getText()));
			client.setServerPort(Integer.parseInt(portField.getText()));
			client.start();
			setVisible(false);
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
