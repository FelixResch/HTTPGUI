package at.resch.html.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import at.resch.html.elements.HR;
import at.resch.html.enums.Style;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Page {
	
	public String title() default "some_page";
	public Style style() default Style.IVORY;
	public Class<?> delimiter () default HR.class;
}
