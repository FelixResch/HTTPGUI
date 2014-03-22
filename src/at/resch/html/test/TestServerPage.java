package at.resch.html.test;

import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Location;
import at.resch.html.annotations.Page;
import at.resch.html.elements.BR;
import at.resch.html.elements.COMMAND;
import at.resch.html.elements.H1;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.METER;
import at.resch.html.enums.Browsers;

@Page(title="Standard Response", delimiter=BR.class)
@Identifier("tsp")
@Location(path="/tsp")
public class TestServerPage {

	private String target;
	private Browsers userAgent;
	
	public TestServerPage() {
		this.target = "/tsp";
		this.userAgent = Browsers.FIREFOX;
	}
	
	public TestServerPage(String target, Browsers userAgent) {
		this.target = target;
		this.userAgent = userAgent;
	}
	
	@Content
	public void showPage(HTMLElement html) {
		html.setStyle("text-align: center; background: #eeeeee;");
		html.addObject(new H1("Hi, This Is Server!"));
		html.addObject("This Server is currently being built and there is no page on it thou may access!");
		html.addObject(new BR());
		html.addObject("The URL you requested is not valid: " + target);
		html.addObject(new BR());
		html.addObject("Your Browser seems to be: " + userAgent);
	}
	
	@Content
	public void showProgress(HTMLElement html) {
		html.setStyle("text-align: center; background: #eeeeee;");
		html.addObject("Construction Progress: ");
		METER meter = new METER();
		meter.setValue("0.1");
		html.addObject(meter);
		html.addObject(new BR());
		html.addObject(new COMMAND());
	}

}
