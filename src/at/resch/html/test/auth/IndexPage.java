package at.resch.html.test.auth;

import at.resch.html.HTMLAttribute;
import at.resch.html.annotations.Action;
import at.resch.html.annotations.CompilerWeight;
import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Location;
import at.resch.html.annotations.Page;
import at.resch.html.annotations.Priority;
import at.resch.html.elements.BR;
import at.resch.html.elements.DIV;
import at.resch.html.elements.H1;
import at.resch.html.elements.BUTTON;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.P;
import at.resch.html.events.Updates;
import at.resch.html.server.Session;

@Page(title="User Authentication")
@Identifier("index")
@Priority
@Location(path="/")
public class IndexPage {
	
	@Content
	@CompilerWeight(1)
	public void createHeader(HTMLElement html) {
		html.setId("page_main");
		html.addObject(Session.getCompiledPartial("main_cont"));
	}
	
	@Action(name="nav-reg", args={})
	public void navigateRegister(Updates updates) {
		updates.addUpdate("page_main", Session.getCompiledPartial("register").renderHTML());
		updates.addUpdate("title", "Register");
	}
}
