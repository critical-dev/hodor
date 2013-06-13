/******************************************************
	Cours :           LOG730
	Session :         Été 2010
	Groupe :          01
	Projet :          Laboratoire #2
	Étudiants : Artom Lifshitz 
				Chrystophe Chabert
	Code(s) perm. : LIFA29108505
					CHAC12098902
	Date dernière modif: 2013-06-11
	Date création :   2010-05-21
******************************************************
Exécutable de Application Deux. À l'exécution, l'objet
tente de se connecter à [IP]:12045 pour établir
un lien avec le bus d'événements.

Délai de traitement des événements : 2 secondes.
Événement synchronisé : affiche le mot "Avez".

Classe modifiée pour envoyer le ID au serveur.
******************************************************/ 
package application;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import events.IEventSynchronized;
import events.IPartTwoEvent;

public class MainPartTwo {

	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		String ip = JOptionPane.showInputDialog("Entrez le IP du EventBus", "127.0.0.1");

		List<Class> listenedEvents = new ArrayList<Class>();
		listenedEvents.add(IPartTwoEvent.class);
		listenedEvents.add(IEventSynchronized.class);
		//Envoi du ID au serveur.
		EventBusConnector bus = new EventBusConnector(1, listenedEvents, ip, 12045);
		UIMainWindow window = new UIMainWindow(bus, "App Deux", "Avez", 2);
		bus.addObserver(window);
		
		bus.start();
		window.setVisible(true);
	}

}
