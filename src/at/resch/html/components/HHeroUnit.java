package at.resch.html.components;

import at.resch.html.elements.DIV;

public class HHeroUnit extends HComponent<DIV> {

	public HHeroUnit(String id) {
		super(new DIV(), id);
		setStyleClass("hero-unit");
	}

}
