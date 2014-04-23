package at.resch.html.components;

import at.resch.html.HTMLCompiler;
import at.resch.html.elements.UL;
import at.resch.html.server.Session;

public class HBreadCrumb extends HComponent<UL> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7006267625455633176L;
	
	private String session_ressource;

	public HBreadCrumb(String id, String session_ressource) {
		super(new UL(), id);
		setStyleClass("breadcrumb");
		this.session_ressource = session_ressource;
		revalidate();
	}
	
	public void revalidate() {
		parent.clearChildren();
		parent.addObject(HTMLCompiler.compileObjectToPage(Session.getPartialInstance(session_ressource)));
	}

}
