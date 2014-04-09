package at.resch.html.test;

import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Location;
import at.resch.html.annotations.Page;
import at.resch.html.elements.HTMLElement;

@Page(title="Error Page")
@Identifier("provoke")
@Location(path="/error")
public class ProvokeErrorPage {

	@Content
	public void create(HTMLElement html) throws Exception {
		throw new Exception("Ha ya fools");
	}

}
