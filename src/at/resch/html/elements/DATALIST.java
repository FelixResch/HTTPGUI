package at.resch.html.elements;

import at.resch.html.annotations.NotSupportedInBrowsers;
import at.resch.html.enums.Browsers;

@NotSupportedInBrowsers(browsers={Browsers.SAFARI})
public class DATALIST extends HTMLElement {

	public DATALIST(Object... children) {
		super("datalist", children);
	}

	public DATALIST() {
		super("datalist");
	}

}
