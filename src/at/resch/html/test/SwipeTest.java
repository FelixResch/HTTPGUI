package at.resch.html.test;

import at.resch.html.HTMLAttribute;
import at.resch.html.annotations.Action;
import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Location;
import at.resch.html.annotations.Page;
import at.resch.html.components.Info;
import at.resch.html.elements.DIV;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.BUTTON;
import at.resch.html.enums.Style;
import at.resch.html.events.Updates;
import at.resch.html.server.Session;

@Page(title="Touch Test", style=Style.BOOTSTRAP_DEFAULT)
@Identifier("touch_test")
@Location(path="/touch")
public class SwipeTest {

	@Content
	public void touchContent(HTMLElement html) {
		DIV div = new DIV();
		div.setId("touch_div");
		div.setStyle("width: 400px; height: 400px; background: #eeee00");
		html.addObject(div);
		html.addObject(new BUTTON("Start Touch", new HTMLAttribute("id", "touch_enable_btn"), new HTMLAttribute("onclick", "invokeAction('enable_touch')")));
		Session.getCurrent().store("touch.state", "off");
	}

	@Action(name="enable_touch", args={})
	public void enableTouch(Updates updates) {
		if(Session.getCurrent().get("touch.state").equals("off")) {
			updates.addUpdate("touch", "{\"element\":\"touch_div\", \"calls\":[\"touch_up\", \"touch_right\", \"touch_down\", \"touch_left\"]}");
			updates.addUpdate("messages", new Info("Touch enabled").renderHTML());
			updates.addUpdate("touch_enable_btn", "Stop Touch");
			Session.getCurrent().store("touch.state", "on");
		} else {
			updates.addUpdate("untouch", "touch_div");
			updates.addUpdate("messages", new Info("Touch disabled").renderHTML());
			updates.addUpdate("touch_enable_btn", "Start Touch");
			Session.getCurrent().store("touch.state", "off");
		}
	}
	
	@Action(name="touch_up", args={})
	public void touchUp(Updates updates) {
		updates.addUpdate("messages", new Info("UP").renderHTML());
	}
	@Action(name="touch_right", args={})
	public void touchRight(Updates updates) {
		updates.addUpdate("messages", new Info("RIGHT").renderHTML());
	}
	@Action(name="touch_down", args={})
	public void touchDown(Updates updates) {
		updates.addUpdate("messages", new Info("DOWN").renderHTML());
	}
	@Action(name="touch_left", args={})
	public void touchLeft(Updates updates) {
		updates.addUpdate("messages", new Info("LEFT").renderHTML());
	}
}
