package at.resch.html.test;

import at.resch.html.annotations.Action;
import at.resch.html.annotations.CompilerWeight;
import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Inject;
import at.resch.html.annotations.Location;
import at.resch.html.annotations.Page;
import at.resch.html.annotations.Priority;
import at.resch.html.elements.A;
import at.resch.html.elements.BR;
import at.resch.html.elements.BUTTON;
import at.resch.html.elements.DIALOG;
import at.resch.html.elements.H1;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.INPUT;
import at.resch.html.elements.P;
import at.resch.html.events.Updates;
import at.resch.html.server.Session;


@Page(title="Welcome")
@Identifier("welcome")
@Priority(at.resch.html.enums.Priority.VENDOR)
@Location(path="/welcome")
public class WelcomePage {
	
	private int counter = 0;
	
	@Inject("client.address")
	public String client_address;

	@Content
	@CompilerWeight(1)
	public void createWelcomePage(HTMLElement html) {
		html.setStyle("text-align: center");
		html.addObject(new H1("Welcome on this Server"));
		html.addObject(Session.getCompiledPartial("info"));
		A link = new A("Bitte hier klicken");
		link.setOnClickHandler("invokeAction('incCounter')");
		html.addObject(link);
		html.addObject(new BR());
		P p = new P("Here will the counter appear!");
		html.addObject(p);
		html.addObject(new BR());
		p.setId("counter");
		html.addObject(client_address);
		Session.getCurrent().store("welcome.string", "You have been welcomed");
	}
	
	@Content
	@CompilerWeight(2)
	public void echoContent(HTMLElement html) {
		INPUT input = new INPUT();
		input.setType("text");
		input.setId("strval");
		input.setPlaceholder("Some Text");
		html.addObject(input);
		BUTTON button = new BUTTON("Click");
		button.setOnClickHandler("invokeAction('echo')");
		html.addObject(button);
	}
	
	@Content
	@CompilerWeight(3)
	public void addFooter(HTMLElement html) {
		html.addObject(Session.getCompiledPartial("footer"));
	}

	@Action(name="incCounter", args={})
	public void incCounter(Updates updates) {
		counter++;
		updates.addUpdate("counter", "" + counter);
	}
	
	@Action(name="echo", args={"strval"})
	public void echo(Updates updates, String[] values) {
		updates.addUpdate("counter", values[0]);
	}
}
