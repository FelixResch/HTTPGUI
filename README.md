HTTPGUI
Folgende URLs können momentan aufgerufen werden:
	*/bootstrap (Test mit Bootstrap)
	*/touch (Test für Touch, nur auf Touch basierten Geräten)
	*/ (Einfacher Dienst zum Registrieren und Einloggen, DB benötigt)
	*/tsp (Einfache Test Server Page

Error Page:
	*404

Neue Seiten müssen Folgendes Enthalten:
	*@Page(title=”Title”)
	*@Identifier(“id”)
	*@Location(path=”/url/zu/seite”)

Um Content zu Definieren muss folgende Methode definiert werden:

	@Content
	public void randomMethodName(HTMLElement html) {
		//HTMLElemente oder Strings zu html hinzufügen
	}
