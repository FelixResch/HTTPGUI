package at.resch.html.test;

import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Location;
import at.resch.html.annotations.Page;
import at.resch.html.components.ContentManager;
import at.resch.html.components.HButton;
import at.resch.html.components.HHeroUnit;
import at.resch.html.components.HTextBox;
import at.resch.html.components.handlers.HActionHandler;
import at.resch.html.components.handlers.HKeyEventArgs;
import at.resch.html.components.handlers.HOnClickAction;
import at.resch.html.components.handlers.HOnKeyDownAction;
import at.resch.html.elements.BR;
import at.resch.html.elements.H1;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.LABEL;
import at.resch.html.elements.TABLE;
import at.resch.html.elements.TD;
import at.resch.html.elements.TR;
import at.resch.html.enums.MessageType;
import at.resch.html.enums.Style;
import at.resch.html.events.Updates;

@Page(title="Test HAction Page", style=Style.BOOTSTRAP_DEFAULT)
@Identifier("thap")
@Location(path="/thap")
public class TestHActionPage {

	@Content
	public void addContent(HTMLElement html) {
		html.setStyleClass("container");
		LABEL label = new LABEL();
		label.setId("info");
		HTextBox textin = new HTextBox("something");
		HHeroUnit heroUnit = new HHeroUnit("main_content");
		heroUnit.addObject(new H1("HAction Test"));
		heroUnit.addObject("Simple echo Program for HAction Test");
		heroUnit.addObject(new BR());
		TABLE table = new TABLE(new TR(new TD(textin), new TD(label)));
		heroUnit.addObject(table);
		//heroUnit.addObject(new BR());
		textin.enableEvent("onkeydown");
		textin.setOnKeyDownListener("textin_onkeydown");
		HButton button = new HButton("very-button");
		button.enableEvent("onclick");
		button.addObject("Click");
		button.setOnClickListener("button_onclick");
		button.setButtonType(HButton.BUTTON_SUCCESS);
		button.setButtonSize(HButton.BUTTON_LARGE);
		heroUnit.addObject(button);
		html.addObject(heroUnit);
	}

	@HActionHandler(eventType=HOnClickAction.class, name="button_onclick", args="something")
	public void button_onclick(Updates updates, String[] args, String param) {
		ContentManager.addMessage(MessageType.INFO, args[0]);
	}
	
	@HActionHandler(eventType=HOnKeyDownAction.class, name="textin_onkeydown", args="something")
	public void textin_onkeydown(Updates updates, String[] args, HKeyEventArgs eventArgs) {
		if(args[0].length() < 5 || args[0].length() > 15)
			updates.addUpdate("info", "Text is too short/long");
		else
			updates.addUpdate("info", "This text is okay");
	}

}
