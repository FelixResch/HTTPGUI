package at.resch.html.test.auth;

import at.resch.html.HTMLAttribute;
import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Partial;
import at.resch.html.elements.BR;
import at.resch.html.elements.BUTTON;
import at.resch.html.elements.DIV;
import at.resch.html.elements.H1;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.P;
import at.resch.html.server.Session;

@Partial
@Identifier("main_cont")
public class MainPageContent {

	@Content
	public void addMainCont(HTMLElement html) {
		html.setStyle("text-align: center; float:center");
		html.addObject(new H1("User Authentication"));
		html.addObject(new P("This is the User Authentication Service for THCStudios", new BR(), "Please enter a Username to check if the User exists"));
		html.addObject(Session.getCompiledPartial("login"));
		html.addObject(new DIV(new HTMLAttribute("style", "text-align:right"), 
				new BUTTON(new HTMLAttribute("onclick", "invokeAction('nav-reg')"), "Register")));
	}
}
