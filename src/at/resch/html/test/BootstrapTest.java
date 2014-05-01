package at.resch.html.test;

import at.resch.html.HTMLAttribute;
import at.resch.html.annotations.Action;
import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Location;
import at.resch.html.annotations.Page;
import at.resch.html.components.ContentManager;
import at.resch.html.components.HDataTable;
import at.resch.html.components.HSQLDataTable;
import at.resch.html.components.models.ColumnDefinition;
import at.resch.html.components.models.TableModel;
import at.resch.html.elements.A;
import at.resch.html.elements.BR;
import at.resch.html.elements.DIV;
import at.resch.html.elements.H1;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.LI;
import at.resch.html.elements.P;
import at.resch.html.elements.UL;
import at.resch.html.enums.MessageType;
import at.resch.html.enums.Style;
import at.resch.html.events.Updates;
import at.resch.html.server.Session;

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
		TableModel tm = new TableModel(new ColumnDefinition[] {new ColumnDefinition("Name"), new ColumnDefinition("Age")});
		HDataTable table = new HDataTable("table", tm);
		table.getModel().addRow("Felix", "17");
		table.getModel().addRow("Something", "Somewhen");
		table.revalidate();
		ContentManager.connectToJDBCDB("mysqldb", "com.mysql.jdbc.Driver", "localhost", "3306", "root", "toor", "projektorg");
		HSQLDataTable hsql = new HSQLDataTable("hsql_test", "select * from v_project_contribs", "mysqldb");
		hero_unit.addObject(new BR());
		hero_unit.addObject(Session.getCurrent().get("session.token"));
		hero_unit.addObject(new BR());
		hero_unit.addObject(new BR());
		hero_unit.addObject(table);
		hero_unit.addObject(new BR());
		hero_unit.addObject(new BR());
		hero_unit.addObject(hsql);
		html.addObject(hero_unit);
	}
	
	@Action(name="nav_action", args={})
	public void navAction(Updates updates, String param) {
		ContentManager.addMessage(MessageType.INFO, param);
	}
	
}