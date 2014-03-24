package at.resch.html.test;


import at.resch.html.annotations.CompilerWeight;
import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Location;
import at.resch.html.annotations.Page;
import at.resch.html.elements.CODE;
import at.resch.html.elements.H1;
import at.resch.html.elements.HTMLElement;

@Page(title="Hello World")
@Location(path="/test")
@Identifier("helloWorld")
public class TestPage {

	//Main Content
	@Content
	@CompilerWeight(1)
	public void makeMain(HTMLElement html) {
		html.setId("page_main");
		html.addObject(new H1("Hello World"));
		html.addObject("This is a Test for the very simple HTML Compiler!");
	}
	
	@Content(parent=CODE.class)
	@CompilerWeight(2)
	public void someText(HTMLElement html) {
		html.addObject("Here is some other text");
	}
}
