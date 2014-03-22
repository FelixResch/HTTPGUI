package at.resch.html.components;

import at.resch.html.HTMLCompiler;
import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Page;
import at.resch.html.elements.BR;
import at.resch.html.elements.H1;
import at.resch.html.elements.HTMLElement;
import at.resch.html.server.Session;

@Page(title="Fehler")
@Identifier("sep")
public class StandardErrorPage {

	private int errNo = 404;
	private String target;
	
	public StandardErrorPage(String target) {
		this.target = target;
	}
	
	public StandardErrorPage(int errNo, String target) {
		this.errNo = errNo;
		this.target = target;
	}
	
	@Content
	public void renderErrorPage(HTMLElement html) {
		html.addObject(new H1("Fehler " + errNo));
		html.addObject("Der Server kann die angegebene Datei nicht finden: " + target);
		html.addObject(Session.getCompiledPartial("info"));
		html.addObject(new BR());
		html.addObject(Session.getCompiledPartial("footer"));
	}

}
