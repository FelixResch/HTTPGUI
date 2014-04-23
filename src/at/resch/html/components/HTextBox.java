package at.resch.html.components;

import at.resch.html.elements.INPUT;

public class HTextBox extends HComponent<INPUT> {

	public HTextBox(String id) {
		super(new INPUT(), id);
		parent.setType("text");
	}

}
