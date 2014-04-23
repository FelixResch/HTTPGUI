package at.resch.html.server;

import at.resch.html.HTMLAttribute;
import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Page;
import at.resch.html.components.HButton;
import at.resch.html.components.HHeroUnit;
import at.resch.html.elements.BR;
import at.resch.html.elements.H1;
import at.resch.html.elements.HTMLElement;
import at.resch.html.enums.Style;

@Page(title="Please reload!", style=Style.BOOTSTRAP_DEFAULT)
@Identifier("relpls")
public class ReloadPage {

	@Content
	public void addContent(HTMLElement html) {
		html.setStyleClass("container");
		HHeroUnit heroUnit = new HHeroUnit("main_content");
		heroUnit.addObject(new H1("Please reload"));
		heroUnit.addObject("Your session expired. To enable your Session again please click on the button below or reload");
		HButton button = new HButton("very-button");
		button.addAttribute(new HTMLAttribute("href", "javascript:location.reload(true)"));
		button.setButtonType(HButton.BUTTON_DANGER);
		button.setButtonSize(HButton.BUTTON_LARGE);
		button.addObject("Click");
		heroUnit.addObject(new BR());
		heroUnit.addObject(button);
		html.addObject(heroUnit);
	}


}
