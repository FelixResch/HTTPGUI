package at.resch.html.test;

import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Location;
import at.resch.html.annotations.Page;
import at.resch.html.annotations.Priority;
import at.resch.html.elements.A;
import at.resch.html.elements.DIALOG;
import at.resch.html.elements.H1;
import at.resch.html.elements.HTMLElement;
import at.resch.html.server.Session;


@Page(title="Welcome")
@Identifier("welcome")
@Priority(at.resch.html.enums.Priority.VENDOR)
@Location(path="/")
public class WelcomePage {

	@Content
	public void createWelcomePage(HTMLElement html) {
		html.setStyle("text-align: center");
		html.addObject(new H1("Welcome on this Server"));
		html.addObject(Session.getCompiledPartial("info"));
		html.addObject(new DIALOG("Hello World"));
		A link = new A("Bitte hier klicken");
		link.setHref("/test");
		html.addObject(link);
	}
	
	@Content
	public void addFooter(HTMLElement html) {
		html.addObject(Session.getCompiledPartial("footer"));
	}

}
