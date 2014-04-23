package at.resch.html.components;

import at.resch.html.elements.DIV;

public class HContainer extends HComponent<DIV>{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8627244397819577870L;

	public HContainer(String id) {
		super(new DIV(), id);
		setStyleClass("container");
	}
}
