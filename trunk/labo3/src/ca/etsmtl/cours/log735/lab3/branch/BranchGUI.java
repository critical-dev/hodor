package ca.etsmtl.cours.log735.lab3.branch;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import net.miginfocom.swing.MigLayout;

public class BranchGUI extends JFrame implements Observer, ActionListener {

	private static final long serialVersionUID = -3229493735001667568L;
	
	private static final String COMMAND_START = "START";
	private static final String COMMAND_CAPTURE = "CAPTURE";
	
	private Branch branch;
	
	private JTextArea operationsLog;
	JTextField bankIpField;
	JTextField initialMoneyField;
	JDialog startDialog;

	public BranchGUI(String appName) {
		super(appName);
		JPanel panel = new JPanel(new BorderLayout());
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		JButton stateCapture = new JButton("Capture d'état");
		stateCapture.setActionCommand(COMMAND_CAPTURE);
		stateCapture.addActionListener(this);
		toolbar.add(stateCapture);
		panel.add(toolbar, BorderLayout.NORTH);
		operationsLog = new JTextArea();
		panel.add(operationsLog, BorderLayout.CENTER);
		this.setSize(500, 500);
		this.setTitle(appName);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setContentPane(panel);
		//this.pack();
		this.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 4, Toolkit.getDefaultToolkit().getScreenSize().height / 3);
		this.setVisible(true);
		init();
	}
	
	private void init() {
		startDialog = new JDialog(this, "Paramètres de démarrage", true);
		JPanel panel = new JPanel(new MigLayout());
		panel.add(new JLabel("Adresse IP de la banque"));
		bankIpField = new JTextField(30);
		panel.add(bankIpField, "wrap");
		panel.add(new JLabel("Montant initial"));
		initialMoneyField = new JTextField(30);
		panel.add(initialMoneyField, "wrap");
		JButton startButton = new JButton("Démarrer");
		startButton.setActionCommand(COMMAND_START);
		startButton.addActionListener(this);
		panel.add(startButton);
		startDialog.setContentPane(panel);
		startDialog.pack();
		startDialog.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 4, Toolkit.getDefaultToolkit().getScreenSize().height / 3);
		startDialog.setVisible(true);
	}

	public static void main(String[] args) {
		new BranchGUI("Succursale");
	}

	@Override
	public void update(Observable o, Object text) {
		operationsLog.append((String) text);
	}

	@Override
	public void actionPerformed(ActionEvent action) {
		String command = action.getActionCommand();
		if (command.equals(COMMAND_CAPTURE)) {
			branch.captureState();
		}
		if (command.equals(COMMAND_START)) {
			try {
				InetAddress bankIp = InetAddress.getByName(bankIpField.getText());
				int initialMoney = Integer.parseInt(initialMoneyField.getText());
				branch = new Branch(initialMoney, bankIp);
				branch.addObserver(this);
				startDialog.setVisible(false);
			} catch (UnknownHostException ex) {
				JOptionPane.showMessageDialog(this, "Adresse IP incorrecte", "Erreur", JOptionPane.ERROR_MESSAGE);
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Montant initial incorrect", "Erreur", JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Impossible de se connecter à la banque", "Erreur", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public JTextArea getOperationsLog() {
		return operationsLog;
	}

	public void setOperationsLog(JTextArea operationsLog) {
		this.operationsLog = operationsLog;
	}
}
