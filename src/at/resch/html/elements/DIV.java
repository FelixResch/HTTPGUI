package at.resch.html.elements;

import at.resch.html.annotations.RenderMode;
import at.resch.html.enums.Mode;

@RenderMode(Mode.FULL)
public class DIV extends HTMLElement {

	public DIV(Object... children) {
		super("div", children);
	}

	public DIV() {
		super("div");
	}

}
