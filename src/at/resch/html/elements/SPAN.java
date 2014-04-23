package at.resch.html.elements;

import at.resch.html.annotations.RenderMode;
import at.resch.html.enums.Mode;


@RenderMode(Mode.FULL)
public class SPAN extends HTMLElement {

	public SPAN(Object... children) {
		super("span", children);
	}

	public SPAN() {
		super("span");
	}

}
