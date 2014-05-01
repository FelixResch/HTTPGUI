package at.resch.html.test;

import at.resch.html.annotations.Action;
import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Location;
import at.resch.html.annotations.Page;
import at.resch.html.annotations.Priority;
import at.resch.html.components.ContentManager;
import at.resch.html.elements.BUTTON;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.INPUT;
import at.resch.html.enums.MessageType;
import at.resch.html.events.Updates;
import at.resch.html.server.Session;

@Page(title="Input Test")
@Identifier("input_test")
@Location(path="/input_test")
@Priority(at.resch.html.enums.Priority.STANDARD)
public class InputTest {

	public InputTest() {
		
	}
	
	@Content
	public void addContent(HTMLElement html) {
		{
			INPUT input = new INPUT();
			input.setId("input");
			input.setValue("test");
			html.addObject(input);
		}
		{
			BUTTON button = new BUTTON("Echo");
			ContentManager.addEventToElement(button, "onclick", "button_onclick");
			html.addObject(button);
		}
	}
	
	@Action(name="button_onclick", args="input")
	public void button_onclick(Updates updates, String[] args) {
		String counter = (String) Session.getCurrent().get("input_test.counter");
		if(counter == null) {
			counter = "0";
		}
		int count = Integer.parseInt(counter);
		count++;
		Session.getCurrent().store("input_test.counter", "" + count);
		ContentManager.addMessage(MessageType.INFO, args[0] + " Clicks: " + count);
	}

}
