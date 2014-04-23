package at.resch.html.components;

import java.io.Serializable;

import at.resch.html.HTMLCompiler;
import at.resch.html.annotations.Action;
import at.resch.html.elements.A;
import at.resch.html.elements.DIV;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.LI;
import at.resch.html.elements.UL;
import at.resch.html.events.Updates;
import at.resch.html.server.Session;

public class HTabView extends HComponent<DIV> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7913041555516597698L;

	private TabEntry[] entries;
	private HTMLElement container;
	
	public HTabView(HTMLElement container, String id, TabEntry ... entries) {
		super(new DIV(), id);
		if(container.getId() == null)
			throw new RuntimeException("Can't put tabview into container without id");
		parent.setStyleClass("container");
		this.entries = entries;
		Session.getCurrent().store(id + ".current", entries[0].display);
		display();
		this.container = container;
	}
	
	public void display() {
		String entry = Session.getCurrent().get(parent.getId() + ".current").toString();
		String selected = "";
		parent.clearChildren();
		UL navbar = new UL();
		for (TabEntry e : entries) {
			if(e.display.equals(entry)) {
				A info = new A(e.display);
				LI li = new LI(info);
				li.setStyleClass("active");
				navbar.addObject(li);
				selected = e.ressource;
			} else {
				LI li = new LI();
				A link = new A(e.display);
				ContentManager.addEventToElement(link, "onclick", "htabbar.change", parent.getId() + "." + e.display);
				li.addObject(link);
				navbar.addObject(li);
			}
		}
		navbar.setStyleClass("nav nav-tabs");
		parent.addObject(navbar);
		DIV content = new DIV(HTMLCompiler.compileObjectToPage(Session.getPartialInstance(selected)));
		parent.addObject(content);
	}

	public HTMLElement getContainer() {
		return container;
	}

	public static class TabEntry implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -969388882622564224L;
		private String display;
		private String ressource;

		public TabEntry(String display, String ressource) {
			super();
			this.display = display;
			this.ressource = ressource;
		}

		public String getDisplay() {
			return display;
		}

		public void setDisplay(String display) {
			this.display = display;
		}

		public String getRessource() {
			return ressource;
		}

		public void setRessource(String ressource) {
			this.ressource = ressource;
		}
	}
}
