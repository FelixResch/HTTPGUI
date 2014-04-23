package at.resch.html.components;

import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Partial;
import at.resch.html.elements.FOOTER;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.SMALL;

@Partial(parent=DefaultFooter.class)
@Identifier("footer")
public class StandardFooter {

	@Content(parent=SMALL.class)
	public void makeFooter(HTMLElement html) {
		html.setStyle("text-align: left;");
		html.addObject("HTTPGUI Server (c) 2014, Felix Resch");
	}
}
