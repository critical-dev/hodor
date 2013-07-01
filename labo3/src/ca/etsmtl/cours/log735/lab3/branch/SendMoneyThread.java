package ca.etsmtl.cours.log735.lab3.branch;

public class SendMoneyThread extends Thread {
	
	private final static int DELAY_MIN = 5;
	private final static int DELAY_MAX = 10;
	
	private Branch branch;
	
	public SendMoneyThread(Branch branch) {
		this.branch = branch;
	}

	@Override
	public void run() {
		while (true) {
			int delay = (int) (DELAY_MIN + (DELAY_MAX - DELAY_MIN) * Math.random());
			try {
				sleep(delay * 1000);
				branch.sendMoney();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
