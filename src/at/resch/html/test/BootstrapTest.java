package at.resch.html.test;

import at.resch.html.HTMLAttribute;
import at.resch.html.annotations.Action;
import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Location;
import at.resch.html.annotations.Page;
import at.resch.html.components.ContentManager;
import at.resch.html.components.Warning;
import at.resch.html.elements.A;
import at.resch.html.elements.DIV;
import at.resch.html.elements.H1;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.LI;
import at.resch.html.elements.P;
import at.resch.html.elements.UL;
import at.resch.html.enums.MessageType;
import at.resch.html.enums.Style;
import at.resch.html.events.Updates;

@Page(title="Bootstrap Test", style=Style.BOOTSTRAP_DEFAULT)
@Identifier("bootstrap_test")
@Location(path="/bootstrap")
public class BootstrapTest {

	@Content
	public void test(HTMLElement html) {
		html.setStyleClass("container");
		html.addObject(new H1(new A(new HTMLAttribute("href", "#"), "Bootstrap Site")));
		DIV navbar = new DIV();
		navbar.setStyleClass("navbar");
		DIV navbar_inner = new DIV();
		navbar_inner.setStyleClass("navbar-inner");
		DIV container = new DIV();
		container.setStyleClass("container");
		UL nav = new UL();
		nav.setStyleClass("nav");
		nav.addObject(new LI(new HTMLAttribute("class", "active"), new A(new HTMLAttribute("href", "#"), "Home")));
		nav.addObject(new LI(new A(ContentManager.getActionAttribute("onclick", "nav_action", "Projects"), "Projects")));
		nav.addObject(new LI(new A(ContentManager.getActionAttribute("onclick", "nav_action", "Services"), "Services")));
		nav.addObject(new LI(new A(ContentManager.getActionAttribute("onclick", "nav_action", "Downloads"), "Downloads")));
		nav.addObject(new LI(new A(ContentManager.getActionAttribute("onclick", "nav_action", "About"), "About")));
		nav.addObject(new LI(new A(ContentManager.getActionAttribute("onclick", "nav_action", "Contact"), "Contact")));
		container.addObject(nav);
		navbar_inner.addObject(container);
		navbar.addObject(navbar_inner);
		html.addObject(navbar);
		DIV hero_unit = new DIV();
		hero_unit.setStyleClass("hero-unit");
		hero_unit.addObject(new H1("Marketting Stuff!"));
		hero_unit.addObject(new P("Some text so this section won't look so empty"));
		A btn = new A();
		btn.setHref("#");
		btn.setStyleClass("btn btn-large btn-success");
		btn.addObject("Get Started");
		hero_unit.addObject(btn);
		html.addObject(hero_unit);
	}
	
	@Action(name="nav_action", args={})
	public void navAction(Updates updates, String[] args, String param) {
		ContentManager.addMessage(MessageType.INFO, param);
		ContentManager.addMessage(MessageType.ERROR, "Some error happened here");
	}
	
}