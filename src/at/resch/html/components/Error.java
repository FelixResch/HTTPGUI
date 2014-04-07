package at.resch.html.components;

import at.resch.html.elements.DIV;

public class Error extends DIV {

	public Error(String message) {
		super(message);
		setStyleClass("alert error alert-error");
		setStyle("width: 300px; padding: 10p");
	}
}
