package ca.etsmtl.cours.log735.lab3.branch;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import net.miginfocom.swing.MigLayout;

public class BranchGUI extends JFrame {

	private static final long serialVersionUID = -3229493735001667568L;
	
	private Branch branch;
	
	JTextArea captureLog;
	JTextField bankIpField;
	JTextField initialMoneyField;
	
	private class StartCaptureListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			branch.captureState();
		}
	}
	
	private class StartBranchListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ev) {
			try {
				InetAddress bankIp = InetAddress.getByName(bankIpField.getText());
				int initialMoney = Integer.parseInt(initialMoneyField.getText());
				branch = new Branch(initialMoney, bankIp);
			} catch (UnknownHostException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
	}

	public BranchGUI(String appName) {
		super(appName);
		JPanel panel = new JPanel(new BorderLayout());
		JToolBar toolbar = new JToolBar();
		JButton stateCapture = new JButton("Capture d'état");
		stateCapture.addActionListener(new StartCaptureListener());
		toolbar.add(stateCapture);
		panel.add(toolbar, BorderLayout.NORTH);
		captureLog = new JTextArea();
		panel.add(captureLog, BorderLayout.CENTER);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setContentPane(panel);
		this.pack();
		this.setVisible(true);
		init();
	}
	
	private void init() {
		JDialog startDialog = new JDialog(this, "Paramètres de démarrage", true);
		JPanel panel = new JPanel(new MigLayout());
		panel.add(new JLabel("Adresse IP de la banque"));
		bankIpField = new JTextField(30);
		panel.add(bankIpField, "wrap");
		panel.add(new JLabel("Montant initial"));
		initialMoneyField = new JTextField(30);
		panel.add(initialMoneyField, "wrap");
		JButton startButton = new JButton("Démarrer");
		startButton.addActionListener(new StartBranchListener());
		panel.add(startButton);
		startDialog.setContentPane(panel);
		startDialog.pack();
		startDialog.setVisible(true);
	}

	public static void main(String[] args) {
		new BranchGUI("Succursale");
	}
}
