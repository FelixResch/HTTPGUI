package at.resch.html.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import at.resch.html.elements.DIV;
import at.resch.html.elements.HR;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Partial {
	
	public Class<?> delimiter () default HR.class;
	public Class<?> parent () default DIV.class;
}
