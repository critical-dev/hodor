package ca.etsmtl.cours.log735.lab3.bank;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 		BankGUI
 * 
 * Classe en charge de montrer visuellement les succursales présentement
 * connectées et connues de la banque. Lorsque le bouton Démarrer est cliqué,
 * la banque écoute sur le port <code> Bank.PORT </code> pour des connexions.
 * Cette classe affiche aussi, de façon asynchrone, le montant total d'argent connu
 * dans le système.
 * 
 * */
public class BankGUI implements Observer, MouseListener{
	
	private JFrame bankFrame;
	private JPanel bankPanel;
	private LayoutManager mgr;
	private String bankFrameTitle;
	private Color bankBGPreferredcolor;
	private JButton bankListenerBtn;
	private JTextArea bankBranchesListArea;
	private String bankBranchesList;
	private static final String DEFAULT_HEADER = "Succursales connues :\n";
	private JScrollPane bankListScrollPane;
	private JLabel totalMoneyLabel;
	private JTextField totalMoneyTextField;
	
	private Bank bank;
	
	public static final int PREF_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width / 2,
							PREF_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height / 3;
	public static final int PREF_X = Toolkit.getDefaultToolkit().getScreenSize().width / 4,
							PREF_Y = Toolkit.getDefaultToolkit().getScreenSize().height / 3;
	
	public BankGUI(String bankFrameTitle){
		this.bankFrameTitle = bankFrameTitle;
		mgr = new BorderLayout();
		bankBGPreferredcolor = Color.black;
		bankBranchesList = DEFAULT_HEADER;
		init();
	}
	
	private void init(){
		bankFrame = new JFrame(bankFrameTitle);
		bankPanel = new JPanel();
		
		totalMoneyLabel = new JLabel("Argent total dans le système :");
		totalMoneyTextField = new JTextField();
		totalMoneyTextField.setEditable(false);
		
		bankListenerBtn = new JButton("Demarrer");
		bankListenerBtn.addMouseListener(this);
		
		bankBranchesListArea = new JTextArea(bankBranchesList);
		bankBranchesListArea.setEditable(false);
		
		bankListScrollPane = new JScrollPane(bankBranchesListArea);
		
		bankPanel.setBackground(bankBGPreferredcolor);
		bankPanel.setLayout(mgr);
		bankPanel.add(bankListenerBtn,BorderLayout.WEST);
		bankPanel.add(bankListScrollPane,BorderLayout.CENTER);
		bankPanel.add(totalMoneyLabel, BorderLayout.SOUTH);
		
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new GridLayout(0, 1));
		southPanel.add(totalMoneyLabel);
		southPanel.add(totalMoneyTextField);
		bankPanel.add(southPanel, BorderLayout.SOUTH);
		
		bankFrame.getContentPane().add(bankPanel);
		bankFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		bankFrame.setSize(PREF_WIDTH, PREF_HEIGHT);
		bankFrame.setLocation(PREF_X, PREF_Y);
		bankFrame.setVisible(true);
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0 instanceof Bank){
			System.out.println("Updating frame branches list..");
			//small delay introduced to let the listener thread work..
			try {
				Thread.sleep(5000);//to let updater thread set the new money amount
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			HashMap<UUID, InetAddress> branches = ((Bank) arg0).getCurrentBranches();
			int i = 0;
			bankBranchesList = DEFAULT_HEADER;
			for(UUID id : branches.keySet()){
				i++;
				bankBranchesList += "Succursale ["+i+"]: "+id+"@"+branches.get(id)+"\n";
			}
			bankBranchesListArea.setText(bankBranchesList);
			
			//Update total known amount of money in the system
			totalMoneyTextField.setText(Bank.BANK_TOTAL_MONEY_IN_THE_SYSTEM + "");
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if(arg0.getSource() == bankListenerBtn){
			if(bankListenerBtn.isEnabled()){
				bankListenerBtn.setEnabled(false);
				bank.start();
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	//start everything.
	public static void main(String[] args) {
		Bank bank = new Bank();
		BankGUI bankGui = new BankGUI("Banque 2000 !");
		bankGui.setBank(bank);
		bank.addObserver(bankGui);
	}

}
