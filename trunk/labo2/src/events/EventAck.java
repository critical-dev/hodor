package events;
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
	Date cr�ation :   2013-06-07
******************************************************
Nouvel �v�nement envoy� par le client au serveur.
******************************************************/ 
public class EventAck extends EventBase implements IEventAck {

	private static final long serialVersionUID = -1156382431931313979L;

	public EventAck(String m) {
		super(m);
	}

}
