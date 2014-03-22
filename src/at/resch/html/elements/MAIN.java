package at.resch.html.elements;

import at.resch.html.annotations.NotSupportedInBrowsers;

@NotSupportedInBrowsers
public class MAIN extends HTMLElement {

	public MAIN(Object... children) {
		super("main", children);
	}

	public MAIN(String tagName) {
		super("main");
	}

}
