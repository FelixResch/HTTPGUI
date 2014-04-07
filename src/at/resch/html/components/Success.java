package at.resch.html.components;

import at.resch.html.elements.DIV;

public class Success extends DIV {

	public Success(String message) {
		super(message);
		setStyleClass("alert success alert-success");
		setStyle("width: 300px; padding: 10px");
	}
}
