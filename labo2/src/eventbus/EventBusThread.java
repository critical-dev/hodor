/******************************************************
	Cours :           LOG730
	Session :         �t� 2010
	Groupe :          01
	Projet :          Laboratoire #2
	�tudiants : Artom Lifshitz 
				Chrystophe Chabert
	Code(s) perm. : LIFA29108505
					CHAC12098902
	Date derni�re modif: 2013-06-11
	Date cr�ation :   2010-05-21
******************************************************
Thread qui achemine les �v�nements contenus sur le bus
d'�v�nements aux Communicators qui sont enregistr�s.
******************************************************/ 
package eventbus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import events.IEvent;
import events.IEventSynchronized;

public class EventBusThread extends Thread implements IEventBusThread {
	private List<IEventBusCommunicator> lstComm = new ArrayList<IEventBusCommunicator>();
	private EventBusServerThread server;
	private List<IEvent> eventsToSend = new ArrayList<IEvent>();
	
	public EventBusThread(int port) {
		server = new EventBusServerThread(port, this);
		server.start();
	}
	
	public void run() {
		while(true) {
			try {
				Thread.sleep(1000);
				synchronized(eventsToSend) {
					if (eventsToSend.size() > 0) {
						System.out.println("Envoie de l'�v�nement " + eventsToSend.get(0).toString());
						// Sort the communicators by their client ID to make sure we're sending in the right order
						// on classe les instances de IEventBusCommunicator par ordre de numero de client
						Collections.sort(lstComm);
						for(IEventBusCommunicator ievc : lstComm) {
							IEvent event = eventsToSend.get(0);
								ievc.sendToListener(event);
							System.out.println("Send to communicator for client " + ievc.getClientId());
						}
						eventsToSend.remove(0);
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addEvent(IEvent ie) {
		eventsToSend.add(ie);
	}

	public void attachCommunicator(IEventBusCommunicator iebc) {
		lstComm.add(iebc);
	}
	
}
