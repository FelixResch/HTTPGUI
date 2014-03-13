package at.resch.html.elements;

public class DOCTYPE extends HTMLElement {

	public DOCTYPE(Object... children) {
		super("!DOCTYPE", children);
	}

	public DOCTYPE() {
		super("!DOCTYPE");
	}

	@Override
	public String renderHTML() {
		return "<!DOCTYPE html>";
	}

}
