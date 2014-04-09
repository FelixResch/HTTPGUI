package at.resch.html;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import at.resch.html.elements.BODY;
import at.resch.html.elements.DOCTYPE;
import at.resch.html.elements.H1;
import at.resch.html.elements.HEAD;
import at.resch.html.elements.HTML;
import at.resch.html.elements.HTMLDocument;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.STYLE;
import at.resch.html.elements.TITLE;
import at.resch.html.enums.Mode;
import at.resch.html.annotations.RenderMode;

public class HTMLObject {

	private String tagName;

	public HTMLObject(String tagName) {
		this.tagName = tagName;
	}

	public HTMLObject(String tagName, Object... children) {
		this(tagName);
		for (Object object : children) {
			if (object instanceof HTMLAttribute)
				this.attributes.add((HTMLAttribute) object);
			else
				this.children.add(object);
		}
	}

	public List<Object> getChildren() {
		return children;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	private List<Object> children = new ArrayList<>();

	private List<HTMLAttribute> attributes = new ArrayList<>();

	public void addAttribute(HTMLAttribute a) {
		Iterator<HTMLAttribute> iterator = attributes.iterator();
		while (iterator.hasNext()) {
			HTMLAttribute next = iterator.next();
			if (next.getName().equals(a.getName())) {
				next.setValue(a.getValue());
				return;
			}
		}
		attributes.add(a);
	}

	public void removeAttribute(String name) {
		Iterator<HTMLAttribute> iterator = attributes.iterator();
		while (iterator.hasNext()) {
			HTMLAttribute next = iterator.next();
			if (next.getName().equals(name))
				iterator.remove();
		}
	}

	public HTMLAttribute getAttribute(String name) {
		Iterator<HTMLAttribute> iterator = attributes.iterator();
		while (iterator.hasNext()) {
			HTMLAttribute next = iterator.next();
			if (next.getName().equals(name))
				return next;
		}
		return null;
	}
	
	public void addObject(Object a) {
		children.add(a);
	}

	public String renderHTML() {
		String ret = "";
		Mode mode = Mode.NONE;
		if(getClass().isAnnotationPresent(RenderMode.class))
			mode = getClass().getAnnotation(RenderMode.class).value();
		if (tagName != null) {
			ret += "<" + tagName;
			if (!attributes.isEmpty()) {
				ret += " ";
				for (HTMLAttribute html : attributes) {
					ret += html.render() + " ";
				}
			}
		}
		if (children.isEmpty() && tagName != null) {
			switch(mode) {
			case FULL:
				ret += "></" + tagName + ">";
				break;
			case SMOOTH:
				ret += ">";
				break;
			default:
			case HALF:
				ret += "/>";
				break;
			}
			
		} else if (children.size() == 1 && tagName != null) {
			ret += ">";
			for (Object html : children) {
				if (html instanceof HTMLAttribute)
					continue;
				if (html instanceof HTMLObject) {
					ret += ((HTMLObject) html).renderHTML();
				} else {
					ret += html.toString();
				}
			}
			ret += "</" + tagName + ">";
		} else {
			if(tagName != null)
				ret += ">\n";
			for (Object html : children) {
				if (html instanceof HTMLAttribute)
					continue;
				if (html instanceof HTMLObject) {
					ret += ((HTMLObject) html).renderHTML() + "\n";
				} else {
					ret += html.toString() + "\n";
				}
			}
			if(tagName != null)
				ret += "</" + tagName + ">";
		}
		return ret;
	}

	public static void main(String[] args) {
		HTMLElement ele = new HTMLDocument(new DOCTYPE(), new HTML(new HEAD(new TITLE("THIS IS API"),
				new STYLE("h1 {color: #ff0000;}")), new BODY(new H1(
				"This is Heading"), "And some Text")));
		System.out.println(ele.renderHTML());
	}
}
