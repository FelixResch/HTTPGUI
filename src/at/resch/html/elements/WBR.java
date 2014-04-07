package at.resch.html.elements;

import at.resch.html.annotations.NotSupportedInBrowsers;

@NotSupportedInBrowsers
public class WBR extends HTMLElement {

	public WBR(Object... children) {
		super("wbr", children);
	}

	public WBR() {
		super("wbr");
	}

}
