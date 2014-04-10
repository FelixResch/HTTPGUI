package at.resch.html.components;

import java.util.HashMap;
import java.util.List;

import at.resch.html.HTMLAttribute;
import at.resch.html.annotations.Action;
import at.resch.html.components.handlers.HOnClickListener;
import at.resch.html.elements.HTMLElement;
import at.resch.html.events.Updates;
import at.resch.html.server.Session;

public abstract class HComponent<T extends HTMLElement> extends HTMLElement {

	private T parent;
	
	private HOnClickListener on_click_listener;
	
	public HComponent(T parent, String id) {
		super("HComponent");
		this.parent = parent;
		setId(id);
	}
	
	public void enableEvent(String event) {
		addAttribute(ContentManager.getActionAttribute(event, "hcomponent.handler." + event + ".param", this.getId()));
		Session.getCurrent().store("_hcomponent.object." + this.getId(), this);
	}

	@Override
	public String renderHTML() {
		return parent.renderHTML();
	}
	
	
	
	@Override
	public List<Object> getChildren() {
		return parent.getChildren();
	}

	@Override
	public String getTagName() {
		return parent.getTagName();
	}

	@Override
	public void setTagName(String tagName) {
		parent.setTagName(tagName);
	}

	@Override
	public void addAttribute(HTMLAttribute a) {
		parent.addAttribute(a);
	}

	@Override
	public void removeAttribute(String name) {
		parent.removeAttribute(name);
	}

	@Override
	public HTMLAttribute getAttribute(String name) {
		return parent.getAttribute(name);
	}

	@Override
	public void addObject(Object a) {
		parent.addObject(a);
	}

	@Override
	public void set(String name, String value) {
		parent.set(name, value);
	}

	@Override
	public String get(String name) {
		return parent.get(name);
	}

	public HOnClickListener getOn_click_listener() {
		return on_click_listener;
	}

	public void setOn_click_listener(HOnClickListener on_click_listener) {
		this.on_click_listener = on_click_listener;
	}
	
}
