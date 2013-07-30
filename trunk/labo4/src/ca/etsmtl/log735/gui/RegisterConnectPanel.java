package ca.etsmtl.log735.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ca.etsmtl.log735.client.Client;
import ca.etsmtl.log735.server.Server;

public class RegisterConnectPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 7150608199212705016L;
	
	private static final String COMMAND_CONNECT = "COMMAND_CONNECT";
	private static final String COMMAND_REGISTER = "COMMAND_REGISTER";
	
	private static final String DEFAULT_SERVER = "localhost";
	private static final String DEFAULT_PORT = String.valueOf(Server.SERVER_CLIENT_LISTEN_PORT);
	private static final String DEFAULT_USERNAME = "artom";
	private static final String DEFAULT_PASSWORD = "password";
	
	private class ServerInfoPanel extends JPanel {

		private static final long serialVersionUID = -1239621763719460091L;
		
		public JTextField serverField = new JTextField(DEFAULT_SERVER);
		public JTextField portField = new JTextField(DEFAULT_PORT);

		public ServerInfoPanel() {
			super(new MigLayout("fillx, wrap 2"));
			JLabel title = new JLabel("Server information");
			title.setFont(title.getFont().deriveFont(title.getFont().getSize() * 1.2F));
			add(title, "span 2, align center");
			add(new JLabel("Server IP"), "shrink");
			add(serverField, "growx, pushx");
			add(new JLabel("Server port"), "shrink");
			add(portField, "growx, pushx");
			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
		}
	}
	
	private class RegisterPanel extends JPanel {
		
		private static final long serialVersionUID = -945485481320805046L;
		
		public JTextField usernameField = new JTextField(DEFAULT_USERNAME);
		public JPasswordField passwordField = new JPasswordField(DEFAULT_PASSWORD);
		public JPasswordField passwordConfField = new JPasswordField(DEFAULT_PASSWORD);
		
		public RegisterPanel(ActionListener listener) {
			super(new MigLayout("fillx, wrap 2"));
			JLabel title = new JLabel("Register");
			title.setFont(title.getFont().deriveFont(title.getFont().getSize() * 1.2F));
			add(title, "span 2, align center");
			add(new JLabel("Username"), "shrink");
			add(usernameField, "growx, pushx");
			add(new JLabel("Password"), "shrink");
			add(passwordField, "growx, pushx");
			add(new JLabel("Password confirmation"), "shrink");
			add(passwordConfField, "growx, pushx");
			JButton registerButton = new JButton("Register");
			registerButton.addActionListener(listener);
			registerButton.setActionCommand(COMMAND_REGISTER);
			add(registerButton, "span 2, align center");
			setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
		}		
	}
	
	private class ConnectPanel extends JPanel {

		private static final long serialVersionUID = -8462494730524498683L;
		
		public JTextField usernameField = new JTextField(DEFAULT_USERNAME);
		public JPasswordField passwordField = new JPasswordField(DEFAULT_PASSWORD);
		
		public ConnectPanel(ActionListener listener) {
			super(new MigLayout("fillx, wrap 2"));
			JLabel title = new JLabel("Connect");
			title.setFont(title.getFont().deriveFont(title.getFont().getSize() * 1.2F));
			add(title, "span 2, align center");
			add(new JLabel("Username"), "shrink");
			add(usernameField, "growx, pushx");
			add(new JLabel("Password"), "shrink");
			add(passwordField, "growx, pushx");
			JButton connectButton = new JButton("Connect");
			connectButton.addActionListener(listener);
			connectButton.setActionCommand(COMMAND_CONNECT);
			add(connectButton, "span 2, align center");
		}		
	}
	
	private Client client;
	private ServerInfoPanel serverInfoPanel;
	private RegisterPanel registerPanel;
	private ConnectPanel connectPanel;
	
	public RegisterConnectPanel(Client client) {
		super(new MigLayout("fillx, wrap 2"));
		this.client = client;
		serverInfoPanel = new ServerInfoPanel();
		registerPanel = new RegisterPanel(this);
		connectPanel = new ConnectPanel(this);
		add(serverInfoPanel, "growx,span 2");
		add(registerPanel, "growx, aligny top");
		add(connectPanel, "growx, aligny top");
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getActionCommand().equals(COMMAND_CONNECT)) {
			try {
				client.login(
						InetAddress.getByName(serverInfoPanel.serverField.getText()),
						Integer.parseInt(serverInfoPanel.portField.getText()),
						connectPanel.usernameField.getText(),
						new String(connectPanel.passwordField.getPassword())
						);
			} catch (NumberFormatException e) {
				ClientGUI.error("Invalid IP address");
			} catch (UnknownHostException e) {
				ClientGUI.error("Unknow host");
			} catch (IOException e) {
				ClientGUI.error("Unable to connect");
			}
		}
		if (evt.getActionCommand().equals(COMMAND_REGISTER)) {
			try {
				client.register(
						InetAddress.getByName(serverInfoPanel.serverField.getText()),
						Integer.parseInt(serverInfoPanel.portField.getText()),
						registerPanel.usernameField.getText(),
						new String(registerPanel.passwordField.getPassword()),
						new String(registerPanel.passwordConfField.getPassword())
						);
			} catch (NumberFormatException e) {
				ClientGUI.error("Invalid IP address");
			} catch (UnknownHostException e) {
				ClientGUI.error("Unknown host");
			} catch (IOException e) {
				ClientGUI.error("Unable to connect");
			}
		}
	}
}
