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
Ex�cutable de Application Deux. � l'ex�cution, l'objet
tente de se connecter � [IP]:12045 pour �tablir
un lien avec le bus d'�v�nements.

D�lai de traitement des �v�nements : 2 secondes.
�v�nement synchronis� : affiche le mot "Avez".

Classe modifi�e pour envoyer le ID au serveur.
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
