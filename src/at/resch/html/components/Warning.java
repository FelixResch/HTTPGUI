package at.resch.html.components;

import at.resch.html.elements.DIV;


public class Warning extends DIV {

	public Warning(String message) {
		super(message);
		setStyleClass("alert");
		setStyle("width: 300px; padding: 10px");
	}
}
