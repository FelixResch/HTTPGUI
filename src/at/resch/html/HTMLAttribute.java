package at.resch.html;

import java.io.Serializable;

public class HTMLAttribute implements Serializable {

	private String name;
	private String value;
	
	public HTMLAttribute(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String render() {
		return name + "=\"" + value + "\"";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
