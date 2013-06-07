/******************************************************
	Cours :           LOG730
	Session :         Été 2010
	Groupe :          01
	Projet :          Laboratoire #2
	Date création :   2010-05-21
******************************************************
Classe qui gère la transmission et la réception
d'événements du côté du bus d'événements.

La classe est en constante attente de nouveaux événements
à l'aide d'un Thread. Lorsque le bus d'événements associé
au Communicator lui envoie un événement, le Communicator
envoie l'événement aux Applications à l'aide d'un second
Thread.
******************************************************/ 
package eventbus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import events.IEvent;

public class EventBusCommunicator extends Thread implements IEventBusCommunicator {
	
	private int clientId;
	
	//Tampon d'événements à envoyer.
	private List<IEvent> lstEventsToSend = new ArrayList<IEvent>();
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private ReadEventFromStream readStream;
	
	//Thread qui écoute les événements provenant des applications.
	//Le Communicator achemine les événements sur le bus d'événements.
	class ReadEventFromStream extends Thread {
		private ObjectInputStream ois;
		private IEventBusThread eventBus;
		public ReadEventFromStream(ObjectInputStream ois, IEventBusThread eventBus) {
			this.ois = ois;
			this.eventBus = eventBus;
		}
		
		public void run() {
			while(true) {
				try {
					IEvent event = (IEvent)ois.readObject();
					System.out.println("Nouvelle événement dans le bus: " + event.toString());
					eventBus.addEvent(event);	
				}
				catch(Exception e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}

	
	public EventBusCommunicator(Socket s, IEventBusThread iebt)
	{
		// Création du thread de lecture des évènements dans le Bus.
		try {
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			// Read the client's ID
			clientId = (Integer) ois.readObject();
			readStream = new ReadEventFromStream(ois, iebt);
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void start() {
		super.start();
		readStream.start();
	}

	//Thread qui envoie au bus d'événements les événements générés par
	//son application.
	public void run() {
		while(true) {
			try {
				//Offrir une pause au thread
				Thread.sleep(1000);

				synchronized(lstEventsToSend){
					for(IEvent e : lstEventsToSend)
						oos.writeObject(e);
					lstEventsToSend.clear();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendToListener(IEvent ie) {
		lstEventsToSend.add(ie);
	}
	
	public int getClientId() {
		return clientId;
	}

	@Override
	public int compareTo(IEventBusCommunicator ievc) {
		if (clientId < ievc.getClientId())
			return -1;
		else if (clientId > ievc.getClientId())
			return 1;
		else
			return 0;
	}

	@Override
	public void sendSyncro(IEvent event) {
		try {
			oos.writeObject(event);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}