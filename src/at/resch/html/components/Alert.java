package at.resch.html.components;

import at.resch.html.elements.DIV;


public class Alert extends DIV {

	public Alert(String message) {
		super(message);
		setStyle("alert");
	}
}
