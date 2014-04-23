package at.resch.html.components;

import java.util.Iterator;
import java.util.List;

import at.resch.html.HTMLAttribute;
import at.resch.html.HTMLCompiler;
import at.resch.html.annotations.Action;
import at.resch.html.elements.BR;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.Heading;
import at.resch.html.enums.MessageType;
import at.resch.html.events.Updates;
import at.resch.html.server.Session;

public class ContentManager {

	public static void addLineBreak(HTMLElement html) {
		html.addObject(new BR());
	}

	public static void addHeading(HTMLElement html, int level, String message) {
		html.addObject(new Heading(level, message));
	}

	public static void setTitle(String title) {
		Session.getCurrent().getUpdates().addUpdate("title", title);
	}
	
	public static void setElementContent(HTMLElement html, String ressource) {
		if (html.getId() != null)
			setElementContent(html.getId(), ressource);
	}

	public static void setElementContent(String id, String ressource) {
		Session.getCurrent()
				.getUpdates()
				.addUpdate(
						id,
						HTMLCompiler.compileObjectToPage(
								Session.getPartialInstance(ressource))
								.renderHTML());
	}

	public static void enableEventListening(HTMLElement html, String url) {
		if (html.getId() != null)
			enableEventListening(html.getId(), url);
	}

	public static void enableEventListening(String id, String url) {
		Session.getCurrent()
				.getUpdates()
				.addUpdate("stream",
						"{\"url\":\"" + url + "\",\"display\":\"" + id + "\"}");
	}

	public static void bufferImage(String url) {
		Session.getCurrent().getUpdates().addUpdate("buffer_img", url);
	}

	public static void addMessage(MessageType type, String message) {
		List<HTMLElement> messages = (List<HTMLElement>) Session.getCurrent()
				.get("message.list");
		switch (type) {
		case ERROR:
			messages.add(new Error(message));
			break;
		case INFO:
			messages.add(new Info(message));
			break;
		case ALERT:
			messages.add(new Warning(message));
			break;
		case SUCCESS:
			messages.add(new Success(message));
			break;
		default:
			break;
		}
		String msgs = "";
		for (Iterator<HTMLElement> iterator = messages.iterator(); iterator
				.hasNext();) {
			HTMLElement htmlElement = (HTMLElement) iterator.next();
			msgs += htmlElement.renderHTML() + " ";
		}
		Session.getCurrent().getUpdates().addUpdate("messages", msgs);
		Session.getCurrent().store("message.list", messages);
	}

	public static void setNoScroll() {
		Session.getCurrent().getUpdates().addUpdate("noscroll", "noscroll");
	}
	
	public static HTMLAttribute getActionAttribute(String event, String handler) {
		return new HTMLAttribute(event, "invokeAction('" + handler + "', undefined, event)");
	}

	public static void addEventToElement(HTMLElement html, String event,
			String handler) {
		html.addAttribute(getActionAttribute(event, handler));
	}

	public static HTMLAttribute getActionAttribute(String event,
			String handler, String param) {
		return new HTMLAttribute(event, "invokeAction('" + handler + "', '"
				+ param + "', event)");
	}

	public static void addEventToElement(HTMLElement html, String event,
			String handler, String param) {
		html.addAttribute(getActionAttribute(event, handler, param));
	}

	public static void enableTouch(HTMLElement html, String touch_up,
			String touch_right, String touch_down, String touch_left) {
		if (html.getId() != null)
			enableTouch(html.getId(), touch_up, touch_right, touch_down,
					touch_left);
	}

	public static void requestParams(String action, String[] args) {
		Updates u = Session.getCurrent().getUpdates();
		String updateString = "{\"element\":\"" + action + "\", \"params\":[";
		for (int i = 0; i < args.length; i++) {
			String a = args[i];
			updateString += "\"" + a + "\""
					+ (i == args.length - 1 ? "" : ", ");
		}
		updateString += "]}";
		u.addUpdate("invoke", updateString);
	}

	public static void requestParams(String action, String[] args, String param) {
		Updates u = Session.getCurrent().getUpdates();
		String updateString = "{\"element\":\"" + action + "\", \"params\":[";
		for (int i = 0; i < args.length; i++) {
			String a = args[i];
			updateString += "\"" + a + "\""
					+ (i == args.length - 1 ? "" : ", ");
		}
		updateString += "], \"param\":\"" + param + "\"}";
		u.addUpdate("invoke", updateString);
	}

	public static void enableTouch(String id, String touch_up,
			String touch_right, String touch_down, String touch_left) {
		Session.getCurrent()
				.getUpdates()
				.addUpdate(
						"touch",
						"{\"element\":\"" + id + "\", \"calls\":[\"" + touch_up
								+ "\", \"" + touch_right + "\", \""
								+ touch_down + "\", \"" + touch_left + "\"]}");
	}

	public static void enableTouchSideways(HTMLElement html,
			String touch_right, String touch_left) {
		if (html.getId() != null)
			enableTouchSideways(html.getId(), touch_right, touch_left);
	}

	public static void enableTouchSideways(String id, String touch_right,
			String touch_left) {
		Session.getCurrent()
				.getUpdates()
				.addUpdate(
						"touchsw",
						"{\"element\":\"" + id + "\", \"calls\":[\"" + null
								+ "\", \"" + touch_right + "\", \"" + null
								+ "\", \"" + touch_left + "\"]}");
	}

	public static void disableTouch(HTMLElement html) {
		if (html.getId() != null)
			disableTouch(html.getId());
	}

	public static void disableTouch(String id) {
		Session.getCurrent().getUpdates().addUpdate("untouch", id);
	}

}
