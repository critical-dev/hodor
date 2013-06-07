/******************************************************
	Cours :           LOG730
	Session :         �t� 2010
	Groupe :          01
	Projet :          Laboratoire #2
	Date cr�ation :   2010-05-21
******************************************************
Classe qui g�re la transmission et la r�ception
d'�v�nements du c�t� du bus d'�v�nements.

La classe est en constante attente de nouveaux �v�nements
� l'aide d'un Thread. Lorsque le bus d'�v�nements associ�
au Communicator lui envoie un �v�nement, le Communicator
envoie l'�v�nement aux Applications � l'aide d'un second
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
import events.IEventAck;
import events.IEventSynchronized;

public class EventBusCommunicator extends Thread implements IEventBusCommunicator {
	
	private int clientId;
	private volatile boolean ackPending;
	
	//Tampon d'�v�nements � envoyer.
	private List<IEvent> lstEventsToSend = new ArrayList<IEvent>();
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private ReadEventFromStream readStream;
	
	//Thread qui �coute les �v�nements provenant des applications.
	//Le Communicator achemine les �v�nements sur le bus d'�v�nements.
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
					System.out.println("Nouvelle �v�nement dans le bus: " + event.toString());
					if (event instanceof IEventAck) {
						ackPending = false;
						System.out.println(clientId + " Set ackPending to false");
					} else 
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
		// Cr�ation du thread de lecture des �v�nements dans le Bus.
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

	//Thread qui envoie au bus d'�v�nements les �v�nements g�n�r�s par
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
		// Must wait for ACK if event is synchro
		if (ie instanceof IEventSynchronized)
			ackPending = true;
		lstEventsToSend.add(ie);
		// Busy-wait until ACK arrives
		if (ie instanceof IEventSynchronized)
			while (ackPending);
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
}