package at.resch.html.components;

import at.resch.html.elements.DIV;

public class Info extends DIV {

	public Info(String message) {
		super(message);
		setStyleClass("alert info alert-info");
		setStyle("width: 300px; padding: 10px");
	}
}
