package ca.etsmtl.cours.log735.lab3.branch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Observable;
import java.util.UUID;

import ca.etsmtl.cours.log735.message.TxnMessage;

public class ChannelWatcherThread extends Observable{
	
	private ObjectInputStream ois;
	private boolean keepWatchingChannel = true;
	private ChannelWatcher internalWatcher;
	
	public ChannelWatcherThread(ObjectInputStream ois){
		this.ois = ois;
		internalWatcher = new ChannelWatcher();
	}
	
	class ChannelWatcher extends Thread{
		public ChannelWatcher(){
			
		}
		@Override
		public void run(){
			while(keepWatchingChannel){
				try {
					Object input = ois.readObject();
					if(input instanceof TxnMessage){
						//we notify that we received something!
						requestNotify((TxnMessage)input);
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void startWatching(){
		if(!internalWatcher.isAlive() && internalWatcher!=null){
			System.out.println("ChannelWatcher started.");
			internalWatcher.start();
		}
	}
	
	public void stopWatching(){
		System.out.println("ChannelWatcher stopping.");
		keepWatchingChannel = false;
	}
	
	//called by the internal watcher only.
	protected void requestNotify(TxnMessage inputTxn){
		setChanged();
		System.out.println("ChannelWatcher : a transaction occured, notifying CapState");
		notifyObservers(inputTxn);
		clearChanged();
	}

	public ObjectInputStream getOIS() {
		return ois;
	}

}
