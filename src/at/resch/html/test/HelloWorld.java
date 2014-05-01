package at.resch.html.test;

import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Location;
import at.resch.html.annotations.Page;
import at.resch.html.annotations.Priority;
import at.resch.html.elements.HTMLElement;

@Page(title="Hello World")
@Location(path="/hello_world")
@Identifier("hello_world")
public class HelloWorld {

	public HelloWorld() {
		
	}
	
	@Content
	public void addContent(HTMLElement html) {
		html.addObject("Hello World");
	}

}
