package ca.etsmtl.cours.log735.lab3.branch;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ca.etsmtl.cours.log735.lab3.bank.Bank;
import ca.etsmtl.cours.log735.message.TotalMoneyRequestMessage;

public class BankTotalAmountFetcherThread extends Thread{
	private Branch branch;
	public BankTotalAmountFetcherThread(Branch branch){
		this.branch = branch;
	};
	@Override
	public void run(){
		try {
			Socket client = new Socket(branch.getBankIp(), Bank.PORT);
			ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
			System.out.println("Sent request to get bank total money amount.");
			oos.writeObject(new TotalMoneyRequestMessage(branch.getMyId()));
			oos.close();
			client.close();
		} catch (IOException e) {
			System.err.println(">> Error occured when requesting total money amount to bank.");
			e.printStackTrace();
		}
	}
}
