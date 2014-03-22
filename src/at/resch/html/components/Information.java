package at.resch.html.components;

import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Partial;
import at.resch.html.elements.BR;
import at.resch.html.elements.H2;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.METER;

@Partial
@Identifier("info")
public class Information {

	@Content
	public void makeInformation(HTMLElement html) {
		html.addObject(new H2("Informationen"));
		html.addObject("Dieser Server wird momentan aufgesetzt oder gewartet bitte kontaktieren Sie den Server Administrator sollte diese Nachricht unerwartet kommen.");
		html.addObject(new BR());
		html.addObject("Fortschritt: ");
		METER meter = new METER("20%");
		meter.setValue("0.2");
		html.addObject(meter);
	}
	
}
