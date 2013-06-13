package events;
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
	Date création :   2013-06-07
******************************************************
Nouvel évènement envoyé par le client au serveur.
******************************************************/ 
public class EventAck extends EventBase implements IEventAck {

	private static final long serialVersionUID = -1156382431931313979L;

	public EventAck(String m) {
		super(m);
	}

}
