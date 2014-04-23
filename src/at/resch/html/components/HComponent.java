package at.resch.html.components;

import java.util.List;

import at.resch.html.HTMLAttribute;
import at.resch.html.annotations.Restoreable;
import at.resch.html.elements.HTMLElement;
import at.resch.html.server.Session;

@Restoreable
public abstract class HComponent<T extends HTMLElement> extends HTMLElement {

	private static final long serialVersionUID = -3567900056678876447L;

	protected T parent;

	private transient String on_click_listener;
	private transient String on_key_down_listener;

	public HComponent(T parent, String id) {
		super("HComponent");
		this.parent = parent;
		setId(id);
		Session.getCurrent().store("_hcomponent.object." + this.getId(), this);
	}

	public void enableEvent(String event) {
		addAttribute(ContentManager.getActionAttribute(event,
				"hcomponent.handler." + event + ".param", this.getId()));
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

	public String getOnClickListener() {
		return on_click_listener;
	}

	public void setOnClickListener(String on_click_listener) {
		this.on_click_listener = on_click_listener;
	}

	public String getOnKeyDownListener() {
		return on_key_down_listener;
	}

	public void setOnKeyDownListener(String on_key_down_listener) {
		this.on_key_down_listener = on_key_down_listener;
	}

}
