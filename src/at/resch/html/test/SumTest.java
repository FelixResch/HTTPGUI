package at.resch.html.test;

import at.resch.html.annotations.Content;
import at.resch.html.annotations.Identifier;
import at.resch.html.annotations.Location;
import at.resch.html.annotations.Page;
import at.resch.html.components.ContentManager;
import at.resch.html.components.HButton;
import at.resch.html.components.HContainer;
import at.resch.html.components.HHeroUnit;
import at.resch.html.components.HTextBox;
import at.resch.html.components.handlers.HActionHandler;
import at.resch.html.components.handlers.HOnClickAction;
import at.resch.html.elements.HTMLElement;
import at.resch.html.elements.SPAN;
import at.resch.html.enums.Style;
import at.resch.html.events.Updates;

@Page(style = Style.BOOTSTRAP_DEFAULT, title = "Sum Test")
@Identifier("sum.test")
@Location(path = "/sum_test")
public class SumTest {

	@Content
	public void addContent(HTMLElement html) {
		HContainer container = new HContainer("container");
		HHeroUnit hero = new HHeroUnit("view");
		container.addObject(hero);
		ContentManager.addHeading(hero, 1, "Simple Sum Calculator");
		ContentManager.addLineBreak(hero);
		html.addObject(container);
		{
			HTextBox no1 = new HTextBox("no1");
			no1.set("value", "5");
			hero.addObject(no1);
		}
		hero.addObject(" + ");
		{
			HTextBox no2 = new HTextBox("no2");
			no2.set("value", "5");
			hero.addObject(no2);
		}
		hero.addObject(" = ");
		{
			SPAN label = new SPAN("10");
			label.setId("result");
			hero.addObject(label);
		}
		ContentManager.addLineBreak(hero);
		{
			HButton calc = new HButton("calc");
			calc.setButtonType(HButton.BUTTON_PRIMARY);
			calc.setButtonSize(HButton.BUTTON_LARGE);
			calc.enableEvent("onclick");
			calc.setOnClickListener("calc_onclick");
			calc.addObject("Calculate");
			hero.addObject(calc);
		}
	}
	
	@HActionHandler(args={"no1", "no2"}, name="calc_onclick", eventType=HOnClickAction.class) 
	public void calc_onclick(Updates updates, String[] args, String param) {
		int no1, no2;
		no1 = Integer.parseInt(args[0]);
		no2 = Integer.parseInt(args[1]);
		updates.addUpdate("result", (no1 + no2) + ""); 
	}

}
