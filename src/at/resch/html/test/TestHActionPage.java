package at.resch.html.test;

import com.google.common.collect.Sets;

import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Location;
import at.resch.html.annotations.Page;
import at.resch.html.components.ContentManager;
import at.resch.html.components.HButton;
import at.resch.html.components.HHeroUnit;
import at.resch.html.components.handlers.HOnClickListener;
import at.resch.html.elements.BR;
import at.resch.html.elements.H1;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.INPUT;
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
		INPUT textin = new INPUT();
		HHeroUnit heroUnit = new HHeroUnit("main_content");
		textin.setType("text");
		textin.setId("something");
		heroUnit.addObject(new H1("HAction Test"));
		heroUnit.addObject("Simple echo Program for HAction Test");
		heroUnit.addObject(new BR());
		heroUnit.addObject(textin);
		heroUnit.addObject(new BR());
		HButton button = new HButton("very-button");
		button.enableEvent("onclick");
		button.addObject("Click");
		button.setOn_click_listener(new HOnClickListener() {
			
			@Override
			public void invoke(Updates u, String[] args, String param) {
				ContentManager.addMessage(MessageType.ERROR, args[0]);
			}
			
			@Override
			public String[] getArguments() {
				return new String[] {"something"};
			}
		});
		button.setButtonType(HButton.BUTTON_DANGER);
		button.setButtonSize(HButton.BUTTON_LARGE);
		heroUnit.addObject(button);
		html.addObject(heroUnit);
	}


}
