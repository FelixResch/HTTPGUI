package at.resch.html.test;

import at.resch.html.annotations.Action;
import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Location;
import at.resch.html.annotations.Page;
import at.resch.html.components.ContentManager;
import at.resch.html.elements.HTMLElement;
import at.resch.html.enums.Style;
import at.resch.html.events.Updates;

@Page(title="Event Stream Test", onload="sse.onload", style=Style.BOOTSTRAP_DEFAULT)
@Identifier("event_stream.test")
@Location(path="/sse")
public class EventStreamTest {

	@Content
	public void addContent(HTMLElement html) {
		html.setId("display");
	}
	
	@Action(name="sse.onload", args={})
	public void onLoad(Updates u) {
		ContentManager.enableEventListening("display", "/cus/jobs/all");
	}

}
